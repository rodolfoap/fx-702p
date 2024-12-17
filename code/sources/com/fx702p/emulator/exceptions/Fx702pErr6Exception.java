package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr6Exception extends Fx702pException
{
  public Fx702pErr6Exception()
  {
    this(null);
  }

  public Fx702pErr6Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-6", anInterpreter);
  }
}
