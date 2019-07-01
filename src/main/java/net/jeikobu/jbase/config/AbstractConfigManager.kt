package net.jeikobu.jbase.config

import net.dv8tion.jda.core.entities.Guild
import net.jeikobu.jbase.impl.config.VolatileStorage
import java.util.*

abstract class AbstractConfigManager {
    val volatileStorage = VolatileStorage()

    abstract val globalConfig: IGlobalConfig
    abstract fun getGuildConfig(guild: Guild): AbstractGuildConfig

    fun getLocale(guild: Guild): Locale {
        return getGuildConfig(guild).guildLocale.orElse(globalConfig.globalLocale)
    }

    fun getCommandPrefix(guild: Guild): String {
        return getGuildConfig(guild).commandPrefix.orElse(globalConfig.defaultCommandPrefix)
    }
}