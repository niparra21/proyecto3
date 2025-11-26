package scanner;

import java.util.*;

/**
 * Colector simple para P1 y P2.
 * - Mantiene todos los tokens y errores que registra el .flex (add / addError).
 * - Expone métodos para limpiar e imprimir reportes (printSummary / printTokens / printErrors).
 */
public final class TokenCollector {

    // === Modelos internos ===
    public static final class TokenEntry {
        public final String tipo;   // p.ej. IDENTIFICADOR, LITERAL_ENTERO, OPERADOR, PALABRA_RESERVADA...
        public final String lexema; // tal cual llega del .flex
        public final int linea;     // 1-based
        public TokenEntry(String tipo, String lexema, int linea) {
        this.tipo = tipo; this.lexema = lexema; this.linea = linea;
        }
    }
    public static final class ErrorEntry {
        public final String codigo; // p.ej. ERROR_HEX_INVALIDO
        public final String lexema;
        public final int linea;
        public ErrorEntry(String codigo, String lexema, int linea) {
        this.codigo = codigo; this.lexema = lexema; this.linea = linea;
        }
    }

    // === Almacenamiento ===
    private static final List<TokenEntry> TOKENS = new ArrayList<>();
    private static final List<ErrorEntry> ERRORES = new ArrayList<>();

    // === API llamada por el .flex ===
    public static void add(String tipo, String lexema, int linea) {
        TOKENS.add(new TokenEntry(tipo, lexema, linea));
    }
    public static void addError(String codigo, String lexema, int linea) {
        ERRORES.add(new ErrorEntry(codigo, lexema, linea));
    }

    // === Utilidades para el runner ===
    public static void reset() {
        TOKENS.clear();
        ERRORES.clear();
    }

  /** Resumen corto: #tokens por tipo + #errores por código. */
    public static void printSummary() {
        System.out.println("=== RESUMEN ===");

        Map<String, Integer> porTipo = new LinkedHashMap<>();
        for (TokenEntry t : TOKENS) porTipo.merge(t.tipo, 1, Integer::sum);
        if (porTipo.isEmpty()) {
        System.out.println("(sin tokens)");
        } else {
        System.out.println("-- TOKENS POR TIPO --");
        porTipo.forEach((k,v)-> System.out.printf("%-20s : %d%n", k, v));
        }

        if (ERRORES.isEmpty()) {
        System.out.println("\n-- ERRORES LEXICOS --\n(none)");
        } else {
        System.out.println("\n-- ERRORES LEXICOS --");
        Map<String, Integer> porCodigo = new LinkedHashMap<>();
        for (ErrorEntry e : ERRORES) porCodigo.merge(e.codigo, 1, Integer::sum);
        porCodigo.forEach((k,v)-> System.out.printf("%-30s : %d%n", k, v));
        }
    }

  /** Tabla estilo P1: Token | Tipo de Token | Línea(s). Agrupa por (lexema,tipo). */
    public static void printTokens() {
    System.out.println("=== TOKENS ENCONTRADOS ===");
        if (TOKENS.isEmpty()) {
            System.out.println("(ninguno)");
            return;
        }

        // Agrupa por (lexema, tipo) y acumula conteos por línea
        Map<String, Group> mapa = new LinkedHashMap<>();
        for (TokenEntry t : TOKENS) {
            String key = t.lexema + "\u0001" + t.tipo;
            mapa.computeIfAbsent(key, k -> new Group(t.lexema, t.tipo))
                .addLinea(t.linea);
        }

        // Cabecera
        System.out.printf("%-16s %-18s %s%n", "Token", "Tipo de Token", "Línea(s)");
        for (Group g : mapa.values()) {
            System.out.printf("%-16s %-18s %s%n", g.lexema, g.tipo, g.lineasDetalladas());
        }
    }
  /** Lista de errores con línea y lexema. */
    public static void printErrors() {
        System.out.println("=== ERRORES LEXICOS ===");
        if (ERRORES.isEmpty()) {
            System.out.println("(ninguno)");
            return;
        }
        System.out.printf("%-30s %-8s %s%n", "Codigo", "Linea", "Lexema");
        for (ErrorEntry e : ERRORES) {
            System.out.printf("%-30s %-8d %s%n", e.codigo, e.linea, e.lexema);
        }
    }

  // === Clase auxiliar para agrupación ===
    private static final class Group {
        final String lexema;
        final String tipo;
        // línea -> veces en esa línea
        final java.util.TreeMap<Integer, Integer> porLinea = new java.util.TreeMap<>();

        Group(String lexema, String tipo) {
            this.lexema = lexema;
            this.tipo = tipo;
        }

        void addLinea(int linea) {
            porLinea.merge(linea, 1, Integer::sum);
        }

        // Construye:  "1, 3, 4(2), 8(4)"  (solo pone (n) si n>1)
        String lineasDetalladas() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (java.util.Map.Entry<Integer, Integer> e : porLinea.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            int linea = e.getKey();
            int count = e.getValue();
            if (count == 1) sb.append(linea);
            else sb.append(linea).append("(").append(count).append(")");
            }
            return sb.toString();
        }
    }

}
