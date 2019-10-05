/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.github.caciocavallosilano.cacio.ctc.junit;

import static org.assertj.swing.annotation.GUITestFinder.isGUITest;
import static org.assertj.swing.junit.runner.Formatter.testNameFrom;

import java.lang.reflect.Method;

import org.assertj.swing.junit.runner.FailureScreenshotTaker;
import org.assertj.swing.junit.runner.ImageFolderCreator;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class CacioAssertJRunner extends CacioTestRunner {

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

    public CacioAssertJRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
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
