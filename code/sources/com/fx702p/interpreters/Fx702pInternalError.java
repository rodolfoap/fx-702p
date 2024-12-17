package com.fx702p.interpreters;

@SuppressWarnings("serial")
public class Fx702pInternalError extends RuntimeException
{
  public Fx702pInternalError(String aMessage)
  {
    super(aMessage);
  }

  public Fx702pInternalError(String aMessage, Throwable aParentException)
  {
    super(aMessage, aParentException);
  }
}
