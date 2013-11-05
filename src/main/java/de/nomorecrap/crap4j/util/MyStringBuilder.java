package de.nomorecrap.crap4j.util;

public class MyStringBuilder{

  private StringBuilder myStringBuilder;
  private int currentIndentation;
  
  public MyStringBuilder() {
    myStringBuilder = new StringBuilder();
    currentIndentation = 0;
  }
  
  private void pad() {
    for(int i=0; i< currentIndentation; i++) {
      myStringBuilder.append(" ");
    }
  }
  
  private void newline() {
    myStringBuilder.append("\n");
  }
  
  public MyStringBuilder start(String s) {
    append(s);
    indent();
    return this;
  }

  private void indent() {
    currentIndentation = currentIndentation + 2;
  }

  public MyStringBuilder end(String s) {
    unindent();
    append(s);
    return this;
  }

  private void unindent() {
    currentIndentation = currentIndentation - 2;
  }

  
  public MyStringBuilder append(String s) {
    pad();
    myStringBuilder.append(s);
    newline();
    return this;
  }
  
  public String toString() {
    return myStringBuilder.toString();
  }
}
