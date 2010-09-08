package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> block_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class ScBlock_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScBlock_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScBlock_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).toString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
