package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  gate_instance  <br>
 *     ::= [ name_of_gate_instance ] (  terminal  {, terminal } ) 
 */
class ScGate_instance extends ScVerilog {
    public ScGate_instance(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGATE_INSTANCE);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
