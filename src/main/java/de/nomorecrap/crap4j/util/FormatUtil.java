package de.nomorecrap.crap4j.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

  public static NumberFormat getNumberFormatter() {
    NumberFormat nf = NumberFormat.getInstance();
    return setFloatDigits(nf);
  }

  public static NumberFormat getUSNumberFormatter() {
    NumberFormat nf = NumberFormat.getInstance(Locale.US);
    return setFloatDigits(nf);
  }

  
  private static NumberFormat setFloatDigits(NumberFormat nf) {
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    nf.setGroupingUsed(false);
    return nf;
  }

}
