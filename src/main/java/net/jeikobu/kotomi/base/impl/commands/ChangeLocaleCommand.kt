package net.jeikobu.kotomi.base.impl.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.kotomi.base.command.AbstractCommand
import net.jeikobu.kotomi.base.command.Command
import net.jeikobu.kotomi.base.command.CommandData
import org.pmw.tinylog.Logger
import java.lang.RuntimeException
import java.util.*

@Command(name = "changeLocale", argsLength = 1, permissions = [Permission.ADMINISTRATOR])
class ChangeLocaleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        try {
            val locale = Locale(args[0])
            configManager.getGuildConfig(destinationGuild).guildLocale = locale
            destinationChannel.sendMessage(getLocalized("localeCommand.changeSuccessful", locale)).queue()
        } catch (e: RuntimeException) {
            Logger.error(e, "Error during conversion")
            destinationChannel.sendMessage(getLocalized("localeCommand.badLocaleFormat")).queue()
        }
    }

    override fun usageMessage(): String {
        return getLocalized("localeCommand.usage", configManager.getCommandPrefix(destinationGuild))
    }
}