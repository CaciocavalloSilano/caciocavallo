package net.java.openjdk.cacio.ctc;

import java.util.Locale;

class KeyStrokeMappingFactory {

    private static KeyStrokeMappingFactory instance = new KeyStrokeMappingFactory();

    private KeyStrokeMapping mapping;

    static KeyStrokeMappingFactory getInstance() {
        return instance;
    }

    KeyStrokeMapping getKeyStrokeMapping() {
        if (mapping == null) {
            Locale locale = Locale.getDefault();
            if (locale.getCountry().equals("DE")) {
                mapping = new KeyStrokeMappingDE();
            } else {
                mapping = new KeyStrokeMappingEN();
            }
        }
        return mapping;
    }
}
