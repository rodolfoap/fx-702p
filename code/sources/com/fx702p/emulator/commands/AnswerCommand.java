package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class AnswerCommand extends AbstractCommand
{
  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.lastAnswer();
  }

  static public final AnswerCommand ANSWER_COMMAND = new AnswerCommand();
}