package net.jeikobu.jbase.config

import net.dv8tion.jda.core.entities.Guild
import net.jeikobu.jbase.impl.config.VolatileStorage
import java.util.*

abstract class AbstractConfigManager {
    val volatileStorage = VolatileStorage()

    abstract val globalConfig: IGlobalConfig

    fun getGuildConfig(guild: Guild): AbstractGuildConfig {
        return getGuildConfig(guild, globalConfig)
    }

    protected abstract fun getGuildConfig(guild: Guild, globalConfig: IGlobalConfig): AbstractGuildConfig

    fun getLocale(guild: Guild): Locale {
        return getGuildConfig(guild).guildLocale ?: globalConfig.globalLocale
    }

    fun getCommandPrefix(guild: Guild): String {
        return getGuildConfig(guild).commandPrefix ?: globalConfig.defaultCommandPrefix
    }
}