/*EDITED*/

package com.fx702p.parser;

public class EscapedCharNode extends SimpleNode
{
  public EscapedCharNode(int id)
  {
    super(id);
  }

  public EscapedCharNode(Fx702pParser p, int id)
  {
    super(p, id);
  }

  public int delta = 0;
}
