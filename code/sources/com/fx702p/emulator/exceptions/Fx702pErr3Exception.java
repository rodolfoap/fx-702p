package com.fx702p.emulator.exceptions;

import com.fx702p.interpreters.*;

@SuppressWarnings("serial")
public class Fx702pErr3Exception extends Fx702pException
{
  public Fx702pErr3Exception()
  {
    this(null);
  }

  public Fx702pErr3Exception(Fx702pAbstractInterpreter anInterpreter)
  {
    super("ERR-3", anInterpreter);
  }
}
