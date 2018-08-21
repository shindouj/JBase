package net.jeikobu.jbase;

import net.jeikobu.jbase.command.CommandManager;
import net.jeikobu.jbase.config.AbstractConfigManager;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public abstract class AbstractBot {
    private final IDiscordClient client;
    private final EventDispatcher dispatcher;
    private final CommandManager commandManager;
    private final AbstractConfigManager configManager;

    public AbstractBot(ClientBuilder clientBuilder, AbstractConfigManager configManager) {
        commandManager = new CommandManager(configManager);
        this.configManager = configManager;

        clientBuilder.withToken(configManager.getGlobalConfig().getToken());

        client = clientBuilder.build();
        dispatcher = client.getDispatcher();
        dispatcher.registerListener(commandManager);
    }

    public IDiscordClient getClient() {
        return client;
    }

    public EventDispatcher getDispatcher() {
        return dispatcher;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AbstractConfigManager getConfigManager() {
        return configManager;
    }
}
