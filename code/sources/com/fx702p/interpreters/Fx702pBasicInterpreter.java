package com.fx702p.interpreters;

import static com.fx702p.emulator.Fx702pConstants.DEG_MODE;
import static com.fx702p.emulator.Fx702pConstants.LINE_NUMBER_MAX;
import static com.fx702p.emulator.Fx702pConstants.PROGRAM_COUNT;
import static com.fx702p.emulator.Fx702pConstants.PRT_CSR_MAX;
import static com.fx702p.emulator.Fx702pConstants.PRT_OFF_MODE;
import static com.fx702p.emulator.Fx702pConstants.WAIT_MAX;

import java.io.*;
import java.math.BigDecimal;

import com.fx702p.Fx702pFullParser;
import com.fx702p.emulator.*;
import com.fx702p.emulator.exceptions.*;
import com.fx702p.parser.*;

public class Fx702pBasicInterpreter extends Fx702pAbstractCalculator
{
  public Fx702pBasicInterpreter(Fx702pEmulator anEmulator)
  {
    super(anEmulator);
  }

  public void setExtraVisitor(ExtraVisitor anExtraVisitor)
  {
    extraVisitor = anExtraVisitor;
  }

  @Override
  public void afterError()
  {
    if (inputError)
    {
      // TODO
    }
    else
    {
      end();
    }
  }

  @Override
  public void setRunMode()
  {
    end();
  }

  @Override
  public void execute(String anInputBuffer)
  {
    if (anInputBuffer == null || anInputBuffer.length() == 0)
    {
      continuation = inputStoppedContinuation;
      inputPrompt = "";
      stopProgramOnInput();
    }
    else
    {
      try
      {
        if (inputVariable.isStringVariable())
        {
          inputVariable.setValue(anInputBuffer);
        }
        else
        {
          ByteArrayInputStream inputStream = new ByteArrayInputStream(anInputBuffer.getBytes());
          Fx702pFullParser inputParser;

          inputParser = new Fx702pFullParser(inputStream);
          Node expression = inputParser.Expression();
          Object value = expression.jjtAccept(this, null);
          inputVariable.setValue(value);
        }
        contProgramOnInput();
      }
      catch (ParseException exception)
      {
        reportInputError(new Fx702pErr2Exception());
      }
      catch (Fx702pException exception)
      {
        // We should never be here
        reportInputError(exception);
      }
      catch (UnsupportedEncodingException exception)
      {
        // We should never be here
        reportInputError(exception);
      }
    }
  }

  protected void reportInputError(Throwable anException)
  {
    inputError = true;
    super.reportError(anException);
  }

  @Override
  protected void reportError(Throwable anException)
  {
    inputError = false;
    super.reportError(anException);
  }

  public void end()
  {
    programRunning = false;
    continuation = null;
    if (extraVisitor != null)
    {
      extraVisitor.visitProgramEnd();
    }
  }

  public void changeActiveProgramIndex(int aProgramIndex)
  {
    getMemory().setActiveProgramIndex(aProgramIndex);
  }

  public int getActiveProgramIndex()
  {
    return getMemory().getActiveProgramIndex();
  }

  public int getCurrentLine()
  {
    return getMemory().getActiveProgram().getParsedProgram().getLine(currentLineIndex).getLine();
  }

  @Override
  public void runProgram()
  {
    programRunning = true;

    ASTProgram program = getMemory().getActiveProgram().getParsedProgram();
    if (program != null)
    {
      prepareRunningEnvironment(program);
      continuation = (Continuation)visit(program, null);
    }
    else
    {
      continuation = new End();
    }
  }

  public Fx702pEmulator.ProgramStatus continueExecution()
  {
    if (programRunning)
    {
      BasicInstructionIndex basicInstructionIndex = continuation.getBasicInstructionIndex();
      if (!suspendedOnBreakpoint && basicInstructionIndex != null && getMemory().getActiveProgram().containsBreakpoint(basicInstructionIndex))
      {
        suspendProgramOnBreakpoint();
        suspendedOnBreakpoint = true;
        if (extraVisitor != null)
        {
          extraVisitor.visitBreakpoint(basicInstructionIndex);
        }
        return Fx702pEmulator.ProgramStatus.SUSPENDED;
      }
      else
      {
        suspendedOnBreakpoint = false;
        continuation.callContinuation(this);
      }
    }
    return programRunning ? Fx702pEmulator.ProgramStatus.RUNNING : Fx702pEmulator.ProgramStatus.ENDED;
  }

  public boolean isContinuationDebuggable()
  {
    return continuation != null && !(continuation instanceof PrintWaitInstruction) && !suspendedOnBreakpoint;
  }

  protected int getLineIndexByLineNumber(int aLineNumber)
  {
    return getMemory().getActiveProgram().getParsedProgram().getLineIndexByLineNumber(aLineNumber);
  }

  protected void executeInstruction(BasicInstructionIndex aBasicInstructionIndex)
  {
    try
    {
      ASTLine line = getMemory().getActiveProgram().getParsedProgram().getLine(aBasicInstructionIndex.getLineIndex());
      continuation = (Continuation)line.jjtAccept(this, aBasicInstructionIndex.getInstructionIndex());
      if (continuation == null)
      {
        continuation = nextInstruction();
      }
    }
    catch (ArithmeticException exception)
    {
      reportError(new Fx702pErr3Exception(this));
    }
    catch (Fx702pException exception)
    {
      reportError(exception);
    }
  }

  protected void executeSubInstruction(BasicInstructionIndex aBasicInstructionIndex, int aPrintInstructionIndex)
  {
    try
    {
      currentLineIndex = aBasicInstructionIndex.getLineIndex();
      currentInstructionIndex = aBasicInstructionIndex.getInstructionIndex();
      ASTLine line = getMemory().getActiveProgram().getParsedProgram().getLine(currentLineIndex);
      lastInstructionIndex = line.jjtGetNumChildren();

      continuation = (Continuation)line.jjtGetChild(currentInstructionIndex).jjtAccept(this, aPrintInstructionIndex);

      // Non-branching instructions returns null. We cannot send it back to the
      // main loop as null means end of program
      // So we generate a branch to the next instruction in the line or to the
      // next line
      // Of course, the last instruction of the last line generates an End.
      if (continuation == null)
      {
        continuation = nextInstruction();
      }
    }
    catch (ArithmeticException exception)
    {
      reportError(new Fx702pErr3Exception(this));
    }
    catch (Fx702pException exception)
    {
      reportError(exception);
    }
  }

  protected void prepareRunningEnvironment(ASTProgram aProgram)
  {
    gsbStackIndex = 0;
    forLoopStackIndex = 0;
    lastLineIndex = aProgram.jjtGetNumChildren();
  }

  @Override
  protected String formatNumber(BigDecimal aNumber)
  {
    if (printFormatter != null)
    {
      printFormatter.setRoundingMode(getMemory().getRoundingMode());
      return printFormatter.format(aNumber);
    }
    else
    {
      return super.formatNumber(aNumber);
    }
  }

  public void printWait()
  {
    printWait(prtWait);
  }

  public void setContinuation(Continuation aContinuation)
  {
    continuation = aContinuation;
  }

  public Continuation nextInstruction()
  {
    int nextInstructionIndex = currentInstructionIndex + 1;
    int nextLineIndex = currentLineIndex;
    if (nextInstructionIndex == lastInstructionIndex)
    {
      nextInstructionIndex = 0;
      nextLineIndex++;
      if (nextLineIndex == lastLineIndex)
      {
        return new End();
      }
    }
    return new NextInstruction(nextLineIndex, nextInstructionIndex);
  }

  public Continuation nextProgramInstruction()
  {
    Continuation continuation = nextInstruction();
    if (continuation instanceof End)
    {
      return continuation;
    }
    else if (continuation instanceof NextInstruction)
    {
      return new NextProgramInstruction(getActiveProgramIndex(), ((NextInstruction)continuation).getBasicIntructionIndex());
    }
    else
    {
      // We should never be here except if the code of nextInstruction has
      // changed
      // and nextProgramInstruction has not been adapted.
      throw new RuntimeException("Internal Error, unknown Command in nextProgramInstruction");
    }
  }

  public Continuation nextSubInstruction(Node aNode, int aPrintInstructionIndex)
  {
    Node line = aNode.jjtGetParent();
    while (line != null && !(line instanceof ASTLine))
    {
      line = line.jjtGetParent();
    }
    if (line == null)
    {
      throw new RuntimeException("Internal Error, unable to find the line for this instruction");
    }
    int lineIndex = getLineIndexByLineNumber(((ASTLine)line).getLine());
    int instructionIndex = 0;
    while (aNode.jjtGetParent().jjtGetChild(instructionIndex) != aNode)
    {
      instructionIndex++;
    }
    return new NextSubInstruction(lineIndex, instructionIndex, aPrintInstructionIndex);
  }

  public Continuation skipToNextLine()
  {
    int nextLineIndex = currentLineIndex;
    nextLineIndex++;
    if (nextLineIndex == lastLineIndex)
    {
      return new End();
    }
    return new NextInstruction(nextLineIndex, 0);
  }

  public Continuation gotoLine(int aLine)
  {
    Integer nextLineIndex = getLineIndexByLineNumber(aLine);
    if (nextLineIndex == null)
    {
      throw new Fx702pErr4Exception(this);
    }
    else
    {
      return new NextInstruction(nextLineIndex, 0);
    }
  }

  protected void extraVisitorHook(Node aNode, Object aData)
  {
    if (extraVisitor != null)
    {
      aNode.jjtAccept(extraVisitor, aData);
    }
  }

  @Override
  public Object visit(ASTDeg aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTRun aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTList aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  @Override
  public Object visit(ASTClear aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTDefm aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTPassword aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTRom aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTSave aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTVerify aNode, Object aData)
  {
    throw new Fx702pErr2Exception(this);
  }

  public Object visit(ASTLoad aNode, Object aData)
  {
    // TODO
    return null;
  }

  public Object visit(ASTGet aNode, Object aData)
  {
    // TODO
    return null;
  }

  public Object visit(ASTPut aNode, Object aData)
  {
    // TODO
    return null;
  }

  public Object visit(ASTProgram aNode, Object aData)
  {
    return new FirstInstruction();
  }

  public Object visit(ASTLine aNode, Object aStartingInstructionIndex)
  {
    extraVisitorHook(aNode, aStartingInstructionIndex);

    currentLineIndex = getLineIndexByLineNumber(aNode.getLine());
    currentInstructionIndex = (Integer)aStartingInstructionIndex;
    lastInstructionIndex = aNode.jjtGetNumChildren();
    Node instruction = aNode.jjtGetChild((Integer)aStartingInstructionIndex);
    Continuation continuation = (Continuation)instruction.jjtAccept(this, null);

    // Non-branching instructions returns null. We cannot send it back to the
    // main loop as null means end of program
    // So we generate a branch to the next instruction in the line or to the
    // next line
    // Of course, the last instruction of the last line generates an End.
    if (continuation == null)
    {
      return nextInstruction();
    }
    else
    {
      return continuation;
    }
  }

  protected int convertGotoOrGsbArgument(Node aNode, Object aData)
  {
    BigDecimal bigDecimal = toBigDecimal(aNode.jjtAccept(this, aData));
    if (bigDecimal.signum() < 0)
    {
      throw new Fx702pErr5Exception();
    }
    int value = bigDecimal.intValue();
    if (value >= LINE_NUMBER_MAX)
    {
      throw new Fx702pErr5Exception();
    }
    return value;
  }

  protected void checkProgramNumber(int anIndex)
  {
    if (anIndex >= PROGRAM_COUNT)
    {
      throw new Fx702pErr2Exception();
    }
  }

  public Object visit(ASTGoto aNode, Object aData)
  {
    return gotoLine(convertGotoOrGsbArgument(aNode.jjtGetChild(0), null));
  }

  public Object visit(ASTGotoProgram aNode, Object aData)
  {
    int index = convertGotoOrGsbArgument(aNode, aData);
    checkProgramNumber(index);
    return new OtherProgramStart(index);
  }

  public Object visit(ASTGsb aNode, Object aData)
  {
    if (gsbStackIndex >= GSB_STACK_SIZE)
    {
      throw new Fx702pErr7Exception(this);
    }

    gsbStack[gsbStackIndex] = nextInstruction();
    gsbStackIndex++;

    return gotoLine(convertGotoOrGsbArgument(aNode.jjtGetChild(0), null));
  }

  public Object visit(ASTGsbProgram aNode, Object aData)
  {
    if (gsbStackIndex >= GSB_STACK_SIZE)
    {
      throw new Fx702pErr7Exception(this);
    }

    gsbStack[gsbStackIndex] = nextProgramInstruction();
    gsbStackIndex++;

    int index = convertGotoOrGsbArgument(aNode, aData);
    checkProgramNumber(index);
    changeActiveProgramIndex(index);
    return new FirstInstruction();
  }

  public Object visit(ASTReturn aNode, Object aData)
  {
    gsbStackIndex--;
    if (gsbStackIndex < 0)
    {
      throw new Fx702pErr7Exception(this);
    }
    return gsbStack[gsbStackIndex];
  }

  public Object visit(ASTMode aNode, Object aData)
  {
    int mode = toBigDecimal(aNode.jjtGetChild(0).jjtAccept(this, aData)).intValue();
    if (mode >= DEG_MODE && mode <= PRT_OFF_MODE)
    {
      setMode(mode);
    }
    else
    {
      throw new Fx702pErr5Exception(this);
    }
    return null;
  }

  public Object visit(ASTIf aNode, Object aData)
  {
    Node comparison = aNode.jjtGetChild(0);
    Object expressionResult = comparison.jjtAccept(this, aData);
    if (!(expressionResult instanceof Boolean))
    {
      throw new Fx702pErr2Exception();
    }
    if ((Boolean)expressionResult)
    {
      if (aNode.jjtGetNumChildren() == 2)
      {
        if (aNode.jjtGetChild(1) instanceof ASTGoto)
        {
          return visit((ASTGoto)aNode.jjtGetChild(1), aData);
        }
        else if (aNode.jjtGetChild(1) instanceof ASTGotoProgram)
        {
          return visit((ASTGotoProgram)aNode.jjtGetChild(1), aData);
        }
        else
        {
          return nextInstruction();
        }
      }
      else
      {
        return nextInstruction();
      }
    }
    else
    {
      return skipToNextLine();
    }
  }

  @SuppressWarnings("rawtypes")
  protected boolean compare(Node aNode, Comparator aComparator)
  {
    Object a = aNode.jjtGetChild(0).jjtAccept(this, null);
    Object b = aNode.jjtGetChild(1).jjtAccept(this, null);
    if (a.getClass().equals(b.getClass()))
    {
      return aComparator.compare((Comparable)a, (Comparable)b);
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
  }

  public Object visit(ASTEqual aNode, Object aData)
  {
    return compare(aNode, COMPARE_EQUAL);
  }

  public Object visit(ASTNotEqual aNode, Object aData)
  {
    return !compare(aNode, COMPARE_EQUAL);
  }

  public Object visit(ASTGreater aNode, Object aData)
  {
    return compare(aNode, COMPARE_GREATER);
  }

  public Object visit(ASTLesser aNode, Object aData)
  {
    return !compare(aNode, COMPARE_GREATER_EQUAL);
  }

  public Object visit(ASTGreaterOrEqual aNode, Object aData)
  {
    return compare(aNode, COMPARE_GREATER_EQUAL);
  }

  public Object visit(ASTLesserOrEqual aNode, Object aData)
  {
    return !compare(aNode, COMPARE_GREATER);
  }

  public Object visit(ASTFor aNode, Object aData)
  {
    if (forLoopStackIndex >= FOR_LOOP_STACK_SIZE)
    {
      throw new Fx702pErr7Exception(this);
    }

    Variable variable = getVariable(aNode.jjtGetChild(0));
    if (!variable.isLoopVariable())
    {
      throw new Fx702pErr2Exception();
    }

    Object start = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (!(start instanceof BigDecimal))
    {
      throw new Fx702pErr2Exception();
    }

    Object end = aNode.jjtGetChild(2).jjtAccept(this, aData);
    if (!(end instanceof BigDecimal))
    {
      throw new Fx702pErr2Exception();
    }

    Object step;
    if (aNode.jjtGetNumChildren() == 4)
    {
      step = aNode.jjtGetChild(3).jjtAccept(this, aData);
      if (!(step instanceof BigDecimal))
      {
        throw new Fx702pErr2Exception();
      }
    }
    else
    {
      step = BigDecimal.ONE;
    }

    ForLoop forLoop = new ForLoop(variable, (BigDecimal)start, (BigDecimal)end, (BigDecimal)step, nextInstruction());

    forLoopStack[forLoopStackIndex] = forLoop;
    forLoopStackIndex++;

    return forLoop;
  }

  public Object visit(ASTNext aNode, Object aData)
  {
    Variable variable = getVariable(aNode.jjtGetChild(0));
    for (;;)
    {
      if (forLoopStackIndex == 0)
      {
        throw new Fx702pErr7Exception(this);
      }

      if (forLoopStack[forLoopStackIndex - 1].getVariable().equals(variable))
      {
        break;
      }
      else
      {
        forLoopStackIndex--;
      }
    }

    ForLoop forLoop = forLoopStack[forLoopStackIndex - 1];
    forLoop.incrementVariable();
    if (forLoop.isLoopOver())
    {
      invokeEndLoop(forLoop.getVariable());
      forLoopStackIndex--;
      return nextInstruction();
    }
    else
    {
      invokeNextLoop(forLoop.getVariable());
      return forLoop;
    }
  }

  public Object visit(ASTPrint aNode, Object aData)
  {
    int startIndex;
    if (aData != null)
    {
      startIndex = (Integer)aData;
    }
    else
    {
      startIndex = 0;
    }

    printInfos.waitNeeded = true;
    if (aNode.jjtGetNumChildren() != 0)
    {
      for (int i = startIndex, last = aNode.jjtGetNumChildren(); i < last; i++)
      {
        Boolean printDone = (Boolean)aNode.jjtGetChild(i).jjtAccept(this, printInfos);
        if (printDone != null && printDone && i != last - 1)
        {
          return nextSubInstruction(aNode, i + 1);
        }
      }

      printFormatter = null;
      if (printInfos.waitNeeded)
      {
        return new PrintWaitInstruction(nextInstruction());
      }
      else
      {
        return null;
      }
    }
    else
    {
      clearDisplay();
      return null;
    }
  }

  public Object visit(ASTPrintFormat aNode, Object aData)
  {
    printFormatter = new PrintFormatter(aNode.format);

    // We do not force the return to the main command loop after a PrintFormat
    // of course,
    // We print first
    return false;
  }

  public Object visit(ASTPrtExpression aNode, Object aData)
  {
    extraVisitorHook(aNode, aData);

    ((PrintInfos)aData).waitNeeded = true;
    Object prtExpression = aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (prtExpression instanceof BigDecimal)
    {
      setResult((BigDecimal)prtExpression);
      setReusableResult(true);
      printResult(formatNumber((BigDecimal)prtExpression));
    }
    else if (prtExpression instanceof String)
    {
      setReusableResult(false);
      printResult((String)prtExpression);
    }
    else
    {
      throw new Fx702pInternalError("Invalid expression type in PRT, class=" + prtExpression.getClass().getName());
    }
    // We must return to the main command loop here after a print
    // If not, we may execute the next subInstruction, a Comma for instance
    // and send a stop whilst scrolling
    return true;
  }

  public Object visit(ASTCsr aNode, Object aData)
  {
    extraVisitorHook(aNode, aData);

    Object csr = aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (csr instanceof BigDecimal)
    {
      int prtCsr = ((BigDecimal)csr).intValue();
      if (prtCsr < 0 || prtCsr >= PRT_CSR_MAX)
      {
        throw new Fx702pErr5Exception(this);
      }
      setCursorPosition(prtCsr);
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
    // Same comment as for visit(ASTPrtExpression)
    return false;
  }

  public Object visit(ASTComma aNode, Object aData)
  {
    printFormatter = null;
    printWait();
    // Here, we return to the main command loop to allow the print command
    // before the comma and the @Wait command to be executed
    return true;
  }

  public Object visit(ASTSemicolon aNode, Object aData)
  {
    // Here, we return to the main command loop to allow the print command
    // before the semicolon to be executed
    ((PrintInfos)aData).waitNeeded = false;
    return true;
  }

  public Object visit(ASTWait aNode, Object aData)
  {
    checkCommandArguments(1, aData);
    Object wait = ((Node)aData).jjtGetChild(0).jjtAccept(this, null);
    if (wait instanceof BigDecimal)
    {
      prtWait = ((BigDecimal)wait).intValue();
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
    return null;
  }

  public Object visit(ASTInput aNode, Object aData)
  {
    int startIndex;
    if (aData != null)
    {
      startIndex = (Integer)aData;
    }
    else
    {
      startIndex = 0;
      inputPrompt = "";
    }

    for (int i = startIndex, last = aNode.jjtGetNumChildren(); i < last; i++)
    {
      inputStoppedContinuation = nextSubInstruction(aNode, i);
      Boolean inputDone = (Boolean)aNode.jjtGetChild(i).jjtAccept(this, null);
      if (inputDone != null && inputDone && i != last - 1)
      {
        return nextSubInstruction(aNode, i + 1);
      }
    }

    return null;
  }

  public Object visit(ASTInputPrompt aNode, Object aData)
  {
    inputPrompt = ((ASTString)aNode.jjtGetChild(0)).value;
    return false;
  }

  public Object visit(ASTInputVariable aNode, Object aData)
  {
    extraVisitorHook(aNode, aData);

    inputVariable = getVariable(aNode.jjtGetChild(0));
    startInput(inputPrompt);

    return true;
  }

  @Override
  public Object visit(ASTDMS aNode, Object aData)
  {
    DMSResult dmsResult = (DMSResult)super.visit(aNode, aData);
    setResult(dmsResult.value);
    setReusableResult(true);
    printResult(dmsResult.result);
    return new PrintWaitInstruction(nextInstruction());
  }

  public Object visit(ASTComment aNode, Object aData)
  {
    return null;
  }

  static public class PrintInfos
  {
    public boolean waitNeeded = false;
  }

  protected interface Comparator
  {
    @SuppressWarnings("rawtypes")
    public boolean compare(Comparable a, Comparable b);
  }

  static protected final Comparator COMPARE_EQUAL = new Comparator()
    {
      @SuppressWarnings(
        {
          "unchecked",
          "rawtypes"
        })
      public boolean compare(Comparable a, Comparable b)
      {
        return a.compareTo(b) == 0;
      }
    };

  static protected final Comparator COMPARE_GREATER = new Comparator()
    {
      @SuppressWarnings(
        {
          "unchecked",
          "rawtypes"
        })
      public boolean compare(Comparable a, Comparable b)
      {
        return a.compareTo(b) > 0;
      }
    };

  static protected final Comparator COMPARE_GREATER_EQUAL = new Comparator()
    {
      @SuppressWarnings(
        {
          "unchecked",
          "rawtypes"
        })
      public boolean compare(Comparable a, Comparable b)
      {
        return a.compareTo(b) >= 0;
      }
    };

  protected boolean programRunning = false;
  protected boolean suspendedOnBreakpoint = false;
  protected Continuation continuation = null;

  protected Continuation gsbStack[] = new Continuation[GSB_STACK_SIZE];
  protected int gsbStackIndex = 0;

  protected ForLoop forLoopStack[] = new ForLoop[FOR_LOOP_STACK_SIZE];
  protected int forLoopStackIndex = 0;

  protected int currentLineIndex = 0;
  protected int lastLineIndex;
  protected int currentInstructionIndex = 0;
  protected int lastInstructionIndex;

  protected PrintInfos printInfos = new PrintInfos();
  protected int prtWait = WAIT_MAX;
  protected Fx702pFormatter printFormatter = null;

  protected String inputPrompt = null;
  protected Variable inputVariable;
  protected Continuation inputStoppedContinuation;
  protected boolean inputError = false;

  protected ExtraVisitor extraVisitor = null;

  static public final int GSB_STACK_SIZE = 10;
  static public final int FOR_LOOP_STACK_SIZE = 8;
}
