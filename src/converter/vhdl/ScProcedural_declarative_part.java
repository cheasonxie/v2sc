package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> procedural_declarative_part ::=
 *   <dd> { procedural_declarative_item }
 */
class ScProcedural_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScProcedural_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScProcedural_declarative_item(c);
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
