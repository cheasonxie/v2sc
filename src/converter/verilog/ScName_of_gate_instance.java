package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  name_of_gate_instance  <br>
 *     ::=  IDENTIFIER [ range ] 
 */
class ScName_of_gate_instance extends ScVerilog {
    public ScName_of_gate_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME_OF_GATE_INSTANCE);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
