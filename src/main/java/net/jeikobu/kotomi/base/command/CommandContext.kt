package net.jeikobu.kotomi.base.command

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.TextChannel
import net.jeikobu.kotomi.base.config.AbstractConfigManager
import net.jeikobu.kotomi.base.config.AbstractGuildConfig
import net.jeikobu.kotomi.base.config.IGlobalConfig

class CommandContext(val destinationGuild: Guild, val destinationChannel: TextChannel, val sendingUser: Member,
                     val configManager: AbstractConfigManager, val args: List<String>, val message: Message) {
    val globalConfig: IGlobalConfig
        get() = configManager.globalConfig

    val guildConfig: AbstractGuildConfig
        get() = configManager.getGuildConfig(destinationGuild)

    fun getVolatile(key: String) = configManager.volatileStorage.get(destinationGuild, key)
    fun setVolatile(key: String, value: String) = configManager.volatileStorage.set(destinationGuild, key, value)

    fun AbstractCommand.getLocalized(key: String) = getLocalized(configManager.getLocale(destinationGuild), key)
    fun AbstractCommand.getLocalized(key: String, vararg elements: Any) = getLocalized(configManager.getLocale(destinationGuild), key, *elements)
}