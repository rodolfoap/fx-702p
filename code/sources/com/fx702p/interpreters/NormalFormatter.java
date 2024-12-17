package com.fx702p.interpreters;

import java.math.*;
import java.util.Locale;

import com.fx702p.interpreters.formatters.*;

public class NormalFormatter implements Fx702pFormatter
{
  public void setRoundingMode(RoundingMode aRoundingMode)
  {
    roundingMode = aRoundingMode;
  }

  public String format(BigDecimal aNumber)
  {
    if (aNumber.compareTo(BigDecimal.ZERO) == 0)
    {
      return " 0";
    }
    else
    {
      Fx702pDecimalFormat decimalFormat = getFx702pDecimalFormat(aNumber);
      String formattedNumber = decimalFormat.format(aNumber).replace("E", "e ");
      if (formattedNumber.charAt(0) != '-')
      {
        return ' ' + formattedNumber;
      }
      else
      {
        return formattedNumber;
      }
    }
  }

  protected Fx702pDecimalFormat getFx702pDecimalFormat(BigDecimal aNumber)
  {
    Fx702pDecimalFormat decimalFormat;
    if (Math.abs(aNumber.doubleValue()) < MIN_NO_EXPONENT || Math.abs(aNumber.doubleValue()) >= MAX_NO_EXPONENT)
    {
      decimalFormat = new Fx702pDecimalFormat(getExponentFormat());
    }
    else
    {
      int magnitude = (int)Math.floor(Math.log10(Math.abs(aNumber.doubleValue()))) + 3;
      decimalFormat = new Fx702pDecimalFormat(getNoExponentFormats()[magnitude]);
    }
    prepareDecimalFormat(decimalFormat);
    return decimalFormat;
  }

  protected void prepareDecimalFormat(Fx702pDecimalFormat aFx702pDecimalFormat)
  {
    aFx702pDecimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    aFx702pDecimalFormat.setRoundingMode(roundingMode);
  }

  protected String getExponentFormat()
  {
    return EXPONENT_FORMAT;
  }

  protected String[] getNoExponentFormats()
  {
    return NO_EXPONENT_FORMATS;
  }

  protected RoundingMode roundingMode = RoundingMode.HALF_DOWN;

  static public final double MIN_NO_EXPONENT = 1E-3;
  static public final double MAX_NO_EXPONENT = 1E10;

  static public final String EXPONENT_FORMAT = "#.#########E00";

  static public final String[] NO_EXPONENT_FORMATS =
    {
      "#.############",
      "#.###########",
      "#.##########",
      "#.#########",
      "#.########",
      "##.#######",
      "###.######",
      "####.#####",
      "#####.####",
      "######.###",
      "#######.##",
      "########.#",
      "#########",
    };
}
