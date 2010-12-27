package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  task  <br>
 *     ::= <b>task</b>  name_of_task  ; <br>
 *         { tf_declaration } <br>
 *          statement_or_null  <br>
 *         <b>endtask</b> 
 */
class ScTask extends ScVerilog {
    public ScTask(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTASK);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
