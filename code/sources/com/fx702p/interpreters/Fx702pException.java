package com.fx702p.interpreters;

@SuppressWarnings("serial")
public abstract class Fx702pException extends RuntimeException
{
  public Fx702pException(String aMessage)
  {
    super(aMessage);
  }

  public Fx702pException(String aMessage, Fx702pAbstractInterpreter anInterpreter)
  {
    super(aMessage + getPosition(anInterpreter));
  }

  public int getLine()
  {
    return line;
  }

  public void setLine(int aLine)
  {
    line = aLine;
  }

  protected static String getPosition(Fx702pAbstractInterpreter anInterpreter)
  {
    if (anInterpreter == null || !(anInterpreter instanceof Fx702pBasicInterpreter))
    {
      return "";
    }
    else
    {
      Fx702pBasicInterpreter basicInterpreter = (Fx702pBasicInterpreter)anInterpreter;
      return " IN P" + basicInterpreter.getActiveProgramIndex() + "-" + basicInterpreter.getCurrentLine();
    }
  }

  protected int line = -1;
}
