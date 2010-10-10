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
        String pre = prefix.scString();
        String tmp = "";
        
        if(pre.equals(strVhdlType[TYPE_STD_LOGIC_VECTOR])
                  || pre.equals(strVhdlType[TYPE_STD_ULOGIC_VECTOR])) {
            tmp = exps.get(0).scString();

            if(isCommonDeclaration) {
                String[] range = null;
                //TODO: ???
                /*Symbol sym = (Symbol)parser.getSymbol(curNode, tmp);
                if(sym != null) {
                    if(sym.range != null) {
                        range = sym.range;
                    }else {
                        range = new String[3];
                        range[0] = "0";
                        range[1] = RANGE_TO;
                        try {
                            range[2] = String.format("%d", Integer.parseInt(sym.value)-1);
                        }catch(NumberFormatException e) {
                            range[2] = sym.value + "-1";
                        }
                    }
                }else*/ {
                    range = new String[3];
                    range[0] = "0";
                    range[1] = RANGE_TO;
                    range[2] = tmp + "-1";
                }
                ret = getReplaceType(pre, range);
            }else {
                ret = getReplaceType(pre, null);
                ret = encloseBracket(ret);
                ret += tmp;
            }
            return ret;
        }
        
        ret += pre;
        for(int i = 0; i < exps.size(); i++) {
            tmp += exps.get(i).scString();
            if(i < exps.size() - 1) {
                tmp += ", ";
            }
        }
        ret += encloseBracket(tmp, "[]");
        return ret;
    }
}
