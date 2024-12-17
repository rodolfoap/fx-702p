package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public abstract class AbstractCommand implements Command
{
  public abstract void execute(Fx702pEmulator anEmulator);

  public void preExecute(Fx702pEmulator anEmulator)
  {
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
    anEmulator.clearPrefixes();
  }
}
