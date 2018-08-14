package net.jeikobu.jbase.config;

import sx.blah.discord.handle.obj.IGuild;

public abstract class AbstractConfigManager {
    public abstract IGlobalConfig getGlobalConfig();
    public abstract AbstractGuildConfig getGuildConfig(IGuild guild);
}
