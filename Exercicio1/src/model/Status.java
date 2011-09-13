package model;


/**
 * Print status.
 * 
 * @author  akt & kcg
 */
public class Status {
  public static final Status OK = new Status(true);
  public static final Status NOK = new Status(false);

  private boolean value;

  private Status(boolean val) {
    value = val;
  }

  public boolean equals(Object obj) {
    return (obj instanceof Status) && (value == ((Status) obj).value);
  }
}
