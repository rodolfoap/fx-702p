package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class TraceOnModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.TRACE_ON_MODE);
  }

  static public final TraceOnModeCommand TRACE_ON_MODE_COMMAND = new TraceOnModeCommand();
}
