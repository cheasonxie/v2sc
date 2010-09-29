package parser.verilog;

public interface VerilogASTConstants
{
    static final int ASTALWAYS_CONSTRUCT = 0;
    static final int ASTBINARY_BASE = 1;
    static final int ASTBINARY_DIGIT = 2;
    static final int ASTBINARY_NUMBER = 3;
    static final int ASTBINARY_OPERATOR = 4;
    static final int ASTBLOCKING_ASSIGNMENT = 5;
    static final int ASTBLOCK_ITEM_DECLARATION = 6;
    static final int ASTCASE_ITEM = 7;
    static final int ASTCASE_STATEMENT = 8;
    static final int ASTCHARGE_STRENGTH = 9;
    static final int ASTCMOS_SWITCHTYPE = 10;
    static final int ASTCMOS_SWITCH_INSTANCE = 11;
    static final int ASTCOMBINATIONAL_BODY = 12;
    static final int ASTCOMBINATIONAL_ENTRY = 13;
    static final int ASTCONCATENATION = 14;
    static final int ASTCONDITIONAL_STATEMENT = 15;
    static final int ASTCONSTANT_EXPRESSION = 16;
    static final int ASTCONSTANT_MINTYPMAX_EXPRESSION = 17;
    static final int ASTCONSTANT_PRIMARY = 18;
    static final int ASTCONTINUOUS_ASSIGN = 19;
    static final int ASTCONTROLLED_TIMING_CHECK_EVENT = 20;
    static final int ASTCURRENT_STATE = 21;
    static final int ASTDATA_SOURCE_EXPRESSION = 22;
    static final int ASTDECIMAL_BASE = 23;
    static final int ASTDECIMAL_DIGIT = 24;
    static final int ASTDECIMAL_NUMBER = 25;
    static final int ASTDELAY2 = 26;
    static final int ASTDELAY3 = 27;
    static final int ASTDELAY_CONTROL = 28;
    static final int ASTDELAY_OR_EVENT_CONTROL = 29;
    static final int ASTDELAY_VALUE = 30;
    static final int ASTDESCRIPTION = 31;
    static final int ASTDISABLE_STATEMENT = 32;
    static final int ASTDRIVE_STRENGTH = 33;
    static final int ASTEDGE_CONTROL_SPECIFIER = 34;
    static final int ASTEDGE_DESCRIPTOR = 35;
    static final int ASTEDGE_IDENTIFIER = 36;
    static final int ASTEDGE_INDICATOR = 37;
    static final int ASTEDGE_INPUT_LIST = 38;
    static final int ASTEDGE_SENSITIVE_PATH_DECLARATION = 39;
    static final int ASTEDGE_SYMBOL = 40;
    static final int ASTENABLE_GATETYPE = 41;
    static final int ASTENABLE_GATE_INSTANCE = 42;
    static final int ASTENABLE_TERMINAL = 43;
    static final int ASTESCAPED_IDENTIFIER = 44;
    static final int ASTEVENT_CONTROL = 45;
    static final int ASTEVENT_DECLARATION = 46;
    static final int ASTEVENT_EXPRESSION = 47;
    static final int ASTEVENT_TRIGGER = 48;
    static final int ASTEXPRESSION = 49;
    static final int ASTFULL_EDGE_SENSITIVE_PATH_DESCRIPTION = 50;
    static final int ASTFULL_PATH_DESCRIPTION = 51;
    static final int ASTFUNCTION_CALL = 52;
    static final int ASTFUNCTION_DECLARATION = 53;
    static final int ASTFUNCTION_ITEM_DECLARATION = 54;
    static final int ASTGATE_INSTANTIATION = 55;
    static final int ASTHEX_BASE = 56;
    static final int ASTHEX_DIGIT = 57;
    static final int ASTHEX_NUMBER = 58;
    static final int ASTidentifier = 59;
    static final int ASTIDENTIFIER = 60;
    static final int ASTINITIAL_CONSTRUCT = 61;
    static final int ASTINIT_VAL = 62;
    static final int ASTINOUT_DECLARATION = 63;
    static final int ASTINOUT_TERMINAL = 64;
    static final int ASTINPUT_DECLARATION = 65;
    static final int ASTINPUT_IDENTIFIER = 66;
    static final int ASTINPUT_TERMINAL = 67;
    static final int ASTINTEGER_DECLARATION = 68;
    static final int ASTLEVEL_INPUT_LIST = 69;
    static final int ASTLEVEL_SYMBOL = 70;
    static final int ASTLIMIT_VALUE = 71;
    static final int ASTLIST_OF_MODULE_CONNECTIONS = 72;
    static final int ASTLIST_OF_NET_ASSIGNMENTS = 73;
    static final int ASTLIST_OF_NET_DECL_ASSIGNMENTS = 74;
    static final int ASTLIST_OF_NET_IDENTIFIERS = 75;
    static final int ASTLIST_OF_PARAM_ASSIGNMENTS = 76;
    static final int ASTLIST_OF_PATH_DELAY_EXPRESSIONS = 77;
    static final int ASTLIST_OF_PATH_INPUTS = 78;
    static final int ASTLIST_OF_PATH_OUTPUTS = 79;
    static final int ASTLIST_OF_PORTS = 80;
    static final int ASTLIST_OF_PORT_IDENTIFIERS = 81;
    static final int ASTLIST_OF_REAL_IDENTIFIERS = 82;
    static final int ASTLIST_OF_REGISTER_IDENTIFIERS = 83;
    static final int ASTLIST_OF_SPECPARAM_ASSIGNMENTS = 84;
    static final int ASTLOOP_STATEMENT = 85;
    static final int ASTMINTYPMAX_EXPRESSION = 86;
    static final int ASTMODULE_DECLARATION = 87;
    static final int ASTMODULE_INSTANCE = 88;
    static final int ASTMODULE_INSTANTIATION = 89;
    static final int ASTMODULE_ITEM = 90;
    static final int ASTMODULE_ITEM_DECLARATION = 91;
    static final int ASTMODULE_KEYWORD = 92;
    static final int ASTMOS_SWITCHTYPE = 93;
    static final int ASTMOS_SWITCH_INSTANCE = 94;
    static final int ASTMULTIPLE_CONCATENATION = 95;
    static final int ASTNAMED_PORT_CONNECTION = 96;
    static final int ASTNAME_OF_GATE_INSTANCE = 97;
    static final int ASTNAME_OF_INSTANCE = 98;
    static final int ASTNAME_OF_SYSTEM_FUNCTION = 99;
    static final int ASTNAME_OF_UDP_INSTANCE = 100;
    static final int ASTNCONTROL_TERMINAL = 101;
    static final int ASTNET_ASSIGNMENT = 102;
    static final int ASTNET_DECLARATION = 103;
    static final int ASTNET_DECL_ASSIGNMENT = 104;
    static final int ASTNET_LVALUE = 105;
    static final int ASTNET_TYPE = 106;
    static final int ASTNEXT_STATE = 107;
    static final int ASTNON_BLOCKING_ASSIGNMENT = 108;
    static final int ASTNOTIFY_REGISTER = 109;
    static final int ASTNUMBER = 110;
    static final int ASTN_INPUT_GATETYPE = 111;
    static final int ASTN_INPUT_GATE_INSTANCE = 112;
    static final int ASTN_OUTPUT_GATETYPE = 113;
    static final int ASTN_OUTPUT_GATE_INSTANCE = 114;
    static final int ASTOCTAL_BASE = 115;
    static final int ASTOCTAL_DIGIT = 116;
    static final int ASTOCTAL_NUMBER = 117;
    static final int ASTORDERED_PORT_CONNECTION = 118;
    static final int ASTOUTPUT_DECLARATION = 119;
    static final int ASTOUTPUT_IDENTIFIER = 120;
    static final int ASTOUTPUT_SYMBOL = 121;
    static final int ASTOUTPUT_TERMINAL = 122;
    static final int ASTPARALLEL_EDGE_SENSITIVE_PATH_DESCRIPTION = 123;
    static final int ASTPARALLEL_PATH_DESCRIPTION = 124;
    static final int ASTPARAMETER_DECLARATION = 125;
    static final int ASTPARAMETER_OVERRIDE = 126;
    static final int ASTPARAMETER_VALUE_ASSIGNMENT = 127;
    static final int ASTPARAM_ASSIGNMENT = 128;
    static final int ASTPAR_BLOCK = 129;
    static final int ASTPASS_ENABLE_SWITCH_INSTANCE = 130;
    static final int ASTPASS_EN_SWITCHTYPE = 131;
    static final int ASTPASS_SWITCHTYPE = 132;
    static final int ASTPASS_SWITCH_INSTANCE = 133;
    static final int ASTPATH_DECLARATION = 134;
    static final int ASTPATH_DELAY_EXPRESSION = 135;
    static final int ASTPATH_DELAY_VALUE = 136;
    static final int ASTPCONTROL_TERMINAL = 137;
    static final int ASTPOLARITY_OPERATOR = 138;
    static final int ASTPORT = 139;
    static final int ASTPORT_EXPRESSION = 140;
    static final int ASTPORT_REFERENCE = 141;
    static final int ASTPRIMARY = 142;
    static final int ASTPROCEDURAL_CONTINUOUS_ASSIGNMENT = 143;
    static final int ASTPROCEDURAL_TIMING_CONTROL_STATEMENT = 144;
    static final int ASTPULLDOWN_STRENGTH = 145;
    static final int ASTPULLUP_STRENGTH = 146;
    static final int ASTPULL_GATE_INSTANCE = 147;
    static final int ASTPULSE_CONTROL_SPECPARAM = 148;
    static final int ASTRANGE = 149;
    static final int ASTRANGE_OR_TYPE = 150;
    static final int ASTREALTIME_DECLARATION = 151;
    static final int ASTREAL_DECLARATION = 152;
    static final int ASTREAL_NUMBER = 153;
    static final int ASTREGISTER_NAME = 154;
    static final int ASTREG_ASSIGNMENT = 155;
    static final int ASTREG_DECLARATION = 156;
    static final int ASTREG_LVALUE = 157;
    static final int ASTSCALAR_CONSTANT = 158;
    static final int ASTSCALAR_TIMING_CHECK_CONDITION = 159;
    static final int ASTSEQUENTIAL_BODY = 160;
    static final int ASTSEQUENTIAL_ENTRY = 161;
    static final int ASTSEQ_BLOCK = 162;
    static final int ASTSEQ_INPUT_LIST = 163;
    static final int ASTSIGN = 164;
    static final int ASTSIMPLE_IDENTIFIER = 165;
    static final int ASTSIMPLE_PATH_DECLARATION = 166;
    static final int ASTSIZE = 167;
    static final int ASTSOURCE_TEXT = 168;
    static final int ASTSPECIFY_BLOCK = 169;
    static final int ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR = 170;
    static final int ASTSPECIFY_ITEM = 171;
    static final int ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR = 172;
    static final int ASTSPECIFY_TERMINAL_DESCRIPTOR = 173;
    static final int ASTSPECPARAM_ASSIGNMENT = 174;
    static final int ASTSPECPARAM_DECLARATION = 175;
    static final int ASTSTATEMENT = 176;
    static final int ASTSTATEMENT_OR_NULL = 177;
    static final int ASTSTATE_DEPENDENT_PATH_DECLARATION = 178;
    static final int ASTSTRENGTH0 = 179;
    static final int ASTSTRENGTH1 = 180;
    static final int ASTSTRING = 181;
    static final int ASTSYSTEM_TASK_ENABLE = 182;
    static final int ASTSYSTEM_TASK_NAME = 183;
    static final int ASTSYSTEM_TIMING_CHECK = 184;
    static final int ASTTASK_ARGUMENT_DECLARATION = 185;
    static final int ASTTASK_DECLARATION = 186;
    static final int ASTTASK_ENABLE = 187;
    static final int ASTTIME_DECLARATION = 188;
    static final int ASTTIMING_CHECK_CONDITION = 189;
    static final int ASTTIMING_CHECK_EVENT = 190;
    static final int ASTTIMING_CHECK_EVENT_CONTROL = 191;
    static final int ASTTIMING_CHECK_LIMIT = 192;
    static final int ASTUDP_BODY = 193;
    static final int ASTUDP_DECLARATION = 194;
    static final int ASTUDP_INITIAL_STATEMENT = 195;
    static final int ASTUDP_INSTANCE = 196;
    static final int ASTUDP_INSTANTIATION = 197;
    static final int ASTUDP_PORT_DECLARATION = 198;
    static final int ASTUDP_PORT_LIST = 199;
    static final int ASTUNARY_OPERATOR = 200;
    static final int ASTUNSIGNED_NUMBER = 201;
    static final int ASTWAIT_STATEMENT = 202;
    static final int ASTWHITE_SPACE = 203;

    static final String[] ASTNodeName =
    {
        "always_construct",
        "binary_base",
        "binary_digit",
        "binary_number",
        "binary_operator",
        "blocking_assignment",
        "block_item_declaration",
        "case_item",
        "case_statement",
        "charge_strength",
        "cmos_switchtype",
        "cmos_switch_instance",
        "combinational_body",
        "combinational_entry",
        "concatenation",
        "conditional_statement",
        "constant_expression",
        "constant_mintypmax_expression",
        "constant_primary",
        "continuous_assign",
        "controlled_timing_check_event",
        "current_state",
        "data_source_expression",
        "decimal_base",
        "decimal_digit",
        "decimal_number",
        "delay2",
        "delay3",
        "delay_control",
        "delay_or_event_control",
        "delay_value",
        "description",
        "disable_statement",
        "drive_strength",
        "edge_control_specifier",
        "edge_descriptor",
        "edge_identifier",
        "edge_indicator",
        "edge_input_list",
        "edge_sensitive_path_declaration",
        "edge_symbol",
        "enable_gatetype",
        "enable_gate_instance",
        "enable_terminal",
        "escaped_identifier",
        "event_control",
        "event_declaration",
        "event_expression",
        "event_trigger",
        "expression",
        "full_edge_sensitive_path_description",
        "full_path_description",
        "function_call",
        "function_declaration",
        "function_item_declaration",
        "gate_instantiation",
        "hex_base",
        "hex_digit",
        "hex_number",
        "identifier",
        "IDENTIFIER",
        "initial_construct",
        "init_val",
        "inout_declaration",
        "inout_terminal",
        "input_declaration",
        "input_identifier",
        "input_terminal",
        "integer_declaration",
        "level_input_list",
        "level_symbol",
        "limit_value",
        "list_of_module_connections",
        "list_of_net_assignments",
        "list_of_net_decl_assignments",
        "list_of_net_identifiers",
        "list_of_param_assignments",
        "list_of_path_delay_expressions",
        "list_of_path_inputs",
        "list_of_path_outputs",
        "list_of_ports",
        "list_of_port_identifiers",
        "list_of_real_identifiers",
        "list_of_register_identifiers",
        "list_of_specparam_assignments",
        "loop_statement",
        "mintypmax_expression",
        "module_declaration",
        "module_instance",
        "module_instantiation",
        "module_item",
        "module_item_declaration",
        "module_keyword",
        "mos_switchtype",
        "mos_switch_instance",
        "multiple_concatenation",
        "named_port_connection",
        "name_of_gate_instance",
        "name_of_instance",
        "name_of_system_function",
        "name_of_udp_instance",
        "ncontrol_terminal",
        "net_assignment",
        "net_declaration",
        "net_decl_assignment",
        "net_lvalue",
        "net_type",
        "next_state",
        "non_blocking_assignment",
        "notify_register",
        "number",
        "n_input_gatetype",
        "n_input_gate_instance",
        "n_output_gatetype",
        "n_output_gate_instance",
        "octal_base",
        "octal_digit",
        "octal_number",
        "ordered_port_connection",
        "output_declaration",
        "output_identifier",
        "output_symbol",
        "output_terminal",
        "parallel_edge_sensitive_path_description",
        "parallel_path_description",
        "parameter_declaration",
        "parameter_override",
        "parameter_value_assignment",
        "param_assignment",
        "par_block",
        "pass_enable_switch_instance",
        "pass_en_switchtype",
        "pass_switchtype",
        "pass_switch_instance",
        "path_declaration",
        "path_delay_expression",
        "path_delay_value",
        "pcontrol_terminal",
        "polarity_operator",
        "port",
        "port_expression",
        "port_reference",
        "primary",
        "procedural_continuous_assignment",
        "procedural_timing_control_statement",
        "pulldown_strength",
        "pullup_strength",
        "pull_gate_instance",
        "pulse_control_specparam",
        "range",
        "range_or_type",
        "realtime_declaration",
        "real_declaration",
        "real_number",
        "register_name",
        "reg_assignment",
        "reg_declaration",
        "reg_lvalue",
        "scalar_constant",
        "scalar_timing_check_condition",
        "sequential_body",
        "sequential_entry",
        "seq_block",
        "seq_input_list",
        "sign",
        "simple_identifier",
        "simple_path_declaration",
        "size",
        "source_text",
        "specify_block",
        "specify_input_terminal_descriptor",
        "specify_item",
        "specify_output_terminal_descriptor",
        "specify_terminal_descriptor",
        "specparam_assignment",
        "specparam_declaration",
        "statement",
        "statement_or_null",
        "state_dependent_path_declaration",
        "strength0",
        "strength1",
        "string",
        "system_task_enable",
        "system_task_name",
        "system_timing_check",
        "task_argument_declaration",
        "task_declaration",
        "task_enable",
        "time_declaration",
        "timing_check_condition",
        "timing_check_event",
        "timing_check_event_control",
        "timing_check_limit",
        "udp_body",
        "udp_declaration",
        "udp_initial_statement",
        "udp_instance",
        "udp_instantiation",
        "udp_port_declaration",
        "udp_port_list",
        "unary_operator",
        "unsigned_number",
        "wait_statement",
        "white_space",
    };
}
