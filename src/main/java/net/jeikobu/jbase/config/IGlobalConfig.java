package net.jeikobu.jbase.config;

import java.util.Locale;

public interface IGlobalConfig {
    public abstract String getDefaultCommandPrefix();
    public abstract void   setDefaultCommandPrefix(String prefix);

    public abstract Locale getGlobalLocale();
    public abstract void   setGlobalLocale(Locale locale);
}
