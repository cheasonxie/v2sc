package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> architecture_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class ScArchitecture_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> itemList = new ArrayList<ScVhdl>();
    public ScArchitecture_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = new ScBlock_declarative_item(c);
            itemList.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).scString();
            if(i < itemList.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
