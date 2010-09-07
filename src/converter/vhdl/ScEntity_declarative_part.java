package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> entity_declarative_part ::=
 *   <dd> { entity_declarative_item }
 */
class ScEntity_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEntity_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScEntity_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
