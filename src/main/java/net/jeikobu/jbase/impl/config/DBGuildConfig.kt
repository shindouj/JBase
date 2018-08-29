package net.jeikobu.jbase.impl.config

import net.jeikobu.jbase.config.AbstractGuildConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.DatabaseDialect
import org.joda.convert.StringConvert
import org.pmw.tinylog.Logger
import sx.blah.discord.handle.obj.IGuild
import java.lang.RuntimeException
import java.util.*
import javax.sql.DataSource
import kotlin.NoSuchElementException
import kotlin.reflect.full.createInstance

class DBGuildConfig(private val guild: IGuild, dataSource: DataSource) : AbstractGuildConfig(guild) {
    private val db by lazy {
        Database.connect(dataSource)
    }

    private val guildID by lazy {
        guild.longID
    }

    init {
        Database.registerDialect("mariadb") {
            @Suppress("UNCHECKED_CAST")
            val type = (Class.forName("org.jetbrains.exposed.sql.vendors.MysqlDialect") as Class<DatabaseDialect>).kotlin
            type.createInstance()
        }
    }

    override fun getCommandPrefix(): Optional<String> {
        return getValue("commandPrefix", String::class.java)
    }

    override fun setCommandPrefix(prefix: String?) {
        return setValue("commandPrefix", requireNotNull(prefix))
    }

    override fun getGuildLocale(): Optional<Locale> {
        return getValue("guildLocale", Locale::class.java)
    }

    override fun setGuildLocale(locale: Locale?) {
        val strLocale = StringConvert.INSTANCE.convertToString(requireNotNull(locale))
        return setValue("guildLocale", strLocale)
    }

    override fun <T : Any?> getValue(key: String?, defaultValue: String?, valueType: Class<T>?): Optional<T> {
        if (key == null) {
            throw IllegalArgumentException("(GuildConfig.Get) Key must not be null!")
        }

        if (valueType == null) {
            throw IllegalArgumentException("(GuildConfig.Get) Value type must not be null!")
        }

        if (defaultValue == null) {
            Logger.trace("(GuildConfig.Get) No default value supplied for key = {}, returned value might be empty", key)
        }

        val result: String? = transaction(db) {
            //addLogger(StdOutSqlLogger)
            create(GuildKVConfig)

            val query: Query = GuildKVConfig.select { (GuildKVConfig.guildID eq guildID) and (GuildKVConfig.key eq key) }
            try {
                return@transaction query.first()[GuildKVConfig.value]
            } catch (e: NoSuchElementException) {
                Logger.warn("(GuildConfig) Key '{}' not found, falling back to default value", key)
                return@transaction null
            }
        }

        return try {
            val convertedValue = StringConvert.INSTANCE.convertFromString(valueType, result)
                    ?: StringConvert.INSTANCE.convertFromString(valueType, defaultValue)

            Optional.ofNullable(convertedValue)
        } catch (e: RuntimeException) {
            Logger.error(e, "(GuildConfig) Error during conversion")
            Optional.empty()
        }
    }

    override fun setValue(key: String?, value: String?) {
        if (key == null) {
            throw IllegalArgumentException("(GuildConfig.Set) Key must not be null!")
        }

        if (value == null) {
            throw IllegalArgumentException("(GuildConfig.Set) Value must not be null!")
        }

        transaction(db) {
            create(GuildKVConfig)

            GuildKVConfig.insertOrUpdate(   GuildKVConfig.value) {
                it[GuildKVConfig.guildID] = this@DBGuildConfig.guildID
                it[GuildKVConfig.key] = key
                it[GuildKVConfig.value] = value
            }
        }
    }

    object GuildKVConfig : Table() {
        val guildID = long("guildID").primaryKey(0)
        val key = varchar("key", 32).primaryKey(1)
        val value = varchar("value", 1024)

        init {
            index(true, guildID, key) // case 1 - Unique index
        }
    }
}

fun <T : Table> T.insertOrUpdate(vararg onDuplicateUpdateKeys: Column<*>,
                                 body: T.(InsertStatement<Number>) -> Unit) = InsertOrUpdate<Number>(onDuplicateUpdateKeys, this).apply {
    body(this)
    execute(TransactionManager.current())
}

class InsertOrUpdate<Key : Any>(private val onDuplicateUpdateKeys: Array<out Column<*>>, table: Table,
                                isIgnore: Boolean = false) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if (onDuplicateUpdateKeys.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDuplicateUpdateKeys.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}