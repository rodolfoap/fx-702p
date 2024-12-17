package com.fx702p.test;

import java.io.*;

import com.fx702p.Fx702pFullParser;

public class CheckSyntax extends AbstractFx702pTester
{
  static public void main(String[] args)
  {
    new CheckSyntax().processFilenames(args);
  }

  @Override
  public void process(File aFile)
  {
    try
    {
      Fx702pFullParser parser = new Fx702pFullParser(new FileInputStream(aFile));
      parser.ProgramsFile();
    }
    catch (Exception exception)
    {
      System.err.println(exception.getMessage() + " while parsing " + aFile.getName());
    }
  }
}
