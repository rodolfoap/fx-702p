/* Generated By:JJTree: Do not edit this line. ASTClear.java */
/*EDITED*/

package com.fx702p.parser;

public class ASTClear extends SimpleNode
{
  public ASTClear(int id)
  {
    super(id);
  }

  public ASTClear(Fx702pParser p, int id)
  {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(Fx702pParserVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }

  public boolean all = false;
}
