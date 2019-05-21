package Lexer;

import java.util.*;
import Error.*;

public class Lexer {

  public Lexer(char[] input, CompilerError error) {
    this.input = input;
    // end-of-file label
    input[input.length - 1] = '\0';
    // number of the current line
    lineNumber = 1;
    tokenPos = 0;
    this.error = error;
  }

  // contains the keywords
  static private Hashtable<String, Symbol> keywordsTable;

  // this code will be executed only once for each program execution
  static {
    keywordsTable = new Hashtable<String, Symbol>();

    // Palavras reservadas
    keywordsTable.put("IdLiteral", Symbol.IDLITERAL); //Ok
    keywordsTable.put("IntLiteral", Symbol.INTLITERAL); //Ok
    keywordsTable.put("BoolLiteral", Symbol.BOOLLITERAL); //Ok
    keywordsTable.put("StringLiteral", Symbol.STRINGLITERAL); //Ok
    keywordsTable.put("function", Symbol.FUNCTION); //Ok
    keywordsTable.put("if", Symbol.IF); //Ok
    keywordsTable.put("else", Symbol.ELSE); //Ok
    keywordsTable.put("return", Symbol.RETURN);
    keywordsTable.put("while", Symbol.WHILE); //Ok
    keywordsTable.put("var", Symbol.VAR); //Ok
    keywordsTable.put("Int", Symbol.INT); //Ok
    keywordsTable.put("Boolean", Symbol.BOOLEAN); //Ok
    keywordsTable.put("String", Symbol.STRING); //Ok
    keywordsTable.put("false", Symbol.FALSE); // Ok
    keywordsTable.put("true", Symbol.TRUE); // Ok

    // Operadores
    keywordsTable.put("and", Symbol.AND); // Ok
    keywordsTable.put("or", Symbol.OR); // Ok
    keywordsTable.put("+", Symbol.PLUS); //Ok
    keywordsTable.put("-", Symbol.MINUS); //Ok
    keywordsTable.put("*", Symbol.MULT); //Ok
    keywordsTable.put("/", Symbol.DIV); //Ok
    keywordsTable.put("==", Symbol.EQUAL); //Ok
    keywordsTable.put("<", Symbol.LT); //Ok
    keywordsTable.put("<=", Symbol.LTE); //Ok
    keywordsTable.put(">", Symbol.GT); //Ok
    keywordsTable.put(">=", Symbol.GTE); //Ok
    keywordsTable.put("->", Symbol.ARROW); // Ok
    keywordsTable.put("(", Symbol.LPAR); //Ok
    keywordsTable.put(")", Symbol.RPAR); //Ok
    keywordsTable.put("{", Symbol.LBRA); // Ok
    keywordsTable.put("}", Symbol.RBRA); //Ok
    keywordsTable.put("=", Symbol.ASSIGN); //Ok
    keywordsTable.put("!=", Symbol.DIFFERENT); //Ok
    keywordsTable.put(",", Symbol.COMMA); //Ok
    keywordsTable.put(":", Symbol.COLON); //Ok
    keywordsTable.put(";", Symbol.SEMICOLON); //Ok

    // EOF
    keywordsTable.put("eof", Symbol.EOF);
  }

  public void nextToken() {
    // Pula espaços e quebra de linhas
    while (input[tokenPos] == ' ' || input[tokenPos] == '\n' || input[tokenPos] == '\t' || input[tokenPos] == '\r') {
      if (input[tokenPos] == '\n') {
        lineNumber++;
      }
      tokenPos++;
    }

    // Verifica se chegou no fim
    if (input[tokenPos] == '\0') {
      token = Symbol.EOF;
      return;
    }

    // Verifica linhas comentadas
    if (input[tokenPos] == '-' && input[tokenPos + 1] == '-') {
      while (input[tokenPos] != '\n') {
        tokenPos++;
      }

      nextToken();
      return;
    }

    // Verifica se é um número
    String aux = new String();
    while (Character.isDigit(input[tokenPos])) {
      aux = aux + input[tokenPos];
      tokenPos++;
    }

    // Se a string aux não for vazia
    if (!aux.equals("")) {
      // Caso seja um int
      if (Character.isDigit(input[tokenPos + 1])) {
        intValue = Integer.parseInt(aux);

        // Verifica se o número é maior que o valor máximo permitido
        if (intValue > MaxValueInteger || intValue < MinValueInteger) {
          error.signal("Numero não está entre 0 e 2147483647");
        }

        token = Symbol.INTLITERAL;
      }
    }

    // Verifica se é string
    else {
      // Enquanto for letra, anda o token e armazena na string aux
      while (Character.isLetter(input[tokenPos])) {
        aux = aux + input[tokenPos];
        tokenPos++;
      }

      // Se o conteudo não for vazio, pode ser uma palavra reservada
      if (!aux.equals("")) {
        // Verificamos se o conteudo está na tabela hash
        Symbol temp = keywordsTable.get(aux);

        // Se estiver na tabela hash
        if (temp != null) {
          // O token toma o valor encontrado na tabela
          token = temp;
        }
      }

      // Pode ser stringliteral ou simbolo, a string aux aqui com certeza sera vazia
      else {
        // Se não for uma aspas, é um simbolo
        if (input[tokenPos] != '\"') {
          // Como temos apenas um simbolo reservado tem dois caracteres, adicionamos o
          // primeiro
          aux = aux + input[tokenPos];
          tokenPos++;

          // Verificando se o simbolo está na tabela hash
          Symbol temp = keywordsTable.get(aux);

          // Se ele estiver na tabela
          if (temp != null) {
            token = temp;
            stringValue = aux;
          }

          // Se ele não estiver, pode ser o simbolo ASSIGN "=", adicionamos o próximo
          // simbolo a string aux
          else {
            aux = aux + input[tokenPos];
            tokenPos++;

            // Verificando se o simbolo está na tabela hash
            temp = keywordsTable.get(aux);

            // Se ele estiver na tabela
            if (temp != null) {
              token = temp;
              stringValue = aux;
            }
          }
        }

        // Caso contrário é stringliteral
        else {
          int aspas = 0;

          // Enquanto não encontrar as duas aspas, popula a string aux
          while (aspas < 2) {
            // Se encontrar uma aspas, soma no contador
            if (input[tokenPos] == '\"') {
              aspas++;
            }

            aux = aux + input[tokenPos];
            tokenPos++;
          }

          // Valida a string aux
          if (validStringLiteral(aux)) {
            token = Symbol.STRINGLITERAL;
            stringValue = aux;
          }
        }
      }
    }

    // if (DEBUGLEXER)
    //   System.out.println(token.toString());

    lastTokenPos = tokenPos - 1;
  }

  // return the line number of the last token got with getToken()
  public int getLineNumber() {
    return lineNumber;
  }

  public String getCurrentLine() {
    int i = lastTokenPos;
    if (i == 0)
      i = 1;
    else if (i >= input.length)
      i = input.length;

    StringBuffer line = new StringBuffer();
    // go to the beginning of the line
    while (i >= 1 && input[i] != '\n')
      i--;
    if (input[i] == '\n')
      i++;
    // go to the end of the line putting it in variable line
    while (input[i] != '\0' && input[i] != '\n' && input[i] != '\r') {
      line.append(input[i]);
      i++;
    }
    return line.toString();
  }

  public String getStringValue() {
    return stringValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public char getCharValue() {
    return charValue;
  }

  private boolean containsOnlyNumbersAndDigits(String str) {
    // Se a str for vazia ou nula, retorna falso
    if (str == null || str.length() == 0) {
      return false;
    }

    for (int i = 0; i < str.length(); i++) {
      // Se a string possuir algo que não seja digito ou letra, retorna falso
      if (!Character.isLetterOrDigit(str.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  private void readFloat(String aux) {
    // Colocamos o ponto na string aux
    aux = aux + input[tokenPos];
    tokenPos++;

    // Enquanto for digito, popula a string aux
    while (Character.isDigit(input[tokenPos])) {
      aux = aux + input[tokenPos];
      tokenPos++;
    }
  }

  // A STRINGLITERAL 80, incluindo o '\0', logo a STRINGLITERAL entre as aspas,
  // tem tamanho 81 no máximo
  // Pois 80 - 1 = 79
  // 79 + as duas aspas = 81
  private boolean validStringLiteral(String str) {
    // Se começar com \", é uma string literal
    if (str.charAt(0) == '\"') {
      // Se for maior que 81, a string literal tem tamanho maior que 80
      if (str.length() > 81) {
        error.signal("String maior que 79 caracteres");
      }

      // Se o ultimo caracter não for uma '"'
      else if (str.charAt(str.length() - 1) != '\"') {
        error.signal("A string precisa estar entre aspas duplas");
      }

      return true;
    }

    return false;
  }

  // Max string size
  private int MaxStringSize = 81;

  // current token
  public Symbol token;
  private String stringValue;
  private int intValue;
  private float floatValue;
  private char charValue;

  private int tokenPos;
  // input[lastTokenPos] is the last character of the last token
  private int lastTokenPos;
  // program given as input - source code
  private char[] input;

  // number of current line. Starts with 1
  private int lineNumber;

  private CompilerError error;
  private static final int MaxValueInteger = 2147483647;
  private static final int MinValueInteger = 0;
}