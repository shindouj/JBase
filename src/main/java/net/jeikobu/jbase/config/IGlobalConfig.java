package net.jeikobu.jbase.config;

import java.util.Locale;

public interface IGlobalConfig {
    String getDefaultCommandPrefix();
    String getToken();
    boolean useDefaultCommands();
    Locale getGlobalLocale();
    <T> T getValue(String key, Class<T> valueType);
}
