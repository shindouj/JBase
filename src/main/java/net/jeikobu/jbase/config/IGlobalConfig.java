package net.jeikobu.jbase.config;

import java.util.Locale;

public interface IGlobalConfig {
    String getDefaultCommandPrefix();
    String getToken();
    Locale getGlobalLocale();
    <T> T getValue(String key, Class<T> valueType);
}
