package net.jeikobu.jbase.command

import net.dv8tion.jda.core.entities.*
import net.jeikobu.jbase.Localized
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.config.AbstractGuildConfig
import net.jeikobu.jbase.config.IGlobalConfig

abstract class AbstractCommand(commandData: CommandData) : Localized() {
    protected val destinationGuild: Guild = commandData.destinationGuild
    protected val destinationChannel: TextChannel = commandData.destinationChannel
    protected val sendingUser: Member = commandData.sendingUser
    protected val configManager: AbstractConfigManager = commandData.configManager
    protected val args: List<String> = commandData.args

    val globalConfig: IGlobalConfig
        get() = configManager.globalConfig

    val guildConfig: AbstractGuildConfig
        get() = configManager.getGuildConfig(destinationGuild)

    fun getVolatile(key: String) = configManager.volatileStorage.get(destinationGuild, key)
    fun setVolatile(key: String, value: String) = configManager.volatileStorage.set(destinationGuild, key, value)

    fun getLocalized(key: String) = getLocalized(configManager.getLocale(destinationGuild), key)
    fun getLocalized(key: String, vararg elements: Any) = getLocalized(configManager.getLocale(destinationGuild), key, *elements)

    @Throws(IllegalAccessException::class)
    open fun usageMessage(): String {
        throw IllegalAccessException("Unimplemented")
    }

    abstract fun run(message: Message)
}