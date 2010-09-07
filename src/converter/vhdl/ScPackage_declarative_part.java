package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> package_declarative_part ::=
 *   <dd> { package_declarative_item }
 */
class ScPackage_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScPackage_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScPackage_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
