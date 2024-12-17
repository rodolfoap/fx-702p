package com.fx702p.interpreters;

import java.math.BigDecimal;

import com.fx702p.interpreters.formatters.Fx702pDecimalFormat;

public class PrintFormatter extends NormalFormatter
{
  public PrintFormatter(String aFormat)
  {
    format = aFormat;
    if (format.endsWith("^"))
    {
      StringBuilder builder = new StringBuilder();
      builder.append("0");
      int index = format.indexOf('.');
      if (index > 0)
      {
        builder.append('.');
        for (int i = 0, last = format.length() - index - 2; i < last; i++)
        {
          builder.append('0');
        }
      }
      builder.append("E00");
      decimalFormat = new Fx702pDecimalFormat(builder.toString());
      scientificFormat = true;
    }
    else
    {
      decimalFormat = new Fx702pDecimalFormat(format.replace('#', '0'));
      int index = format.indexOf('.');
      maxNumber = Math.pow(10, index > 0 ? index : format.length());
      scientificFormat = false;
    }
  }

  @Override
  public String format(BigDecimal aNumber)
  {
    Fx702pDecimalFormat decimalFormat = getDecimalFormat(aNumber);
    if (scientificFormat)
    {
      return decimalFormat.format(aNumber);
    }
    else
    {
      if (aNumber.doubleValue() >= maxNumber)
      {
        return format;
      }
      else
      {
        char[] result = decimalFormat.format(aNumber).toCharArray();
        for (int i = 0; i < result.length - 1; i++)
        {
          if (result[i] == '0' && result[i + 1] != '.')
          {
            result[i] = ' ';
          }
          else
          {
            break;
          }
        }
        return new String(result);
      }
    }
  }

  protected Fx702pDecimalFormat getDecimalFormat(BigDecimal aNumber)
  {
    prepareDecimalFormat(decimalFormat);
    return decimalFormat;
  }

  protected String format;
  protected Fx702pDecimalFormat decimalFormat;
  protected boolean scientificFormat;
  protected double maxNumber;
}
