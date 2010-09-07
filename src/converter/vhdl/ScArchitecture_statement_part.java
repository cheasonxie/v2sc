package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> architecture_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScArchitecture_statement_part extends ScVhdl {
    ArrayList<ScVhdl> itemList = new ArrayList<ScVhdl>();
    public ScArchitecture_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = new ScArchitecture_statement(c);
            itemList.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += intent() + itemList.get(i).scString();
            if(i < itemList.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
