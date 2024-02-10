package com.github.caciocavallosilano.cacio.ctc.junit;

import com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment;
import net.bytebuddy.implementation.bind.annotation.*;

import java.awt.*;


public class CTCInterceptor {
    @RuntimeType
    public static GraphicsEnvironment intercept() {
        return CTCGraphicsEnvironment.getInstance();
    }
}
