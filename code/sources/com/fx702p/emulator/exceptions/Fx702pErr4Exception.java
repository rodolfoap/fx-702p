package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr4Exception extends Fx702pException
{
  public Fx702pErr4Exception()
  {
    this(null);
  }

  public Fx702pErr4Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-4", anInterpreter);
  }
}
