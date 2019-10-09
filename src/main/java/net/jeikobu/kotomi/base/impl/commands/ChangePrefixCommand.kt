package net.jeikobu.kotomi.base.impl.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.kotomi.base.command.AbstractCommand
import net.jeikobu.kotomi.base.command.Command
import net.jeikobu.kotomi.base.command.CommandData

@Command(name = "changePrefix", argsLength = 1, permissions = [Permission.ADMINISTRATOR])
class ChangePrefixCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        configManager.getGuildConfig(destinationGuild).commandPrefix = args[0]
        destinationChannel.sendMessage(getLocalized("success", args[0])).queue()
    }

    override fun usageMessage(): String {
        return getLocalized("usage", configManager.getCommandPrefix(destinationGuild))
    }
}