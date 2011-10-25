package net.java.openjdk.cacio.ctc.junit;

import static org.fest.swing.annotation.GUITestFinder.isGUITest;
import static org.fest.swing.junit.runner.Formatter.testNameFrom;

import java.lang.reflect.Method;

import javax.swing.plaf.metal.MetalLookAndFeel;

import net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment;
import net.java.openjdk.cacio.ctc.CTCToolkit;

import org.fest.swing.junit.runner.FailureScreenshotTaker;
import org.fest.swing.junit.runner.ImageFolderCreator;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class CacioFESTRunner extends BlockJUnit4ClassRunner {

    class MethodInvoker extends Statement {

        private final FrameworkMethod testMethod;
        private final Object target;
        private final FailureScreenshotTaker screenshotTaker;

        MethodInvoker(FrameworkMethod testMethod, Object target, FailureScreenshotTaker screenshotTaker) {
          this.testMethod= testMethod;
          this.target= target;
          this.screenshotTaker = screenshotTaker;
        }

        public void evaluate() throws Throwable {
          try {
            testMethod.invokeExplosively(target);
          } catch (Throwable t) {
            takeScreenshot();
            throw t;
          }
        }

        private void takeScreenshot() {
          Method realMethod = testMethod.getMethod();
          final Class<?> testClass = realMethod.getDeclaringClass();
          if (!(isGUITest(testClass, realMethod))) return;
          screenshotTaker.saveScreenshot(testNameFrom(testClass, realMethod));
        }
    }

    public CacioFESTRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        System.out.println("cacio fest runner");
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

    /**
     * Returns a <code>{@link Statement}</code> that invokes {@code method} on {@code test}. The created statement will
     * take and save the screenshot of the desktop in case of a failure.
     */
    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
      return new MethodInvoker(method, test, new FailureScreenshotTaker(new ImageFolderCreator().createImageFolder()));
    }
}
