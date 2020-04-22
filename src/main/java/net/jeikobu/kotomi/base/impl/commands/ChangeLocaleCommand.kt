package net.jeikobu.kotomi.base.impl.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.kotomi.base.command.AbstractCommand
import net.jeikobu.kotomi.base.command.CommandData
import org.pmw.tinylog.Logger
import java.lang.RuntimeException
import java.util.*

class ChangeLocaleCommand : AbstractCommand() {
    override val name: String
        get() = "changeLocale"

    override val aliases: List<String>
        get() = listOf("cl")

    override val argsLength: Int
        get() = 1

    override val permissions: List<Permission>
        get() = listOf(Permission.ADMINISTRATOR)

    override fun run(commandData: CommandData, message: Message) = context(commandData, message) {
        try {
            val locale = Locale(args[0])
            configManager.getGuildConfig(destinationGuild).guildLocale = locale
            destinationChannel.sendMessage(getLocalized("localeCommand.changeSuccessful", locale)).queue()
        } catch (e: RuntimeException) {
            Logger.error(e, "Error during conversion")
            destinationChannel.sendMessage(getLocalized("localeCommand.badLocaleFormat")).queue()
        }
    }
}