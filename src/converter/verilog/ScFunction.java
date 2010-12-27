package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  function  <br>
 *     ::= <b>function</b> [ range_or_type ]  name_of_function  ; <br>
 *         { tf_declaration }+ <br>
 *          statement  <br>
 *         <b>endfunction</b> 
 */
class ScFunction extends ScVerilog {
    public ScFunction(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
