package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  combinational_entry  <br>
 *     ::=  level_input_list  :  output_symbol  ; 
 */
class ScCombinational_entry extends ScVerilog {
    public ScCombinational_entry(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMBINATIONAL_ENTRY);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
