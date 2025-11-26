package parser;
import java.util.*;

public final class SyntaxErrorCollector {
  public static final class Entry {
    public final int line, col;
    public final String msg, found, context;
    public Entry(int line, int col, String msg, String found, String context) {
      this.line=line; this.col=col; this.msg=msg; this.found=found; this.context=context;
    }
  }
  private static final List<Entry> ERR = new ArrayList<>();
  public static void reset(){ ERR.clear(); }
  public static void add(int line, int col, String msg, String found, String context){
    ERR.add(new Entry(line, col, msg, found, context));
  }

  public static List<Entry> all(){ return Collections.unmodifiableList(ERR); }

  public static void print(){
    System.out.println("=== ERRORES SINTACTICOS ===");
    if (ERR.isEmpty()) { System.out.println("(ninguno)"); return; }
    System.out.printf("%-8s %-8s %-16s %-14s %s%n","Linea","Col","Encontrado","Contexto","Mensaje");
    for (Entry e: ERR){
      System.out.printf("%-8d %-8d %-16s %-14s %s%n", e.line, e.col, e.found, e.context, e.msg);
    }
  }
}
