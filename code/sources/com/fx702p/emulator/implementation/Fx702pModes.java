package com.fx702p.emulator.implementation;

import static com.fx702p.emulator.Fx702pConstants.DEG_CONVERSION;
import static com.fx702p.emulator.Fx702pConstants.DEG_MAX;
import static com.fx702p.emulator.Fx702pConstants.DEG_MODE;
import static com.fx702p.emulator.Fx702pConstants.GRA_MODE;
import static com.fx702p.emulator.Fx702pConstants.GRD_CONVERSION;
import static com.fx702p.emulator.Fx702pConstants.GRD_MAX;
import static com.fx702p.emulator.Fx702pConstants.PRT_OFF_MODE;
import static com.fx702p.emulator.Fx702pConstants.PRT_ON_MODE;
import static com.fx702p.emulator.Fx702pConstants.RAD_CONVERSION;
import static com.fx702p.emulator.Fx702pConstants.RAD_MAX;
import static com.fx702p.emulator.Fx702pConstants.RAD_MODE;
import static com.fx702p.emulator.Fx702pConstants.RUN_MODE;
import static com.fx702p.emulator.Fx702pConstants.TRACE_OFF_MODE;
import static com.fx702p.emulator.Fx702pConstants.TRACE_ON_MODE;
import static com.fx702p.emulator.Fx702pConstants.WRT_MODE;

import com.fx702p.emulator.Fx702pEmulator;


public class Fx702pModes
{
  public Fx702pModes(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;
  }

  public void setMode(int aMode)
  {
    switch (aMode)
    {
      case RUN_MODE:
      {
        emulator.setRunMode();
        break;
      }
      case WRT_MODE:
      {
        emulator.setWrtMode();
        break;
      }
      case TRACE_ON_MODE:
      {
        traceMode = true;
        emulator.getDisplay().showTrace(true);
        break;
      }
      case TRACE_OFF_MODE:
      {
        traceMode = false;
        emulator.getDisplay().showTrace(false);
        break;
      }
      case DEG_MODE:
      {
        trigonometricMode = DEG_MODE;
        emulator.getMemory().setTrigonometricConversionFactor(DEG_CONVERSION);
        emulator.getMemory().setTrigonometricMaxArgument(DEG_MAX);
        emulator.getDisplay().showDeg(true);
        emulator.getDisplay().showRad(false);
        emulator.getDisplay().showGrd(false);
        break;
      }
      case RAD_MODE:
      {
        trigonometricMode = RAD_MODE;
        emulator.getMemory().setTrigonometricConversionFactor(RAD_CONVERSION);
        emulator.getMemory().setTrigonometricMaxArgument(RAD_MAX);
        emulator.getDisplay().showDeg(false);
        emulator.getDisplay().showRad(true);
        emulator.getDisplay().showGrd(false);
        break;
      }
      case GRA_MODE:
      {
        trigonometricMode = GRA_MODE;
        emulator.getMemory().setTrigonometricConversionFactor(GRD_CONVERSION);
        emulator.getMemory().setTrigonometricMaxArgument(GRD_MAX);
        emulator.getDisplay().showDeg(false);
        emulator.getDisplay().showRad(false);
        emulator.getDisplay().showGrd(true);
        break;
      }
      case PRT_ON_MODE:
      {
        prtMode = true;
        emulator.getDisplay().showPrt(true);
        break;
      }
      case PRT_OFF_MODE:
      {
        prtMode = false;
        emulator.getDisplay().showPrt(false);
        break;
      }
    }
  }

  protected Fx702pEmulator emulator;

  protected boolean traceMode;
  protected boolean prtMode;
  protected int trigonometricMode;
}
