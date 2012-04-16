package net.java.openjdk.cacio.ctc.junit;

import javax.swing.plaf.metal.MetalLookAndFeel;

import net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment;
import net.java.openjdk.cacio.ctc.CTCToolkit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CacioTestRunner extends BlockJUnit4ClassRunner {

    static {
        System.setProperty("awt.toolkit", CTCToolkit.class.getName());
        System.setProperty("java.awt.graphicsenv", CTCGraphicsEnvironment.class.getName());
        System.setProperty("swing.defaultlaf", MetalLookAndFeel.class.getName());
        System.setProperty("java.awt.headless", "false");
    }

    public CacioTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

}
