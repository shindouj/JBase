package net.jeikobu.jbase.command;

import net.jeikobu.jbase.Localized;
import net.jeikobu.jbase.config.AbstractConfigManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Locale;

public abstract class AbstractCommand extends Localized {
    protected final IGuild destinationGuild;
    protected final IChannel destinationChannel;
    protected final IUser sendingUser;
    protected final AbstractConfigManager configManager;
    protected final List<String> args;

    public AbstractCommand(CommandData data) {
        this.destinationGuild = data.destinationGuild;
        this.destinationChannel = data.destinationChannel;
        this.sendingUser = data.sendingUser;
        this.configManager = data.configManager;
        this.args = data.args;
    }

    public IGuild getDestinationGuild() {
        return destinationGuild;
    }

    public IChannel getDestinationChannel() {
        return destinationChannel;
    }

    public IUser getSendingUser() {
        return sendingUser;
    }

    public String usageMessage() throws IllegalAccessException {
        throw new IllegalAccessException("This method is not overridden!");
    }

    public String getLocalized(String key) {
        Locale locale = configManager.getLocale(destinationGuild);
        return getLocalized(locale, key);
    }

    public String getLocalized(String key, Object... elements) {
        Locale locale = configManager.getLocale(destinationGuild);
        return getLocalized(locale, key, elements);
    }

    public abstract void run(IMessage message);
}

