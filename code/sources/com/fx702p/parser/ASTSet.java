/* Generated By:JJTree: Do not edit this line. ASTSet.java */
/*EDITED*/

package com.fx702p.parser;

public class ASTSet extends SimpleNode
{
  public ASTSet(int id)
  {
    super(id);
  }

  public ASTSet(Fx702pParser p, int id)
  {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(Fx702pParserVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }

  public String format;
}
