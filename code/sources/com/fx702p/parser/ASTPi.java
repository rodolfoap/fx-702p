/* Generated By:JJTree: Do not edit this line. ASTPi.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
/*EDITED*/

package com.fx702p.parser;

public class ASTPi extends EscapedCharNode
{
  public ASTPi(int id)
  {
    super(id);
  }

  public ASTPi(Fx702pParser p, int id)
  {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(Fx702pParserVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }
}
/*
 * JavaCC - OriginalChecksum=6fa34258f35ed2c54f72bc2c975af079 (do not edit this
 * line)
 */
