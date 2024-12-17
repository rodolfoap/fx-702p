/*EDITED*/

package com.fx702p.parser;

import com.fx702p.emulator.InstructionBoundary;

public class PrintSubExpressionNode extends SimpleNode
{
  public PrintSubExpressionNode(int id)
  {
    super(id);
  }

  public PrintSubExpressionNode(Fx702pParser p, int id)
  {
    super(p, id);
  }

  public int getLineIndexInSourceCode()
  {
    return ((ASTPrint)parent).getLineIndexInSourceCode();
  }

  public InstructionBoundary getSubInstructionBoundary()
  {
    return ((ASTPrint)parent).getSubInstructionBoundary(this);
  }
}
