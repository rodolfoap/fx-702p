package com.fx702p.interpreters;

import java.math.*;

public interface Fx702pFormatter
{
  public String format(BigDecimal aNumber);
  public void setRoundingMode(RoundingMode aRoundingMode);
}
