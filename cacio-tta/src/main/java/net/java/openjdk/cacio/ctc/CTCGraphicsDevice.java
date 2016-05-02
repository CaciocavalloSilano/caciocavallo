package net.java.openjdk.cacio.ctc;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class CTCGraphicsDevice extends GraphicsDevice {

    private CTCGraphicsConfiguration defaultConfig;

    @Override
    public int getType() {
        return GraphicsDevice.TYPE_RASTER_SCREEN;
    }

    @Override
    public String getIDstring() {
        return "CTCGraphicsDevice";
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
        GraphicsConfiguration[] configs = new GraphicsConfiguration[1];
        configs[0] = getDefaultConfiguration();
        return configs;
    }

    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        if (defaultConfig == null) {
            defaultConfig = new CTCGraphicsConfiguration(this);
        }
        return defaultConfig;
    }

}
