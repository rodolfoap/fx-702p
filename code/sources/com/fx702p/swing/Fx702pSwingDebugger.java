package com.fx702p.swing;

import com.fx702p.debug.Fx702pDebugger;
import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.*;
import com.fx702p.parser.*;

public class Fx702pSwingDebugger extends Fx702pDebugger.AbstractFx702pDebugger
{
  public Fx702pSwingDebugger(Fx702pEmulator anEmulator, DebugWindow aDebugWindow)
  {
    emulator = anEmulator;
    debugWindow = aDebugWindow;
  }

  public void visitBreakpoint(BasicInstructionIndex aBasicInstructionIndex)
  {
    ASTLine line = emulator.getActiveProgram().getParsedProgram().getLine(aBasicInstructionIndex.getLineIndex());
    Fx702pBasicLine basicLine = emulator.getActiveProgram().getBasicSourceCode().getLine(line.getLineIndexInSourceCode());
    InstructionBoundary boundary = basicLine.getParsedLine().getInstructionBoundary(aBasicInstructionIndex.getInstructionIndex());
    debugWindow.selectBreakpoint(basicLine, boundary.begin, boundary.end);
  }

  @Override
  public Object visit(ASTLine aLine, Object anInstructionIndex)
  {
    Fx702pBasicLine basicLine = emulator.getActiveProgram().getBasicSourceCode().getLine(aLine.getLineIndexInSourceCode());
    InstructionBoundary boundary = aLine.getInstructionBoundary((Integer)anInstructionIndex);
    debugWindow.select(basicLine, boundary.begin, boundary.end);

    return null;
  }

  @Override
  public Object visit(ASTPrtExpression aNode, Object aData)
  {
    visitPrintSubExpression(aNode);
    return null;
  }

  @Override
  public Object visit(ASTCsr aNode, Object aData)
  {
    visitPrintSubExpression(aNode);
    return null;
  }

  protected Object visitPrintSubExpression(PrintSubExpressionNode aNode)
  {
    Fx702pBasicLine basicLine = emulator.getActiveProgram().getBasicSourceCode().getLine(aNode.getLineIndexInSourceCode());
    InstructionBoundary boundary = aNode.getSubInstructionBoundary();
    if (boundary != null)
    {
      debugWindow.subSelect(basicLine, boundary.begin, boundary.end);
    }

    return null;
  }

  @Override
  public Object visit(ASTInputVariable aNode, Object aData)
  {
    Fx702pBasicLine basicLine = emulator.getActiveProgram().getBasicSourceCode().getLine(aNode.getLineIndexInSourceCode());
    InstructionBoundary boundary = aNode.getSubInstructionBoundary();
    if (boundary != null)
    {
      debugWindow.subSelect(basicLine, boundary.begin, boundary.end);
    }

    return null;
  }

  public void visitProgramEnd()
  {
    debugWindow.clearSelection();
  }

  @Override
  public void allClear()
  {
    debugWindow.allClear();
  }

  public void home()
  {
  }

  @Override
  public void cont()
  {
    debugWindow.cont();
  }

  @Override
  public void contProgram()
  {
    debugWindow.contProgram();
  }

  @Override
  public void endProgram()
  {
    debugWindow.endProgram();
  }

  @Override
  public void endScroll()
  {
    debugWindow.endScroll();
  }

  @Override
  public void endWaitAfterPrint()
  {
    debugWindow.endWaitAfterPrint();
  }

  @Override
  public void cancelWaitAfterPrint()
  {
    debugWindow.cancelWaitAfterPrint();
  }


  @Override
  public void enterString(String aString)
  {
    debugWindow.enterString(aString);
  }

  @Override
  public void execute(String aString)
  {
    debugWindow.execute(aString);
  }

  @Override
  public void input(String aAnInputPrompt)
  {
    debugWindow.input(aAnInputPrompt);
  }

  @Override
  public void reportFx702pError(Fx702pException aAnError)
  {
    debugWindow.reportFx702pError(aAnError);
  }

  @Override
  public void resultPrinted()
  {
    debugWindow.resultPrinted();
  }

  public void startMultiLinePrint()
  {
  }

  public void endMultiLinePrint()
  {
  }

  @Override
  public void runProgram()
  {
    debugWindow.runProgram();
  }

  @Override
  public void setRunMode()
  {
    debugWindow.setRunMode();
  }

  @Override
  public void setWrtMode()
  {
    debugWindow.setWrtMode();
  }

  @Override
  public void startScroll()
  {
    debugWindow.startScroll();
  }

  public void stepInProgram()
  {
    debugWindow.stepInProgram();
  }

  public void debugAndStepActiveProgram()
  {
    debugWindow.debugAndStepActiveProgram();
  }

  public void nextLoop(Variable aVariable)
  {
    debugWindow.nextLoop(aVariable);
  }

  public void endLoop(Variable aVariable)
  {
    debugWindow.endLoop(aVariable);
  }

  @Override
  public void stop()
  {
    debugWindow.stop();
  }

  @Override
  public void stopProgram()
  {
    debugWindow.stopProgram();
  }

  public void suspendProgram()
  {
    debugWindow.suspendProgram();
  }

  public void resumeProgram()
  {
    debugWindow.resumeProgram();
  }

  public void loadProgram(int aProgramIndex)
  {
    debugWindow.loadProgram(aProgramIndex);
  }

  public void clearProgram(int aProgramIndex)
  {
    debugWindow.clearProgram(aProgramIndex);
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
    debugWindow.setActiveProgramIndex(aProgramIndex);
  }

  @Override
  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    debugWindow.waitAfterPrint(aPrintWait, null);
  }

  protected Fx702pEmulator emulator;
  protected DebugWindow debugWindow;
}
