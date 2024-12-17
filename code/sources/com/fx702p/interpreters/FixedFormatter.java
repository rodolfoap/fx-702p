package com.fx702p.interpreters;

import java.math.BigDecimal;

import com.fx702p.interpreters.formatters.Fx702pDecimalFormat;

public class FixedFormatter extends NormalFormatter
{
  public FixedFormatter(int aPrecision)
  {
    precision = aPrecision;
  }

  protected Fx702pDecimalFormat getDecimalFormat(BigDecimal aNumber)
  {
    Fx702pDecimalFormat decimalFormat;
    if (Math.abs(aNumber.doubleValue()) >= MAX_NO_EXPONENT)
    {
      decimalFormat = new Fx702pDecimalFormat(getExponentFormat());
    }
    else
    {
      int magnitude = (int)Math.floor(Math.log10(Math.abs(aNumber.doubleValue()))) + 3;
      magnitude = Math.min(getNoExponentFormats().length - 1, Math.max(0, magnitude));
      String format = getNoExponentFormats()[magnitude];
      int dotIndex = format.indexOf('.');
      int length = Math.min(format.length(), dotIndex + precision + 1);
      decimalFormat = new Fx702pDecimalFormat(format.substring(0, length));
    }
    prepareDecimalFormat(decimalFormat);
    return decimalFormat;
  }

  @Override
  protected String[] getNoExponentFormats()
  {
    return NO_EXPONENT_FIXED_FORMATS;
  }

  protected int precision;

  static public final String[] NO_EXPONENT_FIXED_FORMATS =
    {
      "#.000000000000",
      "#.00000000000",
      "#.0000000000",
      "#.000000000",
      "#.00000000",
      "##.0000000",
      "###.000000",
      "####.00000",
      "#####.0000",
      "######.000",
      "#######.00",
      "########.0",
      "#########",
    };
}
