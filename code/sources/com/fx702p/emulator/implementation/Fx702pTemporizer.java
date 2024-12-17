package com.fx702p.emulator.implementation;

public class Fx702pTemporizer
{
  public void setSpeed(int aSpeed)
  {
    if (aSpeed >= MIN_SPEED || aSpeed <= MAX_SPEED)
    {
      if (aSpeed >= DEFAULT_SPEED)
      {
        // timeToSleep=DEFAULT when speed=DEFAULT
        // timeToSpeel=0 when speed=MAX
        // and a linear interpolation in-between
        timeToSleep = (int)(DEFAULT_TIME_TO_SLEEP * (1 - (aSpeed - DEFAULT_SPEED) / (float)(MAX_SPEED - DEFAULT_SPEED)));
      }
      else
      {
        // timeToSleep=DEFAULT when speed=DEFAULT
        // timeToSpeel=DEFAULT_TIME_TO_SLEEP*MAX_TIME_TO_SLEEP_FACTOR when
        // speed=MIN
        // and a linear interpolation in-between
        timeToSleep = (int)(DEFAULT_TIME_TO_SLEEP * (MAX_TIME_TO_SLEEP_FACTOR - (MAX_TIME_TO_SLEEP_FACTOR - 1) * (aSpeed - MIN_SPEED) / (float)(DEFAULT_SPEED - MIN_SPEED)));
      }
    }
    else
    {
      System.err.println("Invalid speed: " + aSpeed + ", must be between " + MIN_SPEED + " and " + MAX_SPEED);
    }
  }

  public void temporize()
  {
    try
    {
      Thread.sleep(timeToSleep);
    }
    catch (InterruptedException exception)
    {
    }
  }

  protected int timeToSleep = DEFAULT_TIME_TO_SLEEP;

  static public final int DEFAULT_TIME_TO_SLEEP = 16;
  static public final int MAX_TIME_TO_SLEEP_FACTOR = 10;

  static public final int MIN_SPEED = 1;
  static public final int MAX_SPEED = 9;
  static public final int DEFAULT_SPEED = 5;
}
