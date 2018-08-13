package net.jeikobu.jbase.command;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public abstract class AbstractCommand {
    private final IGuild destinationGuild;
    private final IChannel destinationChannel;
    private final IUser sendingUser;

    public AbstractCommand(final IGuild destinationGuild, final IChannel destinationChannel, final IUser sendingUser) {
        this.destinationGuild = destinationGuild;
        this.destinationChannel = destinationChannel;
        this.sendingUser = sendingUser;
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

    public abstract void run(IMessage message);
}

