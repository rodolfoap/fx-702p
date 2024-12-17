package com.fx702p.emulator;

import java.util.List;

import com.fx702p.parser.ASTLine.LineInfos;
import com.fx702p.parser.*;

public class InstructionBoundary
{
  public InstructionBoundary(int aBegin)
  {
    begin = aBegin;
  }

  public InstructionBoundary(int aBegin, int anEnd)
  {
    begin = aBegin;
    end = anEnd;
  }

  @Override
  public String toString()
  {
    return "begin=" + begin + ", end=" + end;
  }

  public int begin;
  public int end;

  static public void resynchronizeLinePositions(Fx702pBasicSourceCode aBasicSourceCode, int aLineIndexInSourceCode, List<InstructionBoundary> theInstructionBoundaries)
  {
    resynchronizePositions(aBasicSourceCode, aLineIndexInSourceCode, theInstructionBoundaries, 1, null, null);
  }

  static public void resynchronizeInputOrPrintPositions(Fx702pBasicSourceCode aBasicSourceCode, int aLineIndexInSourceCode, List<InstructionBoundary> theInstructionBoundaries, SimpleNode aNode, LineInfos theLineInfos)
  {
    resynchronizePositions(aBasicSourceCode, aLineIndexInSourceCode, theInstructionBoundaries, 0, aNode, theLineInfos);
  }

  static private void resynchronizePositions(Fx702pBasicSourceCode aBasicSourceCode, int aLineIndexInSourceCode, List<InstructionBoundary> theInstructionBoundaries, int anOffset, SimpleNode aNode, LineInfos theLineInfos)
  {
    Fx702pBasicLine basicLine = aBasicSourceCode.getLine(aLineIndexInSourceCode);

    for (int i = 0, last = theInstructionBoundaries.size(); i < last; i++)
    {
      InstructionBoundary instructionBoundary = theInstructionBoundaries.get(i);
      int begin = instructionBoundary.begin + anOffset;
      int end;
      String basicLineAsString = basicLine.toString();
      if (i < last - 1)
      {
        end = theInstructionBoundaries.get(i + 1).begin;
      }
      else
      {
        if (aNode != null && theLineInfos != null)
        {
          end = -1;
          theLineInfos.boundariesToFix.put(aNode, instructionBoundary);
        }
        else
        {
          end = basicLineAsString.length();
        }
      }
      if (end >= 0)
      {
        for (int j = begin; j < end; j++)
        {
          char c = basicLineAsString.charAt(j);
          if (c != ' ' && c != '\t')
          {
            begin = j;
            break;
          }
        }

        for (int j = end - 1; j >= begin; j--)
        {
          char c = basicLineAsString.charAt(j);
          if (c != ' ' && c != '\t')
          {
            end = j + 1;
            break;
          }
        }
      }
      instructionBoundary.begin = begin;
      instructionBoundary.end = end;

      // Commented lines are useful for debugging
      //
      // String instruction = basicLineAsString.substring(begin, end);
      // if (i < last - 1)
      // {
      // System.out.print(instruction + " : ");
      // }
      // else
      // {
      // System.out.println(instruction);
      // }
    }
  }
}
