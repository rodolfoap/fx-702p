package com.fx702p.emulator;

public class TokenHelper
{
  static public boolean isComment(String aLine)
  {
    return aLine.matches("[ \t]*(//|/\\*|\\\\\\*).*");
  }

  static public String convertStringLiteral(String aString)
  {
    return convert(aString, true);
  }

  static public String convertLine(String aString)
  {
    return convert(aString, false);
  }

  static public String convertForOutput(String aString)
  {
    StringBuilder converted = new StringBuilder();

    for (int i = 0, last = aString.length(); i < last; i++)
    {
      char c = aString.charAt(i);
      switch (c)
      {
        case '{':
        {
          converted.append("\\<");
          break;
        }
        case '}':
        {
          converted.append("\\>");
          break;
        }
        case 'e':
        {
          converted.append("\\e");
          break;
        }
        case '~':
        {
          converted.append("\\=");
          break;
        }
        case 'p':
        {
          converted.append("\\p");
          break;
        }
        default:
        {
          converted.append(c);
        }
      }
    }

    return converted.toString();
  }

  static protected String convert(String aString, boolean checkValidity)
  {
    StringBuilder converted = new StringBuilder();

    for (int i = 0, last = aString.length(); i < last; i++)
    {
      char c = aString.charAt(i);
      if (c == '\\')
      {
        i++;
        if (i < last)
        {
          c = aString.charAt(i);
          switch (c)
          {
            case '<':
            {
              c = '{';
              break;
            }
            case '>':
            {
              c = '}';
              break;
            }
            case 'e':
            case 'E':
            {
              c = 'e';
              break;
            }
            case '=':
            {
              c = '~';
              break;
            }
            case 'p':
            case 'P':
            {
              c = 'p';
              break;
            }
            default:
            {
              Console.reportError("Unknown character '" + c + "' after \\");
            }
          }
        }
        else
        {
          Console.reportError("Character \\ cannot be the last one of a String");
        }

      }
      else if (Character.isLowerCase(c))
      {
        c = Character.toUpperCase(c);
      }

      if (checkValidity)
      {
        if (Characters.isValidStringCharacter(c))
        {
          converted.append(c);
        }
        else
        {
          Console.reportError("Invalid character '" + c + "'");
        }
      }
      else
      {
        converted.append(c);
      }
    }
    return converted.toString();
  }

  static public String convertFloatLiteral(String aString)
  {
    StringBuilder converted = new StringBuilder();

    for (int i = 0, last = aString.length(); i < last; i++)
    {
      char c = aString.charAt(i);
      if (c == '\\')
      {
        i++;
        if (i < last)
        {
          c = aString.charAt(i);
          switch (c)
          {
            case 'e':
            case 'E':
            {
              c = 'E';
              break;
            }

            default:
            {
              Console.reportError("Unknown character '" + c + "' after \\");
            }
          }
        }
        else
        {
          Console.reportError("Character \\ cannot be the last one of a Float");
        }

      }

      converted.append(c);
    }
    return converted.toString();
  }
}
