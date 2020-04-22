package net.jeikobu.kotomi.base.impl.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.kotomi.base.command.AbstractCommand
import net.jeikobu.kotomi.base.command.CommandData

class ChangePrefixCommand : AbstractCommand() {
    override val name: String
        get() = "changePrefix"

    override val aliases: List<String>
        get() = listOf("cp")

    override val argsLength: Int
        get() = 1

    override val permissions: List<Permission>
        get() = listOf(Permission.ADMINISTRATOR)

    override fun run(commandData: CommandData, message: Message) = context(commandData, message) {
        configManager.getGuildConfig(destinationGuild).commandPrefix = args[0]
        destinationChannel.sendMessage(getLocalized("success", args[0])).queue()
    }
}