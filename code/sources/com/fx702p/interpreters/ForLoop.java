package com.fx702p.interpreters;

import java.math.BigDecimal;

import com.fx702p.emulator.Variable;

public class ForLoop implements Continuation
{
  public ForLoop(Variable aVariable, BigDecimal aStart, BigDecimal anEnd, BigDecimal aStep, Continuation aContinuation)
  {
    variable = aVariable;
    start = aStart;
    end = anEnd;
    step = aStep;
    continuation = aContinuation;

    variable.setValue(start);
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    continuation.callContinuation(anInterpreter);
  }

  public Variable getVariable()
  {
    return variable;
  }

  public BigDecimal getStart()
  {
    return start;
  }

  public BigDecimal getEnd()
  {
    return end;
  }

  public BigDecimal getStep()
  {
    return step;
  }

  public void incrementVariable()
  {
    variable.setValue(((BigDecimal)variable.getValue()).add(step));
  }

  public boolean isLoopOver()
  {
    if (step.doubleValue() > 0)
    {
      return ((BigDecimal)variable.getValue()).doubleValue() > end.doubleValue();
    }
    else if (step.doubleValue() < 0)
    {
      return ((BigDecimal)variable.getValue()).doubleValue() < end.doubleValue();
    }
    else
    {
      // If step is 0, we iterate indefinetely. The Fx702p does throws an error
      // in this case.
      return false;
    }
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }

  protected Variable variable;
  protected BigDecimal start;
  protected BigDecimal end;
  protected BigDecimal step;
  protected Continuation continuation;
}
