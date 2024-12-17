package com.fx702p.interpreters;

import com.fx702p.emulator.exceptions.Fx702pErr2Exception;
import com.fx702p.parser.*;

public abstract class Fx702pAbstractParserVisitor implements Fx702pParserVisitor
{
  public Object visit(SimpleNode aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSavedDefm aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTMode aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDefm aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPassword aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRom aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCommand aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRun aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTList aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTClear aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }


  public Object visit(ASTProgramsFile aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSingleProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMultiplePrograms aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVariablesAndProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLine aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTAssignment aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTAddition aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSubstraction aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMultiplication aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDivision aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTUnaryMinus aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPower aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFactorial aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPi aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInteger aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFloat aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTString aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDollar aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVariable aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTIndexedVariable aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFunctionCall aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFunctionArguments aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFunctionWithParenthesisArguments aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRandom aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCnt aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdx aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdy aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdxn aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSdyn aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMx aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTMy aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSx aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSy aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSx2 aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSy2 aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSxy aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLra aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLrb aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCor aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTKey aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSin aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCos aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTTan aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcSin aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcCos aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcTan aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicSin aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicCos aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTHyperbolicTan aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicSin aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicCos aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTArcHyperbolicTan aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSqrt aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTExp aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLn aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLog aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInt aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFrac aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTAbs aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSign aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEox aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEoy aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLength aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTMid aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRound aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGoto aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGotoProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGsb aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGsbProgram aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTReturn aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPrint aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPrintFormat aNode, Object aData)
  {
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
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTComma aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSemicolon aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTInput aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
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
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVac aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSac aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTStop aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEnd aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTWait aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTStat aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDel aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTRPC aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPRC aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDMS aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTDeg aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCommandArguments aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTCommandWithParenthesisArguments aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLoad aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTSave aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTVerify aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGet aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTPut aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTIf aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTEqual aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTNotEqual aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGreater aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLesser aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTGreaterOrEqual aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTLesserOrEqual aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTFor aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTNext aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTComment aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTClosedSubExpression aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTOpenSubExpression aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTProgramSignature aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTBreakpoint aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTWatch aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTWatchpoint aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }

  public Object visit(ASTStatWatchpoint aNode, Object aData)
  {
    aNode.childrenAccept(this, aData);
    return null;
  }
}
