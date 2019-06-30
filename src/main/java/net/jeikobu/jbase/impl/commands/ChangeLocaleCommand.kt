package net.jeikobu.jbase.impl.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import org.pmw.tinylog.Logger
import java.lang.RuntimeException
import java.util.*

@Command(name = "changeLocale", argsLength = 1, permissions = [Permission.ADMINISTRATOR])
class ChangeLocaleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        try {
            val locale = Locale(args[0])
            configManager.getGuildConfig(destinationGuild).setGuildLocale(locale)
            destinationChannel.sendMessage(getLocalized("localeCommand.changeSuccessful", locale))
        } catch (e: RuntimeException) {
            Logger.error(e, "Error during conversion")
            destinationChannel.sendMessage(getLocalized("localeCommand.badLocaleFormat"))
        }
    }

    override fun usageMessage(): String {
        return getLocalized("localeCommand.usage", configManager.getCommandPrefix(destinationGuild))
    }
}