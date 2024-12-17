package com.fx702p.emulator;

import java.math.*;

import com.fx702p.interpreters.NormalFormatter;

public interface Fx702pConstants
{
  static public final int DISPLAY_SIZE = 20;
  static public final int WAIT_MAX = 1000;
  static public final int PRT_CSR_MAX = DISPLAY_SIZE;
  static public final double DMS_MAX = 100000;
  static public final int LINE_NUMBER_MAX = 10000;
  static public final long NORMAL_PRINT_SCROLL_DELAY = 500;
  static public final long FAST_PRINT_SCROLL_DELAY = 30;
  static public final long PRINT_WAIT_DELAY = 50;
  static public final int INPUT_BUFFER_MAX_SIZE = 62;
  static public final String INPUT_PROMPT = "?";

  static public final int PROGRAM_RUNNING_MODE = -1;
  static public final int RUN_MODE = 0;
  static public final int WRT_MODE = 1;
  static public final int TRACE_ON_MODE = 2;
  static public final int TRACE_OFF_MODE = 3;
  static public final int DEG_MODE = 4;
  static public final int RAD_MODE = 5;
  static public final int GRA_MODE = 6;
  static public final int PRT_ON_MODE = 7;
  static public final int PRT_OFF_MODE = 8;

  static public final int DEFAULT_VARIABLES_COUNT = 26;
  static public final int ARRAY_COUNT_MAX = 200;

  static public final int DEFAULT_PROGRAM_STEPS_COUNT = 1680;
  static public final int DEFM_MAX = 20;

  static public final int ARRAY_FIRST_DIMENSION_MAX = 20;
  static public final int ARRAY_SECOND_DIMENSION_MAX = 10;
  static public final int FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE = Character.getNumericValue('A');

  static public final int X_INDEX = 23;
  static public final int Y_INDEX = 24;

  static public final int PROGRAM_COUNT = 10;

  static public final double DEG_CONVERSION = Math.PI / 180;
  static public final double RAD_CONVERSION = 1;
  static public final double GRD_CONVERSION = Math.PI / 200;
  static public final double DEG_MAX = 1440;
  static public final double RAD_MAX = 8 * Math.PI;
  static public final double GRD_MAX = 1600;
  static public final double HYPERBOLIC_MAX_ARGUMENT = 230;


  static public final BigDecimal SIXTY = new BigDecimal(60);
  static public final BigDecimal SQUARED_SIXTY = new BigDecimal(3600);
  static public final BigDecimal BIG_PI = new BigDecimal(Math.PI);
  static public final BigDecimal HALF = BigDecimal.ONE.divide(new BigDecimal(2));
  static public final MathContext MATH_CONTEXT = new MathContext(12);

  static public final NormalFormatter NORMAL_FORMATTER = new NormalFormatter();
  static public final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_DOWN;


  static public final int ALL_PROGRAMS_LOADED = -1;

  static public final int FILENAME_MAX_LENGTH = 8;
  static public final String FILENAME_SUFFIX = ".702";
}
