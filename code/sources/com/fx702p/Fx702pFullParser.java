package com.fx702p;

import java.io.*;

import com.fx702p.emulator.Fx702pBasicSourceCode;
import com.fx702p.parser.Fx702pParser;

public class Fx702pFullParser extends Fx702pParser
{
  public Fx702pFullParser(InputStream anInputStream) throws UnsupportedEncodingException
  {
    super(new Fx702pParenthesisTokenManager(anInputStream));
  }

  public Fx702pBasicSourceCode getBasicSourceCode()
  {
    return ((Fx702pParenthesisTokenManager)token_source).getBasicSourceCode();
  }
}
