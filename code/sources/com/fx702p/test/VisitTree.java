package com.fx702p.test;

import java.io.*;

import com.fx702p.Fx702pFullParser;
import com.fx702p.parser.*;

public class VisitTree extends AbstractFx702pTester implements Fx702pParserVisitor
{
  static public void main(String[] args)
  {
    new VisitTree().processFilenames(args);
  }

  @Override
  public void process(File aFile)
  {
    Fx702pFullParser parser;
    try
    {
      parser = new Fx702pFullParser(new FileInputStream(aFile));
      ASTProgram program = parser.Program();
      program.jjtAccept(this, null);
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  public void displayTree(String aNodeName, SimpleNode aNode)
  {
    System.out.println(indent + aNodeName);
    String lastIndent = indent;
    indent = indent + "  ";

    aNode.childrenAccept(this, null);

    indent = lastIndent;
  }

  public Object visit(SimpleNode aNode, Object aData)
  {
    displayTree("SimpleNode", aNode);
    return null;
  }

  public Object visit(ASTSavedDefm aNode, Object aData)
  {
    displayTree("SavedDefm", aNode);
    return null;
  }

  public Object visit(ASTDefm aNode, Object aData)
  {
    displayTree("Defm", aNode);
    return null;
  }

  public Object visit(ASTPassword aNode, Object aData)
  {
    displayTree("Pass", aNode);
    return null;
  }

  public Object visit(ASTRom aNode, Object aData)
  {
    displayTree("Rom", aNode);
    return null;
  }

  public Object visit(ASTRun aNode, Object aData)
  {
    displayTree("Run", aNode);
    return null;
  }

  public Object visit(ASTList aNode, Object aData)
  {
    displayTree("List", aNode);
    return null;
  }

  public Object visit(ASTClear aNode, Object aData)
  {
    displayTree("Clr", aNode);
    return null;
  }

  public Object visit(ASTLoad aNode, Object aData)
  {
    displayTree("Load", aNode);
    return null;
  }

  public Object visit(ASTSave aNode, Object aData)
  {
    displayTree("Save", aNode);
    return null;
  }

  public Object visit(ASTGet aNode, Object aData)
  {
    displayTree("Get", aNode);
    return null;
  }

  public Object visit(ASTPut aNode, Object aData)
  {
    displayTree("Put", aNode);
    return null;
  }

  public Object visit(ASTVerify aNode, Object aData)
  {
    displayTree("Ver", aNode);
    return null;
  }

  public Object visit(ASTProgramsFile aNode, Object aData)
  {
    displayTree("ASTProgramsFile", aNode);
    return null;
  }

  public Object visit(ASTMultiplePrograms aNode, Object aData)
  {
    displayTree("ASTMultiplePrograms", aNode);
    return null;
  }

  public Object visit(ASTSingleProgram aNode, Object aData)
  {
    displayTree("ASTSingleProgram", aNode);
    return null;
  }

  public Object visit(ASTProgram aNode, Object aData)
  {
    displayTree("ASTProgram", aNode);
    return null;
  }

  public Object visit(ASTVariablesAndProgram aNode, Object aData)
  {
    displayTree("ASTVariablesAndProgram", aNode);
    return null;
  }

  public Object visit(ASTLine aNode, Object aData)
  {
    displayTree("ASTLine " + aNode.getLine(), aNode);
    return null;
  }

  public Object visit(ASTAssignment aNode, Object aData)
  {
    displayTree("ASTAssignment", aNode);
    return null;
  }

  public Object visit(ASTAddition aNode, Object aData)
  {
    displayTree("ASTAddition", aNode);
    return null;
  }

  public Object visit(ASTSubstraction aNode, Object aData)
  {
    displayTree("ASTSubstraction", aNode);
    return null;
  }

  public Object visit(ASTMultiplication aNode, Object aData)
  {
    displayTree("ASTMultiplication", aNode);
    return null;
  }

  public Object visit(ASTDivision aNode, Object aData)
  {
    displayTree("ASTDivision", aNode);
    return null;
  }

  public Object visit(ASTUnaryMinus aNode, Object aData)
  {
    displayTree("ASTUnaryMinus", aNode);
    return null;
  }

  public Object visit(ASTPower aNode, Object aData)
  {
    displayTree("ASTPower", aNode);
    return null;
  }

  public Object visit(ASTFactorial aNode, Object aData)
  {
    displayTree("ASTFactorial", aNode);
    return null;
  }

  public Object visit(ASTFunctionCall aNode, Object aData)
  {
    displayTree("ASTFunctionCall", aNode);
    return null;
  }

  public Object visit(ASTRandom aNode, Object aData)
  {
    displayTree("ASTRandom", aNode);
    return null;
  }

  public Object visit(ASTCnt aNode, Object aData)
  {
    displayTree("ASTCnt", aNode);
    return null;
  }

  public Object visit(ASTSdx aNode, Object aData)
  {
    displayTree("ASTSdx", aNode);
    return null;
  }

  public Object visit(ASTSdy aNode, Object aData)
  {
    displayTree("ASTSdy", aNode);
    return null;
  }

  public Object visit(ASTSdxn aNode, Object aData)
  {
    displayTree("ASTSdxn", aNode);
    return null;
  }

  public Object visit(ASTSdyn aNode, Object aData)
  {
    displayTree("ASTSdyn", aNode);
    return null;
  }

  public Object visit(ASTMx aNode, Object aData)
  {
    displayTree("ASTMx", aNode);
    return null;
  }

  public Object visit(ASTMy aNode, Object aData)
  {
    displayTree("ASTMy", aNode);
    return null;
  }

  public Object visit(ASTSx aNode, Object aData)
  {
    displayTree("ASTSx", aNode);
    return null;
  }

  public Object visit(ASTSy aNode, Object aData)
  {
    displayTree("ASTSy", aNode);
    return null;
  }

  public Object visit(ASTSx2 aNode, Object aData)
  {
    displayTree("ASTSx2", aNode);
    return null;
  }

  public Object visit(ASTSy2 aNode, Object aData)
  {
    displayTree("ASTSy2", aNode);
    return null;
  }

  public Object visit(ASTSxy aNode, Object aData)
  {
    displayTree("ASTSxy", aNode);
    return null;
  }

  public Object visit(ASTLra aNode, Object aData)
  {
    displayTree("ASTLra", aNode);
    return null;
  }

  public Object visit(ASTLrb aNode, Object aData)
  {
    displayTree("ASTLrb", aNode);
    return null;
  }

  public Object visit(ASTCor aNode, Object aData)
  {
    displayTree("ASTCor", aNode);
    return null;
  }

  public Object visit(ASTKey aNode, Object aData)
  {
    displayTree("ASTKey", aNode);
    return null;
  }

  public Object visit(ASTSin aNode, Object aData)
  {
    displayTree("ASTSin", aNode);
    return null;
  }

  public Object visit(ASTCos aNode, Object aData)
  {
    displayTree("ASTCos", aNode);
    return null;
  }

  public Object visit(ASTTan aNode, Object aData)
  {
    displayTree("ASTTan", aNode);
    return null;
  }

  public Object visit(ASTArcSin aNode, Object aData)
  {
    displayTree("ASTArcSin", aNode);
    return null;
  }

  public Object visit(ASTArcCos aNode, Object aData)
  {
    displayTree("ASTArcCos", aNode);
    return null;
  }

  public Object visit(ASTArcTan aNode, Object aData)
  {
    displayTree("ASTArcTan", aNode);
    return null;
  }

  public Object visit(ASTHyperbolicSin aNode, Object aData)
  {
    displayTree("ASTHyperbolicSin", aNode);
    return null;
  }

  public Object visit(ASTHyperbolicCos aNode, Object aData)
  {
    displayTree("ASTHyperbolicCos", aNode);
    return null;
  }

  public Object visit(ASTHyperbolicTan aNode, Object aData)
  {
    displayTree("ASTHyperbolicTan", aNode);
    return null;
  }

  public Object visit(ASTArcHyperbolicSin aNode, Object aData)
  {
    displayTree("ASTArcHyperbolicSin", aNode);
    return null;
  }

  public Object visit(ASTArcHyperbolicCos aNode, Object aData)
  {
    displayTree("ASTArcHyperbolicCos", aNode);
    return null;
  }
  public Object visit(ASTArcHyperbolicTan aNode, Object aData)
  {
    displayTree("ASTArcHyperbolicTan", aNode);
    return null;
  }

  public Object visit(ASTSqrt aNode, Object aData)
  {
    displayTree("ASTSqrt", aNode);
    return null;
  }

  public Object visit(ASTExp aNode, Object aData)
  {
    displayTree("ASTExp", aNode);
    return null;
  }

  public Object visit(ASTLn aNode, Object aData)
  {
    displayTree("ASTLn", aNode);
    return null;
  }

  public Object visit(ASTLog aNode, Object aData)
  {
    displayTree("ASTLog", aNode);
    return null;
  }

  public Object visit(ASTInt aNode, Object aData)
  {
    displayTree("ASTInt", aNode);
    return null;
  }

  public Object visit(ASTFrac aNode, Object aData)
  {
    displayTree("ASTFrac", aNode);
    return null;
  }

  public Object visit(ASTAbs aNode, Object aData)
  {
    displayTree("ASTAbs", aNode);
    return null;
  }

  public Object visit(ASTSign aNode, Object aData)
  {
    displayTree("ASTSign", aNode);
    return null;
  }

  public Object visit(ASTDMS aNode, Object aData)
  {
    displayTree("ASTDMS", aNode);
    return null;
  }

  public Object visit(ASTEox aNode, Object aData)
  {
    displayTree("ASTEox", aNode);
    return null;
  }

  public Object visit(ASTEoy aNode, Object aData)
  {
    displayTree("ASTEoy", aNode);
    return null;
  }

  public Object visit(ASTLength aNode, Object aData)
  {
    displayTree("ASTLength", aNode);
    return null;
  }

  public Object visit(ASTMid aNode, Object aData)
  {
    displayTree("ASTMid", aNode);
    return null;
  }

  public Object visit(ASTRound aNode, Object aData)
  {
    displayTree("ASTRound", aNode);
    return null;
  }

  public Object visit(ASTInteger aNode, Object aData)
  {
    displayTree("ASTInteger=" + aNode.value, aNode);
    return null;
  }

  public Object visit(ASTFloat aNode, Object aData)
  {
    displayTree("ASTFloat=" + aNode.value, aNode);
    return null;
  }

  public Object visit(ASTString aNode, Object aData)
  {
    displayTree("ASTString=\"" + aNode.value + '"', aNode);
    return null;
  }

  public Object visit(ASTPi aNode, Object aData)
  {
    displayTree("ASTPi", aNode);
    return null;
  }

  public Object visit(ASTVariable aNode, Object aData)
  {
    displayTree("ASTVariable " + (aNode.isString ? "String " : "") + aNode.name, aNode);
    return null;
  }

  public Object visit(ASTIndexedVariable aNode, Object aData)
  {
    displayTree("ASTIndexedVariable " + (aNode.isString ? "String " : "") + aNode.name, aNode);
    return null;
  }

  public Object visit(ASTDollar aNode, Object aData)
  {
    displayTree("ASTDollar", aNode);
    return null;
  }

  public Object visit(ASTGoto aNode, Object aData)
  {
    displayTree("ASTGoto", aNode);
    return null;
  }

  public Object visit(ASTGotoProgram aNode, Object aData)
  {
    displayTree("ASTGotoProgram", aNode);
    return null;
  }

  public Object visit(ASTGsb aNode, Object aData)
  {
    displayTree("ASTGsb", aNode);
    return null;
  }

  public Object visit(ASTGsbProgram aNode, Object aData)
  {
    displayTree("ASTGsbProgram", aNode);
    return null;
  }

  public Object visit(ASTReturn aNode, Object aData)
  {
    displayTree("ASTReturn", aNode);
    return null;
  }

  public Object visit(ASTMode aNode, Object aData)
  {
    displayTree("ASTMode", aNode);
    return null;
  }

  public Object visit(ASTIf aNode, Object aData)
  {
    displayTree("ASTIf", aNode);
    return null;
  }

  public Object visit(ASTEqual aNode, Object aData)
  {
    displayTree("ASTEqual", aNode);
    return null;
  }

  public Object visit(ASTNotEqual aNode, Object aData)
  {
    displayTree("ASTNotEqual", aNode);
    return null;
  }

  public Object visit(ASTGreater aNode, Object aData)
  {
    displayTree("ASTGreater", aNode);
    return null;
  }

  public Object visit(ASTLesser aNode, Object aData)
  {
    displayTree("ASTLesser", aNode);
    return null;
  }

  public Object visit(ASTGreaterOrEqual aNode, Object aData)
  {
    displayTree("ASTGreaterOrEqual", aNode);
    return null;
  }

  public Object visit(ASTLesserOrEqual aNode, Object aData)
  {
    displayTree("ASTLesserOrEqual", aNode);
    return null;
  }

  public Object visit(ASTFunctionArguments aNode, Object aData)
  {
    displayTree("ASTFunctionArguments", aNode);
    return null;
  }

  public Object visit(ASTFunctionWithParenthesisArguments aNode, Object aData)
  {
    displayTree("ASTFunctionWithParenthesisArguments", aNode);
    return null;
  }

  public Object visit(ASTVac aNode, Object aData)
  {
    displayTree("ASTVac", aNode);
    return null;
  }

  public Object visit(ASTSac aNode, Object aData)
  {
    displayTree("ASTSac", aNode);
    return null;
  }

  public Object visit(ASTStop aNode, Object aData)
  {
    displayTree("ASTStop", aNode);
    return null;
  }

  public Object visit(ASTEnd aNode, Object aData)
  {
    displayTree("ASTEnd", aNode);
    return null;
  }

  public Object visit(ASTWait aNode, Object aData)
  {
    displayTree("ASTWait", aNode);
    return null;
  }

  public Object visit(ASTStat aNode, Object aData)
  {
    displayTree("ASTStat", aNode);
    return null;
  }

  public Object visit(ASTDel aNode, Object aData)
  {
    displayTree("ASTDel", aNode);
    return null;
  }

  public Object visit(ASTRPC aNode, Object aData)
  {
    displayTree("ASTRPC", aNode);
    return null;
  }

  public Object visit(ASTPRC aNode, Object aData)
  {
    displayTree("ASTPRC", aNode);
    return null;
  }

  public Object visit(ASTDeg aNode, Object aData)
  {
    displayTree("ASTDeg", aNode);
    return null;
  }

  public Object visit(ASTCommand aNode, Object aData)
  {
    displayTree("ASTCommand", aNode);
    return null;
  }

  public Object visit(ASTPrint aNode, Object aData)
  {
    displayTree("ASTPrint", aNode);
    return null;
  }

  public Object visit(ASTPrintFormat aNode, Object aData)
  {
    displayTree("ASTPrintFormat \"" + aNode.format + '"', aNode);
    return null;
  }

  public Object visit(ASTCsr aNode, Object aData)
  {
    displayTree("ASTCsr", aNode);
    return null;
  }

  public Object visit(ASTInput aNode, Object aData)
  {
    displayTree("ASTInput", aNode);
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

  public Object visit(ASTSet aNode, Object aData)
  {
    displayTree("ASTSet " + aNode.format, aNode);
    return null;
  }

  public Object visit(ASTCommandArguments aNode, Object aData)
  {
    displayTree("ASTCommandArguments", aNode);
    return null;
  }

  public Object visit(ASTCommandWithParenthesisArguments aNode, Object aData)
  {
    displayTree("ASTCommandWithParenthesisArguments", aNode);
    return null;
  }

  public Object visit(ASTFor aNode, Object aData)
  {
    displayTree("ASTFor", aNode);
    return null;
  }

  public Object visit(ASTNext aNode, Object aData)
  {
    displayTree("ASTNext", aNode);
    return null;
  }

  public Object visit(ASTComma aNode, Object aData)
  {
    displayTree("ASTComma", aNode);
    return null;
  }
  public Object visit(ASTSemicolon aNode, Object aData)
  {
    displayTree("ASTSemicolon", aNode);
    return null;
  }

  public Object visit(ASTPrtExpression aNode, Object aData)
  {
    displayTree("ASTPrtExpression", aNode);
    return null;
  }

  public Object visit(ASTProgramSignature aNode, Object aData)
  {
    displayTree("ASTCRC", aNode);
    return null;
  }

  public Object visit(ASTClosedSubExpression aNode, Object aData)
  {
    displayTree("ASTClosedSubExpression", aNode);
    return null;
  }

  public Object visit(ASTOpenSubExpression aNode, Object aData)
  {
    displayTree("ASTOpenSubExpression", aNode);
    return null;
  }

  public Object visit(ASTBreakpoint aNode, Object aData)
  {
    displayTree("ASTBreakpoint", aNode);
    return null;
  }

  public Object visit(ASTWatch aNode, Object aData)
  {
    displayTree("ASTWatch", aNode);
    return null;
  }

  public Object visit(ASTWatchpoint aNode, Object aData)
  {
    displayTree("ASTWatchpoint", aNode);
    return null;
  }

  public Object visit(ASTStatWatchpoint aNode, Object aData)
  {
    displayTree("ASTStatWatchpoint", aNode);
    return null;
  }

  public Object visit(ASTComment aNode, Object aData)
  {
    displayTree("ASTComment", aNode);
    return null;
  }

  private String indent = "";
}
