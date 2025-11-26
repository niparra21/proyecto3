package scanner;


public enum TestFile {
    VALID_BASICS("01_valid_basics.abs", "Prueba básica con variables, literales y reservadas"),
    NUMBERS_VALID("02_numbers_valid.abs", "Números válidos (decimales, octales, hex, reales)"),
    NUMBERS_INVALID("03_numbers_invalid.abs", "Reales inválidos (.5, 5.), hex/oct malos, num+texto"),
    IDENTIFIERS_RESERVED("04_identifiers_and_reserved.abs", "Identificadores, reservadas y operador-palabra"),
    STRINGS_CHARS("05_strings_and_chars.abs", "Strings y chars válidos + errores de cierre"),
    COMMENTS_OPERATORS("06_comments_and_operators.abs", "Comentarios, operadores largos (++ -- ** <= >= <>)"),
    MIXED_EDGES("07_mixed_edge_cases.abs", "Casos límite: id largo, char inválido, punto suelto"),
    ALL_IN_ONE("08_all_in_one.abs", "Regresión: mezcla de todos los anteriores");

    private final String fileName;
    private final String description;

    TestFile(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }

    public String getFileName() { return fileName; }
    public String getDescription() { return description; }
}