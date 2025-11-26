package scanner;

import java_cup.runtime.Symbol;
import parser.sym;

%%
%public
%class Scanner
%unicode
%cup
%line
%column
%ignorecase

%{
  private Symbol sym(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }
  private Symbol sym(int type, Object val) {
    return new Symbol(type, yyline+1, yycolumn+1, val);
  }
%}

/* =============================== MACROS (sin {n,m}) =============================== */

/* espacios */
ESPACIOS              = [ \t\f\r\n]+

/* básicos */
DIGITO                = [0-9]
LETRA                 = [A-Za-z]

/* identificadores (longitud se valida en la acción) */
ID_BASE               = {LETRA}({LETRA}|{DIGITO})*

/* símbolo ilegal dentro de identificador (algo no permitido entre letras/dígitos) */
SIMBOLO_ILEGAL        = [^A-Za-z0-9 \t\r\n\+\-\*\/,;\(\)\[\]:\.\^=<>\"\'\{\}]
ID_CON_SIM_ILLEGAL    = {LETRA}({LETRA}|{DIGITO})*{SIMBOLO_ILEGAL}({LETRA}|{DIGITO})*

/* comentarios no anidados */
COM_LLAVES            = \{[^}]*\}
COM_PAREST            = \(\*([^*]|\*+[^)])*\*+\)

/* números (validación fina en acciones donde aplica) */
HEX_CAND              = 0[xX][A-Za-z0-9]+
OCT_CAND              = 0[0-9]+
DEC_ENTERO            = 0|([1-9][0-9]*)
EXP                   = [eE][+-]?{DIGITO}+
REAL_PUNTO            = {DIGITO}+\.{DIGITO}+         /* exige dígito a ambos lados del punto */
REAL                  = {REAL_PUNTO}({EXP})?
NUM_DEC_E             = {DEC_ENTERO}{EXP}            /* 3e2, 5E+7 */

/* errores específicos P1 para reales con punto suelto */
ERROR_REAL_PUNTO_LIDER = \.{DIGITO}+({EXP})?
ERROR_REAL_PUNTO_COLA  = {DIGITO}+\.({EXP})?

/* número seguido de cola de id: 123abc, 0xZ1k, 0789x, 1.2e+3foo, 3e2foo */
ID_TAIL               = {LETRA}({LETRA}|{DIGITO})*
NUM_SEGUIDO_TEXTO     = ({HEX_CAND}|{OCT_CAND}|{REAL}|{NUM_DEC_E}|{DEC_ENTERO}){ID_TAIL}

/* literales texto (no multilínea) */
STRING                = \"([^\\\"\n]|\\.)*\"
CHAR                  = \'([^\\\'\n]|\\.)\'

/* incompletos (hasta fin de línea) */
STRING_INCOMP         = \"[^\"\r\n]*
CHAR_INCOMP           = \'[^\'\r\n]*

/* palabras operadoras del parser */
OPER_PALABRA_PARSER   = AND|OR|NOT|DIV|MOD
/* operadoras extra P1 (si las tenías), CUP las verá como IDENT */
OPER_PALABRA_EXTRA    = IN|SHL|SHR

/* reservadas del parser */
RES_PARSER = PROGRAM|VAR|BEGIN|END|IF|THEN|ELSE|WHILE|DO|FOR|TO|FUNCTION|PROCEDURE|READ|WRITE|INT|CHAR|REAL|STRING

/* reservadas extra de P1 (no usadas por el parser; se reportan como reservada y se devuelven IDENT) */
RES_P1_EXTRA = ABSOLUTE|ARRAY|ASM|CASE|CONST|CONSTRUCTOR|DESTRUCTOR|EXTERNAL|DOWNTO|EXIT|FILE|FORWARD|GOTO|INLINE|INTERFACE|LABEL|NIL|OBJECT|OF|PACKED|PRIVATE|PROTECTED|PUBLIC|PUBLISHED|RECORD|REPEAT|SET|TYPE|UNIT|UNTIL|USES|WITH

%%

/* =============================== REGLAS (ordenado) =============================== */

/* 1) Espacios y comentarios */
{ESPACIOS}       { /* skip */ }
{COM_LLAVES}     { /* skip */ }
{COM_PAREST}     { /* skip */ }

/* 2) Guardas de error de reales con punto suelto (P1) */
{ERROR_REAL_PUNTO_LIDER}  { TokenCollector.addError("ERROR_REAL_PUNTO_LIDER", yytext(), yyline+1); }
{ERROR_REAL_PUNTO_COLA}   { TokenCollector.addError("ERROR_REAL_PUNTO_COLA",  yytext(), yyline+1); }

/* 3) NÚMEROS VÁLIDOS (en este orden) */
/* 3.1) Real con punto y opcional exponente */
{REAL} {
  TokenCollector.add("LITERAL_REAL", yytext(), yyline+1);
  return sym(sym.REAL_LIT, yytext());
}
/* 3.2) Entero con exponente (sin punto): 3e2, 5E+7 */
{NUM_DEC_E} {
  TokenCollector.add("LITERAL_REAL", yytext(), yyline+1);
  return sym(sym.REAL_LIT, yytext());
}
/* 3.3) Hex y Oct candidatos con validación en acción */
{HEX_CAND} {
  String s = yytext();
  if (s.matches("0[xX][0-9A-Fa-f]+")) {
    TokenCollector.add("LITERAL_HEX", s, yyline+1);
    return sym(sym.INT_LIT, s);
  } else {
    TokenCollector.addError("ERROR_HEX_INVALIDO", s, yyline+1);
  }
}
{OCT_CAND} {
  String s = yytext();
  if (s.matches("0[0-7]+")) {
    TokenCollector.add("LITERAL_OCTAL", s, yyline+1);
    return sym(sym.INT_LIT, s);
  } else {
    TokenCollector.addError("ERROR_OCTAL_INVALIDO", s, yyline+1);
  }
}
/* 3.4) Entero decimal */
{DEC_ENTERO} {
  TokenCollector.add("LITERAL_ENTERO", yytext(), yyline+1);
  return sym(sym.INT_LIT, yytext());
}

/* 4) Número seguido de texto (ERROR) */
{NUM_SEGUIDO_TEXTO} {
  TokenCollector.addError("ERROR_NUMERO_SEGUIDO_DE_TEXTO", yytext(), yyline+1);
}

/* 5) Strings / Chars válidos */
{STRING} {
  TokenCollector.add("LITERAL_STRING", yytext(), yyline+1);
  return sym(sym.STRING_LIT, yytext());
}
{CHAR} {
  TokenCollector.add("LITERAL_CHAR", yytext(), yyline+1);
  return sym(sym.CHAR_LIT, yytext());
}

/* 6) Incompletos (cuando llega fin de línea) */
{STRING_INCOMP} / \r?\n { TokenCollector.addError("ERROR_STRING_SIN_CIERRE", yytext(), yyline+1); }
{CHAR_INCOMP}   / \r?\n { TokenCollector.addError("ERROR_CHAR_SIN_CIERRE",   yytext(), yyline+1); }

/* 7) Identificador con símbolo ilegal en medio (ERROR) */
{ID_CON_SIM_ILLEGAL} {
  TokenCollector.addError("ERROR_IDENTIFICADOR_SIMBOLO_ILEGAL", yytext(), yyline+1);
}

/* 8) Palabras operadoras del parser */
"AND" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.AND); }
"OR"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.OR); }
"NOT" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.NOT); }
"DIV" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.DIV_KW); }
"MOD" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.MOD_KW); }

/* 9) Palabras operadoras extra de P1 (CUP las verá como IDENT) */
{OPER_PALABRA_EXTRA} {
  TokenCollector.add("OPERADOR", yytext(), yyline+1);
  return sym(sym.IDENT, yytext());
}

/* 10) Reservadas del parser (todas antes que IDENT) */
"PROGRAM"   { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.PROGRAM); }
"VAR"       { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.VAR); }
"BEGIN"     { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.BEGIN); }
"END"       { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.END); }
"IF"        { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.IF); }
"THEN"      { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.THEN); }
"ELSE"      { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.ELSE); }
"WHILE"     { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.WHILE); }
"DO"        { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.DO); }
"FOR"       { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.FOR); }
"TO"        { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.TO); }
"FUNCTION"  { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.FUNCTION); }
"PROCEDURE" { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.PROCEDURE); }
"READ"      { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.READ); }
"WRITE"     { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.WRITE); }
"INT"       { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.INT); }
"CHAR"      { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.CHAR); }
"REAL"      { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.REAL); }
"STRING"    { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.STRING); }
"RETURN"    { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); return sym(sym.RETURN); }

/* 11) Reservadas extra de P1 → IDENT para CUP (pero se registran como reservada) */
{RES_P1_EXTRA} {
  TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1);
  return sym(sym.IDENT, yytext());
}

/* 12) Operadores y separadores SIMBÓLICOS (largos → cortos) */
":=" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.ASSIGN); }
"++" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.INCR); }
"--" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.DECR); }
"**" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.IDENT, yytext()); }  /* reporta pero CUP lo ignora */

">=" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.GE); }
"<=" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.LE); }
"<>" { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.NE); }

"^"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.CARET); }
"+"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.PLUS); }
"-"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.MINUS); }
"*"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.TIMES); }
"/"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.DIVIDE); }
"="  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.EQ); }
">"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.GT); }
"<"  { TokenCollector.add("OPERADOR", yytext(), yyline+1); return sym(sym.LT); }

"("  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.LPAREN); }
")"  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.RPAREN); }
"["  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.LBRACK); }
"]"  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.RBRACK); }
","  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.COMMA); }
";"  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.SEMI); }
":"  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.COLON); }
"."  { TokenCollector.add("SEPARADOR", yytext(), yyline+1); return sym(sym.DOT); }

/* 13) Identificador válido (validación de longitud aquí) */
{ID_BASE} {
  String s = yytext();
  if (s.length() > 127) {
    TokenCollector.addError("ERROR_IDENTIFICADOR_LONGITUD", s, yyline+1);
    return sym(sym.IDENT, s);  // sigue el flujo
  } else {
    TokenCollector.add("IDENTIFICADOR", s, yyline+1);
    return sym(sym.IDENT, s);
  }
}

/* 14) Catch-all y EOF */
.      { TokenCollector.addError("ERROR_LEXICO", yytext(), yyline+1); }
<<EOF>>{ return sym(sym.EOF); }
