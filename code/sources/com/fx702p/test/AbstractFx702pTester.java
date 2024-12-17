package com.fx702p.test;

import java.io.*;


public abstract class AbstractFx702pTester
{
  abstract public void process(File aFile);

  public void processFilenames(String[] theFilenames)
  {
    for (String filename : theFilenames)
    {
      File file = new File(filename);
      if (file.exists())
      {
        if (file.isFile())
        {
          process(file);
        }
        else if (file.isDirectory())
        {
          processDirectory(file);
        }
      }
      else
      {
        System.err.println("File " + filename + " does not exists");
      }
    }
  }

  public void processDirectory(File aFile)
  {
    File[] files = aFile.listFiles(new FilenameFilter()
      {
        public boolean accept(File aDirectory, String aName)
        {
          return aName.endsWith(FX702P_SUFFIX);
        }
      });

    for (File file : files)
    {
      process(file);
    }
  }

  static public final String FX702P_SUFFIX = ".702";
}
