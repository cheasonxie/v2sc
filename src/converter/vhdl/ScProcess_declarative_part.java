package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> process_declarative_part ::=
 *   <dd> { process_declarative_item }
 */
class ScProcess_declarative_part extends ScVhdl {
    ArrayList<ScProcess_declarative_item> items = new ArrayList<ScProcess_declarative_item>();
    public ScProcess_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScProcess_declarative_item item = new ScProcess_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).toString() + "\r\n";
        }
        return ret;
    }
}
