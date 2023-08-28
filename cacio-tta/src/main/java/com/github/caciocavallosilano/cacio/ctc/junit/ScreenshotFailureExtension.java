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

import org.assertj.swing.junit.runner.FailureScreenshotTaker;
import org.assertj.swing.junit.runner.ImageFolderCreator;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.swing.annotation.GUITestFinder.isGUITest;
import static org.assertj.swing.junit.runner.Formatter.testNameFrom;

public class ScreenshotFailureExtension implements TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler {

    private final FailureScreenshotTaker screenshotTaker;

    public ScreenshotFailureExtension() {
        System.setProperty("org.assertj.swing.preserve_screenshots", "true");
        this.screenshotTaker = new FailureScreenshotTaker(new ImageFolderCreator().createImageFolder());

        try {
            // yuck, the anonymous logger level can't be set
            // the jul logger seems to work for tests in this repos, but it doesn't when using as a library, maybe because of logback/slf4j
            Field loggerField = FailureScreenshotTaker.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            Logger logger = (Logger) loggerField.get(null);
            logger.setLevel(Level.FINE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        takeScreenshot(context);
        throw throwable;
    }

    private void takeScreenshot(ExtensionContext context) {
        if (isGUITest(context.getRequiredTestClass(), context.getRequiredTestMethod())) {
            screenshotTaker.saveScreenshot(testNameFrom(context.getRequiredTestClass(), context.getRequiredTestMethod()));
        }
    }

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        takeScreenshot(context);
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        takeScreenshot(context);
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        takeScreenshot(context);
        throw throwable;
    }

    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        takeScreenshot(context);
        throw throwable;
    }
}
