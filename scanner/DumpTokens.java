package scanner;

import java.io.FileReader;
import java_cup.runtime.Symbol;
import parser.sym;

public class DumpTokens {

  private static String nameOf(int id) {
    switch (id) {
      // Reservadas
      case sym.PROGRAM: return "PROGRAM";
      case sym.VAR: return "VAR";
      case sym.BEGIN: return "BEGIN";
      case sym.END: return "END";
      case sym.IF: return "IF";
      case sym.THEN: return "THEN";
      case sym.ELSE: return "ELSE";
      case sym.WHILE: return "WHILE";
      case sym.DO: return "DO";
      case sym.FOR: return "FOR";
      case sym.TO: return "TO";
      case sym.FUNCTION: return "FUNCTION";
      case sym.PROCEDURE: return "PROCEDURE";
      case sym.READ: return "READ";
      case sym.WRITE: return "WRITE";
      case sym.INT: return "INT";
      case sym.CHAR: return "CHAR";
      case sym.REAL: return "REAL";
      case sym.STRING: return "STRING";

      // Bool/rel
      case sym.AND: return "AND";
      case sym.OR: return "OR";
      case sym.NOT: return "NOT";
      case sym.EQ: return "EQ";
      case sym.GE: return "GE";
      case sym.GT: return "GT";
      case sym.LE: return "LE";
      case sym.LT: return "LT";
      case sym.NE: return "NE";

      // Arit/Asig
      case sym.ASSIGN: return "ASSIGN";
      case sym.INCR: return "INCR";
      case sym.DECR: return "DECR";
      case sym.PLUS: return "PLUS";
      case sym.MINUS: return "MINUS";
      case sym.TIMES: return "TIMES";
      case sym.DIVIDE: return "DIVIDE";
      case sym.DIV_KW: return "DIV_KW";
      case sym.MOD_KW: return "MOD_KW";

      // Sep
      case sym.LPAREN: return "LPAREN";
      case sym.RPAREN: return "RPAREN";
      case sym.LBRACK: return "LBRACK";
      case sym.RBRACK: return "RBRACK";
      case sym.COMMA: return "COMMA";
      case sym.SEMI: return "SEMI";
      case sym.COLON: return "COLON";
      case sym.DOT: return "DOT";

      // Léxicos
      case sym.IDENT: return "IDENT";
      case sym.STRING_LIT: return "STRING_LIT";
      case sym.CHAR_LIT: return "CHAR_LIT";
      case sym.INT_LIT: return "INT_LIT";
      case sym.REAL_LIT: return "REAL_LIT";

      case sym.EOF: return "EOF";
    }
    return "UNKNOWN(" + id + ")";
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("Uso: java scanner.DumpTokens <archivo.abs>");
      System.exit(1);
    }
    Scanner sc = new Scanner(new FileReader(args[0]));
    while (true) {
      Symbol t = sc.next_token();     // JFlex con %cup expone next_token()
      String name = nameOf(t.sym);
      Object val = t.value;
      System.out.printf("(%3d:%-3d) %-12s %s%n",
          t.left, t.right, name, (val!=null? val.toString() : ""));
      if (t.sym == sym.EOF) break;
    }

    // (Opcional) Si quieres ver el resumen de P1:
    // TokenCollector.printSummary(); // si tienes un método así
  }
}
