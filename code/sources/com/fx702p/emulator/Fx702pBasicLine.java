package com.fx702p.emulator;

import com.fx702p.parser.ASTLine;

public class Fx702pBasicLine
{
  public Fx702pBasicLine(String aLine)
  {
    line = aLine;
  }

  public ASTLine getParsedLine()
  {
    return parsedLine;
  }

  public void setParsedLine(ASTLine aParsedLine)
  {
    parsedLine = aParsedLine;
  }

  @Override
  public String toString()
  {
    return line;
  }

  protected String line;
  protected ASTLine parsedLine;
}
