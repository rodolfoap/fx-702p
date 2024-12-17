package com.fx702p.interpreters;

import java.math.BigDecimal;

import com.fx702p.interpreters.formatters.Fx702pDecimalFormat;

public class ScientificFormatter extends NormalFormatter
{
  public ScientificFormatter(int aPrecision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("0.");
    for (int i = 0; i < aPrecision - 1; i++)
    {
      builder.append('0');
    }
    builder.append("E00");
    exponentFormat = builder.toString();
  }

  protected Fx702pDecimalFormat getDecimalFormat(BigDecimal aNumber)
  {
    Fx702pDecimalFormat decimalFormat = new Fx702pDecimalFormat(getExponentFormat());
    prepareDecimalFormat(decimalFormat);
    return decimalFormat;
  }

  @Override
  protected String getExponentFormat()
  {
    return exponentFormat;
  }

  protected String exponentFormat;
}
