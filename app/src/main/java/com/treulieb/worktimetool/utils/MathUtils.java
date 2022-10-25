package com.treulieb.worktimetool.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {

    public static final MathContext mathContext = new MathContext(3, RoundingMode.HALF_DOWN);
    public static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

    public static BigDecimal decimal(float val) {
        return new BigDecimal(val, mathContext);
    }

    public static BigDecimal decimal(String val) {
        return new BigDecimal(val, mathContext);
    }
}
