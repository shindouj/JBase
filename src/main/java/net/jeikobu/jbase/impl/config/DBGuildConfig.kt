package net.jeikobu.jbase.impl.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.jeikobu.jbase.config.AbstractGuildConfig
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.convert.StringConvert
import org.pmw.tinylog.Logger
import sx.blah.discord.handle.obj.IGuild
import java.lang.RuntimeException
import java.util.*
import kotlin.NoSuchElementException

class DBGuildConfig(private val guild: IGuild, hikariConfigPath: String) : AbstractGuildConfig(guild) {
    private val hikariDS by lazy {
        HikariDataSource(HikariConfig(hikariConfigPath))
    }

    private val db by lazy {
        Database.connect(hikariDS)
    }

    private val guildID by lazy {
        guild.longID
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
            Logger.error("(GuildConfig.Get) No default value supplied for key = {}, returned value might be empty", key)
        }

        val result: String? = transaction(db) {
            create(GuildKVConfig)

            val query: Query = GuildKVConfig.select { (GuildKVConfig.guildID eq guildID) and (GuildKVConfig.key eq key) }
            try {
                return@transaction query.first()[GuildKVConfig.value]
            } catch (e: NoSuchElementException) {
                Logger.error("(GuildConfig) Key not found, falling back to default value")
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

            GuildKVConfig.insertOrUpdate(GuildKVConfig.value) {
                it[GuildKVConfig.guildID] = this@DBGuildConfig.guildID
                it[GuildKVConfig.key] = key
                it[GuildKVConfig.value] = value
            }
        }
    }

    object GuildKVConfig : IntIdTable() {
        val guildID = long("guildID").uniqueIndex("guildToKey")
        val key = varchar("key", 32).uniqueIndex("guildToKey")
        val value = varchar("key", 1024)
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