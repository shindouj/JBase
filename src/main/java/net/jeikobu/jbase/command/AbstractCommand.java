package net.jeikobu.jbase.command;

import io.rincl.Rincled;
import net.jeikobu.jbase.config.AbstractConfigManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class AbstractCommand implements Rincled {
    private final IGuild destinationGuild;
    private final IChannel destinationChannel;
    private final IUser sendingUser;
    private final AbstractConfigManager configManager;

    private AbstractCommand(final IGuild destinationGuild, final IChannel destinationChannel, final IUser sendingUser,
                           final AbstractConfigManager configManager) {
        this.destinationGuild = destinationGuild;
        this.destinationChannel = destinationChannel;
        this.sendingUser = sendingUser;
        this.configManager = configManager;
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

    public abstract void run(IMessage message);
}

