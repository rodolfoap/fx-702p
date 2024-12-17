package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public interface Command
{
  public void execute(Fx702pEmulator anEmulator);
  public void preExecute(Fx702pEmulator anEmulator);
  public void postExecute(Fx702pEmulator anEmulator);
}
