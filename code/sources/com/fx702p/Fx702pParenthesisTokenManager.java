package com.fx702p;

import java.io.*;
import java.util.Vector;

import com.fx702p.emulator.*;
import com.fx702p.parser.*;

public class Fx702pParenthesisTokenManager extends Fx702pParserTokenManager
{
  public Fx702pParenthesisTokenManager(InputStream aStream) throws UnsupportedEncodingException
  {
    super(new RecordingCharStream(aStream));
  }

  @Override
  public Token getNextToken()
  {
    if (realToken != null)
    {
      if (parenthesisCounter == 0)
      {
        Token token = realToken;
        realToken = null;
        return token;
      }
      else
      {
        parenthesisCounter--;
        return new CloseParenthesisToken();
      }
    }
    Token token = super.getNextToken();
    switch (token.kind)
    {
      case OPEN_PARENTHESIS:
      {
        parenthesisCounter++;
        break;
      }
      case CLOSE_PARENTHESIS:
      {
        parenthesisCounter--;
        break;
      }
      // We must not close parenthesis on COMMA because it is used in function
      // arguments lists
      case SEPARATOR:
      case SEMICOLON:
      case EOL:
      case EOF:
      case EQ:
      case NE:
      case GT:
      case LT:
      case GE:
      case LE:
      {
        if (parenthesisCounter > 0)
        {
          realToken = token;
          parenthesisCounter--;
          token = new CloseParenthesisToken();
        }
        break;
      }
      default:
      {
        break;
      }
    }
    return token;
  }

  public Fx702pBasicSourceCode getBasicSourceCode()
  {
    return ((RecordingCharStream)input_stream).getBasicSourceCode();
  }

  @SuppressWarnings("serial")
  static private class CloseParenthesisToken extends Token
  {
    public CloseParenthesisToken()
    {
      kind = CLOSE_VIRTUAL_PARENTHESIS;
      image = tokenImage[CLOSE_PARENTHESIS];
    }
  }

  static private class RecordingCharStream extends SimpleCharStream
  {
    public RecordingCharStream(InputStream aStream) throws UnsupportedEncodingException
    {
      super(aStream, null, 1, 1);
    }

    public Fx702pBasicSourceCode getBasicSourceCode()
    {
      return basicSourceCode;
    }

    @Override
    public char readChar() throws java.io.IOException
    {
      IOException endOfFile = null;
      char c = ' '; // Useless initialization but needed to make the compiler
      // believe it is always done
      try
      {
        c = super.readChar();
      }
      catch (IOException exception)
      {
        endOfFile = exception;
      }
      if (!endOfFileAlreadyFound && (line != currentLineIndex || endOfFile != null))
      {
        if (currentLine != null)
        {
          StringBuilder builder = new StringBuilder();
          for (Character character : currentLine)
          {
            builder.append(character);
          }
          String lineAsString = builder.toString();

          Fx702pBasicLine basicLine;
          if (TokenHelper.isComment(lineAsString))
          {
            basicLine = new Fx702pCommentLine(lineAsString);
          }
          else
          {
            basicLine = new Fx702pBasicLine(TokenHelper.convertLine(lineAsString));
          }
          if (endOfFile == null)
          {
            basicSourceCode.addLine(line - 2, basicLine);
          }
          else
          {
            basicSourceCode.addLine(line - 1, basicLine);
          }
        }
        currentLine = new Vector<Character>();
        currentLineIndex = line;
      }

      if (endOfFile != null)
      {
        endOfFileAlreadyFound = true;
        throw endOfFile;
      }
      else
      {
        if (c != '\n' && c != '\r')
        {
          int currentSize = currentLine.size();
          if (column - 1 >= currentSize)
          {
            currentLine.setSize(column);
            for (int i = currentSize; i < column - 1; i++)
            {
              currentLine.set(i, ' ');
            }
          }
          currentLine.set(column - 1, c);
        }
        return c;
      }
    }

    private Vector<Character> currentLine = null;
    private int currentLineIndex = -1;
    private Fx702pBasicSourceCode basicSourceCode = new Fx702pBasicSourceCode();
    private boolean endOfFileAlreadyFound = false;
  }

  private int parenthesisCounter = 0;
  private Token realToken = null;
}
