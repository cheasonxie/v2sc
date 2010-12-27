package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  specify_item  <br>
 *     ::=  specparam_declaration  <br>
 *     ||=  path_declaration  <br>
 *     ||=  level_sensitive_path_declaration  <br>
 *     ||=  edge_sensitive_path_declaration  <br>
 *     ||=  system_timing_check  <br>
 *     ||=  sdpd  
 */
class ScSpecify_item extends ScVerilog {
    public ScSpecify_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSPECIFY_ITEM);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
