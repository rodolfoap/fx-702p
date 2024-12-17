package com.fx702p.emulator;

import java.util.*;

public class Fx702pBasicSourceCode
{
  public Fx702pBasicSourceCode()
  {
  }

  public Fx702pBasicSourceCode(Fx702pBasicSourceCode aBasicSourceCode, int aBeginLine, int anEndLine)
  {
    if (aBeginLine < 0)
    {
      aBeginLine = 0;
    }
    if (anEndLine < 0)
    {
      anEndLine = aBasicSourceCode.getLines().size();
    }
    for (int i = aBeginLine; i < anEndLine; i++)
    {
      addLine(i - aBeginLine, aBasicSourceCode.getLine(i));
    }
  }

  public void addLine(int anIndex, Fx702pBasicLine aLine)
  {
    if (anIndex >= lines.size())
    {
      lines.setSize(anIndex + 1);
    }
    lines.set(anIndex, aLine);
  }

  public List<Fx702pBasicLine> getLines()
  {
    return lines;
  }

  public Fx702pBasicLine getLine(int aLine)
  {
    return lines.get(aLine);
  }

  protected Vector<Fx702pBasicLine> lines = new Vector<Fx702pBasicLine>();
}
