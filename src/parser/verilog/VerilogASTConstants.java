package parser.verilog;

public interface VerilogASTConstants
{
    static final int ASTALWAYS_STATEMENT = 0;
    static final int ASTASSIGNMENT = 1;
    static final int ASTBLOCK_DECLARATION = 2;
    static final int ASTBLOCKING_ASSIGNMENT = 3;
    static final int ASTCASE_ITEM = 4;
    static final int ASTCHARGE_STRENGTH = 5;
    static final int ASTCOMBINATIONAL_ENTRY = 6;
    static final int ASTCONCATENATION = 7;
    static final int ASTCONDITIONAL_PORT_EXPRESSION = 8;
    static final int ASTCONSTANT_EXPRESSION = 9;
    static final int ASTCONTINUOUS_ASSIGN = 10;
    static final int ASTCONTROLLED_TIMING_CHECK_EVENT = 11;
    static final int ASTDATA_SOURCE_EXPRESSION = 12;
    static final int ASTDECIMAL_NUMBER = 13;
    static final int ASTDELAY = 14;
    static final int ASTDELAY_CONTROL = 15;
    static final int ASTDELAY_OR_EVENT_CONTROL = 16;
    static final int ASTDESCRIPTION = 17;
    static final int ASTDRIVE_STRENGTH = 18;
    static final int ASTEDGE = 19;
    static final int ASTEDGE_CONTROL_SPECIFIER = 20;
    static final int ASTEDGE_DESCRIPTOR = 21;
    static final int ASTEDGE_IDENTIFIER = 22;
    static final int ASTEDGE_INPUT_LIST = 23;
    static final int ASTEDGE_SENSITIVE_PATH_DECLARATION = 24;
    static final int ASTEVENT_CONTROL = 25;
    static final int ASTEVENT_DECLARATION = 26;
    static final int ASTEVENT_EXPRESSION = 27;
    static final int ASTEXPANDRANGE = 28;
    static final int ASTEXPRESSION = 29;
    static final int ASTFUNCTION = 30;
    static final int ASTFUNCTION_CALL = 31;
    static final int ASTGATE_DECLARATION = 32;
    static final int ASTGATE_INSTANCE = 33;
    static final int ASTidentifier = 34;
    static final int ASTIDENTIFIER = 35;
    static final int ASTINIT_VAL = 36;
    static final int ASTINITIAL_STATEMENT = 37;
    static final int ASTINOUT_DECLARATION = 38;
    static final int ASTINPUT_DECLARATION = 39;
    static final int ASTINPUT_IDENTIFIER = 40;
    static final int ASTINPUT_LIST = 41;
    static final int ASTINTEGER_DECLARATION = 42;
    static final int ASTLEVEL_INPUT_LIST = 43;
    static final int ASTLEVEL_SENSITIVE_PATH_DECLARATION = 44;
    static final int ASTLIST_OF_ASSIGNMENTS = 45;
    static final int ASTLIST_OF_MODULE_CONNECTIONS = 46;
    static final int ASTLIST_OF_PARAM_ASSIGNMENTS = 47;
    static final int ASTLIST_OF_PATH_INPUTS = 48;
    static final int ASTLIST_OF_PATH_OUTPUTS = 49;
    static final int ASTLIST_OF_PORTS = 50;
    static final int ASTLIST_OF_REGISTER_VARIABLES = 51;
    static final int ASTLIST_OF_VARIABLES = 52;
    static final int ASTLVALUE = 53;
    static final int ASTMINTYPMAX_EXPRESSION = 54;
    static final int ASTMODULE = 55;
    static final int ASTMODULE_INSTANCE = 56;
    static final int ASTMODULE_INSTANTIATION = 57;
    static final int ASTMODULE_ITEM = 58;
    static final int ASTMODULE_PORT_CONNECTION = 59;
    static final int ASTMULTIPLE_CONCATENATION = 60;
    static final int ASTNAME_OF_BLOCK = 61;
    static final int ASTNAME_OF_EVENT = 62;
    static final int ASTNAME_OF_FUNCTION = 63;
    static final int ASTNAME_OF_GATE_INSTANCE = 64;
    static final int ASTNAME_OF_INSTANCE = 65;
    static final int ASTNAME_OF_MEMORY = 66;
    static final int ASTNAME_OF_MODULE = 67;
    static final int ASTNAME_OF_PORT = 68;
    static final int ASTNAME_OF_REGISTER = 69;
    static final int ASTNAME_OF_SYSTEM_FUNCTION = 70;
    static final int ASTNAME_OF_SYSTEM_TASK = 71;
    static final int ASTNAME_OF_TASK = 72;
    static final int ASTNAME_OF_UDP = 73;
    static final int ASTNAME_OF_UDP_INSTANCE = 74;
    static final int ASTNAME_OF_VARIABLE = 75;
    static final int ASTNAMED_PORT_CONNECTION = 76;
    static final int ASTNET_DECLARATION = 77;
    static final int ASTNEXT_STATE = 78;
    static final int ASTNON_BLOCKING_ASSIGNMENT = 79;
    static final int ASTNOTIFY_REGISTER = 80;
    static final int ASTNULL = 81;
    static final int ASTNUMBER = 82;
    static final int ASTOUTPUT_DECLARATION = 83;
    static final int ASTOUTPUT_IDENTIFIER = 84;
    static final int ASTOUTPUT_TERMINAL_NAME = 85;
    static final int ASTPAR_BLOCK = 86;
    static final int ASTPARAM_ASSIGNMENT = 87;
    static final int ASTPARAMETER_DECLARATION = 88;
    static final int ASTPARAMETER_OVERRIDE = 89;
    static final int ASTPARAMETER_VALUE_ASSIGNMENT = 90;
    static final int ASTPATH_DECLARATION = 91;
    static final int ASTPATH_DELAY_EXPRESSION = 92;
    static final int ASTPATH_DELAY_VALUE = 93;
    static final int ASTPATH_DESCRIPTION = 94;
    static final int ASTPOLARITY_OPERATOR = 95;
    static final int ASTPORT = 96;
    static final int ASTPORT_EXPRESSION = 97;
    static final int ASTPORT_REFERENCE = 98;
    static final int ASTPRIMARY = 99;
    static final int ASTRANGE = 100;
    static final int ASTRANGE_OR_TYPE = 101;
    static final int ASTREAL_DECLARATION = 102;
    static final int ASTREG_DECLARATION = 103;
    static final int ASTREGISTER_VARIABLE = 104;
    static final int ASTSCALAR_CONSTANT = 105;
    static final int ASTSCALAR_EVENT_EXPRESSION = 106;
    static final int ASTSCALAR_EXPRESSION = 107;
    static final int ASTSCALAR_TIMING_CHECK_CONDITION = 108;
    static final int ASTSDPD = 109;
    static final int ASTSDPD_CONDITIONAL_EXPRESSSION = 110;
    static final int ASTSEQ_BLOCK = 111;
    static final int ASTSEQUENTIAL_ENTRY = 112;
    static final int ASTSOURCE_TEXT = 113;
    static final int ASTSPECIFY_BLOCK = 114;
    static final int ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR = 115;
    static final int ASTSPECIFY_ITEM = 116;
    static final int ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR = 117;
    static final int ASTSPECIFY_TERMINAL_DESCRIPTOR = 118;
    static final int ASTSPECPARAM_DECLARATION = 119;
    static final int ASTSTATE = 120;
    static final int ASTSTATEMENT = 121;
    static final int ASTSTATEMENT_OR_NULL = 122;
    static final int ASTSTRING = 123;
    static final int ASTSYSTEM_IDENTIFIER = 124;
    static final int ASTSYSTEM_TASK_ENABLE = 125;
    static final int ASTSYSTEM_TIMING_CHECK = 126;
    static final int ASTTABLE_DEFINITION = 127;
    static final int ASTTABLE_ENTRIES = 128;
    static final int ASTTASK = 129;
    static final int ASTTASK_ENABLE = 130;
    static final int ASTTERMINAL = 131;
    static final int ASTTF_DECLARATION = 132;
    static final int ASTTIME_DECLARATION = 133;
    static final int ASTTIMING_CHECK_CONDITION = 134;
    static final int ASTTIMING_CHECK_EVENT = 135;
    static final int ASTTIMING_CHECK_EVENT_CONTROL = 136;
    static final int ASTTIMING_CHECK_LIMIT = 137;
    static final int ASTUDP = 138;
    static final int ASTUDP_DECLARATION = 139;
    static final int ASTUDP_INITIAL_STATEMENT = 140;
    static final int ASTUDP_INSTANCE = 141;
    static final int ASTUDP_INSTANTIATION = 142;
    static final int ASTUNSIGNED_NUMBER = 143;

    static final String[] ASTNodeName =
    {
        "always_statement",
        "assignment",
        "block_declaration",
        "blocking_assignment",
        "case_item",
        "charge_strength",
        "combinational_entry",
        "concatenation",
        "conditional_port_expression",
        "constant_expression",
        "continuous_assign",
        "controlled_timing_check_event",
        "data_source_expression",
        "decimal_number",
        "delay",
        "delay_control",
        "delay_or_event_control",
        "description",
        "drive_strength",
        "edge",
        "edge_control_specifier",
        "edge_descriptor",
        "edge_identifier",
        "edge_input_list",
        "edge_sensitive_path_declaration",
        "event_control",
        "event_declaration",
        "event_expression",
        "expandrange",
        "expression",
        "function",
        "function_call",
        "gate_declaration",
        "gate_instance",
        "identifier",
        "IDENTIFIER",
        "init_val",
        "initial_statement",
        "inout_declaration",
        "input_declaration",
        "input_identifier",
        "input_list",
        "integer_declaration",
        "level_input_list",
        "level_sensitive_path_declaration",
        "list_of_assignments",
        "list_of_module_connections",
        "list_of_param_assignments",
        "list_of_path_inputs",
        "list_of_path_outputs",
        "list_of_ports",
        "list_of_register_variables",
        "list_of_variables",
        "lvalue",
        "mintypmax_expression",
        "module",
        "module_instance",
        "module_instantiation",
        "module_item",
        "module_port_connection",
        "multiple_concatenation",
        "name_of_block",
        "name_of_event",
        "name_of_function",
        "name_of_gate_instance",
        "name_of_instance",
        "name_of_memory",
        "name_of_module",
        "name_of_port",
        "name_of_register",
        "name_of_system_function",
        "name_of_system_task",
        "name_of_task",
        "name_of_udp",
        "name_of_udp_instance",
        "name_of_variable",
        "named_port_connection",
        "net_declaration",
        "next_state",
        "non_blocking_assignment",
        "notify_register",
        "NULL",
        "number",
        "output_declaration",
        "output_identifier",
        "output_terminal_name",
        "par_block",
        "param_assignment",
        "parameter_declaration",
        "parameter_override",
        "parameter_value_assignment",
        "path_declaration",
        "path_delay_expression",
        "path_delay_value",
        "path_description",
        "polarity_operator",
        "port",
        "port_expression",
        "port_reference",
        "primary",
        "range",
        "range_or_type",
        "real_declaration",
        "reg_declaration",
        "register_variable",
        "scalar_constant",
        "scalar_event_expression",
        "scalar_expression",
        "scalar_timing_check_condition",
        "sdpd",
        "sdpd_conditional_expresssion",
        "seq_block",
        "sequential_entry",
        "source_text",
        "specify_block",
        "specify_input_terminal_descriptor",
        "specify_item",
        "specify_output_terminal_descriptor",
        "specify_terminal_descriptor",
        "specparam_declaration",
        "state",
        "statement",
        "statement_or_null",
        "string",
        "system_identifier",
        "system_task_enable",
        "system_timing_check",
        "table_definition",
        "table_entries",
        "task",
        "task_enable",
        "terminal",
        "tf_declaration",
        "time_declaration",
        "timing_check_condition",
        "timing_check_event",
        "timing_check_event_control",
        "timing_check_limit",
        "udp",
        "udp_declaration",
        "udp_initial_statement",
        "udp_instance",
        "udp_instantiation",
        "unsigned_number",
    };
}
