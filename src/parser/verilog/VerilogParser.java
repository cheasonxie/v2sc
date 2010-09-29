package parser.verilog;

import java.io.Reader;

import parser.CommentBlock;
import parser.IASTNode;
import parser.IParser;
import parser.ISymbol;
import parser.ISymbolTable;
import parser.ParserException;
import parser.Token;

public class VerilogParser implements IParser, VerilogTokenConstants, VerilogASTConstants
{
    
    protected VerilogTokenManager tokenMgr = null;
    protected ASTNode curNode = null;       // current parsing node
    protected ASTNode lastNode = null;      // last parsed node
    protected ASTNode sourceText = null;   // source text node
    
    /**
     *  true -- just only parse symbols(if exist)<br>
     *  false -- parse all AST
     */
    protected boolean parseSymbol = false;
    
    /**
     * constructor, file path version
     */
    public VerilogParser(boolean parseSymbol) {
        this.parseSymbol = parseSymbol;
    }
    
    /**
     * token which has its own symbol table call this function to start<br>
     * use pair with endBlock
     */
    void startBlock() {
        //TODO:add here
    }
    
    /**
     * token which has its own symbol table call this function to end<br>
     * use pair with startBlock
     */
    void endBlock() {
      //TODO:add here
    }
    
    void openNodeScope(ASTNode n) throws ParserException  {
        curNode = n;
        n.setFirstToken(tokenMgr.getNextToken());
    }
    
    void closeNodeScope(ASTNode n) throws ParserException  {
        n.setLastToken(tokenMgr.getCurrentToken());
        lastNode = curNode;
        curNode = (ASTNode)n.getParent();
    }
    
    Token consumeToken(int kind) throws ParserException {
        Token oldToken = tokenMgr.getCurrentToken();
        Token token = tokenMgr.toNextToken();
        if(kind == SEMICOLON) {     // consume continuous semicolons
            if(token == null || token.kind != SEMICOLON) {
                throw new ParserException(oldToken);
            }
            Token prev = token;
            while(token != null && token.kind == SEMICOLON) {
                prev = token;
                token = tokenMgr.toNextToken();
            }
            tokenMgr.setCurrentToken(prev);
            return prev;
        }else {
            if(token != null && token.kind == kind) {
                return token;
            }
        }
        throw new ParserException(oldToken);
    }
    
    /**
     * check whether specified token is behind base token
     */
    boolean checkLateComming(Token token, Token base) throws ParserException {
        if(base == null) { return false; }
        if(token == null) { return true; }
        if(token.beginLine > base.beginLine 
            || (token.beginLine == base.beginLine
                && token.beginColumn > base.beginColumn)) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * find token in block between "from" and "to" (including "from", but not "to")
     * before call this function, you must in one block(after keyword token)
     */
    Token findTokenInBlock(Token from, int kind, Token to) throws ParserException {
        //TODO: add here
        return null;
    }
    
    /**
     * find token in block between current token and "to" (not including and "to")
     * <br> ignore blocks in this bolck
     * <br>before call this function, you must in one block(after keyword token)
     */
    Token findTokenInBlock(int kind, Token to) throws ParserException {
        return findTokenInBlock(tokenMgr.getNextToken(), kind, to);
    }
    
    /**
     * find token in block between "from" token and "to" (including "from", but not "to")
     * no ignore
     */
    Token findToken(Token from, int kind, Token to) throws ParserException {
        Token token = from;
        Token ret = null;
        while(token != null)
        {
            if(checkLateComming(token, to) || token == to) {
                break;
            } else if(token.kind == kind) {
                ret = token;
                break;
            }
            
            token = tokenMgr.getNextToken(token);
        }
        return ret;
    }
    
    /**
     * find token in block between current token and "to" (not including and "to")
     * no ignore
     */
    Token findToken(int kind, Token to) throws ParserException {
        return findToken(tokenMgr.getNextToken(), kind, to);
    }
    
    /**
     * always_construct ::= <b>always</b> statement 
     */
    void always_construct(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTALWAYS_CONSTRUCT);
        openNodeScope(node);
        consumeToken(ALWAYS);
        statement(node, endToken);
        closeNodeScope(node);
    }

    /**
     * binary_base ::= ¡¯b | ¡¯B 
     */
//    void binary_base(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTBINARY_BASE);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * binary_digit ::= x | X | z | Z | 0 | 1 
     */
//    void binary_digit(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTBINARY_DIGIT);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * binary_number ::= [ size ] binary_base binary_digit { _ | binary_digit } 
     */
//    void binary_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTBINARY_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * binary_operator ::= <br>
     *         + | - | * | / | % | == | != | === | !== | && | || <br>
     *         | < | <= | > | >= | & | | | ^ | ^~ | ~^ | >> | << 
     */
//    void binary_operator(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTBINARY_OPERATOR);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * blocking assignment ::= reg_lvalue = [ delay_or_event_control ] expression 
     */
    void blocking_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCKING_ASSIGNMENT);
        openNodeScope(node);
        Token tmpToken = findToken(EQ, endToken);
        reg_lvalue(node, tmpToken);
        closeNodeScope(node);
    }

    /**
     * block_item_declaration ::= parameter_declaration | reg_declaration <br>
     *                         | integer_declaration | real_declaration | time_declaration <br>
     *                         | realtime_declaration | event_declaration 
     */
    void block_item_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTBLOCK_ITEM_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * case_item ::= expression { , expression } : statement_or_null <br>
     *             | <b>default<b> [ : ] statement_or_null 
     */
    void case_item(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_ITEM);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * case_statement ::= <b>case</b> ( expression ) case_item { case_item } <b>endcase</b> <br>
     *                 | <b>casez</b> ( expression ) case_item { case_item } <b>endcase</b> <br>
     *                 | <b>casex</b> ( expression ) case_item { case_item } <b>endcase</b> 
     */
    void case_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCASE_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * charge_strength ::= ( <b>small</b> ) | ( <b>medium</b> ) | ( <b>large</b> ) 
     */
    void charge_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCHARGE_STRENGTH);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * cmos_switchtype ::= <b>cmos</b> | <b>rcmos</b> 
     */
    void cmos_switchtype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCMOS_SWITCHTYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * cmos_switch_instance ::= ( output_terminal , input_terminal , <br>
     *                             ncontrol_terminal , pcontrol_terminal ) 
     */
    void cmos_switch_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCMOS_SWITCH_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * combinational_body ::= <b>table</b> combinational_entry { combinational_entry } <b>endtable</b> 
     */
    void combinational_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMBINATIONAL_BODY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * combinational_entry ::= level_input_list : output_symbol ; 
     */
    void combinational_entry(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCOMBINATIONAL_ENTRY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * concatenation ::= { expression { , expression } } 
     */
    void concatenation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONCATENATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * conditional_statement ::= <b>if</b> ( expression ) statement_or_null [ <b>else</b> statement_or_null ] 
     */
    void conditional_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONDITIONAL_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * constant_expression ::= constant_primary <br>
     *                     | unary_operator constant_primary <br>
     *                     | constant_expression binary_operator constant_expression <br>
     *                     | constant_expression ? constant_expression : constant_expression <br>
     *                     | string 
     */
    void constant_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTANT_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * constant_mintypmax_expression ::= constant_expression <br>
     *                             | constant_expression : constant_expression : constant_expression 
     */
    void constant_mintypmax_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTANT_MINTYPMAX_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * constant_primary ::= number | <b>parameter</b> _identifier <br>
     *                 | constant _concatenation | constant _multiple_concatenation 
     */
    void constant_primary(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONSTANT_PRIMARY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * continuous_assign ::= <b>assign</b> [ drive_strength ] [ delay3 ] list_of_net_assignments ; 
     */
    void continuous_assign(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONTINUOUS_ASSIGN);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * controlled_timing_check_event ::= timing_check_event_control <br>
     *                         specify_terminal_descriptor [ &&& timing_check_condition ] 
     */
    void controlled_timing_check_event(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCONTROLLED_TIMING_CHECK_EVENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * current_state ::= level_symbol 
     */
    void current_state(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTCURRENT_STATE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * data_source_expression ::= expression 
     */
    void data_source_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDATA_SOURCE_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * decimal_base ::= ¡¯d | ¡¯D 
     */
    void decimal_base(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDECIMAL_BASE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * decimal_digit ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 
     */
    void decimal_digit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDECIMAL_DIGIT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * decimal_number ::= [ sign ] unsigned_number <br>
     *                 | [ size ] decimal_base unsigned_number 
     */
//    void decimal_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTDECIMAL_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * delay2 ::= # delay_value | # ( delay_value [ , delay_value ] ) 
     */
    void delay2(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY2);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * delay3 ::= # delay_value | # ( delay_value [ , delay_value [ , delay_value ] ] ) 
     */
    void delay3(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY3);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * delay_control ::= # delay_value | # ( mintypmax_expression ) 
     */
    void delay_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_CONTROL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * delay_or_event_control ::= delay_control | event_control | <b>repeat</b> ( expression ) event_control 
     */
    void delay_or_event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_OR_EVENT_CONTROL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * delay_value ::= unsigned_number | parameter_identifier | constant_mintypmax_expression 
     */
    void delay_value(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDELAY_VALUE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * description ::= module_declaration | udp_declaration 
     */
    void description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDESCRIPTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * disable_statement ::= <b>disable</b> <b>task</b> _identifier ; | <b>disable</b> block _identifier ; 
     */
    void disable_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDISABLE_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * drive_strength ::= ( strength0 , strength1 ) | ( strength1 , strength0 ) <br>
     *                 | ( strength0 , <b>highz1</b> ) | ( strength1 , <b>highz0</b> ) <br>
     *                 | ( <b>highz1</b> , strength0 ) | ( <b>highz0</b> , strength1 ) 
     */
    void drive_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTDRIVE_STRENGTH);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_control_specifier ::= [ edge_descriptor [ , edge_descriptor ] ] 
     */
    void edge_control_specifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_CONTROL_SPECIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_descriptor ::= 01 | 10 | 0x | x1 | 1x | x0 
     */
    void edge_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_DESCRIPTOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_identifier ::= <b>posedge</b> | <b>negedge</b> 
     */
    void edge_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_IDENTIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_indicator ::= ( level_symbol level_symbol ) | edge_symbol 
     */
    void edge_indicator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_INDICATOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_input_list ::= { level_symbol } edge_indicator { level_symbol } 
     */
    void edge_input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_INPUT_LIST);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_sensitive_path_declaration ::= parallel_edge_sensitive_path_description = path_delay_value <br>
     *                                 | full_edge_sensitive_path_description = path_delay_value 
     */
    void edge_sensitive_path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_SENSITIVE_PATH_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * edge_symbol ::= r | R | f | F | p | P | n | N | * 
     */
    void edge_symbol(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEDGE_SYMBOL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * enable_gatetype ::= <b>bufif0</b> | <b>bufif1</b> | <b>notif0</b> | <b>notif1</b> 
     */
    void enable_gatetype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENABLE_GATETYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * enable_gate_instance ::= [ name_of_gate_instance ] <br>
     *                         ( output_terminal , input_terminal , enable_terminal ) 
     */
    void enable_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENABLE_GATE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * enable_terminal ::= scalar _expression 
     */
    void enable_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTENABLE_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * escaped_identifier ::= \ {Any_ASCII_character_except_white_space} white_space 
     */
    void escaped_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTESCAPED_IDENTIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * event_control ::= @ <b>event</b> _identifier | @ ( event_expression ) 
     */
    void event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_CONTROL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * event_declaration ::= <b>event</b> <b>event</b> _identifier { , <b>event</b> _identifier } ; 
     */
    void event_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * event_expression ::= expression | <b>event</b> _identifier <br>
     *                 | <b>posedge</b> expression | <b>negedge</b> expression <br>
     *                 | event_expression <b>or</b> event_expression 
     */
    void event_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * event_trigger ::= -> <b>event</b> _identifier ; 
     */
    void event_trigger(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEVENT_TRIGGER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * expression ::= primary | unary_operator primary <br>
     *             | expression binary_operator expression | expression ? expression : expression <br>
     *             | string <br>
     *             
     */
    void expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTEXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * full_edge_sensitive_path_description ::= ( [ edge_identifier ] list_of_path_inputs *> <br>
     *                                     list_of_path_outputs <br>
     *                                     [ polarity_operator ] : data_source_expression ) ) 
     */
    void full_edge_sensitive_path_description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFULL_EDGE_SENSITIVE_PATH_DESCRIPTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * full_path_description ::= ( list_of_path_inputs [ polarity_operator ] *> list_of_path_outputs ) 
     */
    void full_path_description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFULL_PATH_DESCRIPTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * function_call ::= <b>function</b> _identifier ( expression { , expression} ) <br>
     *             | name_of_system_function [ ( expression { , expression} ) ] 
     */
    void function_call(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION_CALL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * function_declaration ::= <b>function</b> [ range_or_type ] <b>function</b> _identifier ; <br>
     *                             function_item_declaration { function_item_declaration } <br>
     *                             statement <br>
     *                         <b>endfunction</b> 
     */
    void function_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * function_item_declaration ::= block_item_declaration | input_declaration 
     */
    void function_item_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTFUNCTION_ITEM_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * gate_instantiation ::= n_input_gatetype [ drive_strength ] <br>
     *                         [delay2] n_input_gate_instance { , n_input_gate_instance } ; <br>
     *                     | n_output_gatetype [ drive_strength ] [ delay2 ] <br>
     *                         n_output_gate_instance { , n_output_gate_instance } ; <br>
     *                     | enable_gatetype [ drive_strength ] [ delay3 ] <br>
     *                         enable_gate_instance { , enable_gate_instance} ; <br>
     *                     | mos_switchtype [ delay3 ] <br>
     *                         mos_switch_instance { , mos_switch_instance } ; <br>
     *                     | pass_switchtype pass_switch_instance { , pass_switch_instance } ; <br>
     *                     | pass_en_switchtype [ delay3 ] <br>
     *                         pass_en_switch_instance { , pass_en_switch_instance } ; <br>
     *                     | cmos_switchtype [ delay3 ] <br>
     *                         cmos_switch_instance { , cmos_switch_instance } ; <br>
     *                     | <b>pullup</b> [ pullup_strength ] <br>
     *                         pull_gate_instance { , pull_gate_instance } ; <br>
     *                     | <b>pulldown</b> [ pulldown_strength ] <br>
     *                         pull_gate_instance { , pull_gate_instance } ; 
     */
    void gate_instantiation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTGATE_INSTANTIATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * hex_base ::= ¡¯h | ¡¯H 
     */
//    void hex_base(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTHEX_BASE);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * hex_digit ::= x | X | z | Z | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 <br>
     *             | a | b | c | d | e | f | A | B | C | D | E | F 
     */
//    void hex_digit(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTHEX_DIGIT);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * hex_number ::= [ size ] hex_base hex_digit { _ | hex_digit } 
     */
//    void hex_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTHEX_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * identifier ::= IDENTIFIER [ { . IDENTIFIER } ] <br>
     *         The period in identifier may <b>not</b> be preceded <b>or</b> followed by a space. 
     */
    void identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTidentifier);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * IDENTIFIER ::= simple_identifier | escaped_identifier 
     */
//    void IDENTIFIER(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTIDENTIFIER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * initial_construct ::= <b>initial</b> statement 
     */
    void initial_construct(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINITIAL_CONSTRUCT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * init_val ::= 1¡¯b0 | 1¡¯b1 | 1¡¯bx | 1¡¯bX | 1¡¯B0 | 1¡¯B1 | 1¡¯Bx | 1¡¯BX | 1 | 0 
     */
    void init_val(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINIT_VAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * inout_declaration ::= <b>inout</b> [ range ] list_of_port_identifiers ; 
     */
    void inout_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINOUT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * inout_terminal ::= terminal _identifier | terminal _identifier [ constant_expression ] 
     */
    void inout_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINOUT_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * input_declaration ::= <b>input</b> [ range ] list_of_port_identifiers ; 
     */
    void input_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * input_identifier ::= input_port _identifier | inout_port _identifier 
     */
    void input_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_IDENTIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * input_terminal ::= scalar _expression 
     */
    void input_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINPUT_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * integer_declaration ::= <b>integer</b> list_of_register_identifiers ; 
     */
    void integer_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTINTEGER_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * level_input_list ::= level_symbol { level_symbol } 
     */
    void level_input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLEVEL_INPUT_LIST);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * level_symbol ::= 0 | 1 | x | X | ? | b | B 
     */
    void level_symbol(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLEVEL_SYMBOL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * limit_value ::= constant_mintypmax_expression 
     */
    void limit_value(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIMIT_VALUE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_module_connections ::= ordered_port_connection { , ordered_port_connection } <br>
     *                             | named_port_connection { , named_port_connection } 
     */
    void list_of_module_connections(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_MODULE_CONNECTIONS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_net_assignments ::= net_assignment { , net_assignment } 
     */
    void list_of_net_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_NET_ASSIGNMENTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_net_decl_assignments ::= net_decl_assignment { , net_decl_assignment } 
     */
    void list_of_net_decl_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_NET_DECL_ASSIGNMENTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_net_identifiers ::= net _identifier { , net _identifier } 
     */
    void list_of_net_identifiers(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_NET_IDENTIFIERS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_param_assignments ::= param_assignment { , param_assignment } 
     */
    void list_of_param_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PARAM_ASSIGNMENTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_path_delay_expressions ::= t _path_delay_expression <br>
     *                                 | trise _path_delay_expression , tfall _path_delay_expression <br>
     *                                 | trise _path_delay_expression , tfall _path_delay_expression , tz _path_delay_expression <br>
     *                                 | t01 _path_delay_expression , t10 _path_delay_expression , t0z _path_delay_expression , <br>
     *                                     tz1 _path_delay_expression , t1z _path_delay_expression , tz0 _path_delay_expression <br>
     *                                 | t01 _path_delay_expression , t10 _path_delay_expression , t0z _path_delay_expression , <br>
     *                                     tz1 _path_delay_expression , t1z _path_delay_expression , tz0 _path_delay_expression , <br>
     *                                     t0x _path_delay_expression , tx1 _path_delay_expression , t1x _path_delay_expression , <br>
     *                                     tx0 _path_delay_expression , txz _path_delay_expression , tzx _path_delay_expression 
     */
    void list_of_path_delay_expressions(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PATH_DELAY_EXPRESSIONS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_path_inputs ::= specify_input_terminal_descriptor { , specify_input_terminal_descriptor } 
     */
    void list_of_path_inputs(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PATH_INPUTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_path_outputs ::= specify_output_terminal_descriptor { , specify_output_terminal_descriptor } 
     */
    void list_of_path_outputs(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PATH_OUTPUTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_ports ::= ( port { , port } ) 
     */
    void list_of_ports(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PORTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_port_identifiers ::= port _identifier { , port _identifier } 
     */
    void list_of_port_identifiers(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_PORT_IDENTIFIERS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_real_identifiers ::= real _identifier { , real _identifier } 
     */
    void list_of_real_identifiers(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_REAL_IDENTIFIERS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_register_identifiers ::= register_name { , register_name } 
     */
    void list_of_register_identifiers(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_REGISTER_IDENTIFIERS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * list_of_specparam_assignments ::= specparam_assignment { , specparam_assignment } 
     */
    void list_of_specparam_assignments(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLIST_OF_SPECPARAM_ASSIGNMENTS);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * loop_statement ::= <b>forever</b> statement <br>
     *                 | <b>repeat</b> ( expression ) statement <br>
     *                 | <b>while</b> ( expression ) statement <br>
     *                 | <b>for</b> ( reg_assignment ; expression ; reg_assignment ) statement 
     */
    void loop_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTLOOP_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * mintypmax_expression ::= expression <br>
     *                     | expression : expression : expression 
     */
    void mintypmax_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMINTYPMAX_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_declaration ::= module_keyword <b>module</b> _identifier <br>
     *                         [ list_of_ports ] ; {module_item } <b>endmodule</b> 
     */
    void module_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_instance ::= name_of_instance ( [ list_of_module_connections ] ) 
     */
    void module_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_instantiation ::= <b>module</b> _identifier [ parameter_value_assignment ] <br>
     *                         module_instance { , module_instance } ; 
     */
    void module_instantiation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_INSTANTIATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_item ::= module_item_declaration | parameter_override <br>
     *             | continuous_assign | gate_instantiation | udp_instantiation <br>
     *             | module_instantiation | specify_block | initial_construct <br>
     *             | always_construct 
     */
    void module_item(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_ITEM);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_item_declaration ::= parameter_declaration | input_declaration <br>
     *                         | output_declaration | inout_declaration | net_declaration <br>
     *                         | reg_declaration | integer_declaration | real_declaration <br>
     *                         | time_declaration | realtime_declaration | event_declaration <br>
     *                         | task_declaration | function_declaration 
     */
    void module_item_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_ITEM_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * module_keyword ::= <b>module</b> | <b>macromodule</b> 
     */
    void module_keyword(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMODULE_KEYWORD);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * mos_switchtype ::= <b>nmos</b> | <b>pmos</b> | <b>rnmos</b> | <b>rpmos</b> 
     */
    void mos_switchtype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMOS_SWITCHTYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * mos_switch_instance ::= [ name_of_gate_instance ] ( output_terminal , input_terminal , enable_terminal ) 
     */
    void mos_switch_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMOS_SWITCH_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * multiple_concatenation ::= { expression { expression { , expression } } } 
     */
    void multiple_concatenation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTMULTIPLE_CONCATENATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * named_port_connection ::= . port _identifier ( [ expression ] ) 
     */
    void named_port_connection(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAMED_PORT_CONNECTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * name_of_gate_instance ::= gate_instance _identifier [ range ] 
     */
    void name_of_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_GATE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * name_of_instance ::= module_instance _identifier [ range ] 
     */
    void name_of_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * name_of_system_function ::= $ identifier <br>
     *                 Note: the $ in name_of_system_function may <b>not</b> be followed by a space. 
     */
    void name_of_system_function(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_SYSTEM_FUNCTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * name_of_udp_instance ::= udp_instance _identifier [ range ] 
     */
    void name_of_udp_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNAME_OF_UDP_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * ncontrol_terminal ::= scalar _expression 
     */
    void ncontrol_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNCONTROL_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * net_assignment ::= net_lvalue = expression 
     */
    void net_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * net_declaration ::= net_type [ <b>vectored</b> | <b>scalared</b> ] [ range ] [ delay3 ] list_of_net_identifiers ; <br>
     *                 | <b>trireg</b> [ <b>vectored</b> | <b>scalared</b> ] <br>
     *                     [ charge_strength ] [ range ] [ delay3 ] list_of_net_identifiers ; <br>
     *                 | net_type [ <b>vectored</b> | <b>scalared</b> ] <br>
     *                     [drive_strength] [range] [delay3] list_of_net_decl_assignments ; 
     */
    void net_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * net_decl_assignment ::= net _identifier = expression 
     */
    void net_decl_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_DECL_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * net_lvalue ::= net _identifier | net _identifier [ expression ] <br>
     *             | net _identifier [ msb _constant_expression : lsb _constant_expression ] <br>
     *             | net _concatenation 
     */
    void net_lvalue(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_LVALUE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * net_type ::= <b>wire</b> | <b>tri</b> | <b>tri1</b> | <b>supply0</b> | <b>wand</b> | <b>triand</b> | <b>tri0</b> | <b>supply1</b> | <b>wor</b> | <b>trior</b> 
     */
    void net_type(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNET_TYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * next_state ::= output_symbol | - 
     */
    void next_state(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNEXT_STATE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * non-blocking assignment ::= reg_lvalue <= [ delay_or_event_control ] expression 
     */
    void non_blocking_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNON_BLOCKING_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * notify_register ::= register _identifier 
     */
    void notify_register(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTNOTIFY_REGISTER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * number ::= decimal_number | octal_number | binary_number | hex_number | real_number 
     */
//    void number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTNUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * n_input_gatetype ::= <b>and</b> | <b>nand</b> | <b>or</b> | <b>nor</b> | <b>xor</b> | <b>xnor</b> 
     */
    void n_input_gatetype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTN_INPUT_GATETYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * n_input_gate_instance ::= [ name_of_gate_instance ] ( output_terminal , input_terminal { , input_terminal } ) 
     */
    void n_input_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTN_INPUT_GATE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * n_output_gatetype ::= <b>buf</b> | <b>not</b> 
     */
    void n_output_gatetype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTN_OUTPUT_GATETYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * n_output_gate_instance ::= [ name_of_gate_instance ] ( output_terminal { , output_terminal } , input_terminal ) 
     */
    void n_output_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTN_OUTPUT_GATE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * octal_base ::= ¡¯o | ¡¯O 
     */
//    void octal_base(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTOCTAL_BASE);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * octal_digit ::= x | X | z | Z | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 
     */
//    void octal_digit(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTOCTAL_DIGIT);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * octal_number ::= [ size ] octal_base octal_digit { _ | octal_digit} 
     */
//    void octal_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTOCTAL_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * ordered_port_connection ::= [ expression ] 
     */
    void ordered_port_connection(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTORDERED_PORT_CONNECTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * output_declaration ::= <b>output</b> [ range ] list_of_port_identifiers ; 
     */
    void output_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * output_identifier ::= output_port _identifier | inout_port _identifier 
     */
    void output_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_IDENTIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * output_symbol ::= 0 | 1 | x | X 
     */
    void output_symbol(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_SYMBOL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * output_terminal ::= terminal _identifier | terminal _identifier [ constant_expression ] 
     */
    void output_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTOUTPUT_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * parallel_edge_sensitive_path_description ::= ( [ edge_identifier ] specify_input_terminal_descriptor => <br>
     *                                         specify_output_terminal_descriptor <br>
     *                                         [ polarity_operator ] : data_source_expression ) ) 
     */
    void parallel_edge_sensitive_path_description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARALLEL_EDGE_SENSITIVE_PATH_DESCRIPTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * parallel_path_description ::= ( specify_input_terminal_descriptor <br>
     *                             [ polarity_operator ] => specify_output_terminal_descriptor ) 
     */
    void parallel_path_description(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARALLEL_PATH_DESCRIPTION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * parameter_declaration ::= <b>parameter</b> list_of_param_assignments ; 
     */
    void parameter_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * parameter_override ::= <b>defparam</b> list_of_param_assignments ; 
     */
    void parameter_override(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_OVERRIDE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * parameter_value_assignment ::= # ( expression { , expression } ) 
     */
    void parameter_value_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAMETER_VALUE_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * param_assignment ::= <b>parameter</b> _identifier = constant_expression 
     */
    void param_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPARAM_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * par_block ::= <b>fork</b> [ : block _identifier { block_item_declaration } ] { statement } <b>join</b> 
     */
    void par_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPAR_BLOCK);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pass_enable_switch_instance ::= [ name_of_gate_instance ] ( inout_terminal , inout_terminal , enable_terminal ) 
     */
    void pass_enable_switch_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPASS_ENABLE_SWITCH_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pass_en_switchtype ::= <b>tranif0</b> | <b>tranif1</b> | <b>rtranif1</b> | <b>rtranif0</b> 
     */
    void pass_en_switchtype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPASS_EN_SWITCHTYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pass_switchtype ::= <b>tran</b> | <b>rtran</b> 
     */
    void pass_switchtype(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPASS_SWITCHTYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pass_switch_instance ::= [ name_of_gate_instance ] ( inout_terminal , inout_terminal ) 
     */
    void pass_switch_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPASS_SWITCH_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * path_declaration ::= simple_path_declaration ; <br>
     *                     | edge_sensitive_path_declaration ; | state-dependent_path_declaration ; 
     */
    void path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * path_delay_expression ::= constant_mintypmax_expression 
     */
    void path_delay_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DELAY_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * path_delay_value ::= list_of_path_delay_expressions | ( list_of_path_delay_expressions ) 
     */
    void path_delay_value(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPATH_DELAY_VALUE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pcontrol_terminal ::= scalar _expression 
     */
    void pcontrol_terminal(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPCONTROL_TERMINAL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * polarity_operator ::= + | - 
     */
    void polarity_operator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPOLARITY_OPERATOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * port ::= [ port_expression ] | . port _identifier ( [ port_expression ] ) 
     */
    void port(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * port_expression ::= port_reference | { port_reference { , port_reference } } 
     */
    void port_expression(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_EXPRESSION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * port_reference ::= port _identifier <br>
     *                 | port _identifier [ constant_expression ] <br>
     *                 | port _identifier [ msb _constant_expression : lsb _constant_expression ] 
     */
    void port_reference(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPORT_REFERENCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * primary ::= number | identifier | identifier [ expression ] <br>
     *         | identifier [ msb _constant_expression : lsb _constant_expression ] <br>
     *         | concatenation | multiple_concatenation | function_call <br>
     *         | ( mintypmax_expression ) 
     */
    void primary(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPRIMARY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * procedural_continuous_assignment ::= <b>assign</b> reg_assignment ; <br>
     *                                     | <b>deassign</b> reg_lvalue ; | <b>force</b> reg_assignment ; <br>
     *                                     | <b>force</b> net_assignment ; | <b>release</b> reg_lvalue ; <br>
     *                                     | <b>release</b> net_lvalue ; 
     */
    void procedural_continuous_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURAL_CONTINUOUS_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * procedural_timing_control_statement ::= delay_or_event_control statement_or_null 
     */
    void procedural_timing_control_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPROCEDURAL_TIMING_CONTROL_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pulldown_strength ::= ( strength0 , strength1 ) <br>
     *                     | ( strength1 , strength0 ) | ( strength0 ) 
     */
    void pulldown_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPULLDOWN_STRENGTH);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pullup_strength ::= ( strength0 , strength1 ) <br>
     *                 | ( strength1 , strength0 ) | ( strength1 ) 
     */
    void pullup_strength(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPULLUP_STRENGTH);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pull_gate_instance ::= [ name_of_gate_instance ] ( output_terminal ) 
     */
    void pull_gate_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPULL_GATE_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * pulse_control_specparam ::= PATHPULSE$ = ( reject _limit_value [ , error _limit_value ] ) ; <br>
     *                 | PATHPULSE$ specify_input_terminal_descriptor $ specify_output_terminal_descriptor <br>
     *                     = ( reject _limit_value [ , error _limit_value ] ) ; 
     */
    void pulse_control_specparam(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTPULSE_CONTROL_SPECPARAM);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * range ::= [ msb _constant_expression : lsb _constant_expression ] 
     */
    void range(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * range_or_type ::= range | <b>integer</b> | real | realtime | <b>time</b> 
     */
    void range_or_type(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTRANGE_OR_TYPE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * realtime_declaration ::= realtime list_of_real_identifiers ; 
     */
    void realtime_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREALTIME_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * real_declaration ::= real list_of_real_identifiers ; 
     */
    void real_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREAL_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * real_number ::= [ sign ] unsigned_number . unsigned_number <br>
     *             | [ sign ] unsigned_number [ . unsigned_number] e [ sign ] unsigned_number <br>
     *             | [ sign ] unsigned_number [ . unsigned_number] e [ sign ] unsigned_number 
     */
//    void real_number(IASTNode p, Token endToken) throws ParserException {
//        ASTNode node = new ASTNode(p, ASTREAL_NUMBER);
//        openNodeScope(node);
//        closeNodeScope(node);
//    }

    /**
     * register_name ::= register _identifier <br>
     *                 | memory _identifier [ upper_limit _constant_expression : <br>
     *                     lower_limit _constant_expression ] 
     */
    void register_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREGISTER_NAME);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * reg_assignment ::= reg_lvalue = expression 
     */
    void reg_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREG_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * reg_declaration ::= <b>reg</b> [ range ] list_of_register_identifiers ; 
     */
    void reg_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREG_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * reg_lvalue ::= <b>reg</b> _identifier | <b>reg</b> _identifier [ expression ] <br>
     *             | <b>reg</b> _identifier [ msb _constant_expression : lsb _constant_expression ] <br>
     *             | <b>reg</b> _concatenation 
     */
    void reg_lvalue(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTREG_LVALUE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * scalar_constant ::= 1¡¯b0 | 1¡¯b1 | 1¡¯B0 | 1¡¯B1 | ¡¯b0 | ¡¯b1 | ¡¯B0 | ¡¯B1 | 1 | 0 
     */
    void scalar_constant(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_CONSTANT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * scalar_timing_check_condition ::= expression <br>
     *                             | ~ expression | expression == scalar_constant <br>
     *                             | expression === scalar_constant <br>
     *                             | expression != scalar_constant <br>
     *                             | expression !== scalar_constant 
     */
    void scalar_timing_check_condition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSCALAR_TIMING_CHECK_CONDITION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * sequential_body ::= [ udp_initial_statement ] <br>
     *                 <b>table</b> sequential_entry { sequential_entry } <b>endtable</b> 
     */
    void sequential_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQUENTIAL_BODY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * sequential_entry ::= seq_input_list : current_state : next_state ; 
     */
    void sequential_entry(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQUENTIAL_ENTRY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * seq_block ::= <b>begin</b> [ : block _identifier <br>
     *                 { block_item_declaration } ] { statement } <br>
     *               <b>end</b> 
     */
    void seq_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQ_BLOCK);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * seq_input_list ::= level_input_list | edge_input_list 
     */
    void seq_input_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSEQ_INPUT_LIST);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * sign ::= + | - 
     */
    void sign(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIGN);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * simple_identifier ::= [ a-zA-Z_ ][ a-zA-Z_$0-9 ] 
     */
    void simple_identifier(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMPLE_IDENTIFIER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * simple_path_declaration ::= parallel_path_description = path_delay_value <br>
     *                         | full_path_description = path_delay_value 
     */
    void simple_path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIMPLE_PATH_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * size ::= unsigned_number 
     */
    void size(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSIZE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * source_text ::= { description } 
     */
    void source_text(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSOURCE_TEXT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specify_block ::= <b>specify</b> [ specify_item ] <b>endspecify</b> 
     */
    void specify_block(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_BLOCK);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specify_input_terminal_descriptor ::= input_identifier <br>
     *                             | input_identifier [ constant_expression ] <br>
     *                             | input_identifier [ msb _constant_expression : lsb _constant_expression ] 
     */
    void specify_input_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_INPUT_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specify_item ::= specparam_declaration | path_declaration | system_timing_check 
     */
    void specify_item(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_ITEM);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specify_output_terminal_descriptor ::= output_identifier <br>
     *                             | output_identifier [ constant_expression ] <br>
     *                             | output_identifier [ msb _constant_expression : lsb _constant_expression ] 
     */
    void specify_output_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_OUTPUT_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specify_terminal_descriptor ::= specify_input_terminal_descriptor <br>
     *                             | specify_output_terminal_descriptor 
     */
    void specify_terminal_descriptor(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECIFY_TERMINAL_DESCRIPTOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specparam_assignment ::= <b>specparam</b> _identifier = constant_expression | pulse_control_specparam 
     */
    void specparam_assignment(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECPARAM_ASSIGNMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * specparam_declaration ::= <b>specparam</b> list_of_specparam_assignments ; 
     */
    void specparam_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSPECPARAM_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * statement ::= blocking_assignment ; | non_blocking assignment ; <br>
     *             | procedural_continuous_assignments ; <br>
     *             | procedural_timing_control_statement | conditional_statement <br>
     *             | case_statement | loop_statement | wait_statement <br>
     *             | disable_statement | event_trigger | seq_block | par_block <br>
     *             | task_enable | system_task_enable 
     */
    void statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * statement_or_null ::= statement | ; 
     */
    void statement_or_null(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTATEMENT_OR_NULL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * state_dependent_path_declaration ::= <b>if</b> ( conditional_expression ) simple_path_declaration <br>
     *                                     | <b>if</b> ( conditional_expression ) edge_sensitive_path_declaration <br>
     *                                     | ifnone simple_path_declaration 
     */
    void state_dependent_path_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTATE_DEPENDENT_PATH_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * strength0 ::= <b>supply0</b> | <b>strong0</b> | <b>pull0</b> | <b>weak0</b> 
     */
    void strength0(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTRENGTH0);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * strength1 ::= <b>supply1</b> | <b>strong1</b> | <b>pull1</b> | <b>weak1</b> 
     */
    void strength1(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTRENGTH1);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * string ::= " { Any_ASCII_Characters_except_new_line } " 
     */
    void string(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSTRING);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * system_task_enable ::= system_task_name [ ( expression { , expression } ) ] ; 
     */
    void system_task_enable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_TASK_ENABLE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * system_task_name ::= $ identifier <br>
     *             Note: The $ may <b>not</b> be followed by a space. 
     */
    void system_task_name(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_TASK_NAME);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * system_timing_check ::= $setup ( timing_check_event , timing_check_event , <br>
     *                         timing_check_limit [ , notify_register ] ) ; <br>
     *                     | $hold ( timing_check_event , timing_check_event , <br>
     *                         timing_check_limit [ , notify_register ] ) ; <br>
     *                     | $period ( controlled_timing_check_event , timing_check_limit <br>
     *                         [ , notify_register ] ) ; <br>
     *                     | $width ( controlled_timing_check_event , timing_check_limit , <br>
     *                         constant_expression [ , notify_register ] ) ; <br>
     *                     | $skew ( timing_check_event , timing_check_event , <br>
     *                         timing_check_limit [ , notify_register ] ) ; <br>
     *                     | $recovery ( controlled_timing_check_event , timing_check_event , <br>
     *                         timing_check_limit [ , notify_register ] ) ; <br>
     *                     | $setuphold ( timing_check_event , timing_check_event , <br>
     *                         timing_check_limit , timing_check_limit [ , notify_register ] ) ; 
     */
    void system_timing_check(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTSYSTEM_TIMING_CHECK);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * task_argument_declaration ::= block_item_declaration | output_declaration | inout_declaration 
     */
    void task_argument_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTASK_ARGUMENT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * task_declaration ::= <b>task</b> <b>task</b> _identifier ; {task_item_declaration} statement_or_null <b>endtask</b> 
     */
    void task_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTASK_DECLARATION);
        openNodeScope(node);
        consumeToken(TASK);
        Token tmpToken = findToken(SEMICOLON, endToken);
        identifier(node, tmpToken);
        closeNodeScope(node);
    }

    /**
     * task_enable ::= <b>task</b> _identifier [ ( expression { , expression } ) ] ; 
     */
    void task_enable(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTASK_ENABLE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * time_declaration ::= <b>time</b> list_of_register_identifiers ; 
     */
    void time_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIME_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * timing_check_condition ::= scalar_timing_check_condition | ( scalar_timing_check_condition ) 
     */
    void timing_check_condition(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_CONDITION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * timing_check_event ::= [ timing_check_event_control ] <br>
     *                     specify_terminal_descriptor [ &&& timing_check_condition ] 
     */
    void timing_check_event(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_EVENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * timing_check_event_control ::= <b>posedge</b> | <b>negedge</b> | edge_control_specifier 
     */
    void timing_check_event_control(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_EVENT_CONTROL);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * timing_check_limit ::= expression 
     */
    void timing_check_limit(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTTIMING_CHECK_LIMIT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_body ::= combinational_body | sequential_body 
     */
    void udp_body(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_BODY);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_declaration ::= <b>primitive</b> udp _identifier ( udp_port_list ) ; <br>
     *                 udp_port_declaration { udp_port_declaration } udp_body <br>
     *                 <b>endprimitive</b> 
     */
    void udp_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_initial_statement ::= <b>initial</b> udp_output_port _identifier = init_val ; 
     */
    void udp_initial_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INITIAL_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_instance ::= [ name_of_udp_instance ] <br>
     *             ( output_port_connection , input_port_connection { , input_port_connection } ) 
     */
    void udp_instance(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INSTANCE);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_instantiation ::= udp _identifier [ drive_strength ] [ delay2 ] udp_instance { , udp_instance } ; 
     */
    void udp_instantiation(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_INSTANTIATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_port_declaration ::= output_declaration | input_declaration | reg_declaration 
     */
    void udp_port_declaration(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_PORT_DECLARATION);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * udp_port_list ::= output_port _identifier , input_port _identifier { , input_port _identifier } 
     */
    void udp_port_list(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUDP_PORT_LIST);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * unary_operator ::= + | - | ! | ~ | & | ~& | | | ~| | ^ | ~^ | ^~ 
     */
    void unary_operator(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUNARY_OPERATOR);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * unsigned_number ::= decimal_digit { _ | decimal_digit } 
     */
    void unsigned_number(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTUNSIGNED_NUMBER);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * wait_statement ::= <b>wait</b> ( expression ) statement_or_null 
     */
    void wait_statement(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWAIT_STATEMENT);
        openNodeScope(node);
        closeNodeScope(node);
    }

    /**
     * white_space ::= space | tab | newline 
     */
    void white_space(IASTNode p, Token endToken) throws ParserException {
        ASTNode node = new ASTNode(p, ASTWHITE_SPACE);
        openNodeScope(node);
        closeNodeScope(node);
    }
    
    @Override
    public CommentBlock[] getComment() {
        return null;
    }

    @Override
    public IASTNode getRoot() {
        return null;
    }

    @Override
    public ISymbol getSymbol(IASTNode node, String name) {
        return null;
    }

    @Override
    public IASTNode parse(String path) throws ParserException {
        return null;
    }

    @Override
    public IASTNode parse(Reader reader) throws ParserException {
        return null;
    }

    @Override
    public ISymbol getSymbol(IASTNode node, String[] names) {
        return null;
    }

    @Override
    public ISymbolTable getTableOfSymbol(IASTNode node, String name)
    {
        return null;
    }

}
