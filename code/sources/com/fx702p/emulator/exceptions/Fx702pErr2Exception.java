package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr2Exception extends Fx702pException
{
  public Fx702pErr2Exception()
  {
    this(null);
  }

  public Fx702pErr2Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-2", anInterpreter);
  }
}
