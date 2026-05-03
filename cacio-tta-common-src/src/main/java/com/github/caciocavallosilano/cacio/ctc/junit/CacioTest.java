package com.github.caciocavallosilano.cacio.ctc.junit;

import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@GUITest
@ExtendWith({CacioExtension.class, ScreenshotFailureExtension.class})
@Inherited
public @interface CacioTest {
}
