package net.jeikobu.jbase;

import net.jeikobu.jbase.command.CommandManager;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public abstract class AbstractBot {
    private final IDiscordClient client;
    private final EventDispatcher dispatcher;
    private final CommandManager commandManager = CommandManager.INSTANCE;

    public AbstractBot(ClientBuilder clientBuilder) {
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
}
