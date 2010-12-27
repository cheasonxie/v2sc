package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  module_item  <br>
 *     ::=  parameter_declaration  <br>
 *     ||=  input_declaration  <br>
 *     ||=  output_declaration  <br>
 *     ||=  inout_declaration  <br>
 *     ||=  net_declaration  <br>
 *     ||=  reg_declaration  <br>
 *     ||=  time_declaration  <br>
 *     ||=  integer_declaration  <br>
 *     ||=  real_declaration  <br>
 *     ||=  event_declaration  <br>
 *     ||=  gate_declaration  <br>
 *     ||=  udp_instantiation  <br>
 *     ||=  module_instantiation  <br>
 *     ||=  parameter_override  <br>
 *     ||=  continuous_assign  <br>
 *     ||=  specify_block  <br>
 *     ||=  initial_statement  <br>
 *     ||=  always_statement  <br>
 *     ||=  task  <br>
 *     ||=  function  
 */
class ScModule_item extends ScVerilog {
    public ScModule_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODULE_ITEM);
    }

    public String scString() {
        String ret = "";
        return ret;
    }
}
