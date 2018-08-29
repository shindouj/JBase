package net.jeikobu.jbase;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public abstract class Localized {
    protected final String getLocalized(Locale locale, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getSimpleName(), locale);
        return bundle.getString(key);
    }

    protected final String getLocalized(Locale locale, String key, Object... elements) {
        String localized = getLocalized(locale, key);

        Pattern p = Pattern.compile("\\{\\}");
        for (Object element: elements) {
            localized = p.matcher(localized).replaceFirst(element.toString());
        }

        return localized;
    }
}
