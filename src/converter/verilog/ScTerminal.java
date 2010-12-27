package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  terminal  <br>
 *     ::=  expression  <br>
 *     ||=  IDENTIFIER  
 */
class ScTerminal extends ScVerilog {
    public ScTerminal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERMINAL);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
