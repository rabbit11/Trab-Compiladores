//     Nome                    RA
    // Bruno Asti Baradel      726499
    // Pablo Laranjo           726577
    // Pedro Coelho            743585
    // Vinícius Crepschi       743601

package AST;
import java.util.ArrayList;

import Lexer.*;

//ExprRel ::= ExprAdd [ RelOp ExprAdd ]

public class ExprRel extends Expr {

    private Expr esq, dir;
    private Symbol op;
    private Type tipo;

    public ExprRel(Expr esq, Expr dir, Symbol op, Type tipo){
        this.esq = esq;
        this.dir = dir;
        this.op = op;
        this.tipo = tipo;
    }

    @Override
    public void genC(PW pw){
        esq.genC(pw);

        if(op == Symbol.EQUAL || op == Symbol.DIFFERENT || op == Symbol.LTE
            || op == Symbol.LT || op == Symbol.GTE || op == Symbol.GT) {
            
            pw.printNI(" " + this.op.toString() + " ");
            
            dir.genC(pw);
        }
    }

    public void setOp(Symbol op) {
        this.op = op;
    }

    public void setEsq(Expr esq){
        this.esq = esq;
    }

    public void setDir(Expr dir) {
        this.dir = dir;
    }

    public void setType(Type tipo){
        this.tipo = tipo;
    }

    public Expr getEsq(){
        return this.esq;
    }

    public Expr getDir() {
        return this.dir;
    }

    public Type getType(){
        return this.tipo;
    }

    public Symbol getOperador() {
        return this.op;
    }
}
