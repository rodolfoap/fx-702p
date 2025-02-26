/* Generated By:JJTree: Do not edit this line. ASTLine.java */
/*EDITED*/

package com.fx702p.parser;

import java.util.*;

import com.fx702p.emulator.*;


public class ASTLine extends SimpleNode
{
  public ASTLine(int id)
  {
    super(id);
  }

  public ASTLine(Fx702pParser p, int id)
  {
    super(p, id);
  }


  /** Accept the visitor. **/
  @Override
  public Object jjtAccept(Fx702pParserVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }

  public int getLine()
  {
    return line;
  }

  public int getLineIndexInSourceCode()
  {
    return lineIndexInSourceCode;
  }

  public void setLineIndexInSourceCode(int aLineIndexInSourceCode)
  {
    lineIndexInSourceCode = aLineIndexInSourceCode;
  }

  public int getLineIndexInProgram()
  {
    return lineIndexInProgram;
  }

  public void setLineIndexInProgram(int aLineIndexInProgram)
  {
    lineIndexInProgram = aLineIndexInProgram;
  }

  public InstructionBoundary getInstructionBoundary(int anInstructionIndex)
  {
    return instructionsBoundaries.get(anInstructionIndex);
  }

  public List<InstructionBoundary> getInstructionBoundaries()
  {
    return instructionsBoundaries;
  }

  public void addLine(int aLine, int aColumnEnd, int aLineIndexInSourceCode)
  {
    line = aLine;
    addInstructionPosition(aColumnEnd);
    lineIndexInSourceCode = aLineIndexInSourceCode;
  }

  public void addInstructionPosition(int aPosition)
  {
    instructionsBoundaries.add(new InstructionBoundary(aPosition));
  }

  public void visitAndResynchronize(int anIndex, Fx702pParserVisitor aVisitor, LineInfos aLineInfos)
  {
    InstructionBoundary instructionBoundary = instructionsBoundaries.get(anIndex);
    instructionBoundary.begin -= aLineInfos.columnDelta;
    jjtGetChild(anIndex).jjtAccept(aVisitor, aLineInfos);
  }

  public void resynchronizePositions(Fx702pBasicSourceCode aBasicSourceCode)
  {
    InstructionBoundary.resynchronizeLinePositions(aBasicSourceCode, lineIndexInSourceCode, instructionsBoundaries);
  }

  static public class LineInfos
  {
    public int columnDelta = 0;
    public HashMap<Node, InstructionBoundary> boundariesToFix = new HashMap<Node, InstructionBoundary>();
  }

  private int line;
  private int lineIndexInSourceCode;
  private int lineIndexInProgram;

  private Vector<InstructionBoundary> instructionsBoundaries = new Vector<InstructionBoundary>();
}
