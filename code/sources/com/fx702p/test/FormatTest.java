package com.fx702p.test;

import java.io.*;
import java.text.*;
import java.util.*;

public class FormatTest
{
  static public void main(String[] args)
  {
    BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));

    while (true)
    {
      try
      {
        System.out.print("Format?");
        String format = lineReader.readLine();
        System.out.print("Number?");
        double number = Double.parseDouble(lineReader.readLine());

        DecimalFormat decimalFormat = new DecimalFormat(format);
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        decimalFormat.setMaximumIntegerDigits(2);
        System.out.println(decimalFormat.format(number));
      }
      catch (Exception exception)
      {
        System.err.println(exception.getMessage());
      }
    }
  }
}
