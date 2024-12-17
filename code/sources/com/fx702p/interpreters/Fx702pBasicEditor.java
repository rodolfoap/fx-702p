package com.fx702p.interpreters;

import com.fx702p.emulator.Fx702pEmulator;
import com.fx702p.parser.ASTClear;

public class Fx702pBasicEditor extends Fx702pAbstractInterpreter
{
  public Fx702pBasicEditor(Fx702pEmulator anEmulator)
  {
    super(anEmulator);
  }

  @Override
  public void afterError()
  {
  }

  @Override
  public void execute(String anInputBuffer)
  {
  }

  public Object visit(ASTClear aNode, Object aData)
  {
    if (aNode.all)
    {
      getMemory().clearAllPrograms();
    }
    else
    {
      getMemory().getActiveProgram().clear();
    }
    return null;
  }

}
