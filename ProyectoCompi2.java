import java.util.Scanner;
import parser.MainParser;
import scanner.MainScanner;

/*public class ProyectoCompi2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Analizador Léxico y Sintáctico ===");
        System.out.println("1) Probar Analizador Léxico (Scanner)");
        System.out.println("2) Probar Analizador Sintáctico (Parser)");
        System.out.print("Seleccione una opción: ");
        int opcion = input.nextInt();

        switch (opcion) {
            case 1 -> MainScanner.run();
            case 2 -> MainParser.run();
            default -> System.out.println("Opción inválida.");
        }
    }
}*/

public class ProyectoCompi2 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("=== Proyecto Compiladores e Intérpretes - Etapa 2 ===");
        System.out.println("1) Probar SCANNER (P1)");
        System.out.println("2) Probar PARSER (P2)");
        System.out.print("Opcion: ");
        int op = in.nextInt();
        in.nextLine(); // limpiar

        switch (op) {
            case 1:
                try {
                    // corre el menú del scanner
                    scanner.MainScanner.main(new String[0]);
                } catch (Exception e) {
                    System.err.println("Error ejecutando el scanner: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    // tu MainParser actual solo imprime, pero lo dejamos así
                    parser.MainParser.main(new String[0]);
                } catch (Exception e) {
                    System.err.println("Error ejecutando el parser: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            default:
                System.out.println("Opcion invalida.");
        }
    }
}