package net.jeikobu.jbase.impl.commands

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import org.joda.convert.StringConvert
import org.pmw.tinylog.Logger
import sx.blah.discord.handle.obj.IMessage
import java.lang.RuntimeException
import java.util.*

@Command(name = "changeLocale", argsLength = 1)
class ChangeLocaleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage?) {
        try {
            val locale = StringConvert.INSTANCE.convertFromString(Locale::class.java, args[0])
            configManager.getGuildConfig(destinationGuild).setGuildLocale(locale)
            destinationChannel.sendMessage(getLocalized("localeCommand.changeSuccessful", locale))
        } catch (e: RuntimeException) {
            Logger.error(e, "Error during conversion")
            destinationChannel.sendMessage(getLocalized("localeCommand.badLocaleFormat"))
        }
    }

    override fun usageMessage(): String {
        return getLocalized("localeCommand.usage")
    }
}