package net.jeikobu.kotomi.base.command

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.jeikobu.kotomi.base.Localized
import net.jeikobu.kotomi.base.config.AbstractGuildConfig
import net.jeikobu.kotomi.base.config.IGlobalConfig

abstract class AbstractCommand : Localized() {
    abstract val name: String
    open val aliases: List<String>
        get() = emptyList()
    open val argsLength
        get() = 0
    open val permissions: List<Permission>
        get() = emptyList()

    fun context(commandData: CommandData, message: Message, run: CommandContext.() -> Unit) {
        val context = CommandContext(commandData.destinationGuild, commandData.destinationChannel, commandData.sendingUser,
                commandData.configManager, commandData.args, message)
        context.run()
    }

    abstract fun run(commandData: CommandData, message: Message)
}