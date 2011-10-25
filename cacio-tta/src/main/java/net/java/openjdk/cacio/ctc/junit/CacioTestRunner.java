package net.java.openjdk.cacio.ctc.junit;

import javax.swing.plaf.metal.MetalLookAndFeel;

import net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment;
import net.java.openjdk.cacio.ctc.CTCToolkit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CacioTestRunner extends BlockJUnit4ClassRunner {

    public CacioTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public void run(RunNotifier notifier) {
        String oldTK = System.getProperty("awt.toolkit");
        String oldGE = System.getProperty("java.awt.graphicsenv");
        String oldLAF = System.getProperty("swing.defaultlaf");
        System.setProperty("awt.toolkit", CTCToolkit.class.getName());
        System.setProperty("java.awt.graphicsenv", CTCGraphicsEnvironment.class.getName());
        System.setProperty("swing.defaultlaf", MetalLookAndFeel.class.getName());
        try {
          super.run(notifier);
        } finally {
            resetProperty("awt.toolkit", oldTK);
            resetProperty("java.awt.graphicsenv", oldGE);
            resetProperty("swing.defaultlaf", oldLAF);
        }
    }

    private void resetProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }
}
