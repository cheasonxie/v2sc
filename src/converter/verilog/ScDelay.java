package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  delay  <br>
 *     ::= #  number  <br>
 *     ||= #  identifier  <br>
 *     ||= # ( mintypmax_expression  [, mintypmax_expression ] [, mintypmax_expression ]) 
 */
class ScDelay extends ScVerilog {
    public ScDelay(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
