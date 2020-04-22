package net.jeikobu.kotomi.base.command

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.jeikobu.kotomi.base.Localized
import net.jeikobu.kotomi.base.config.AbstractConfigManager
import org.pmw.tinylog.Logger

class CommandManager(private val configManager: AbstractConfigManager) : Localized(), EventListener {
    private val registeredCommands = ArrayList<AbstractCommand>()

    fun registerCommand(command: AbstractCommand) {
        registeredCommands.add(command)
    }

    fun deregisterCommand(command: AbstractCommand) {
        registeredCommands.remove(command)
    }

    fun registerAll(cmdCollection: Collection<AbstractCommand>) {
        registeredCommands.addAll(cmdCollection)
    }

    fun deregisterAll(cmdCollection: Collection<AbstractCommand>) {
        registeredCommands.removeAll(cmdCollection)
    }

    override fun onEvent(e: Event) {
        if (e is MessageReceivedEvent) {
            val sender = e.guild.getMember(e.author)
            val commandPrefix = configManager.getCommandPrefix(e.guild)
            val locale = configManager.getLocale(e.guild)
            val message = e.message
            val messageStr = message.contentRaw

            if (messageStr.isEmpty() || !messageStr.startsWith(commandPrefix)) {
                return
            }

            val args = messageStr.split(" ")
            if (args.size < 2) {
                return
            }

            val matchingCommands = registeredCommands.filter { it.name == args[1] || it.aliases.contains(args[1]) }
            if (matchingCommands.size > 1) {
                Logger.error("Critical error: More than one command matching!")
                e.textChannel.sendMessage(getLocalized(locale, "fatalError", "More than one command matching")).queue()
            } else if (matchingCommands.isEmpty()) {
                return
            }

            val command = matchingCommands.first()
            val commandData = CommandData(e.guild, e.textChannel, sender, configManager, args.subList(2, args.size))

            if (!sender.permissions.containsAll(command.permissions)) {
                e.textChannel.sendMessage(getLocalized(locale, "insufficientPermissions")).queue()
                return
            }

            if (command.argsLength + 2 > args.size) {
                e.textChannel.sendMessage(getLocalized(locale, "notEnoughArgs")).queue()
                return
            } else {
                command.run(commandData, message)
            }
        }
    }
}