package net.jeikobu.kotomi.base.command

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.jeikobu.kotomi.base.Localized
import net.jeikobu.kotomi.base.config.AbstractConfigManager
import org.pmw.tinylog.Logger
import java.lang.NullPointerException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

class CommandManager(private val configManager: AbstractConfigManager) : Localized(), EventListener {
    private val registeredCommands = ArrayList<KClass<out AbstractCommand>>()

    fun registerCommand(clazz: KClass<out AbstractCommand>) {
        registeredCommands.add(clazz)
    }

    fun deregisterCommand(clazz: KClass<out AbstractCommand>) {
        registeredCommands.remove(clazz)
    }

    fun registerAll(classCollection: Collection<KClass<out AbstractCommand>>) {
        registeredCommands.addAll(classCollection)
    }

    fun deregisterAll(classCollection: Collection<KClass<out AbstractCommand>>) {
        registeredCommands.removeAll(classCollection)
    }

    override fun onEvent(event: Event) {
        if (event is MessageReceivedEvent) {
            val destGuild = event.guild
            val destChannel = event.textChannel
            val sender = destGuild.getMember(event.author)

            val commandPrefix = configManager.getCommandPrefix(destGuild)
            val locale = configManager.getLocale(destGuild)

            val message = event.message
            val messageStr = message.contentRaw

            if (messageStr.isEmpty() || !messageStr.startsWith(commandPrefix)) {
                return
            }

            val args = messageStr.split(" ")

            if (args.size < 2) {
                return
            }

            val suppliedCommandName = args[1]

            for (clazz in registeredCommands) {
                val commandAnnotation = clazz.findAnnotation<Command>()

                if (commandAnnotation == null) {
                    Logger.error("Fatal error during annotation search! One of registered commands have no annotation.")
                    continue
                }

                if (commandAnnotation.name.equals(suppliedCommandName, ignoreCase = true)) {
                    if (!sender.permissions.containsAll(commandAnnotation.permissions.asList())) {
                        destChannel.sendMessage(getLocalized(locale, "insufficientPermissions"))
                        return
                    }

                    val commandData = CommandData(destGuild, destChannel, sender, configManager, args.subList(2, args.size))
                    val command: AbstractCommand? = clazz.primaryConstructor?.call(commandData)

                    if (command == null) {
                        Logger.error("Fatal error during primary constructor call!")
                        destChannel.sendMessage(getLocalized(locale, "fatalError")).queue()
                        return
                    }

                    if (commandAnnotation.argsLength + 2 > args.size) {
                        try {
                            destChannel.sendMessage(command.usageMessage()).queue()
                        } catch (ex: IllegalAccessException) {
                            destChannel.sendMessage(getLocalized(locale, "notEnoughArgs")).queue()
                        }
                        return
                    } else {
                        command.run(message)
                    }
                }
            }
        }
    }

}