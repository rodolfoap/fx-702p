/* Generated By:JJTree: Do not edit this line. ASTIndexedVariable.java */
/*EDITED*/

package com.fx702p.parser;

public class ASTIndexedVariable extends SimpleNode
{
  public ASTIndexedVariable(int id)
  {
    super(id);
  }

  public ASTIndexedVariable(Fx702pParser p, int id)
  {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(Fx702pParserVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }

  public String name;
  public boolean isString = false;
}
