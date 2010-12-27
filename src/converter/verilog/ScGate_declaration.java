package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  gate_declaration  <br>
 *     ::=  gatetype  [ drive_strength ] [ delay ]  gate_instance  {, gate_instance } ; 
 */
class ScGate_declaration extends ScVerilog {
    public ScGate_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGATE_DECLARATION);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
