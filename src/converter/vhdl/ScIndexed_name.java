package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> indexed_name ::=
 *   <dd> prefix ( expression { , expression } )
 */
class ScIndexed_name extends ScVhdl {
    ScPrefix prefix = null;
    ArrayList<ScExpression> exps = new ArrayList<ScExpression>();
    public ScIndexed_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEXED_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScExpression exp = null;
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTEXPRESSION:
                exp = new ScExpression(c);
                exps.add(exp);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += prefix.scString();
        Symbol sym = (Symbol)parser.getSymbol(curNode, prefix.getNameSegments());
        if(sym != null)
            sym = (Symbol)parser.getSymbol(curNode, sym.type);
        
        if(sym != null && sym.arrayRange != null) { // has array index
            ret += "[";
        }else {
            ret += "(";
        }
        
        for(int i = 0; i < exps.size(); i++) {
            ret += exps.get(i).scString();
            if(i < exps.size() - 1) {
                ret += ", ";
            }
        }
        
        if(sym != null && sym.arrayRange != null) {
            ret += "]";
        }else {
            ret += ")";
        }
        return ret;
    }
}
