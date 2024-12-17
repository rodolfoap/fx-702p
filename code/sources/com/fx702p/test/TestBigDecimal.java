package com.fx702p.test;

import java.io.*;
import java.math.BigDecimal;

public class TestBigDecimal
{
  static public void main(String[] args)
  {
    try
    {
      BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("? ");
      String line = lineReader.readLine();
      double d = Double.parseDouble(line);
      BigDecimal bd = new BigDecimal(line);
      System.out.println("double=" + d);
      System.out.println("BigDecimal=" + bd + ", scale=" + bd.scale());
    }
    catch (Exception exception)
    {
      exception.printStackTrace(System.err);
    }
  }
}
