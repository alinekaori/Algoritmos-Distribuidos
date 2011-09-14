package model;

import java.io.Serializable;


/**
 * Print status.
 * 
 * @author akt & kcg
 */
public class SimpleMessage implements Serializable{
  
  private int id;
  private String string;
  
  public SimpleMessage(int id, String string) {
    this.id = id;
    this.string = string;
  }

  public int getId(){
	  return id;
  }
  
  public String getString(){
	  return string;
  }
}
