package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> disconnection_specification ::=
 *   <dd> <b>disconnect</b> guarded_signal_specification <b>after</b> <i>time_</i>expression ;
 */
class ScDisconnection_specification extends ScVhdl {
    public ScDisconnection_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDISCONNECTION_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}
