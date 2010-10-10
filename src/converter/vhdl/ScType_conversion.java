package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> type_conversion ::=
 *   <dd> type_mark ( expression )
 */
class ScType_conversion extends ScVhdl {
    ScType_mark type_mark = null;
    ScExpression expression = null;
    public ScType_conversion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_CONVERSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTYPE_MARK:
                break;
            case ASTEXPRESSION:
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String pre = type_mark.scString();
        String tmp = "";
        
        tmp = expression.scString();

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
}
