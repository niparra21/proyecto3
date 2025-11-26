package scanner;

import java.io.FileReader;
import java.lang.reflect.Method;
import java_cup.runtime.Symbol;
import parser.sym;

public class MainScanner {

  public static void main(String[] args) throws Exception {
    java.util.Scanner input = new java.util.Scanner(System.in);

    System.out.println("=== Pruebas del Analizador Léxico ===");
    int i = 1;
    for (TestFile tf : TestFile.values()) {
      System.out.printf("%d) %s - %s%n", i, tf.getFileName(), tf.getDescription());
      i++;
    }
    System.out.print("Seleccione un número de prueba: ");
    int choice = input.nextInt();
    input.nextLine();

    if (choice < 1 || choice > TestFile.values().length) {
      System.out.println("Opción inválida.");
      return;
    }

    TestFile selected = TestFile.values()[choice - 1];

    // Construimos la ruta usando getFileName() (ajusta el prefijo si tus tests están en otro sitio)
    String path = "scanner/test/" + selected.getFileName();

    // Limpia el recolector P1 si existe (no rompe si no está)
    tryInvokeStatic("scanner.TokenCollector", "reset");

    System.out.printf("%n=== Escaneando: %s ===%n%n", path);

    // Consumir todo el archivo con next_token()
    runJFlex(path);
    TokenCollector.printErrors();
    TokenCollector.printTokens();
    TokenCollector.printSummary();
    System.out.println("\nFin del escaneo.");
    
  }

  private static void runJFlex(String path) throws Exception {
    scanner.Scanner sc = new scanner.Scanner(new FileReader(path));
    while (true) {
      Symbol t = sc.next_token();            // clave para %cup
      if (t.sym == sym.EOF) break;
      // Si quieres ver también el flujo CUP en este modo, descomenta:
      // System.out.printf("(%d:%d) %-12s %s%n", t.left, t.right, nameOf(t.sym),
      //                   (t.value!=null? t.value.toString() : ""));
    }
  }

  // ===== Utilidades de reflexión seguras =====
  private static boolean tryInvokeStatic(String fqcn, String methodName) {
    try {
      Class<?> k = Class.forName(fqcn);
      Method m = k.getMethod(methodName);
      m.invoke(null);
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  private static void tryInvokeStatic(String fqcn, String methodName, Class<?>... sig) {
    try {
      Class<?> k = Class.forName(fqcn);
      Method m = k.getMethod(methodName, sig);
      m.invoke(null);
    } catch (Throwable t) {
      // ignore
    }
  }

  // (Opcional) Para imprimir tokens CUP si activas el print en runJFlex()
  @SuppressWarnings("unused")
  private static String nameOf(int id) {
    switch (id) {
      case sym.PROGRAM: return "PROGRAM"; case sym.VAR: return "VAR"; case sym.BEGIN: return "BEGIN";
      case sym.END: return "END"; case sym.IF: return "IF"; case sym.THEN: return "THEN"; case sym.ELSE: return "ELSE";
      case sym.WHILE: return "WHILE"; case sym.DO: return "DO"; case sym.FOR: return "FOR"; case sym.TO: return "TO";
      case sym.FUNCTION: return "FUNCTION"; case sym.PROCEDURE: return "PROCEDURE";
      case sym.READ: return "READ"; case sym.WRITE: return "WRITE";
      case sym.INT: return "INT"; case sym.CHAR: return "CHAR"; case sym.REAL: return "REAL"; case sym.STRING: return "STRING";
      case sym.AND: return "AND"; case sym.OR: return "OR"; case sym.NOT: return "NOT";
      case sym.EQ: return "EQ"; case sym.GE: return "GE"; case sym.GT: return "GT"; case sym.LE: return "LE"; case sym.LT: return "LT"; case sym.NE: return "NE";
      case sym.ASSIGN: return "ASSIGN"; case sym.INCR: return "INCR"; case sym.DECR: return "DECR";
      case sym.PLUS: return "PLUS"; case sym.MINUS: return "MINUS"; case sym.TIMES: return "TIMES"; case sym.DIVIDE: return "DIVIDE";
      case sym.DIV_KW: return "DIV_KW"; case sym.MOD_KW: return "MOD_KW";
      case sym.LPAREN: return "LPAREN"; case sym.RPAREN: return "RPAREN"; case sym.LBRACK: return "LBRACK"; case sym.RBRACK: return "RBRACK";
      case sym.COMMA: return "COMMA"; case sym.SEMI: return "SEMI"; case sym.COLON: return "COLON"; case sym.DOT: return "DOT";
      case sym.IDENT: return "IDENT"; case sym.STRING_LIT: return "STRING_LIT"; case sym.CHAR_LIT: return "CHAR_LIT";
      case sym.INT_LIT: return "INT_LIT"; case sym.REAL_LIT: return "REAL_LIT";
      case sym.EOF: return "EOF";
      default: return "UNKNOWN(" + id + ")";
    }
  }
}
