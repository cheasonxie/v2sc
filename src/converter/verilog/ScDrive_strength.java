package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  drive_strength  <br>
 *     ::= (  strength0  ,  strength1  ) <br>
 *     ||= (  strength1  ,  strength0  ) 
 */
class ScDrive_strength extends ScVerilog {
    public ScDrive_strength(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDRIVE_STRENGTH);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
