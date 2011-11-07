package net.java.openjdk.cacio.ctc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class KeyStrokeMappingFactory {

    private static KeyStrokeMappingFactory instance = new KeyStrokeMappingFactory();

    private Map<String,KeyStrokeMapping> maps = new HashMap<String,KeyStrokeMapping>();

    static KeyStrokeMappingFactory getInstance() {
        return instance;
    }

    KeyStrokeMapping getKeyStrokeMapping() {
        String lang = Locale.getDefault().getCountry().toLowerCase();
        KeyStrokeMapping mapping = maps.get(lang);
        if (mapping == null) {
            if (lang.equals("de")) {
                mapping = new KeyStrokeMappingDE();
            } else {
                mapping = new KeyStrokeMappingEN();
            }
            maps.put(lang, mapping);
        }
        return mapping;
    }
}
