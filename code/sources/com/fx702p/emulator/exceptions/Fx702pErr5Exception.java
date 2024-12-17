package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr5Exception extends Fx702pException
{
  public Fx702pErr5Exception()
  {
    this(null);
  }

  public Fx702pErr5Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-5", anInterpreter);
  }
}
