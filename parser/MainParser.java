package parser;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import scanner.Scanner;
import scanner.TokenCollector;

public class MainParser {

    
    private static final boolean SHOW_TOKENS = false; // Activar para ver también la tabla de tokens encontrados

    public static void run(String sourcePath) {
        System.out.println("=== Analizando (P2): " + sourcePath + " ===");

        // 1) Reset de colecciones
        TokenCollector.reset();
        SyntaxErrorCollector.reset();

        // 2) Construcción de scanner + parser sobre UTF-8
        try (BufferedReader br = Files.newBufferedReader(Paths.get(sourcePath), StandardCharsets.UTF_8)) {
            Scanner sc = new Scanner(br);

            Parser p = new Parser(sc);
            try {
                p.parse(); 
            } catch (Exception ex) {
                System.err.println("[ABORT] CUP con excepcion: " + ex.getMessage());
                // seguimos para imprimir reportes
            }

        } catch (Exception e) {
            System.err.println("[IO/RUN] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // 3) Reportes (siempre)
        System.out.println();
        if (SHOW_TOKENS) {
            TokenCollector.printTokens();
            System.out.println();
        }
        TokenCollector.printSummary(); // incluye conteo de errores léxicos por código
        System.out.println();
        TokenCollector.printErrors();  // detalle de errores léxicos
        System.out.println();
        SyntaxErrorCollector.print();  // detalle de errores sintácticos
        System.out.println("=== Fin del analisis P2 ===");
    }

    public static void main(String[] args) {
        String path = (args != null && args.length > 0) ? args[0] : "parser/testFile.abs";
        run(path);
    }
}
