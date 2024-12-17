package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr7Exception extends Fx702pException
{
  public Fx702pErr7Exception()
  {
    this(null);
  }

  public Fx702pErr7Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-7", anInterpreter);
  }
}
