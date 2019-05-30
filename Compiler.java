//     Nome                    RA
// Bruno Asti Baradel      726499
// Pablo Laranjo           726577
// Pedro Coelho            743585
// Vinícius Crepschi       743601

/*
    comp7

private void error(String errorMsg) => Error Message

Class Hashtable has methods
    Object put(Object key, Object value)
    Object get(Object key)

Method put inserts value at the table using key as its name (or key!). It returns the table object that had key key. If there were none, it returns null:

  Hashtable h = new Hashtable();
  h.put( "one", new Integer(1) );
  h.put( "two", new Integer(2) );
  Object obj = h.put("one", "I am the one");
  System.out.println(obj);
  // prints 1
  obj = h.get("two");
  System.out.println(obj);
  // prints 2
  System.out.println( h.get("one") ); // prints "I am the one"
  char name = ’a’;
  h.put( name + "", "This is an a");
  System.out.println( h.get("a") );

In the last call to put, we used name + "" as the key. We could not have used just name because name is not an object.

    This compiler uses the same grammar as compiler 6:
       Program ::= VarDecList ':' Expr
       VarDecList ::=  | VarDec VarDecList
       VarDec ::= Letter '=' Number
       Expr::=  '(' oper Expr  Expr ')' | Number | Letter
       Oper ::= '+' | '-' | '*' | '/'
       Number ::= '0'| '1' | ... | '9'
       Letter ::= 'A' | 'B'| ... | 'Z'| 'a'| 'b' | ... | 'z'

     The compiler evaluate the expression

*/

import AST.*;
import Lexer.*;
import java.util.ArrayList;
// import java.util.Hashtable;

public class Compiler {
  private Lexer lexer;
  private SymbolTable table;

  public Program compile(char[] p_input) {
    //error = new CompilerError(null); //COMENTADO PARA COMPILAR
    lexer = new Lexer(p_input);
    table = new SymbolTable();
    // error.setLexer(lexer);

    input = p_input;
    tokenPos = 0;

    // symbolTable = new Hashtable();

    lexer.nextToken();

    Program e = program();
    if (tokenPos != input.length)
      error("Fim de codigo esperado!");

    return e;
  }

  // Program ::= Func {Func}
  private Program program() {
    Func esq = func();
    Func dir = func();

    ArrayList<Func> f = new ArrayList<Func>();
    f.add(esq);
    f.add(dir);

    return new Program(f);
  }

  private void RelOp() {
    if (lexer.token == Symbol.EQUAL || lexer.token == Symbol.LT || lexer.token == Symbol.LTE || lexer.token == Symbol.GT
        || lexer.token == Symbol.GTE) {

    } else {
      System.out.println("Operador não reconhecido");
    }
    lexer.nextToken();
  }

  // ReturnStat ::= "return" Expr ";"
  public ReturnStat ReturnStat() {

    // if (lexer.token != Symbol.RETURN) {
    //   System.out.println("Expected 'Return' but found '" + lexer.getStringValue() + "'");
    // }

    lexer.nextToken();
    Expr expr = expr();


    if (lexer.token != Symbol.SEMICOLON) {
      System.out.println("Expected ';' but found '" + lexer.getStringValue() + "'");
    }

    // lexer.nextToken();
    return new ReturnStat(expr);
  }

  //Stat ::= AssignExprStat | ReturnStat | VarDecStat | IfStat | WhileStat
  private Stat stat() {
    switch (lexer.token) {
    case IDLITERAL:
      assignExprStat();
    case VAR:
      varDecStat();
    case IF:
      ifStat();
    case WHILE:
      whileStat();
    default:
      // will never be executed
      System.out.println("Statement expected");
    }
    return null;
  }

  // StatList ::= "{"{Stat}"}"
  private StatList statList() {
    ArrayList<Stat> v = new ArrayList<Stat>();
    // statements always begin with an identifier, if
    // while (lexer.token == Symbol.IDLITERAL || lexer.token == Symbol.IF) {
    // //NÃO EXISTE SIMBOLOS ID
    //   v.add(stat());
    //   if (lexer.token != Symbol.SEMICOLON)
    //     System.out.println("; expected");
    //   lexer.nextToken();
    // }
    if(lexer.token != Symbol.LBRA){
      System.out.println("Esperado {");
    }
    stat();

    if(lexer.token != Symbol.RBRA){
      System.out.println("Esperado }");
    }

    return new StatList(v);
  }

  //IfStat ::= "if" Expr StatList [ "else" StatList ]
  private IfStat ifStat() {

    if (lexer.token != Symbol.IF) {
      System.out.println("'if' expected");
    }

    lexer.nextToken();

    Expr e = expr();

    lexer.nextToken();

    // devemos checar pela abertura e fechamento de chaves?

    if (lexer.token != Symbol.LBRA) {
      System.out.println("{ expected");
    }

    StatList ifPart = statList();
    StatList elsePart = null;

    if (lexer.token != Symbol.RBRA) {
      System.out.println("} expected");
    }

    lexer.nextToken();

    if (lexer.token == Symbol.ELSE) {

      if (lexer.token != Symbol.LBRA) {
        System.out.println("{ expected");
      }

      lexer.nextToken();
      elsePart = statList();
      lexer.nextToken();

      if (lexer.token != Symbol.RBRA) {
        System.out.println("} expected");
      }
    }

    lexer.nextToken(); // não sei se precisa

    return new IfStat(e, ifPart, elsePart);

  }

  // Type::="Int"|"Boolean"|"String"
  private Type type() {
    Type result;

    if(lexer.token == Symbol.INT){
      System.out.println("int");
    }
    else if(lexer.token == Symbol.BOOLEAN){
      System.out.println("boolean");
    }
    else if(lexer.token == Symbol.STRING){
      System.out.println("String");
    }
    else{
      System.out.println("Tipo não reconhecido");
      return null;
    }

    result = new Type(lexer.token);
    lexer.nextToken();
    return result;
  }

  private VarDecStat varDecStat() {

    if (lexer.token != Symbol.VAR)// ta certo isso?
      System.out.println("Identifier expected");

    lexer.nextToken(); // ta certo isso?

    Type typeVar = type();

    // name of the identifier
    String name = lexer.getStringValue();

    lexer.nextToken();

    if (lexer.token != Symbol.SEMICOLON) {
      System.out.println("; expected");
    }

    lexer.nextToken();

    VarDecStat v = new VarDecStat(name, typeVar);
    return v;
  }

  //WhileStat ::= "while" Expr StatList
  private WhileStat whileStat() {
    if (lexer.token != Symbol.WHILE) {
      System.out.println("'while' expected");
    }

    lexer.nextToken();

    Expr e = expr();

    lexer.nextToken();

    StatList whilePart = statList();

    lexer.nextToken();

    return new WhileStat(whilePart, e);

  }

  public boolean literalBoolean() {

    if (lexer.token == Symbol.BOOLLITERAL) {
      boolean boo = lexer.getBoolValue();
      lexer.nextToken();
      return boo;
    } else {
      System.out.println("Error in the boolean type");
    }

    return false;
  }

  private ParamList paramList() {
    // ParamList ::= ParamDec { ’,’ ParamDec }
   ParamDec p = paramDec();

   ArrayList<ParamDec> pList = new ArrayList<ParamDec>();
   ParamList paramList = new ParamList(pList);
   pList.add(p);

   while (lexer.token == Symbol.COMMA) {
      lexer.nextToken();
      p = paramDec();
      pList.add(p);
   }

   paramList.setArrayParam(pList);
   return paramList;
  }

  private ParamDec paramDec() {
    // ParamDec ::= Id ":" Type
    if (lexer.token != Symbol.IDLITERAL) {
      // error.signal("Identifier expected");
      System.out.println("identifier expected");
    }
    // name of the identifier
    String id = (String) lexer.getStringValue();
    lexer.nextToken();

    if (lexer.token != Symbol.COLON) { // :
      System.out.println(": expected");
      // error.show(": expected");
    } else {
      lexer.nextToken();
    }
    // get the type
    Type typeVar = type();
    ParamDec param = new ParamDec(id, typeVar);

    return param;
  }

  private void error(String errorMsg) {
    if (tokenPos == 0)
      tokenPos = 1;
    else if (tokenPos >= input.length)
      tokenPos = input.length;

    String strInput = new String(input, tokenPos - 1, input.length - tokenPos + 1);
    String strError = "Error at \"" + strInput + "\"";
    System.out.println(strError);
    System.out.println(errorMsg);
    throw new RuntimeException(strError);
  }

  // ExprAnd::= ExprRel {”and”ExprRel}
  private Expr exprAnd() {
    Expr esq, dir;
    esq = exprRel();

    if (lexer.token == Symbol.AND) {
      lexer.nextToken();
      dir = exprRel();
      esq = new ExprAnd(esq, Symbol.AND, dir);
    } else
      System.out.println("'and' expected");

    return esq;
  }

// ExprLiteral ::= LiteralInt | LiteralBoolean | LiteralString
public Expr exprLiteral() {
    Symbol op = lexer.token;

    if(op==Symbol.INTLITERAL)
      lexer.nextToken();
    else System.out.println("Expected type 'int'");

    if(op==Symbol.BOOLLITERAL)
        lexer.nextToken();
    else System.out.println("Expected type 'boolean'");

    if(op == Symbol.STRINGLITERAL)
      lexer.nextToken();
    else System.out.println("Expected type 'string'");

   return new ExprLiteral(op);
}

  // Expr ::= ExprAnd {”or”ExprAnd}
  public Expr expr() {
    Expr esq, dir;
    esq = exprAnd();
    Symbol op;

    while ((op = lexer.token) == Symbol.OR) {
      lexer.nextToken();
      dir = exprAnd();
    }
    return esq;
  }

  //ExprMult ::= ExprUnary {(” ∗ ” | ”/”)ExprUnary}
  private Expr exprMult() {
    Symbol op;
    Expr esq, dir;
    esq = exprUnary();

    while ((op = lexer.token) == Symbol.MULT || op == Symbol.DIV) {
      lexer.nextToken();
      dir = exprUnary();
      esq = new ExprMult(esq, op, dir);
    }
    return esq;
  }

  // ExprAdd ::= ExprMult {(” + ” | ” − ”)ExprMult}
  private Expr exprAdd() {
    Symbol op;
    Expr esq, dir;
    esq = exprMult();

    while ((op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS) {
      lexer.nextToken();
      dir = exprMult();
      esq = new ExprAdd(esq, op, dir);
    }
    return esq;
  }

  // AssignExprStat ::= Expr [ "=" Expr ] ";"
  public Expr assignExprStat() {
    Expr esq, dir;
    esq = expr();

    if (lexer.token == Symbol.ASSIGN) {
      lexer.nextToken();
      dir = expr();
      lexer.nextToken();
      if (lexer.token == Symbol.SEMICOLON)
        lexer.nextToken();
    }

    else
      error("simbolo incorreto");

    return esq;
  }

  private Expr exprRel() {
    // ExprRel ::= ExprAdd [ RelOp ExprAdd ]
    Expr left, right;
    left = exprAdd();
    Symbol op = lexer.token;
    if (op == Symbol.EQUAL || op == Symbol.DIFFERENT || op == Symbol.LTE || op == Symbol.LT || op == Symbol.GTE
        || op == Symbol.GT) {
      lexer.nextToken();
      right = exprAdd();

      left = new ExprRel(left, op, right);
    }
    return left;
  }

  private Expr exprUnary() {
    // ExprUnary ::= [ ( "+" | "-" ) ] ExprPrimary
    Symbol op = lexer.token;
    if (op == Symbol.PLUS || op == Symbol.MINUS)
      lexer.nextToken();

    Expr e = exprPrimary();

    return new ExprUnary(e, op);
  }

   // ExprPrimary ::= Id | FuncCall | ExprLiteral
   private Expr exprPrimary() {
      if(lexer.token == Symbol.IDLITERAL){
         lexer.nextToken();
      }
      else if(lexer.token == Symbol.STRING)
         return funcCall();

      return exprLiteral();
  }

  private Func func() {
    // Func ::= "function" Id [ "(" ParamList ")" ] ["->" Type ] StatList
    if (lexer.token != Symbol.FUNCTION) {
      // should never occur
      System.out.println("Internal compiler error");
      return null;
    }
    lexer.nextToken();

    if (lexer.token != Symbol.IDLITERAL)
      System.out.println("Identifier expected");

    String name = (String) lexer.getStringValue();

    lexer.nextToken();

    ParamList p = null;
    if (lexer.token == Symbol.LPAR) {
      lexer.nextToken();
      p = paramList();
      if (lexer.token != Symbol.RPAR) {
        System.out.println(") expected");
      } else
        lexer.nextToken();
    }

    Type t = null;
    if (lexer.token == Symbol.ARROW) {
      lexer.nextToken();
      t = type();
    }

    statList();

    return new Func(t.getType(), name, p);
  }

  private Expr funcCall() {
    // FuncCall ::= Id "(" [ Expr {”, ”Expr} ] ")"
    if (lexer.token != Symbol.IDLITERAL)
      System.out.println("Identifier expected");

    lexer.nextToken();
    String name = (String) lexer.getStringValue();

    ArrayList<Expr> eList = new ArrayList<Expr>();

    if (lexer.token != Symbol.LPAR) {
      System.out.println("( expected");
    } else
      lexer.nextToken();
    if (lexer.token != Symbol.RPAR) {
      Expr e = expr();
      eList.add(e);
      lexer.nextToken();
      while (lexer.token == Symbol.COMMA) {
        lexer.nextToken(); // space
        e = expr();
        eList.add(e);
      }

      lexer.nextToken();
      if (lexer.token != Symbol.RPAR) {
        System.out.println(") expected");
      }
    }
    return new FuncCall(name, eList);
  }

  private char token;
  private int tokenPos;
  private char[] input;
  // private Hashtable<Character, VarDecStat> symbolTable;
}
