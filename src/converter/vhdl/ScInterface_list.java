package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;


/**
 * <dl> interface_list ::=
 *   <dd> interface_element { ; interface_element }
 */
class ScInterface_list extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScInterface_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode child = (ASTNode)node.getChild(i);
            ScVhdl item = null;
            switch(child.getId())
            {
            case ASTINTERFACE_ELEMENT:
                item = new ScInterface_element(child);
                items.add(item);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += intent() + items.get(i).scString();
            if(i < items.size() - 1) {
                ret += ";\r\n";
            }
        }
        return ret;
    }
}