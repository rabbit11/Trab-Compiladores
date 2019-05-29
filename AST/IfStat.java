package AST;

import java.util.ArrayList;

//IfStat ::= "if" Expr StatList [ "else" StatList ]

public class IfStat extends Stat {
    private Expr expr;
    private StatList ifBody;
    private StatList elseStat;

    public IfStat( Expr expr, StatList ifBody, StatList elseStat ) {
        this.expr = expr;
        this.ifBody = ifBody;
        this.elseStat = elseStat;
    }

    @Override
    public void genC( PW pw ) {
        pw.print("if ( ");
        expr.genC(pw);
        pw.out.println(" ) { ");
        pw.println("}");

        if ( elseStat != null ) {
            pw.println("else {");
            pw.add();
            elseStat.genC(pw);
            pw.sub();
            pw.println("}");
        }
    }
}
