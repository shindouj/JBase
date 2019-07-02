package net.jeikobu.jbase.impl.config

import net.dv8tion.jda.core.entities.Guild
import net.jeikobu.jbase.config.AbstractGuildConfig
import net.jeikobu.jbase.config.IGlobalConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.DatabaseDialect
import org.joda.convert.StringConvert
import org.pmw.tinylog.Logger
import java.lang.RuntimeException
import java.util.*
import javax.sql.DataSource
import kotlin.NoSuchElementException
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class DBGuildConfig(guild: Guild, dataSource: DataSource) : AbstractGuildConfig(guild) {
    private val commandPrefixKey = "commandPrefix"
    private val guildLocaleKey   = "guildLocale"

    override var commandPrefix: String?
        get() = getValue(commandPrefixKey)
        set(value) = setValue(commandPrefixKey, requireNotNull(value))

    override var guildLocale: Locale?
        get() = getValue(guildLocaleKey)
        set(value) = setValue(guildLocaleKey, StringConvert.INSTANCE.convertToString(requireNotNull(value)))

    private val db by lazy {
        Database.connect(dataSource)
    }

    private val guildID by lazy {
        guild.idLong
    }

    init {
        Database.registerDialect("mariadb") {
            @Suppress("UNCHECKED_CAST") val type = (Class.forName(
                "org.jetbrains.exposed.sql.vendors.MysqlDialect") as Class<DatabaseDialect>).kotlin
            type.createInstance()
        }
    }

    override fun <T : Any> getValue(key: String, defaultValue: String?, valueType: KClass<T>): T? {
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
                return@transaction null
            }
        }

        return try {
            StringConvert.INSTANCE.convertFromString(valueType.java, result)
                    ?: StringConvert.INSTANCE.convertFromString(valueType.java, defaultValue)
        } catch (e: RuntimeException) {
            Logger.error(e, "(GuildConfig) Error during conversion")
            null
        }
    }

    override fun setValue(key: String, value: String?) {
        if (value == null) {
            transaction(db) {
                create(GuildKVConfig)

                GuildKVConfig.deleteWhere {
                    (GuildKVConfig.guildID eq this@DBGuildConfig.guildID) and (GuildKVConfig.key eq key)
                }
            }
        } else {
            transaction(db) {
                create(GuildKVConfig)

                GuildKVConfig.insertOrUpdate(GuildKVConfig.value) {
                    it[GuildKVConfig.guildID] = this@DBGuildConfig.guildID
                    it[GuildKVConfig.key] = key
                    it[GuildKVConfig.value] = value
                }
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
                                 body: T.(InsertStatement<Number>) -> Unit) = InsertOrUpdate<Number>(
    onDuplicateUpdateKeys, this).apply {
    body(this)
    execute(TransactionManager.current())
}

class InsertOrUpdate<Key : Any>(private val onDuplicateUpdateKeys: Array<out Column<*>>, table: Table,
                                isIgnore: Boolean = false) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if (onDuplicateUpdateKeys.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDuplicateUpdateKeys.joinToString {
                "${transaction.identity(it)}=VALUES(${transaction.identity(it)})"
            }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}