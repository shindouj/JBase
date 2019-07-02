package net.jeikobu.jbase

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.CommandManager
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.impl.commands.ChangeLocaleCommand
import net.jeikobu.jbase.impl.commands.ChangePrefixCommand
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractBot(configManager: AbstractConfigManager) {
    protected val defaultCommands: MutableList<KClass<out AbstractCommand>> = Arrays.asList(ChangeLocaleCommand::class, ChangePrefixCommand::class)
    protected val commandManager = CommandManager(configManager)
    private val clientBuilder = JDABuilder()

    val client: JDA

    init {
        if (configManager.globalConfig.useDefaultCommands) {
            registerDefaultCommands()
        }

        clientBuilder.setToken(configManager.globalConfig.token)
        clientBuilder.addEventListener(commandManager)

        client = clientBuilder.build()
        client.awaitReady()
    }

    fun registerDefaultCommands() {
        commandManager.registerAll(defaultCommands)
    }

    fun deregisterDefaultCommands() {
        commandManager.deregisterAll(defaultCommands)
    }
}