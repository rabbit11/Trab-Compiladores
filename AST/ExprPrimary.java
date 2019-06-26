package AST;

//ExprPrimary ::= Id | FuncCall | ExprLiteral

public class ExprPrimary extends Expr{
  private VarDecStat var;
  private Expr expr;
  private Type tipo;

  public ExprPrimary(VarDecStat v, Expr e) {
    this.var = v;
    this.expr = e;
    this.tipo = null;
  }

  public void genC(PW pw) {
  }

  public Type getType(){
    return this.tipo;
  }
}
