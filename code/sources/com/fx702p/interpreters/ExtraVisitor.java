package com.fx702p.interpreters;

import com.fx702p.parser.Fx702pParserVisitor;

public interface ExtraVisitor extends Fx702pParserVisitor
{
  public void visitProgramEnd();
  public void visitBreakpoint(BasicInstructionIndex aBasicInstructionIndex);
}
