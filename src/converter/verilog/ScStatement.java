package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  statement  <br>
 *     ::= blocking_assignment  ; <br>
 *     ||=  non_blocking_assignment  ; <br>
 *     ||= <b>if</b> (  expression  )  statement_or_null  <br>
 *     ||= <b>if</b> (  expression  )  statement_or_null  <b>else</b>  statement_or_null  <br>
 *     ||= <b>case</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>casez</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>casex</b> (  expression  ) { case_item }+ <b>endcase</b> <br>
 *     ||= <b>forever</b>  statement  <br>
 *     ||= <b>repeat</b> (  expression  )  statement  <br>
 *     ||= <b>while</b> (  expression  )  statement  <br>
 *     ||= <b>for</b> (  assignment  ;  expression  ;  assignment  )  statement  <br>
 *     ||=  delay_or_event_control   statement_or_null  <br>
 *     ||= <b>wait</b> (  expression  )  statement_or_null  <br>
 *     ||= ->  name_of_event  ; <br>
 *     ||=  seq_block  <br>
 *     ||=  par_block  <br>
 *     ||=  task_enable  <br>
 *     ||=  system_task_enable  <br>
 *     ||= <b>disable</b>  name_of_task  ; <br>
 *     ||= <b>disable</b>  name_of_block  ; <br>
 *     ||= <b>assign</b>  assignment  ; <br>
 *     ||= <b>deassign</b>  lvalue  ; <br>
 *     ||= <b>force</b>  assignment  ; <br>
 *     ||= <b>release</b>  lvalue  ; 
 */
class ScStatement extends ScVerilog {
    public ScStatement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTATEMENT);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
