package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class TraceOffModeCommand extends AbstractCommand
{
  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.TRACE_OFF_MODE);
  }

  static public final TraceOffModeCommand TRACE_OFF_MODE_COMMAND = new TraceOffModeCommand();
}
