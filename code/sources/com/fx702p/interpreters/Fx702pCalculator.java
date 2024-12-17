package com.fx702p.interpreters;

import static com.fx702p.emulator.Fx702pConstants.DEFM_MAX;
import static com.fx702p.emulator.Fx702pConstants.PRT_OFF_MODE;
import static com.fx702p.emulator.Fx702pConstants.RUN_MODE;

import java.io.*;
import java.math.BigDecimal;

import com.fx702p.Fx702pFullParser;
import com.fx702p.emulator.*;
import com.fx702p.emulator.exceptions.*;
import com.fx702p.emulator.implementation.MultiLinePrinter;
import com.fx702p.parser.*;

public class Fx702pCalculator extends Fx702pAbstractCalculator
{
  public Fx702pCalculator(Fx702pEmulator anEmulator)
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
    showBusy(true);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(anInputBuffer.getBytes());
    Fx702pFullParser parser;
    try
    {
      parser = new Fx702pFullParser(inputStream);
      Node calculator = parser.Calculator();
      if (calculator != null)
      {
        Object value = calculator.jjtAccept(this, null);
        if (value != null)
        {
          if (value instanceof BigDecimal)
          {
            setResult((BigDecimal)value);
            setReusableResult(true);
            printResult(formatNumber((BigDecimal)value));
          }
          else if (value instanceof DMSResult)
          {
            DMSResult dmsResult = (DMSResult)value;
            setResult(dmsResult.value);
            setReusableResult(true);
            printResult(dmsResult.result);
          }
          else if (value instanceof String)
          {
            String stringValue = (String)value;
            if (stringValue != null && stringValue.trim().length() != 0)
            {
              setReusableResult(false);
              printResult((String)value);
            }
          }
          else
          {
            throw new Fx702pInternalError("Invalid expression type, class=" + value.getClass().getName());
          }
        }
      }
    }
    catch (ParseException exception)
    {
      reportError(new Fx702pErr2Exception(this));
    }
    catch (ArithmeticException exception)
    {
      reportError(new Fx702pErr3Exception(this));
    }
    catch (Fx702pException exception)
    {
      reportError(exception);
    }
    catch (UnsupportedEncodingException exception)
    {
      // We should never be here
      reportError(exception);
    }
    catch (Throwable exception)
    {
      exception.printStackTrace(System.err);
    }
    finally
    {
      showBusy(false);
    }
  }
  @Override
  protected void printResult(String aStringResult)
  {
    setCursorPosition(0);
    super.printResult(aStringResult);
  }

  public Object visit(ASTMode aNode, Object aData)
  {
    int mode = toBigDecimal(aNode.jjtGetChild(0).jjtAccept(this, aData)).intValue();
    if (mode >= RUN_MODE && mode <= PRT_OFF_MODE)
    {
      setMode(mode);
    }
    else
    {
      throw new Fx702pErr5Exception(this);
    }
    return null;
  }

  @Override
  public void stat(String anInputBuffer)
  {
    StatisticsArguments arguments = getStatisticsArguments(anInputBuffer);
    if (arguments != null)
    {
      printResult(stat(arguments.x, arguments.y));
    }
  }

  @Override
  public void del(String anInputBuffer)
  {
    StatisticsArguments arguments = getStatisticsArguments(anInputBuffer);
    if (arguments != null)
    {
      printResult(del(arguments.x, arguments.y));
    }
  }

  protected StatisticsArguments getStatisticsArguments(String anInputBuffer)
  {
    if (anInputBuffer != null && anInputBuffer.length() != 0)
    {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(anInputBuffer.getBytes());
      Fx702pFullParser parser;
      try
      {
        parser = new Fx702pFullParser(inputStream);
        Node arguments = parser.CommandArguments();
        if (arguments != null)
        {
          return getStatisticsArgument(arguments);
        }
      }
      catch (ParseException exception)
      {
        reportError(new Fx702pErr2Exception(this));
      }
      catch (UnsupportedEncodingException exception)
      {
        // We should never be here
        reportError(exception);
      }
    }
    return null;
  }

  @Override
  public void astat()
  {
    printMultiLines(new AStatPrinter());
  }

  @Override
  public void sac()
  {
    super.sac();
    printResult(null);
  }

  @Override
  protected String stat(BigDecimal x, BigDecimal y)
  {
    super.stat(x, y);
    return buildStatResult("STAT", x, y);
  }

  @Override
  protected String del(BigDecimal x, BigDecimal y)
  {
    super.del(x, y);
    return buildStatResult("DEL", x, y);
  }

  protected String buildStatResult(String aText, BigDecimal x, BigDecimal y)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(aText);
    builder.append(Fx702pConstants.NORMAL_FORMATTER.format(x));
    if (getMemory().getLastY() != null)
    {
      builder.append(',');
      builder.append(Fx702pConstants.NORMAL_FORMATTER.format(y));
    }
    return builder.toString();
  }

  public Object visit(ASTDefm aNode, Object aData)
  {
    int defm = toBigDecimal(aNode.jjtGetChild(0).jjtAccept(this, aData)).intValue();
    if (defm < 0 || defm > DEFM_MAX)
    {
      throw new Fx702pErr5Exception();
    }
    setDefm(defm);
    return null;
  }

  public Object visit(ASTPassword aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTRom aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTRun aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTList aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTLine aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGoto aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGotoProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGsb aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGsbProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTReturn aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTPrint aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTPrintFormat aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTPrtExpression aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTCsr aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTComma aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTSemicolon aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTInput aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTInputPrompt aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTInputVariable aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTWait aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTLoad aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTSave aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTVerify aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGet aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTPut aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTIf aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTEqual aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTNotEqual aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGreater aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTLesser aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTGreaterOrEqual aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTLesserOrEqual aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTFor aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTNext aNode, Object aData)
  {
    return null;
  }

  @Override
  public Object visit(ASTStat aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  @Override
  public Object visit(ASTDel aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  @Override
  public Object visit(ASTSac aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTComment aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  protected class AStatPrinter implements MultiLinePrinter
  {
    public String getLine()
    {
      switch (lineNumber)
      {
        case 0:
        {
          return "*** STAT LIST";
        }
        case 1:
        {
          return "CNT=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getStatCounter());
        }
        case 2:
        {
          return "SX=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getSumX());
        }
        case 3:
        {
          return "SY=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getSumY());
        }
        case 4:
        {
          return "SX2=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getSumX2());
        }
        case 5:
        {
          return "SY2=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getSumY2());
        }
        case 6:
        {
          return "SXY=" + Fx702pConstants.NORMAL_FORMATTER.format(getMemory().getSumXY());
        }
      }
      return null;
    }

    public int getTimeToWait()
    {
      if (lineNumber == 0)
      {
        return FIRST_WAIT;
      }
      else
      {
        return NORMAL_WAIT;
      }
    }

    public void next()
    {
      lineNumber++;
    }

    protected int lineNumber = 0;

    static public final int NORMAL_WAIT = 35;
    static public final int FIRST_WAIT = 43;
  }
}
