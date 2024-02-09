package com.github.caciocavallosilano.cacio.ctc.junit;

import com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.*;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.awt.*;
import java.lang.reflect.Method;


public class CTCInterceptor {
    @RuntimeType
    public static GraphicsEnvironment intercept() {
        return CTCGraphicsEnvironment.getInstance();
    }
}
