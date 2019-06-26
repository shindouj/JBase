package net.jeikobu.jbase.config

import net.jeikobu.jbase.impl.config.VolatileStorage
import sx.blah.discord.handle.obj.IGuild
import java.util.*

abstract class AbstractConfigManager {
    val volatileStorage = VolatileStorage()

    abstract val globalConfig: IGlobalConfig
    abstract fun getGuildConfig(guild: IGuild): AbstractGuildConfig

    fun getLocale(guild: IGuild): Locale {
        return getGuildConfig(guild).guildLocale.orElse(globalConfig.globalLocale)
    }

    fun getCommandPrefix(guild: IGuild): String {
        return getGuildConfig(guild).commandPrefix.orElse(globalConfig.defaultCommandPrefix)
    }
}