package converter.vhdl;

import java.util.ArrayList;
import parser.IParser;
import parser.vhdl.ASTNode;


/**
 * <dl> design_file ::=
 *   <dd> design_unit { design_unit }
 */
class ScDesign_file extends ScVhdl {
    ArrayList<ScVhdl> design_units = new ArrayList<ScVhdl>(); 
    public ScDesign_file(IParser parser) {
        super(parser);
        units = new ArrayList<ScCommonIdentifier>();
        assert(curNode.getId() == ASTDESIGN_FILE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            assert(c.getId() == ASTDESIGN_UNIT);
            ScVhdl dunit = new ScDesign_unit(c);
            design_units.add(dunit);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < design_units.size(); i++) {
            ret += design_units.get(i).scString();
            if(i < design_units.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}
