package net.jeikobu.jbase.impl.commands

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions

@Command(name = "changePrefix", argsLength = 1, permissions = [Permissions.ADMINISTRATOR])
class ChangePrefixCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage?) {
        configManager.getGuildConfig(destinationGuild).setCommandPrefix(args[0])
        destinationChannel.sendMessage(getLocalized("success", args[0]))
    }

    override fun usageMessage(): String {
        return getLocalized("usage")
    }
}