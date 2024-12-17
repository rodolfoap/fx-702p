package com.fx702p.emulator;

public class Console
{
  static public void setImplementation(Implementation anImplementation)
  {
    if (implementation == null)
    {
      implementation = anImplementation;
    }
    else
    {
      System.err.println("Console already defined");
    }
  }

  static public void reportError(String anError)
  {
    if (implementation != null)
    {
      implementation.reportError(anError);
    }
    else
    {
      System.err.println("No console defined");
    }
  }

  public interface Implementation
  {
    public void reportError(String anError);
  }

  static private Implementation implementation = null;
}
