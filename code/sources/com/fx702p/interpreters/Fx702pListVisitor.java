package com.fx702p.interpreters;

import java.io.PrintWriter;

import com.fx702p.parser.*;

public class Fx702pListVisitor implements Fx702pParserVisitor
{
  public Fx702pListVisitor(PrintWriter aWriter)
  {
    writer = aWriter;
  }

  protected Object visitBinaryOperator(String anOperator, SimpleNode aNode, Object aData)
  {
    aNode.jjtGetChild(0).jjtAccept(this, aData);
    writer.print(anOperator);
    aNode.jjtGetChild(1).jjtAccept(this, aData);
    return null;
  }

  public Object visit(SimpleNode aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTSavedDefm aNode, Object aData)
  {
    throw new Fx702pInternalError("Found SavedDefm node in a program");
  }

  public Object visit(ASTMode aNode, Object aData)
  {
    writer.print("MODE ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDefm aNode, Object aData)
  {
    writer.print("DEFM ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPassword aNode, Object aData)
  {
    writer.print("PASSWORD ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRom aNode, Object aData)
  {
    writer.print("ROM ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCommand aNode, Object aData)
  {
    aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (aNode.jjtGetChild(1).jjtGetNumChildren() > 0)
    {
      writer.print(" ");
      aNode.jjtGetChild(1).jjtAccept(this, aData);
    }
    return null;
  }

  public Object visit(ASTRun aNode, Object aData)
  {
    writer.print("RUN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTList aNode, Object aData)
  {
    writer.print("LIST");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTClear aNode, Object aData)
  {
    writer.print("CLR");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTProgramsFile aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTMultiplePrograms aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTSingleProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTVariablesAndProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTProgramSignature aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTBreakpoint aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTWatch aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTWatchpoint aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTStatWatchpoint aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTComment aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTLine aNode, Object aData)
  {
    String line = aNode.getLine() + "";
    while (line.length() < 4)
    {
      line = " " + line;
    }
    writer.print(line);
    writer.print(" ");
    Object lastResult = null;
    for (int i = 0; i < aNode.jjtGetNumChildren(); i++)
    {
      if (i != 0 && lastResult == null)
      {
        writer.print(":");
      }
      lastResult = aNode.jjtGetChild(i).jjtAccept(this, aData);
    }
    writer.println();
    return null;
  }

  public Object visit(ASTClosedSubExpression aNode, Object aData)
  {
    writer.print("(");
    aNode.childrenAccept(this, aData);
    writer.print(")");
    return null;
  }

  public Object visit(ASTOpenSubExpression aNode, Object aData)
  {
    writer.print("(");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTAssignment aNode, Object aData)
  {
    return visitBinaryOperator("=", aNode, aData);
  }

  public Object visit(ASTAddition aNode, Object aData)
  {
    return visitBinaryOperator("+", aNode, aData);
  }

  public Object visit(ASTSubstraction aNode, Object aData)
  {
    return visitBinaryOperator("-", aNode, aData);
  }

  public Object visit(ASTMultiplication aNode, Object aData)
  {
    return visitBinaryOperator("*", aNode, aData);
  }

  public Object visit(ASTDivision aNode, Object aData)
  {
    return visitBinaryOperator("/", aNode, aData);
  }

  public Object visit(ASTUnaryMinus aNode, Object aData)
  {
    writer.print("-");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPower aNode, Object aData)
  {
    return visitBinaryOperator("^", aNode, aData);
  }

  public Object visit(ASTFactorial aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    writer.print("!");
    return null;
  }

  public Object visit(ASTPi aNode, Object aData)
  {
    writer.print("p");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInteger aNode, Object aData)
  {
    writer.print(aNode.image);
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFloat aNode, Object aData)
  {
    writer.print(aNode.image);
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTString aNode, Object aData)
  {
    writer.print(aNode.image);
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDollar aNode, Object aData)
  {
    writer.print("$");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVariable aNode, Object aData)
  {
    writer.print(aNode.name);
    if (aNode.isString)
    {
      writer.print("$");
    }
    if (aNode.jjtGetNumChildren() != 0)
    {
      writer.print("(");
      aNode.jjtGetChild(0).jjtAccept(this, aData);
      if (aNode.jjtGetNumChildren() > 1)
      {
        writer.print(",");
        aNode.jjtGetChild(1).jjtAccept(this, aData);
      }
      writer.print(")");
    }
    return null;
  }

  public Object visit(ASTIndexedVariable aNode, Object aData)
  {
    writer.print(aNode.name);
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFunctionCall aNode, Object aData)
  {
    aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (aNode.jjtGetChild(1).jjtGetNumChildren() > 0)
    {
      aNode.jjtGetChild(1).jjtAccept(this, aData);
    }
    return null;
  }

  protected void printCommaSeparatedChildren(Node aNode, Object aData)
  {
    for (int i = 0, last = aNode.jjtGetNumChildren(); i < last; i++)
    {
      if (i != 0)
      {
        writer.print(",");
      }
      aNode.jjtGetChild(i).jjtAccept(this, aData);
    }
  }

  public Object visit(ASTFunctionArguments aNode, Object aData)
  {
    writer.print(" ");
    printCommaSeparatedChildren(aNode, aData);
    return null;
  }

  public Object visit(ASTFunctionWithParenthesisArguments aNode, Object aData)
  {
    writer.print("(");
    printCommaSeparatedChildren(aNode, aData);
    writer.print(")");
    return null;
  }

  public Object visit(ASTCommandArguments aNode, Object aData)
  {
    printCommaSeparatedChildren(aNode, aData);
    return null;
  }

  public Object visit(ASTCommandWithParenthesisArguments aNode, Object aData)
  {
    writer.print("(");
    printCommaSeparatedChildren(aNode, aData);
    writer.print(")");
    return null;
  }

  public Object visit(ASTRandom aNode, Object aData)
  {
    writer.print("RAN#");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCnt aNode, Object aData)
  {
    writer.print("CNT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdx aNode, Object aData)
  {
    writer.print("SDX");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdy aNode, Object aData)
  {
    writer.print("SDY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdxn aNode, Object aData)
  {
    writer.print("SDXN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdyn aNode, Object aData)
  {
    writer.print("SDYN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMx aNode, Object aData)
  {
    writer.print("MX");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMy aNode, Object aData)
  {
    writer.print("MY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSx aNode, Object aData)
  {
    writer.print("MX");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSy aNode, Object aData)
  {
    writer.print("SY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSx2 aNode, Object aData)
  {
    writer.print("SX2");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSy2 aNode, Object aData)
  {
    writer.print("SY2");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSxy aNode, Object aData)
  {
    writer.print("SXY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLra aNode, Object aData)
  {
    writer.print("LRA");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLrb aNode, Object aData)
  {
    writer.print("LRB");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCor aNode, Object aData)
  {
    writer.print("COR");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTKey aNode, Object aData)
  {
    writer.print("KEY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSin aNode, Object aData)
  {
    writer.print("SIN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCos aNode, Object aData)
  {
    writer.print("COS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTTan aNode, Object aData)
  {
    writer.print("TAN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcSin aNode, Object aData)
  {
    writer.print("ASN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcCos aNode, Object aData)
  {
    writer.print("ACS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcTan aNode, Object aData)
  {
    writer.print("ATN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicSin aNode, Object aData)
  {
    writer.print("HSN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicCos aNode, Object aData)
  {
    writer.print("HCS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicTan aNode, Object aData)
  {
    writer.print("HTN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicSin aNode, Object aData)
  {
    writer.print("AHS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicCos aNode, Object aData)
  {
    writer.print("AHC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicTan aNode, Object aData)
  {
    writer.print("AHT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSqrt aNode, Object aData)
  {
    writer.print("SQR");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTExp aNode, Object aData)
  {
    writer.print("EXP");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLn aNode, Object aData)
  {
    writer.print("LN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLog aNode, Object aData)
  {
    writer.print("LOG");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInt aNode, Object aData)
  {
    writer.print("INT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFrac aNode, Object aData)
  {
    writer.print("FRAC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTAbs aNode, Object aData)
  {
    writer.print("ABS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSign aNode, Object aData)
  {
    writer.print("SGN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEox aNode, Object aData)
  {
    writer.print("EOX");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEoy aNode, Object aData)
  {
    writer.print("EOY");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLength aNode, Object aData)
  {
    writer.print("LEN");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMid aNode, Object aData)
  {
    writer.print("MID");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRound aNode, Object aData)
  {
    writer.print("RND");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGoto aNode, Object aData)
  {
    writer.print("GOTO ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGotoProgram aNode, Object aData)
  {
    writer.print("GOTO #");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGsb aNode, Object aData)
  {
    writer.print("GSB ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGsbProgram aNode, Object aData)
  {
    writer.print("GSB #");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTReturn aNode, Object aData)
  {
    writer.print("RET");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPrint aNode, Object aData)
  {
    writer.print("PRT ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPrintFormat aNode, Object aData)
  {
    writer.print(aNode.format);
    writer.print(";");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPrtExpression aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCsr aNode, Object aData)
  {
    writer.print("CSR ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTComma aNode, Object aData)
  {
    writer.print(",");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSemicolon aNode, Object aData)
  {
    writer.print(";");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInput aNode, Object aData)
  {
    writer.print("INP ");
    printCommaSeparatedChildren(aNode, aData);
    return null;
  }

  public Object visit(ASTInputPrompt aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInputVariable aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSet aNode, Object aData)
  {
    writer.print("SET " + aNode.format);
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVac aNode, Object aData)
  {
    writer.print("VAC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSac aNode, Object aData)
  {
    writer.print("SAC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTStop aNode, Object aData)
  {
    writer.print("STOP");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEnd aNode, Object aData)
  {
    writer.print("END");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTWait aNode, Object aData)
  {
    writer.print("WAIT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTStat aNode, Object aData)
  {
    writer.print("STAT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDel aNode, Object aData)
  {
    writer.print("DEL");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRPC aNode, Object aData)
  {
    writer.print("RPC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPRC aNode, Object aData)
  {
    writer.print("PRC");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDMS aNode, Object aData)
  {
    writer.print("DMS");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDeg aNode, Object aData)
  {
    writer.print("DEG");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLoad aNode, Object aData)
  {
    writer.print("LOAD");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSave aNode, Object aData)
  {
    writer.print("SAVE");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVerify aNode, Object aData)
  {
    writer.print("VER");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGet aNode, Object aData)
  {
    writer.print("GET");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPut aNode, Object aData)
  {
    writer.print("PUT");
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTIf aNode, Object aData)
  {
    writer.print("IF ");
    aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (aNode.jjtGetNumChildren() == 2)
    {
      if (aNode.jjtGetChild(1) instanceof ASTGoto)
      {
        writer.print(" THEN ");
        ((ASTGoto)aNode.jjtGetChild(1)).childrenAccept(this, aData);
      }
      else if (aNode.jjtGetChild(1) instanceof ASTGotoProgram)
      {
        writer.print("THEN #");
        ((ASTGotoProgram)aNode.jjtGetChild(1)).childrenAccept(this, aData);
      }
      else
      {
        writer.print(";");
        return true;
      }
    }
    else
    {
      writer.print(";");
      return true;
    }
    return null;
  }

  public Object visit(ASTEqual aNode, Object aData)
  {
    return visitBinaryOperator("=", aNode, aData);
  }

  public Object visit(ASTNotEqual aNode, Object aData)
  {
    return visitBinaryOperator("<>", aNode, aData);
  }

  public Object visit(ASTGreater aNode, Object aData)
  {
    return visitBinaryOperator(">", aNode, aData);
  }

  public Object visit(ASTLesser aNode, Object aData)
  {
    return visitBinaryOperator("<", aNode, aData);
  }

  public Object visit(ASTGreaterOrEqual aNode, Object aData)
  {
    return visitBinaryOperator(">=", aNode, aData);
  }

  public Object visit(ASTLesserOrEqual aNode, Object aData)
  {
    return visitBinaryOperator("<=", aNode, aData);
  }

  public Object visit(ASTFor aNode, Object aData)
  {
    writer.print("FOR ");
    aNode.jjtGetChild(0).jjtAccept(this, aData);
    writer.print("=");
    aNode.jjtGetChild(1).jjtAccept(this, aData);
    writer.print(" TO ");
    aNode.jjtGetChild(2).jjtAccept(this, aData);
    if (aNode.jjtGetNumChildren() == 4)
    {
      writer.print(" STEP ");
      aNode.jjtGetChild(3).jjtAccept(this, aData);
    }
    return null;
  }

  public Object visit(ASTNext aNode, Object aData)
  {
    writer.print("NEXT ");
    aNode.childrenAccept(this, aData);
    return null;
  }

  protected PrintWriter writer;
}
