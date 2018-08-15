package net.jeikobu.jbase.config;

import sx.blah.discord.handle.obj.IGuild;

import java.util.Locale;

public abstract class AbstractGlobalConfig {
    public AbstractGlobalConfig(IGuild guild) {

    }

    public abstract String getDefaultCommandPrefix();
    public abstract void   setDefaultCommandPrefix(String prefix);

    public abstract Locale getGlobalLocale();
    public abstract void   setGlobalLocale(Locale locale);
}
