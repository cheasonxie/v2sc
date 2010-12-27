package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  block_declaration  <br>
 *     ::=  parameter_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  integer_declaration  <br>
 *     ||=  real_declaration  <br>
 *     ||=  time_declaration  <br>
 *     ||=  event_declaration  
 */
class ScBlock_declaration extends ScVerilog {
    public ScBlock_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_DECLARATION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
