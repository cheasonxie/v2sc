package converter.vhdl;

import java.util.ArrayList;

import common.printFileAndLineNumber;

import parser.IASTNode;
import parser.vhdl.ASTNode;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlTokenConstants;
import converter.SCSymbol;
import converter.SCTreeNode;

public class SCVhdlNode extends SCTreeNode 
    implements SCVhdlConstants, VhdlASTConstants, VhdlTokenConstants
{
    static SCVhdlEntity_declaration curModule = null;
    public boolean targetIsLogical = false;
    
    int startLine = 0;
    int endLine = 0;
    ArrayList<String> myComment = new ArrayList<String>();
    String myLineComment = "";
    
    public SCVhdlNode(SCTreeNode p, ASTNode node)
    {
        super(p);
        if(node == null)
            return;

        targetIsLogical = node.isLogic();
        startLine = node.getFirstToken().beginLine;
        endLine = node.getLastToken().endLine;
        if(curFileNode == null)
            return;

        int nextLine = curFileNode.getNextComment();
        if(nextLine == startLine && startLine == endLine) {
            ArrayList<String> com = curFileNode.getComment(nextLine);
            if(com != null)
                myLineComment = "        " + com.get(0);
        } else {    
            while(nextLine < startLine) {
                ArrayList<String> com = curFileNode.getComment(nextLine);
                if(com == null)
                    break;
                myComment.addAll(com);
                int newNextLine = curFileNode.getNextComment();
                if(newNextLine < startLine)
                {
                    // insert several empty lines
                    for(int i = 0; i < newNextLine-nextLine-1; i++)
                        myComment.add("");
                }
                nextLine = newNextLine;
            }
        }

    }
    
    @Override
    public String toString()
    {
        String tab = intent();
        String ret = "";
        int i = 0;
        
        //if(myComment.size() > 0)
        //    ret += "\r\n";
        for(i = 0; i < myComment.size(); i++)
        {
            ret += tab + myComment.get(i) + "\r\n";
        }
        ret += postToString();
        int len = ret.length();
        if(startLine == endLine && ret.endsWith("\r\n")
                && !myLineComment.isEmpty()) {
            ret = ret.substring(0, len-2);
            ret += myLineComment + "\r\n";
        }

        return ret;
    }
    
    public String postToString() {
        return "";
    }
    
    protected void warning(String msg) {
        System.out.println("line--" + startLine + ": warning: " + msg);
    }
    
    protected void error() {
        System.err.println("line--" + startLine + ": ");
        new printFileAndLineNumber("=========not support========");
    }
    
    protected void error(String msg) {
        System.err.println("line--" + startLine + ": ");
        new printFileAndLineNumber(msg);
    }
    
    public void checkAndAddSymbol(String name)
    {
        name.toLowerCase();
        SCSymbol sym = getSymbol(name);
        if(sym == null)
        {
            sym = new SCSymbol(SC_SUBPROGRAM, name, null);
            getFileNode().curBlockSymbol.add(sym);
        }
    }
    
    //TODO get bitwidth of symbol
    int getSymbolBitWidth(String name)
    {
        int ret = 1;
        SCSymbol sym = getSymbol(name);
        if(sym != null && isRangeValid(sym.range)) {
            int lid1 = sym.range.length - 3;
            int lid2 = sym.range.length - 1;
            if(Character.isDigit(sym.range[lid1].charAt(0))
                    && Character.isDigit(sym.range[lid2].charAt(0))) {
                int n1 = Integer.parseInt(sym.range[lid1]);
                int n2 = Integer.parseInt(sym.range[lid2]);
                if(n2 > n1)
                    ret = n2 - n1 + 1;
                else
                    ret = n1 - n2 + 1;
            }else{
                SCSymbol sym1 = getSymbol(sym.range[lid1]);
                SCSymbol sym2 = getSymbol(sym.range[lid2]);
                if(sym1 != null && sym2 != null) {
                    if(Character.isDigit(sym1.value.charAt(0))
                            && Character.isDigit(sym1.value.charAt(2))) {
                        int n1 = Integer.getInteger(sym.range[lid1]);
                        int n2 = Integer.getInteger(sym.range[lid2]);
                        if(n2 > n1)
                            ret = n2 - n1 + 1;
                        else
                            ret = n1 - n2 + 1;
                    }
                }else{
                    ret = 32;   // default
                }
            }
        }else if(sym != null){
            switch(sym.type)
            {
            case SC_BIT:
            case SC_LOGIC:
            case SC_BOOL:
                ret = 1;
                break;
            case SC_C_UINT:
            case SC_UINT:
            case SC_C_INT:
            case SC_INT:
            case SC_FLOAT:
                ret = 32;
                break;
           default:
               ret = 1;
               break;
            }
        }
            
        return ret;
    }
    
    static String getNumber(String str, int addition)
    {
        int index, index2;
        int radix = 10;
        
        index = str.indexOf('#');
        index2 = str.lastIndexOf('#');
        radix = 10;
        String ret = "";

        if (index > 0) {
            assert (index2 > index);
            radix = Integer.parseInt(str.substring(0, index));
            str = str.substring(index + 1, index2);
        }
        
        int num = Integer.parseInt(str, radix) + addition;

        switch (radix) {
        case 16:
            ret = String.format("0x%x", num);
            break;
        case 2:
        case 8:
        case 10:
            ret = String.format("%d", num);
            break;
        default:
            break;
        }
        return ret;
    }
    
    int getScType(String type)
    {
        int ret = SC_INVALID_TYPE;
        ret = getSymbolType(type);
        if(ret != SC_INVALID_TYPE)
            return ret;
        
        int[] rtypes = replaceFastTypes;
        if (!fastSimulation)
            rtypes = replaceTypes;

        for (int i = 0; i < rtypes.length; i++) {
            if (type.equalsIgnoreCase(vhdlTypes[i])) {
                ret = rtypes[i];
                break;
            }
        }
        return ret;
    }
    
    boolean isRangeValid(String[] range)
    {
        return ((range != null) && (range[0] != null)
                && (range[0].length() > 0));
    }
    
    String[] getScRange(String type, String[] oldrange)
    {
        int i = 0;
        String[] ret = oldrange;
        
        SCSymbol sym = getSymbol(type);
        if(sym != null)
        {
            int oldlen = 0;
            int newlen = 0;
            if(ret != null && ret[0] != null && ret[0].length() > 0)
                oldlen = ret.length;
            if(isRangeValid(sym.range))
                newlen = sym.range.length;

            String[] retTmp = null;

            if(newlen > 0 && oldlen > 0)
            {
                retTmp = new String[oldlen+newlen];
                for(i = 0; i < newlen; i++)
                    retTmp[i] = sym.range[i];
                for(i = 0; i < oldlen; i++)
                    retTmp[i+newlen] = ret[i];
            }else if(newlen == 0) {
                retTmp = ret;
            }else {
                retTmp = sym.range;
            }
            
            ret = retTmp;
        }
        return ret;
    }
    
    String getReplaceType(String type)
    {
        String ret = type;
        int[] rtypes = replaceFastTypes;
        if (!fastSimulation)
            rtypes = replaceTypes;

        for (int i = 0; i < rtypes.length; i++) {
            if (type.equalsIgnoreCase(vhdlTypes[i])) {
                ret = scType[rtypes[i]];
                break;
            }
        }
        return ret;
    }
    
    static boolean isBracketClose(String str, String strBracket)
    {
        boolean ret = false;
        if(str.charAt(0) == strBracket.charAt(0)
                && str.charAt(str.length()- 1) == strBracket.charAt(1)) {
            int num = 0;
            for(int i = 1; i < str.length() - 1; i++) {
                if(str.charAt(i) == strBracket.charAt(0))
                    num ++;
                else if(str.charAt(i) == strBracket.charAt(1))
                    num --;
                if(num < 0)
                    break;
            }
            if(num >= 0)
                ret = true;
        }
        return ret;
    }
    
    static boolean needBracket(String str)
    {
        boolean ret = false;
        final String forbid = "|+-*/=?:!@#$%^&><\'\"";
        for(int i = 0; i < str.length(); i++)
        {
            if(forbid.indexOf(str.charAt(i)) >= 0)
            {
                ret = true;
                break;
            }
        }
        return ret;
    }

    static String roundBracket(String str, String strBracket, boolean force)
    {
        String ret = "";
        if(str.isEmpty())
            return "";
        if(!force && !needBracket(str))
            return str;
        if(isBracketClose(str, strBracket)) {
            ret = str;
        }else{
            ret = strBracket.charAt(0) + str + strBracket.charAt(1);
        }
        return ret;
    }
    
    // add "()" at two sides
    static String roundBracket(String str, boolean force)
    {
        return roundBracket(str, "()", force);
    }

    // eliminate "()"
    static String trimBracket(String input)
    {
        String ret = input;
        if(isBracketClose(input, "()"))
        {
            ret = input.substring(1, input.length() - 1);
        }
        return ret;
    }

    static String addOne(String input)
    {
        input = trimBracket(input);

        String str, ret;
        int index1 = input.indexOf('-');
        int index2 = input.indexOf('+');
        int index = -1, addition = 0;
        if (index1 > 0) {
            index = index1;
            addition = -1;
        } else if (index2 > 0) {
            index = index2;
            addition = 1;
        }

        ret = "";
        if (index > 0) {
            String strtmp;
            str = input.substring(index + 1);
            str = str.trim();
            if (Character.isDigit(str.charAt(0)) || str.charAt(0) == '#')
                strtmp = getNumber(str, addition);
            else
                strtmp = str;

            strtmp = strtmp.trim();
            ret += input.substring(0, index).trim();
            if (!strtmp.equals("0") && !strtmp.equals("0x0")) {
                ret += " " + input.charAt(index) + " " + strtmp;
            }
        } else {
            if (Character.isDigit(input.charAt(0)) || input.charAt(0) == '#')
            {
                ret += getNumber(input, 1);
            }
            else
            { 
                ret += input + " + 1";
                ret = roundBracket(ret, false);
            }
        }
        return ret;
    }

    // vector type
    String getReplaceType(String type, String[] range)
    {
        String ret = getReplaceType(type);
        if (!isRangeValid(range)) {
            if (ret.equals(scType[SC_UINT]))
                ret = "sc_uint_base";
            return ret;
        }

        String from = range[0];
        String dir = range[1];
        String to = range[2];
        String max = to;
        if (dir.equalsIgnoreCase(RANGE_DOWNTO))
            max = from;

        ret += "<" + addOne(max) + ">";
        return ret;
    }

    String getReplaceOperator(String token)
    {
        String ret = token;
        for (int i = 0; i < vhdlOperators.length; i++) {
            if (token.equalsIgnoreCase(vhdlOperators[i])) {
                if (targetIsLogical)
                    ret = scOperators[replaceBooleanOp[i]];
                else
                    ret = scOperators[replaceOp[i]];
                break;
            }
        }
        return ret;
    }
    
    static String getReplaceValue(String str)
    {
        str.trim();

        String ret = str;
        if ((str.equals("\'0\'")) || (str.equalsIgnoreCase("false")))
            ret = "0";
        else if ((str.equals("\'1\'")) || (str.equalsIgnoreCase("true")))
            ret = "1";
        return ret;
    }
    
    static String getSCTime(String vhdlTime) {
        String ret = scTimeScale[SC_NS];
        for(int i = 0; i < vhdlTimeScale.length; i++)
        {
            if(vhdlTime.equalsIgnoreCase(vhdlTimeScale[i])) {
                ret = scTimeScale[i];
                break;
            }
        }
        return ret;
    }
    
    /**
     * <dl> base ::=
     *   <dd> integer
     */
    static String getBase(String str) {
        return getInteger(str);
    }

    /**
     * <dl> base_specifier ::=
     *   <dd> b | o | x
     */
    static int getBase_specifier(String str) {
        int ret = 2;
        if(str.equalsIgnoreCase("b")) {
            ret = 2;
        }else if(str.equalsIgnoreCase("o")) {
            ret = 8;
        }else if(str.equalsIgnoreCase("x")) {
            ret = 16;
        }
        return ret;
    }

    /**
     * <dl> based_integer ::=
     *   <dd> extended_digit { [underline ] extended_digit }
     */
    static String getBased_integer(String str) {
        return getInteger(str);
    }
    
    /**
     * <dl> exponent ::=
     *   <dd> e [ + ] integer | e - integer
     */
    static String getExponent(String str) {
        return str;     //TODO vhdl is the same as c language?
    }

    /**
     * <dl> integer ::=
     *   <dd> digit { [ underline ] digit }
     */
    static String getInteger(String str) {
        String ret = "";
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != '_') {
                ret += str.charAt(i);
            }
        }
        return ret;
    }
    
    /**
     * <dl> basic_identifier ::=
     *   <dd> letter { [ underline ] letter_or_digit }
     */
    static String getBasic_identifier(String str) {
        return str;
    }
    
    /**
     * <dl> bit_value ::=
     *   <dd> extended_digit { [ underline ] extended_digit }
     */
    static String getBit_value(String str) {
        return getBased_integer(str);
    }
}

class SCVhdlToken extends SCVhdlNode {
    String image = null;
    public SCVhdlToken(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTVOID);
        image = node.firstTokenImage();
    }

    public String postToString() {
        return image;
    }
}

/**
 * <dl> abstract_literal ::=
 *   <dd> decimal_literal | based_literal
 */
class SCVhdlAbstract_literal extends SCVhdlNode {
    SCVhdlNode thisNode = null;
    public SCVhdlAbstract_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTABSTRACT_LITERAL);
        int kind = node.getFirstToken().kind;
        if(kind == decimal_literal) {
            thisNode = new SCVhdlDecimal_literal(this, node);
        }else if(kind == based_literal) {
            thisNode = new SCVhdlBased_literal(this, node);
        }
    }

    public String postToString() {
        return thisNode.postToString();
    }
}

/**
 * <dl> access_type_definition ::=
 *   <dd> <b>access</b> subtype_indication
 */
class SCVhdlAccess_type_definition extends SCVhdlNode {
    SCVhdlNode sub = null;
    public SCVhdlAccess_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTACCESS_TYPE_DEFINITION);
        sub = new SCVhdlSubtype_indication(this, (ASTNode)node.getChild(0));
    }

    public String postToString() {
        warning("token access ignored");
        return sub.postToString();
    }
}

/**
 * <dl> across_aspect ::=
 *   <dd> identifier_list
 *   [ tolerance_aspect ]
 *   [ := expression ] <b>across</b>
 */
class SCVhdlAcross_aspect extends SCVhdlNode {
    SCVhdlNode idlist = null;
    SCVhdlNode toleranceAspect = null;
    SCVhdlNode expression = null;
    public SCVhdlAcross_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTACROSS_ASPECT);
        int i;
        for(i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTIDENTIFIER_LIST:
                idlist = new SCVhdlIdentifier_list(this, (ASTNode)c);
                break;
            case ASTTOLERANCE_ASPECT:
                toleranceAspect = new SCVhdlTolerance_aspect(this, (ASTNode)c);
                break;
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, (ASTNode)c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        assert(idlist != null);
        String ret = idlist.postToString();
        if(toleranceAspect != null) {
            ret += toleranceAspect.postToString();
        }
        if(expression != null) {
            ret += " = ";
            ret += expression.postToString();
        }
        warning("token across ignored");
        return ret;
    }
}

/**
 * <dl> actual_designator ::=
 *   <dd> expression
 *   <br> | <i>signal_</i>name
 *   <br> | <i>variable_</i>name
 *   <br> | <i>file_</i>name
 *   <br> | <i>terminal_</i>name
 *   <br> | <i>quantity_</i>name
 *   <br> | <b>open</b>
 */
class SCVhdlActual_designator extends SCVhdlNode {
    SCVhdlNode subNode = null;
    boolean isOpen = false;
    public SCVhdlActual_designator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTACTUAL_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTEXPRESSION:
            subNode = new SCVhdlExpression(this, c);
            break;
        case ASTVOID:
            isOpen = true;
            break;
        case ASTNAME:
            subNode = new SCVhdlName(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        String ret = "";
        if(subNode != null)
            ret = subNode.postToString();
        return ret;
    }
}

/**
 * <dl> actual_parameter_part ::=
 *   <dd> <i>parameter_</i>association_list
 */
class SCVhdlActual_parameter_part extends SCVhdlNode {
    SCVhdlNode paramList = null;
    public SCVhdlActual_parameter_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTACTUAL_PARAMETER_PART);
        paramList = new SCVhdlAssociation_list(this, node);
    }

    public String postToString() {
        return paramList.postToString();
    }
}

/**
 * <dl> actual_part ::=
 *   <dd> actual_designator
 *   <br> | <i>function_</i>name ( actual_designator )
 *   <br> | type_mark ( actual_designator )
 */
class SCVhdlActual_part extends SCVhdlNode {
    SCVhdlNode name = null;
    SCVhdlNode designator = null;
    public SCVhdlActual_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTACTUAL_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            case ASTACTUAL_DESIGNATOR:
                designator = new SCVhdlActual_designator(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(((SCVhdlActual_designator)designator).isOpen) {
            warning("token open ignored");
        }
        if(name != null) {
            ret += name.postToString();
            ret += "(" + designator.postToString() + ")";
        }else {
            ret += designator.postToString();
        }
        return ret;
    }
}

/**
 * <dl> adding_operator ::=
 *   <dd> + | - | &
 */
class SCVhdlAdding_operator extends SCVhdlNode {
    SCVhdlToken token = null;
    public SCVhdlAdding_operator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTADDING_OPERATOR);
        token = new SCVhdlToken(this, node);
    }

    public String postToString() {
        return token.postToString();
    }
}

/**
 * <dl> aggregate ::=
 *   <dd> ( element_association { , element_association } )
 */
class SCVhdlAggregate extends SCVhdlNode {
    ArrayList<SCVhdlNode> elementList = new ArrayList<SCVhdlNode>();
    public SCVhdlAggregate(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTAGGREGATE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            SCVhdlNode n = new SCVhdlElement_association(this, (ASTNode)c);
            elementList.add(n);
        }
    }

    public String postToString() {
        String ret = "(";
        for(int i = 0; i < elementList.size(); i++) {
            ret += elementList.get(i).postToString();
            if(i < elementList.size() - 1) {
                ret += ", ";
            }
        }
        ret += ")";
        return ret;
    }
}

/**
 * <dl> alias_declaration ::=
 *   <dd> <b>alias</b> alias_designator [ : alias_indication ] <b>is</b> name [ signature ] ;
 */
class SCVhdlAlias_declaration extends SCVhdlNode {
    SCVhdlNode designator = null;
    SCVhdlNode indication = null;
    SCVhdlNode name = null;
    SCVhdlNode signature = null;
    public SCVhdlAlias_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTALIAS_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTALIAS_DESIGNATOR:
                designator = new SCVhdlAlias_designator(this, c);
                break;
            case ASTALIAS_INDICATION:
                indication = new SCVhdlAlias_indication(this, c);
                break;
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            case ASTSIGNATURE:
                signature = new SCVhdlSignature(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "define ";
        ret += designator.postToString();
        if(indication != null) {
            warning("alias indication ignored");
        }
        ret += " " + name.postToString();
        if(signature != null) {
            warning("alias signature ignored");
        }
        return ret;
    }
}

/**
 * <dl> alias_designator ::=
 *   <dd> identifier
 *   <br> | character_literal
 *   <br> | operator_symbol
 */
class SCVhdlAlias_designator extends SCVhdlNode {
    SCVhdlNode subNode = null;
    public SCVhdlAlias_designator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTALIAS_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        if(id != ASTIDENTIFIER) {
            warning("only support identifier alias");
        }
        switch(id)
        {
        case ASTIDENTIFIER:
            subNode = new SCVhdlIdentifier(this, c);
            break;
        case ASTVOID:   // character literal
            subNode = new SCVhdlToken(this, c);
            break;
        case ASTOPERATOR_SYMBOL:
            subNode = new SCVhdlOperator_symbol(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return subNode.postToString();
    }
}

/**
 * <dl> alias_indication ::=
 *   <dd> subtype_indication
 *   <br> | subnature_indication
 */
class SCVhdlAlias_indication extends SCVhdlNode {
    SCVhdlNode subNode = null;
    public SCVhdlAlias_indication(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTALIAS_INDICATION);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new SCVhdlSubtype_indication(this, c);
            break;
        case ASTSUBNATURE_INDICATION:
            subNode = new SCVhdlSubnature_indication(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return subNode.postToString();
    }
}

/**
 * <dl> allocator ::=
 *   <dd> <b>new</b> subtype_indication
 *   <br> | <b>new</b> qualified_expression
 */
class SCVhdlAllocator extends SCVhdlNode {
    SCVhdlNode subNode = null;
    public SCVhdlAllocator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTALLOCATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new SCVhdlSubtype_indication(this, c);
            break;
        case ASTQUALIFIED_EXPRESSION:
            subNode = new SCVhdlQualified_expression(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return subNode.postToString();
    }
}

/**
 * <dl> architecture_body ::=
 *   <dd> <b>architecture</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> architecture_declarative_part </ul>
 *   <b>begin</b>
 *   <ul> architecture_statement_part </ul>
 *   <b>end</b> [ <b>architecture</b> ] [ <i>architecture_</i>simple_name ] ;
 */
class SCVhdlArchitecture_body extends SCVhdlNode {
    SCVhdlNode identifier = null;
    SCVhdlNode entity_name = null;
    SCVhdlNode delarative_part = null;
    SCVhdlNode statement_part = null;
    public SCVhdlArchitecture_body(SCVhdlNode p, ASTNode node) {
        super(p, node);
        level = 0;
        assert(node.getId() == ASTARCHITECTURE_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTNAME:
                entity_name = new SCVhdlName(this, c);
                assert(curModule != null 
                        && curModule.getName() == entity_name.postToString());
                curModule.setArchitectureBody(this);
                break;
            case ASTARCHITECTURE_DECLARATIVE_PART:
                delarative_part = new SCVhdlArchitecture_declarative_part(this, c);
                break;
            case ASTARCHITECTURE_STATEMENT_PART:
                statement_part = new SCVhdlArchitecture_statement_part(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = delarative_part.postToString() + "\r\n";
        ret += statement_part.postToString();
        return ret;
    }
}

/**
 * <dl> architecture_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class SCVhdlArchitecture_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> itemList = new ArrayList<SCVhdlNode>();
    public SCVhdlArchitecture_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTARCHITECTURE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = new SCVhdlBlock_declarative_item(this, c);
            itemList.add(newNode);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).postToString();
            if(i < itemList.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> architecture_statement ::=
 *   <dd> simultaneous_statement
 *   <br> | concurrent_statement
 */
class SCVhdlArchitecture_statement extends SCVhdlNode {
    SCVhdlNode itemNode = null;
    public SCVhdlArchitecture_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTARCHITECTURE_STATEMENT);
        switch(node.getId())
        {
        case ASTSIMULTANEOUS_STATEMENT:
            itemNode = new SCVhdlSimultaneous_statement(this, node);
            break;
        case ASTCONCURRENT_STATEMENT:
            itemNode = new SCVhdlConcurrent_statement(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return itemNode.toString();
    }
}

/**
 * <dl> architecture_statement_part ::=
 *   <dd> { architecture_statement }
 */
class SCVhdlArchitecture_statement_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> itemList = new ArrayList<SCVhdlNode>();
    public SCVhdlArchitecture_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTARCHITECTURE_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = new SCVhdlArchitecture_statement(this, c);
            itemList.add(newNode);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).postToString();
            if(i < itemList.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> array_nature_definition ::=
 *   <dd> unconstrained_nature_definition | constrained_nature_definition
 */
class SCVhdlArray_nature_definition extends SCVhdlNode {
    SCVhdlNode itemNode = null;
    public SCVhdlArray_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTARRAY_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTUNCONSTRAINED_NATURE_DEFINITION:
                itemNode = new SCVhdlUnconstrained_nature_definition(this, c);
                break;
            case ASTCONSTRAINED_NATURE_DEFINITION:
                itemNode = new SCVhdlConstrained_nature_definition(this, c);
                break;
            default:
                break;
            }            
        }

    }

    public String postToString() {
        return itemNode.postToString();
    }
}

/**
 * <dl> array_type_definition ::=
 *   <dd> unconstrained_array_definition | constrained_array_definition
 */
class SCVhdlArray_type_definition extends SCVhdlNode {
    SCVhdlNode itemNode = null;
    public SCVhdlArray_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTARRAY_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTUNCONSTRAINED_ARRAY_DEFINITION:
            itemNode = new SCVhdlUnconstrained_array_definition(this, node);
            break;
        case ASTCONSTRAINED_ARRAY_DEFINITION:
            itemNode = new SCVhdlConstrained_array_definition(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return itemNode.postToString();
    }
}

/**
 * <dl> assertion ::=
 *   <dd> <b>assert</b> condition
 *   <ul> [ <b>report</b> expression ]
 *   <br> [ <b>severity</b> expression ] </ul>
 */
class SCVhdlAssertion extends SCVhdlNode {
    SCVhdlNode condition = null;
    SCVhdlNode report_exp = null;
    SCVhdlNode severity_exp = null;
    public SCVhdlAssertion(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTASSERTION);
        for(int i = 0; i < node.getChildrenNum(); i += 2) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTVOID);  // the first must be token
            String image = c.firstTokenImage(); 
            c = (ASTNode)node.getChild(i+1);
            if(image.equalsIgnoreCase(tokenImage[ASSERT])) {
                condition = new SCVhdlCondition(this, c);
            }else if(image.equalsIgnoreCase(tokenImage[REPORT])) {
                report_exp = new SCVhdlExpression(this, c);
            }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                severity_exp = new SCVhdlExpression(this, c);
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(report_exp != null) {
            ret += intent() + "if(!(" + condition.postToString() + "))\r\n";
            ret += intent(level+1) + "printf(\"";
            ret += report_exp.postToString() + "\");\r\n";
        }
        ret += "assert(" + condition.postToString() + ");";
        if(severity_exp != null) {
            warning("assertion severity ignored");
        }
        return ret;
    }
}

/**
 * <dl> assertion_statement ::=
 *   <dd> [ label : ] assertion ;
 */
class SCVhdlAssertion_statement extends SCVhdlNode {
    SCVhdlNode label = null;
    SCVhdlNode assertion = null;
    public SCVhdlAssertion_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTASSERTION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                label = new SCVhdlIdentifier(this, c);
                break;
            case ASTASSERTION:
                assertion = new SCVhdlAssertion(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(label != null) {
            warning("label " + label.postToString() + " ignored");
        }
        ret += assertion.postToString() + ";";
        return ret;
    }
}

/**
 * <dl> association_element ::=
 *   <dd> [ formal_part => ] actual_part
 */
class SCVhdlAssociation_element extends SCVhdlNode {
    SCVhdlNode formal_part = null;
    SCVhdlNode actual_part = null;
    public SCVhdlAssociation_element(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTASSOCIATION_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTFORMAL_PART:
                formal_part = new SCVhdlFormal_part(this, c);
                break;
            case ASTACTUAL_PART:
                actual_part = new SCVhdlActual_part(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(formal_part != null) {
            ret += formal_part.postToString();
            ret += "(" + actual_part.postToString() + ")";   //TODO write() ??
        }else {
            ret += actual_part.postToString();
        }
        return ret;
    }
}

/**
 * <dl> association_list ::=
 *   <dd> association_element { , association_element }
 */
class SCVhdlAssociation_list extends SCVhdlNode {
    ArrayList<SCVhdlNode> elements = new ArrayList<SCVhdlNode>();
    public SCVhdlAssociation_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTASSOCIATION_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++){
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTASSOCIATION_ELEMENT);
            SCVhdlNode newNode = new SCVhdlAssociation_element(this, c);
            elements.add(newNode);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < elements.size(); i++) {
            ret += elements.get(i).postToString();
            if(i < elements.size() - 1) {
                ret += ", ";
            }
        }
        return ret;
    }
}

/**
 * <dl> attribute_declaration ::=
 *   <dd> <b>attribute</b> identifier : type_mark ;
 */
class SCVhdlAttribute_declaration extends SCVhdlNode {
    public SCVhdlAttribute_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTATTRIBUTE_DECLARATION);
    }

    public String postToString() {
        error("user defined attribute not support!");
        return "";
    }
}

/**
 * <dl> attribute_designator ::=
 *   <dd> <i>attribute_</i>simple_name
 */
class SCVhdlAttribute_designator extends SCVhdlNode {
    SCVhdlNode name = null;
    public SCVhdlAttribute_designator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTATTRIBUTE_DESIGNATOR);
        name = new SCVhdlSimple_name(this, node);
    }

    public String postToString() {
        return name.postToString();
    }
}

/**
 * <dl> attribute_name ::=
 *   <dd> prefix [ signature ] ' attribute_designator [ ( expression { , expression } ) ]
 */
class SCVhdlAttribute_name extends SCVhdlNode {
    SCVhdlNode prefix = null;
    SCVhdlNode signature = null;
    SCVhdlNode designator = null;
    ArrayList<SCVhdlNode> expressions = new ArrayList<SCVhdlNode>();
    public SCVhdlAttribute_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTATTRIBUTE_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new SCVhdlPrefix(this, c);
                break;
            case ASTSIGNATURE:
                signature = new SCVhdlSignature(this, c);
                break;
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new SCVhdlAttribute_designator(this, c);
                break;
            case ASTEXPRESSION:
                {
                    SCVhdlNode exp  = new SCVhdlExpression(this, c);
                    expressions.add(exp);
                }
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += prefix.postToString();
        if(signature != null) {
            warning("signature ignored");
        }
        ret += "." + designator.postToString();
        ret += "(";
        if(expressions.size() > 0) {
            for(int i = 0; i < expressions.size(); i++) {
                ret += expressions.get(i).postToString();
                if(i < expressions.size() - 1) {
                    ret += ", ";
                }
            }
        }
        ret += ")";
        return ret;
    }
}

/**
 * <dl> attribute_specification ::=
 *   <dd> <b>attribute</b> attribute_designator <b>of</b> entity_specification <b>is</b> expression ;
 */
class SCVhdlAttribute_specification extends SCVhdlNode {
    SCVhdlNode designator = null;
    SCVhdlNode entity = null;
    SCVhdlNode expression = null;
    public SCVhdlAttribute_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTATTRIBUTE_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new SCVhdlAttribute_designator(this, c);
                break;
            case ASTENTITY_SPECIFICATION:
                entity = new SCVhdlEntity_specification(this, c);
                break;
            case ASTEXPRESSION:
                expression  = new SCVhdlExpression(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        error("user define attribute not support");
        return "";
    }
}

/**
 * <dl> based_literal ::=
 *   <dd> base # based_integer [ . based_integer ] # [ exponent ]
 */
class SCVhdlBased_literal extends SCVhdlNode {
    String base = "10";
    String based_integer = "0";
    String fract_based_integer = "";
    String exponent = "";
    public SCVhdlBased_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTBASED_LITERAL);
        String image = node.firstTokenImage();
        int index = image.indexOf('#');
        base = getBase(image.substring(0, index));
        int index1 = image.indexOf('.', index);
        int index2 = image.indexOf('#', index);
        if(index1 > 0) {
            based_integer = getBased_integer(image.substring(index+1, index1));
            if(index2 > 0) {
                fract_based_integer = getBased_integer(image.substring(index1+1, index2));
            }else {
                fract_based_integer = getBased_integer(image.substring(index1+1));
            }
        }else if(index2 > 0) {
            based_integer = getBased_integer(image.substring(index+1, index2));
            exponent = getExponent(image.substring(index2+1));
        }
    }

    public String postToString() {
        String ret = "";
        int radix = Integer.parseInt(base);
        if(radix == 16) {
            ret += "0x";
            ret += based_integer;
            if(!fract_based_integer.isEmpty()) {
                ret += "." + fract_based_integer;
            }
        }else {
            ret += Integer.parseInt(based_integer, radix);
            if(!fract_based_integer.isEmpty()) {
                ret += "." + Integer.parseInt(fract_based_integer, radix);
            }
        }
        ret += exponent;

        return ret;
    }
}

/**
 * <dl> basic_character ::=
 *   <dd>basic_graphic_character | format_effector
 */
//class SCVhdlBasic_character extends SCVhdlNode {
//    public SCVhdlBasic_character(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTBASIC_CHARACTER);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> basic_graphic_character ::=
 *   <dd> upper_case_letter | digit | special_character | space_character
 */
//class SCVhdlBasic_graphic_character extends SCVhdlNode {
//    public SCVhdlBasic_graphic_character(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTBASIC_GRAPHIC_CHARACTER);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> binding_indication ::=
 *   <dd> [ <b>use</b> entity_aspect ]
 *   <br> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ]
 *   
 *   <br><br>use with other nodes
 *   @see SCVhdlComponent_configuration
 *   @see SCVhdlConfiguration_specification
 */
class SCVhdlBinding_indication extends SCVhdlNode {
    SCVhdlNode entity = null;
    SCVhdlNode generic_map = null;
    SCVhdlNode port_map = null;
    public SCVhdlBinding_indication(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBINDING_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTENTITY_ASPECT:
                entity = new SCVhdlEntity_aspect(this, c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new SCVhdlGeneric_map_aspect(this, c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new SCVhdlPort_map_aspect(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        error();
        return ret;
    }
}

/**
 * <dl> bit_string_literal ::=
 *   <dd> base_specifier " [ bit_value ] "
 */
class SCVhdlBit_string_literal extends SCVhdlNode {
    int base = 2;
    String bit_value = "";
    public SCVhdlBit_string_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTBIT_STRING_LITERAL);
        String image = node.firstTokenImage();
        base = getBase_specifier(image.substring(0, 1));
        int index = image.indexOf('\"');
        int index1 = image.lastIndexOf('\"');
        bit_value = image.substring(index+1, index1).trim();
    }

    public String postToString() {
        String ret = "";
        int value = 0;
        switch(base)
        {
        case 2:
            ret = bit_value;
        case 16:
            ret = "0x" + bit_value;
            break;
        case 8:
        default:
            value = Integer.parseInt(bit_value, base);
            ret = String.format("0x%x", value);
            break;
        }
        return ret;
    }
}

/**
 * <dl> block_configuration ::=
 *   <dd> <b>for</b> block_specification
 *   <ul> { use_clause }
 *   <br> { configuration_item }
 *   </ul> <b>end</b> <b>for</b> ;
 *   <br><br>
 *   @see SCVhdlComponent_configuration
 *   @see SCVhdlConfiguration_declaration
 *   @see SCVhdlConfiguration_item
 */
class SCVhdlBlock_configuration extends SCVhdlNode {
    SCVhdlNode spec = null;
    SCVhdlNode use_clause = null;
    SCVhdlNode cfg_item = null;
    public SCVhdlBlock_configuration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_SPECIFICATION:
                spec = new SCVhdlBlock_specification(this, c);
                break;
            case ASTUSE_CLAUSE:
                use_clause = new SCVhdlUse_clause(this, c);
                break;
            case ASTCONFIGURATION_ITEM:
                cfg_item = new SCVhdlConfiguration_item(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        error();
        return ret;
    }
}

/**
 * <dl> block_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | signal_declaration
 *   <br> | <i>shared_</i>variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | component_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | configuration_specification
 *   <br> | disconnection_specification
 *   <br> | step_limit_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 *   <br> | nature_declaration
 *   <br> | subnature_declaration
 *   <br> | quantity_declaration
 *   <br> | terminal_declaration
 */
class SCVhdlBlock_declarative_item extends SCVhdlNode {
    SCVhdlNode itemNode = null;
    public SCVhdlBlock_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTBLOCK_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            itemNode = new SCVhdlSubprogram_declaration(this, node);
            break;
        case ASTSUBPROGRAM_BODY:
            itemNode = new SCVhdlSubprogram_body(this, node);
            break;
        case ASTTYPE_DECLARATION:
            itemNode = new SCVhdlType_declaration(this, node);
            break;
        case ASTSUBTYPE_DECLARATION:
            itemNode = new SCVhdlSubtype_declaration(this, node);
            break;
        case ASTCONSTANT_DECLARATION:
            itemNode = new SCVhdlConstant_declaration(this, node);
            break;
        case ASTSIGNAL_DECLARATION:
            itemNode = new SCVhdlSignal_declaration(this, node);
            break;
        case ASTVARIABLE_DECLARATION:
            itemNode = new SCVhdlVariable_declaration(this, node);
            break;
        case ASTFILE_DECLARATION:
            itemNode = new SCVhdlFile_declaration(this, node);
            break;
        case ASTALIAS_DECLARATION:
            itemNode = new SCVhdlAlias_declaration(this, node);
            break;
        case ASTCOMPONENT_DECLARATION:
            itemNode = new SCVhdlComponent_declaration(this, node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            itemNode = new SCVhdlAttribute_declaration(this, node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            itemNode = new SCVhdlAttribute_specification(this, node);
            break;
        case ASTCONFIGURATION_SPECIFICATION:
            itemNode = new SCVhdlConfiguration_specification(this, node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            itemNode = new SCVhdlDisconnection_specification(this, node);
            break;
        case ASTSTEP_LIMIT_SPECIFICATION:
            itemNode = new SCVhdlStep_limit_specification(this, node);
            break;
        case ASTUSE_CLAUSE:
            itemNode = new SCVhdlUse_clause(this, node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            itemNode = new SCVhdlGroup_template_declaration(this, node);
            break;
        case ASTGROUP_DECLARATION:
            itemNode = new SCVhdlGroup_declaration(this, node);
            break;
        case ASTNATURE_DECLARATION:
            itemNode = new SCVhdlNature_declaration(this, node);
            break;
        case ASTSUBNATURE_DECLARATION:
            itemNode = new SCVhdlSubnature_declaration(this, node);
            break;
        case ASTQUANTITY_DECLARATION:
            itemNode = new SCVhdlQuantity_declaration(this, node);
            break;
        case ASTTERMINAL_DECLARATION:
            itemNode = new SCVhdlTerminal_declaration(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        String ret = "";
        if(itemNode != null) {
            ret = intent() + itemNode.postToString();
        }
        return ret;
    }
}

/**
 * <dl> block_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class SCVhdlBlock_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlBlock_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlBlock_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> block_header ::=
 *   <dd> [ generic_clause
 *   <br>   [ generic_map_aspect ; ] ]
 *   <br> [ port_clause
 *   <br>   [ port_map_aspect ; ] ]
 */
class SCVhdlBlock_header extends SCVhdlNode {
    SCVhdlNode generic = null;
    SCVhdlNode generic_map = null;
    SCVhdlNode port = null;
    SCVhdlNode port_map = null;
    public SCVhdlBlock_header(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new SCVhdlGeneric_clause(this, c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new SCVhdlGeneric_map_aspect(this, c);
                break;
            case ASTPORT_CLAUSE:
                port = new SCVhdlPort_clause(this, c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new SCVhdlPort_map_aspect(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(generic != null) {
            ret += generic.postToString();
            if(generic_map != null) {
                ret += "\r\n" + generic_map.postToString();
            }
        }
        if(!ret.isEmpty()) {
            ret += "\r\n";
        }
        if(port != null) {
            ret += port.postToString();
            if(port_map != null) {
                ret += "\r\n" + port.postToString();
            }
        }
        return ret;
    }
}

/**
 * <dl> block_specification ::=
 *   <dd> <i>architecture_</i>name
 *   <br> | <i>block_statement_</i>label
 *   <br> | <i>generate_statement_</i>label [ ( index_specification ) ]
 */
class SCVhdlBlock_specification extends SCVhdlNode {
    SCVhdlNode name = null;
    SCVhdlNode index_spec = null;
    public SCVhdlBlock_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            case ASTIDENTIFIER:
                name = new SCVhdlLabel(this, c);
                break;
            case ASTINDEX_SPECIFICATION:
                index_spec = new SCVhdlIndex_specification(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += name.postToString();
        if(index_spec != null) {
            ret += "(" + index_spec.postToString() + ")";   //TODO: modify here
        }
        return ret;
    }
}

/**
 * <dl> block_statement ::=
 *   <dd> <i>block_</i>label :
 *   <ul> <b>block</b> [ ( <i>guard_</i>expression ) ] [ <b>is</b> ]
 *   <ul> block_header
 *   <br> block_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> block_statement_part
 *   </ul> <b>end</b> <b>block</b> [ <i>block_</i>label ] ; </ul>
 */
class SCVhdlBlock_statement extends SCVhdlNode {
    SCVhdlNode header = null;
    SCVhdlNode declarative_part = null;
    SCVhdlNode statement_part = null;
    public SCVhdlBlock_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_HEADER:
                header = new SCVhdlBlock_header(this, c);
                break;
            case ASTBLOCK_DECLARATIVE_PART:
                declarative_part = new SCVhdlBlock_declarative_part(this, c);
                break;
            case ASTBLOCK_STATEMENT_PART:
                statement_part = new SCVhdlBlock_statement_part(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        String tab = intent(level+1);
        ret += intent() + "{";
        ret += tab + header.postToString() + "\r\n";
        ret += tab + declarative_part.postToString() + "\r\n";
        ret += tab + statement_part.postToString() + "\r\n";
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> block_statement_part ::=
 *   <dd> { architecture_statement }
 */
class SCVhdlBlock_statement_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlBlock_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBLOCK_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlArchitecture_statement(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> branch_quantity_declaration ::=
 *   <dd> <b>quantity</b> [ across_aspect ] [ through_aspect ] terminal_aspect ;
 */
class SCVhdlBranch_quantity_declaration extends SCVhdlNode {
    SCVhdlNode across = null;
    SCVhdlNode through = null;
    SCVhdlNode terminal = null;
    public SCVhdlBranch_quantity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBRANCH_QUANTITY_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTACROSS_ASPECT:
                across = new SCVhdlAcross_aspect(this, c);
                break;
            case ASTTHROUGH_ASPECT:
                through = new SCVhdlThrough_aspect(this, c);
                break;
            case ASTTERMINAL_ASPECT:
                terminal = new SCVhdlTerminal_aspect(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        error();
        return "";
    }
}

/**
 * <dl> break_element ::=
 *   <dd> [ break_selector_clause ] <i>quantity_</i>name => expression
 */
class SCVhdlBreak_element extends SCVhdlNode {
    SCVhdlNode selector = null;
    SCVhdlNode name = null;
    SCVhdlNode expression = null;
    public SCVhdlBreak_element(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBREAK_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBREAK_SELECTOR_CLAUSE:
                selector = new SCVhdlBreak_selector_clause(this, c);
                break;
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        error();
        return "";
    }
}

/**
 * <dl> break_list ::=
 *   <dd> break_element { , break_element }
 */
class SCVhdlBreak_list extends SCVhdlNode {
    ArrayList<SCVhdlNode> elements = new ArrayList<SCVhdlNode>();
    public SCVhdlBreak_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBREAK_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTBREAK_ELEMENT);
            SCVhdlNode ele = new SCVhdlBreak_element(this, c);
            elements.add(ele);
        }
    }

    public String postToString() {
        error();
        return "";
    }
}

/**
 * <dl> break_selector_clause ::=
 *   <dd> <b>for</b> <i>quantity_</i>name <b>use</b>
 */
class SCVhdlBreak_selector_clause extends SCVhdlNode {
    SCVhdlName name = null;
    public SCVhdlBreak_selector_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBREAK_SELECTOR_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            default:
                break;
            }
        }        
    }

    public String postToString() {
        error();
        return "";
    }
}

/**
 * <dl> break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ <b>when</b> condition ] ;
 */
class SCVhdlBreak_statement extends SCVhdlNode {
    SCVhdlBreak_list break_list = null;
    SCVhdlCondition condition = null;
    public SCVhdlBreak_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTBREAK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new SCVhdlCondition(this, c);
                break;
            case ASTBREAK_LIST:
                break_list = new SCVhdlBreak_list(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        error();
        return "";
    }
}

/**
 * <dl> case_statement ::=
 *   <dd> [ <i>case_</i>label : ]
 *   <ul> <b>case</b> expression <b>is</b>
 *   <ul> case_statement_alternative
 *   <br> { case_statement_alternative }
 *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
 */
class SCVhdlCase_statement extends SCVhdlNode {
    SCVhdlNode expression = null;
    ArrayList<SCVhdlNode> statement_alt = new ArrayList<SCVhdlNode>();
    public SCVhdlCase_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCASE_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, c);
                break;
            case ASTCASE_STATEMENT_ALTERNATIVE:
                newNode = new SCVhdlCase_statement_alternative(this, c);
                statement_alt.add(newNode); 
                break;
            default:
                break;
            }
        }
    }
    
    private boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < statement_alt.size(); i++) {
            SCVhdlCase_statement_alternative alt = 
                (SCVhdlCase_statement_alternative)statement_alt.get(i);
            SCVhdlChoices choices = alt.getChoices();
            if(choices.hasRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public String postToString() {
        String ret = "";
        String val = expression.postToString();
        if(hasRange()) {
            for(int i = 0; i < statement_alt.size(); i++) {
                SCVhdlCase_statement_alternative alt = 
                    (SCVhdlCase_statement_alternative)statement_alt.get(i);
                SCVhdlChoices choices = alt.getChoices();
                ArrayList<SCVhdlChoice> items = choices.getItems();
                String tmp = "";
                boolean isElse = false;
                for(int j = 0; j < items.size(); j++) {
                    SCVhdlChoice choice = items.get(j);
                    if(choice.isRange()) {
                        SCVhdlDiscrete_range range = (SCVhdlDiscrete_range)choice.item;
                        tmp += "(" + val + " >= " + range.getMin() + " && ";
                        tmp += val + " <= " + range.getMax() + ")";
                    }else if(choice.isOthers()) {
                        isElse = true;
                        break;
                    }else {
                        tmp += val + " == " + choice.postToString();
                    }
                    
                    if(j < items.size() - 1) {
                        tmp += " || ";
                    }
                }
                
                if(isElse) {
                    ret += intent() + "else\r\n";
                }else if(i == 0) {
                    ret += intent() + "if(" + tmp + ")";
                }else {
                    ret += intent() + "else if(" + tmp + ")\r\n";
                }
                ret += intent() + "{\r\n";
                ret += intent(level+1) + alt.seq_statements.postToString() + "\r\n";
                ret += "}\r\n";
            }
        }else {
            ret += intent() + "switch(" + expression.postToString() + ")\r\n";
            ret += intent() + "{\r\n";
            for(int i = 0; i < statement_alt.size(); i++) {
                ret += statement_alt.get(i).postToString() + "\r\n";
            }
            ret += intent() + "}";
        }

        return ret;
    }
}

/**
 * <dl> case_statement_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> sequence_of_statements </ul>
 */
class SCVhdlCase_statement_alternative extends SCVhdlNode {
    SCVhdlChoices choices = null;
    SCVhdlNode seq_statements = null;
    public SCVhdlCase_statement_alternative(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCASE_STATEMENT_ALTERNATIVE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCHOICES:
                choices = new SCVhdlChoices(this, c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new SCVhdlSequence_of_statements(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public SCVhdlChoices getChoices() {
        return choices;
    }
    
    public String statementsString() {
        return seq_statements.postToString();
    }

    public String postToString() {
        String ret = "";
        ArrayList<SCVhdlChoice> items = choices.getItems();
        for(int i = 0; i < items.size(); i++) {
            SCVhdlChoice item = items.get(i);
            if(item.isOthers()){
                ret += intent() + "default:\r\n";
            }else {
                ret += intent() + "case " + item.postToString();
                ret += ":\r\n";
            }
        }
        ret += statementsString();
        ret += intent(level+1) + "break;";
        return ret;
    }
}

/**
 * <dl> character_literal ::=
 *   <dd> ' graphic_character '
 */
class SCVhdlCharacter_literal extends SCVhdlNode {
    String str = "";
    public SCVhdlCharacter_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTCHARACTER_LITERAL);
        str = node.firstTokenImage();
    }

    public String postToString() {
        return str;
    }
}

/**
 * <dl> choice ::=
 *   <dd> simple_expression
 *   <br> | discrete_range
 *   <br> | <i>element_</i>simple_name
 *   <br> | <b>others</b>
 */
class SCVhdlChoice extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlChoice(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCHOICE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSIMPLE_EXPRESSION:
            item = new SCVhdlSimple_expression(this, c);
            break;
        case ASTDISCRETE_RANGE:
            item = new SCVhdlDiscrete_range(this, c);
            break;
        case ASTIDENTIFIER:
            item = new SCVhdlSimple_name(this, c);
            break;
        case ASTVOID:
            item = new SCVhdlToken(this, c);
            break;
        default:
            break;
        }
    }
    
    public boolean isRange() {
        return (item instanceof SCVhdlDiscrete_range);
    }
    
    public boolean isOthers() {
        return (item instanceof SCVhdlToken);
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> choices ::=
 *   <dd> choice { | choice }
 */
class SCVhdlChoices extends SCVhdlNode {
    ArrayList<SCVhdlChoice> items = new ArrayList<SCVhdlChoice>();
    public SCVhdlChoices(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCHOICES);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            SCVhdlChoice item = new SCVhdlChoice(this, (ASTNode)node.getChild(i));
            items.add(item);
        }
    }
    
    public boolean hasRange() {
        boolean ret = false;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).isRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    ArrayList<SCVhdlChoice> getItems() {
        return items;
    }

    public String postToString() {
        String ret = "";
        return ret;
    }
}

/**
 * <dl> component_configuration ::=
 *   <dd> <b>for</b> component_specification
 *   <ul> [ binding_indication ; ]
 *   <br> [ block_configuration ]
 *   </ul> <b>end</b> <b>for</b> ;
 */
class SCVhdlComponent_configuration extends SCVhdlNode {
    SCVhdlNode spec = null;
    SCVhdlNode binding = null;
    SCVhdlNode block = null;
    public SCVhdlComponent_configuration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPONENT_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCOMPONENT_SPECIFICATION:
                spec = new SCVhdlComponent_specification(this, c);
                break;
            case ASTBINDING_INDICATION:
                binding = new SCVhdlBinding_indication(this, c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                block = new SCVhdlBlock_configuration(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> component_declaration ::=
 *   <dd> <b>component</b> identifier [ <b>is</b> ]
 *   <ul> [ <i>local_</i>generic_clause ]
 *   <br> [ <i>local_</i>port_clause ]
 *   </ul> <b>end</b> <b>component</b> [ <i>component_</i>simple_name ] ;
 */
class SCVhdlComponent_declaration extends SCVhdlNode {
    public SCVhdlComponent_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPONENT_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> component_instantiation_statement ::=
 *   <dd> <i>instantiation_</i>label :
 *   <ul> instantiated_unit
 *   <ul> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ] ; </ul></ul>
 */
class SCVhdlComponent_instantiation_statement extends SCVhdlNode {
    public SCVhdlComponent_instantiation_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPONENT_INSTANTIATION_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> component_specification ::=
 *   <dd> instantiation_list : <i>component_</i>name
 */
class SCVhdlComponent_specification extends SCVhdlNode {
    public SCVhdlComponent_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPONENT_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> composite_nature_definition ::=
 *   <dd> array_nature_definition
 *   <br> | record_nature_definition
 */
class SCVhdlComposite_nature_definition extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlComposite_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPOSITE_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTARRAY_NATURE_DEFINITION:
                item = new SCVhdlArray_nature_definition(this, c);
                break;
            case ASTRECORD_NATURE_DEFINITION:
                item = new SCVhdlRecord_nature_definition(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> composite_type_definition ::=
 *   <dd> array_type_definition
 *   <br> | record_type_definition
 */
class SCVhdlComposite_type_definition extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlComposite_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCOMPOSITE_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTARRAY_TYPE_DEFINITION:
            item = new SCVhdlArray_type_definition(this, c);
            break;
        case ASTRECORD_TYPE_DEFINITION:
            item = new SCVhdlRecord_type_definition(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> concurrent_assertion_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] assertion ;
 */
class SCVhdlConcurrent_assertion_statement extends SCVhdlNode {
    public SCVhdlConcurrent_assertion_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONCURRENT_ASSERTION_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> concurrent_break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ sensitivity_clause ] [ <b>when</b> condition ] ;
 */
class SCVhdlConcurrent_break_statement extends SCVhdlNode {
    public SCVhdlConcurrent_break_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONCURRENT_BREAK_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> concurrent_procedure_call_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] procedure_call ;
 */
class SCVhdlConcurrent_procedure_call_statement extends SCVhdlNode {
    public SCVhdlConcurrent_procedure_call_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONCURRENT_PROCEDURE_CALL_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> concurrent_signal_assignment_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] conditional_signal_assignment
 *   <br> | [ label : ] [ <b>postponed</b> ] selected_signal_assignment
 */
class SCVhdlConcurrent_signal_assignment_statement extends SCVhdlNode {
    public SCVhdlConcurrent_signal_assignment_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> concurrent_statement ::=
 *   <dd> block_statement
 *   <br> | process_statement
 *   <br> | concurrent_procedure_call_statement
 *   <br> | concurrent_assertion_statement
 *   <br> | concurrent_signal_assignment_statement
 *   <br> | component_instantiation_statement
 *   <br> | generate_statement
 *   <br> | concurrent_break_statement
 */
class SCVhdlConcurrent_statement extends SCVhdlNode {
    public SCVhdlConcurrent_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONCURRENT_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> condition ::=
 *   <dd> <i>boolean_</i>expression
 */
class SCVhdlCondition extends SCVhdlNode {
    SCVhdlNode expression = null;
    public SCVhdlCondition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTCONDITION);
        expression = new SCVhdlExpression(this, node);
    }

    public String postToString() {
        return expression.postToString();
    }
}

/**
 * <dl> condition_clause ::=
 *   <dd> <b>until</b> condition
 */
class SCVhdlCondition_clause extends SCVhdlNode {
    public SCVhdlCondition_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONDITION_CLAUSE);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> conditional_signal_assignment ::=
 *   <dd> target <= options conditional_waveforms ;
 */
class SCVhdlConditional_signal_assignment extends SCVhdlNode {
    public SCVhdlConditional_signal_assignment(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONDITIONAL_SIGNAL_ASSIGNMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> conditional_waveforms ::=
 *   <dd> { waveform <b>when</b> condition <b>else</b> }
 *   <br> waveform [ <b>when</b> condition ]
 */
class SCVhdlConditional_waveforms extends SCVhdlNode {
    public SCVhdlConditional_waveforms(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONDITIONAL_WAVEFORMS);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> configuration_declaration ::=
 *   <dd> <b>configuration</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> configuration_declarative_part
 *   <br> block_configuration
 *   </ul><b>end</b> [ <b>configuration</b> ] [ <i>configuration_</i>simple_name ] ;
 */
class SCVhdlConfiguration_declaration extends SCVhdlNode {
    public SCVhdlConfiguration_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> configuration_declarative_item ::=
 *   <dd> use_clause
 *   <br> | attribute_specification
 *   <br> | group_declaration
 */
class SCVhdlConfiguration_declarative_item extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlConfiguration_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTCONFIGURATION_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTUSE_CLAUSE:
            item = new SCVhdlUse_clause(this, node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new SCVhdlAttribute_specification(this, node);
            break;
        case ASTGROUP_DECLARATION:
            item = new SCVhdlGroup_declaration(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> configuration_declarative_part ::=
 *   <dd> { configuration_declarative_item }
 */
class SCVhdlConfiguration_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlConfiguration_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlConfiguration_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> configuration_item ::=
 *   <dd> block_configuration
 *   <br> | component_configuration
 */
class SCVhdlConfiguration_item extends SCVhdlNode {
    public SCVhdlConfiguration_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONFIGURATION_ITEM);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> configuration_specification ::=
 *   <dd> <b>for</b> component_specification binding_indication ;
 */
class SCVhdlConfiguration_specification extends SCVhdlNode {
    public SCVhdlConfiguration_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONFIGURATION_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> constant_declaration ::=
 *   <dd> <b>constant</b> identifier_list : subtype_indication [ := expression ] ;
 */
class SCVhdlConstant_declaration extends SCVhdlNode {
    public SCVhdlConstant_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONSTANT_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> constrained_array_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> <i>element_</i>subtype_indication
 */
class SCVhdlConstrained_array_definition extends SCVhdlNode {
    public SCVhdlConstrained_array_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONSTRAINED_ARRAY_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> constrained_nature_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> subnature_indication
 */
class SCVhdlConstrained_nature_definition extends SCVhdlNode {
    public SCVhdlConstrained_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONSTRAINED_NATURE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> constraint ::=
 *   <dd> range_constraint
 *   <br> | index_constraint
 */
class SCVhdlConstraint extends SCVhdlNode {
    SCVhdlRange_constraint range = null;
    SCVhdlIndex_constraint index = null;
    public SCVhdlConstraint(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINDEX_CONSTRAINT:
            index = new SCVhdlIndex_constraint(this, c);
            break;
        default:
            break;
        }
    }
    
    public String getMin() {
        String ret = "0";
        if(range != null) {
            ret = range.getMin();
        }else {
            ret = index.getMin();
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(range != null) {
            ret = range.getMax();
        }else {
            ret = index.getMax();
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = false;
        if(range != null) {
            ret = range.isDownto();
        }else {
            ret = index.isDownto();
        }
        return ret;
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> context_clause ::=
 *   <dd> { context_item }
 */
class SCVhdlContext_clause extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlContext_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTCONTEXT_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlContext_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> context_item ::=
 *   <dd> library_clause
 *   <br> | use_clause
 */
class SCVhdlContext_item extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlContext_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTCONTEXT_ITEM);
        switch(node.getId())
        {
        case ASTLIBRARY_CLAUSE:
            item = new SCVhdlLibrary_clause(this, node);
            break;
        case ASTUSE_CLAUSE:
            item = new SCVhdlUse_clause(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        String ret = "";
        ret += item.postToString();
        return ret;
    }
}

/**
 * <dl> decimal_literal ::=
 *   <dd> integer [ . integer ] [ exponent ]
 */
class SCVhdlDecimal_literal extends SCVhdlNode {
    String literal = "";
    public SCVhdlDecimal_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTDECIMAL_LITERAL);
        literal = node.firstTokenImage();
    }

    public String postToString() {
        return literal;
    }
}

/**
 * <dl> declaration ::=
 *   <dd> type_declaration
 *   <br> | subtype_declaration
 *   <br> | object_declaration
 *   <br> | interface_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | component_declaration
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 *   <br> | entity_declaration
 *   <br> | configuration_declaration
 *   <br> | subprogram_declaration
 *   <br> | package_declaration
 *   <br> | nature_declaration
 *   <br> | subnature_declaration
 *   <br> | quantity_declaration
 *   <br> | terminal_declaration
 */
class SCVhdlDeclaration extends SCVhdlNode {
    public SCVhdlDeclaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTDECLARATION);
        // no use by others module
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> delay_mechanism ::=
 *   <dd> <b>transport</b>
 *   <br> | [ <b>reject</b> <i>time_</i>expression ] <b>inertial</b>
 */
class SCVhdlDelay_mechanism extends SCVhdlNode {
    public SCVhdlDelay_mechanism(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDELAY_MECHANISM);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> design_file ::=
 *   <dd> design_unit { design_unit }
 */
class SCVhdlDesign_file extends SCVhdlNode {
    ArrayList<SCVhdlNode> units = new ArrayList<SCVhdlNode>(); 
    public SCVhdlDesign_file(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDESIGN_FILE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTDESIGN_UNIT);
            SCVhdlNode unit = new SCVhdlDesign_unit(this, c);
            units.add(unit);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < units.size(); i++) {
            ret += units.get(i).postToString();
            if(i < units.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> design_unit ::=
 *   <dd> context_clause library_unit
 */
class SCVhdlDesign_unit extends SCVhdlNode {
    SCVhdlNode context_clause = null;
    SCVhdlNode library_unit = null;
    public SCVhdlDesign_unit(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDESIGN_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCONTEXT_CLAUSE:
                context_clause = new SCVhdlContext_clause(this, c);
                break;
            case ASTLIBRARY_UNIT:
                library_unit = new SCVhdlLibrary_unit(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += context_clause.postToString();
        ret += "\r\n";
        ret += library_unit.postToString();
        return ret;
    }
}

/**
 * <dl> designator ::=
 *   <dd> identifier | operator_symbol
 */
class SCVhdlDesignator extends SCVhdlNode {
    public SCVhdlDesignator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDESIGNATOR);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> direction ::=
 *   <dd> <b>to</b> | <b>downto</b>
 */
class SCVhdlDirection extends SCVhdlNode {
    String dir = "to";
    public SCVhdlDirection(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDIRECTION);
        dir = node.firstTokenImage();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> disconnection_specification ::=
 *   <dd> <b>disconnect</b> guarded_signal_specification <b>after</b> <i>time_</i>expression ;
 */
class SCVhdlDisconnection_specification extends SCVhdlNode {
    public SCVhdlDisconnection_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDISCONNECTION_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> discrete_range ::=
 *   <dd> <i>discrete_</i>subtype_indication | range
 */
class SCVhdlDiscrete_range extends SCVhdlNode {
    SCVhdlRange range = null;
    SCVhdlSubtype_indication subtype = null;
    public SCVhdlDiscrete_range(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTDISCRETE_RANGE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE:
            range = new SCVhdlRange(this, c);
            break;
        case ASTSUBTYPE_INDICATION:
            subtype = new SCVhdlSubtype_indication(this, c);
            break;
        default:
            break;
        }
    }
    
    public String getMin() {
        String ret = "0";
        if(range != null) {
            ret = range.getMin();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.getMin();
        }
        return ret;
    }
    public String getMax() {
        String ret = "0";
        if(range != null) {
            ret = range.getMax();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.getMax();
        }
        return ret;
    }
    public boolean isDownto() {
        boolean ret = false;
        if(range != null) {
            ret = range.isDownto();
        }else if(subtype.constraint != null){
            ret = subtype.constraint.isDownto();
        }
        return ret;
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> element_association ::=
 *   <dd> [ choices => ] expression
 */
class SCVhdlElement_association extends SCVhdlNode {
    public SCVhdlElement_association(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTELEMENT_ASSOCIATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> element_declaration ::=
 *   <dd> identifier_list : element_subtype_definition ;
 */
class SCVhdlElement_declaration extends SCVhdlNode {
    public SCVhdlElement_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTELEMENT_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> element_subnature_definition ::=
 *   <dd> subnature_indication
 */
class SCVhdlElement_subnature_definition extends SCVhdlNode {
    SCVhdlSubnature_indication item = null;
    public SCVhdlElement_subnature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTELEMENT_SUBNATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBNATURE_INDICATION:
                item = new SCVhdlSubnature_indication(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> element_subtype_definition ::=
 *   <dd> subtype_indication
 */
class SCVhdlElement_subtype_definition extends SCVhdlNode {
    SCVhdlSubtype_indication item = null;
    public SCVhdlElement_subtype_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTELEMENT_SUBTYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBNATURE_INDICATION:
                item = new SCVhdlSubtype_indication(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> entity_aspect ::=
 *   <dd> <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 *   <br> | <b>open</b>
 */
class SCVhdlEntity_aspect extends SCVhdlNode {
    public SCVhdlEntity_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_class ::=
 *   <dd> <b>entity</b>
 *   <br> | <b>architecture</b>
 *   <br> | <b>configuration</b>
 *   <br> | <b>procedure</b>
 *   <br> | <b>function</b>
 *   <br> | <b>package</b>
 *   <br> | <b>type</b>
 *   <br> | <b>subtype</b>
 *   <br> | <b>constant</b>
 *   <br> | <b>signal</b>
 *   <br> | <b>variable</b>
 *   <br> | <b>component</b>
 *   <br> | <b>label</b>
 *   <br> | <b>literal</b>
 *   <br> | <b>units</b>
 *   <br> | <b>group</b>
 *   <br> | <b>file</b>
 *   <br> | <b>nature</b>
 *   <br> | <b>subnature</b>
 *   <br> | <b>quantity</b>
 *   <br> | <b>terminal</b>
 */
class SCVhdlEntity_class extends SCVhdlNode {
    String image = "";
    public SCVhdlEntity_class(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTENTITY_CLASS);
        image = node.firstTokenImage();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_class_entry ::=
 *   <dd> entity_class [ <> ]
 */
class SCVhdlEntity_class_entry extends SCVhdlNode {
    public SCVhdlEntity_class_entry(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_class_entry_list ::=
 *   <dd> entity_class_entry { , entity_class_entry }
 */
class SCVhdlEntity_class_entry_list extends SCVhdlNode {
    public SCVhdlEntity_class_entry_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_declaration ::=
 *   <dd> <b>entity</b> identifier <b>is</b>
 *   <ul> entity_header
 *   <br> entity_declarative_part
 *   </ul> [ <b>begin</b>
 *   <ul> entity_statement_part ]
 *   </ul> <b>end</b> [ <b>entity</b> ] [ <i>entity_</i>simple_name ] ;
 */
class SCVhdlEntity_declaration extends SCVhdlNode {
    SCVhdlArchitecture_body body = null;
    SCVhdlNode identifier = null;
    SCVhdlNode header = null;
    SCVhdlNode declarative_part = null;
    SCVhdlNode statement_part = null;
    public SCVhdlEntity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_DECLARATION);
        level = 0;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTENTITY_HEADER:
                header = new SCVhdlEntity_header(this, c);
                break;
            case ASTENTITY_DECLARATIVE_PART:
                declarative_part = new SCVhdlEntity_declarative_part(this, c);
                break;
            case ASTENTITY_STATEMENT_PART:
                statement_part = new SCVhdlEntity_statement_part(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        ret += "SC_MODULE(" + getName() + ")\r\n{\r\n";
        ret += header.postToString() + "\r\n";
        ret += declarative_part.postToString() + "\r\n";
        if(statement_part != null) {
            ret += statement_part.postToString();
        }
        ret += "}\r\n";
        return ret;
    }
    
    public void setArchitectureBody(SCVhdlArchitecture_body body) {
        this.body = body;
    }
    
    public String getName() {
        return identifier.postToString();
    }
}

/**
 * <dl> entity_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | signal_declaration
 *   <br> | <i>shared_</i>variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | disconnection_specification
 *   <br> | step_limit_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 *   <br> | nature_declaration
 *   <br> | subnature_declaration
 *   <br> | quantity_declaration
 *   <br> | terminal_declaration
 */
class SCVhdlEntity_declarative_item extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlEntity_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTENTITY_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new SCVhdlSubprogram_declaration(this, node);
            break;
        case ASTSUBPROGRAM_BODY:
            item = new SCVhdlSubprogram_body(this, node);
            break;
        case ASTTYPE_DECLARATION:
            item = new SCVhdlType_declaration(this, node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new SCVhdlSubtype_declaration(this, node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new SCVhdlConstant_declaration(this, node);
            break;
        case ASTSIGNAL_DECLARATION:
            item = new SCVhdlSignal_declaration(this, node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVhdlVariable_declaration(this, node);
            break;
        case ASTFILE_DECLARATION:
            item = new SCVhdlFile_declaration(this, node);
            break;
        case ASTALIAS_DECLARATION:
            item = new SCVhdlAlias_declaration(this, node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new SCVhdlAttribute_declaration(this, node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new SCVhdlAttribute_specification(this, node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            item = new SCVhdlDisconnection_specification(this, node);
            break;
        case ASTSTEP_LIMIT_SPECIFICATION:
            item = new SCVhdlStep_limit_specification(this, node);
            break;
        case ASTUSE_CLAUSE:
            item = new SCVhdlUse_clause(this, node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new SCVhdlGroup_template_declaration(this, node);
            break;
        case ASTGROUP_DECLARATION:
            item = new SCVhdlGroup_declaration(this, node);
            break;
        case ASTNATURE_DECLARATION:
            item = new SCVhdlNature_declaration(this, node);
            break;
        case ASTSUBNATURE_DECLARATION:
            item = new SCVhdlSubnature_declaration(this, node);
            break;
        case ASTQUANTITY_DECLARATION:
            item = new SCVhdlQuantity_declaration(this, node);
            break;
        case ASTTERMINAL_DECLARATION:
            item = new SCVhdlTerminal_declaration(this, node);
            break;

        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> entity_declarative_part ::=
 *   <dd> { entity_declarative_item }
 */
class SCVhdlEntity_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlEntity_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlEntity_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> entity_designator ::=
 *   <dd> entity_tag [ signature ]
 */
class SCVhdlEntity_designator extends SCVhdlNode {
    public SCVhdlEntity_designator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_DESIGNATOR);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_header ::=
 *   <dd> [ <i>formal_</i>generic_clause ]
 *   <br> [ <i>formal_</i>port_clause ]
 */
class SCVhdlEntity_header extends SCVhdlNode {
    SCVhdlNode generic = null;
    SCVhdlNode port = null;
    public SCVhdlEntity_header(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new SCVhdlGeneric_clause(this, c);
                break;
            case ASTPORT_CLAUSE:
                port = new SCVhdlPort_clause(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(generic != null) {
            ret += generic.postToString();
        }
        if(!ret.isEmpty()) {
            ret += "\r\n";
        }
        if(port != null) {
            ret += port.postToString();
        }
        return ret;
    }
}

/**
 * <dl> entity_name_list ::=
 *   <dd> entity_designator { , entity_designator }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class SCVhdlEntity_name_list extends SCVhdlNode {
    public SCVhdlEntity_name_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_NAME_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_specification ::=
 *   <dd> entity_name_list : entity_class
 */
class SCVhdlEntity_specification extends SCVhdlNode {
    public SCVhdlEntity_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> entity_statement ::=
 *   <dd> concurrent_assertion_statement
 *   <br> | <i>passive_</i>concurrent_procedure_call_statement
 *   <br> | <i>passive_</i>process_statement
 */
class SCVhdlEntity_statement extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlEntity_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTENTITY_STATEMENT);
        switch(node.getId())
        {
        case ASTCONCURRENT_ASSERTION_STATEMENT:
            item = new SCVhdlConcurrent_assertion_statement(this, node);
            break;
        case ASTCONCURRENT_PROCEDURE_CALL_STATEMENT:
            item = new SCVhdlConcurrent_procedure_call_statement(this, node);
            break;
        case ASTPROCESS_STATEMENT:
            item = new SCVhdlProcess_statement(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> entity_statement_part ::=
 *   <dd> { entity_statement }
 */
class SCVhdlEntity_statement_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlEntity_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlEntity_statement(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).postToString();
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> entity_tag ::=
 *   <dd> simple_name
 *   <br> | character_literal
 *   <br> | operator_symbol
 */
class SCVhdlEntity_tag extends SCVhdlNode {
    public SCVhdlEntity_tag(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENTITY_TAG);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> enumeration_literal ::=
 *   <dd> identifier | character_literal
 */
class SCVhdlEnumeration_literal extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlEnumeration_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENUMERATION_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                item = new SCVhdlIdentifier(this, c);
                break;
            case ASTVOID:
                item = new SCVhdlCharacter_literal(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> enumeration_type_definition ::=
 *   <dd> ( enumeration_literal { , enumeration_literal } )
 */
class SCVhdlEnumeration_type_definition extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlEnumeration_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTENUMERATION_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                newNode = new SCVhdlEnumeration_literal(this, c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += "{";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += ", ";
            }
        }
        ret += "}";
        return ret;
    }
}

/**
 * <dl> exit_statement ::=
 *   <dd> [ label : ] <b>exit</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
 */
class SCVhdlExit_statement extends SCVhdlNode {
    SCVhdlCondition condition = null;
    public SCVhdlExit_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTEXIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new SCVhdlCondition(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "if(" + condition.postToString() + ")\r\n";
            ret += intent(level+1) + "break;";  //FIXME: may break in switch
        }else {
            ret += intent() + "break;";
        }
        if(parent instanceof SCVhdlCase_statement_alternative) {
            warning("break switch statement instead of loop!");
        }
        return ret;
    }
}

/**
 * <dl> expression ::=
 *   <dd> relation { <b>and</b> relation }
 *   <br> | relation { <b>or</b> relation }
 *   <br> | relation { <b>xor</b> relation }
 *   <br> | relation [ <b>nand</b> relation ]
 *   <br> | relation [ <b>nor</b> relation ]
 *   <br> | relation { <b>xnor</b> relation }
 */
class SCVhdlExpression extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlExpression(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTEXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTRELATION:
                newNode = new SCVhdlRelation(this, c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new SCVhdlToken(this, c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += items.get(0).postToString();
        for(int i = 1; i < items.size() - 1; i += 2){
            ret += " " + getReplaceOperator(items.get(i).postToString()) + " ";
            ret += items.get(i+1).postToString();
        }
        return ret;
    }
}

/**
 * <dl> extended_digit ::=
 *   <dd> digit | letter
 */
//class SCVhdlExtended_digit extends SCVhdlNode {
//    String image = 
//    public SCVhdlExtended_digit(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        //assert(node.getId() == ASTEXTENDED_DIGIT);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> extended_identifier ::=
 *   <dd> \ graphic_character { graphic_character } \
 */
//class SCVhdlExtended_identifier extends SCVhdlNode {
//    public SCVhdlExtended_identifier(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTEXTENDED_IDENTIFIER);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> factor ::=
 *   <dd> primary [ ** primary ]
 *   <br> | <b>abs</b> primary
 *   <br> | <b>not</b> primary
 */
class SCVhdlFactor extends SCVhdlNode {
    SCVhdlPrimary primary0 = null;
    SCVhdlNode operator = null;
    SCVhdlPrimary primary1 = null;
    public SCVhdlFactor(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFACTOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlPrimary newNode = null;
            switch(c.getId())
            {
            case ASTPRIMARY:
                newNode = new SCVhdlPrimary(this, c);
                if(primary0 == null) {
                    primary0 = newNode;
                }else {
                    primary1 = newNode;
                }
                break;
            case ASTVOID:
                operator = new SCVhdlMiscellaneous_operator(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(operator != null
            && operator.postToString().equalsIgnoreCase(vhdlOperators[VHDL_ABS])) {
            String tmp = getReplaceOperator(operator.postToString());
            ret += tmp + "(" + primary0.postToString() + ")";
        }else if(operator != null
            && operator.postToString().equalsIgnoreCase(vhdlOperators[VHDL_NOT])) {
            String tmp = getReplaceOperator(operator.postToString());
            ret += tmp + primary0.postToString();
        }else {
            if(primary1 == null) {
                ret += primary0.postToString();
            }else {
                ret += "(";
                ret += primary0.postToString();
                ret += getReplaceOperator(operator.postToString());
                ret += primary1.postToString();
                ret += ")";
            }
        }
        return ret;
    }
}

/**
 * <dl> file_declaration ::=
 *   <dd> <b>file</b> identifier_list : subtype_indication [ file_open_information ] ;
 */
class SCVhdlFile_declaration extends SCVhdlNode {
    public SCVhdlFile_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFILE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> file_logical_name ::=
 *   <dd> <i>string_</i>expression
 */
class SCVhdlFile_logical_name extends SCVhdlNode {
    public SCVhdlFile_logical_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFILE_LOGICAL_NAME);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> file_open_information ::=
 *   <dd> [ <b>open</b> <i>file_open_kind_</i>expression ] <b>is</b> file_logical_name
 */
class SCVhdlFile_open_information extends SCVhdlNode {
    public SCVhdlFile_open_information(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFILE_OPEN_INFORMATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> file_type_definition ::=
 *   <dd> <b>file of</b> type_mark
 */
class SCVhdlFile_type_definition extends SCVhdlNode {
    public SCVhdlFile_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFILE_TYPE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> floating_type_definition ::=
 *   <dd> range_constraint
 */
//class SCVhdlFloating_type_definition extends SCVhdlNode {
//    public SCVhdlFloating_type_definition(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTFLOATING_TYPE_DEFINITION);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> formal_designator ::=
 *   <dd> <i>generic_</i>name
 *   <br> | <i>port_</i>name
 *   <br> | <i>parameter_</i>name
 */
class SCVhdlFormal_designator extends SCVhdlNode {
    SCVhdlName name = null;
    public SCVhdlFormal_designator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFORMAL_DESIGNATOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return name.postToString();
    }
}

/**
 * <dl> formal_parameter_list ::=
 *   <dd> <i>parameter_</i>interface_list
 */
class SCVhdlFormal_parameter_list extends SCVhdlNode {
    public SCVhdlFormal_parameter_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFORMAL_PARAMETER_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> formal_part ::=
 *   <dd> formal_designator
 *   <br> | <i>function_</i>name ( formal_designator )
 *   <br> | type_mark  ( formal_designator )
 */
class SCVhdlFormal_part extends SCVhdlNode {
    public SCVhdlFormal_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFORMAL_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> free_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication [ := expression ] ;
 */
class SCVhdlFree_quantity_declaration extends SCVhdlNode {
    public SCVhdlFree_quantity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFREE_QUANTITY_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> full_type_declaration ::=
 *   <dd> <b>type</b> identifier <b>is</b> type_definition ;
 */
class SCVhdlFull_type_declaration extends SCVhdlNode {
    SCVhdlNode identifier = null;
    SCVhdlNode type_def = null;
    public SCVhdlFull_type_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFULL_TYPE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTTYPE_DEFINITION:
                type_def = new SCVhdlType_definition(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += "typedef ";
        ret += type_def.postToString() + " ";
        ret += identifier.postToString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> function_call ::=
 *   <dd> <i>function_</i>name [ ( actual_parameter_part ) ]
 */
class SCVhdlFunction_call extends SCVhdlNode {
    public SCVhdlFunction_call(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTFUNCTION_CALL);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> generate_statement ::=
 *   <dd> <i>generate_</i>label :
 *   <ul> generation_scheme <b>generate</b>
 *   <ul> [ {block_declarative_item }
 *   </ul> <b>begin</b> ]
 *   <ul> { architecture_statement }
 *   </ul> <b>end</b> <b>generate</b> [ <i>generate_</i>label ] ; </ul>
 */
class SCVhdlGenerate_statement extends SCVhdlNode {
    public SCVhdlGenerate_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGENERATE_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> generation_scheme ::=
 *   <dd> <b>for</b> <i>generate_</i>parameter_specification
 *   <br> | <b>if</b> condition
 */
class SCVhdlGeneration_scheme extends SCVhdlNode {
    public SCVhdlGeneration_scheme(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGENERATION_SCHEME);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> generic_clause ::=
 *   <dd> <b>generic</b> ( generic_list ) ;
 */
class SCVhdlGeneric_clause extends SCVhdlNode {
    public SCVhdlGeneric_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGENERIC_CLAUSE);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> generic_list ::=
 *   <dd> <i>generic_</i>interface_list
 */
class SCVhdlGeneric_list extends SCVhdlNode {
    SCVhdlNode list = null;
    public SCVhdlGeneric_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGENERIC_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINTERFACE_LIST:
                list = new SCVhdlInterface_list(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return list.postToString();
    }
}

/**
 * <dl> generic_map_aspect ::=
 *   <dd> <b>generic</b> <b>map</b> ( <i>generic_</i>association_list )
 */
class SCVhdlGeneric_map_aspect extends SCVhdlNode {
    public SCVhdlGeneric_map_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGENERIC_MAP_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> graphic_character ::=
 *   <dd> basic_graphic_character | lower_case_letter | other_special_character
 */
//class SCVhdlGraphic_character extends SCVhdlNode {
//    public SCVhdlGraphic_character(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTGRAPHIC_CHARACTER);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> group_constituent ::=
 *   <dd> name | character_literal
 */
class SCVhdlGroup_constituent extends SCVhdlNode {
    public SCVhdlGroup_constituent(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGROUP_CONSTITUENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> group_constituent_list ::=
 *   <dd> group_constituent { , group_constituent }
 */
class SCVhdlGroup_constituent_list extends SCVhdlNode {
    public SCVhdlGroup_constituent_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGROUP_CONSTITUENT_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> group_declaration ::=
 *   <dd> <b>group</b> identifier : <i>group_template_</i>name ( group_constituent_list ) ;
 */
class SCVhdlGroup_declaration extends SCVhdlNode {
    public SCVhdlGroup_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGROUP_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> group_template_declaration ::=
 *   <dd> <b>group</b> identifier <b>is</b> ( entity_class_entry_list ) ;
 */
class SCVhdlGroup_template_declaration extends SCVhdlNode {
    public SCVhdlGroup_template_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGROUP_TEMPLATE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> guarded_signal_specification ::=
 *   <dd> <i>guarded_</i>signal_list : type_mark
 */
class SCVhdlGuarded_signal_specification extends SCVhdlNode {
    public SCVhdlGuarded_signal_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTGUARDED_SIGNAL_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> identifier ::=
 *   <dd> basic_identifier | extended_identifier
 */
class SCVhdlIdentifier extends SCVhdlNode {
    String str = "";
    public SCVhdlIdentifier(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTIDENTIFIER);
        ASTNode c = (ASTNode)node.getChild(0);
        str = c.firstTokenImage();
    }

    public String postToString() {
        return str;
    }
}

/**
 * <dl> identifier_list ::=
 *   <dd> identifier { , identifier }
 */
class SCVhdlIdentifier_list extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlIdentifier_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTIDENTIFIER_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTIDENTIFIER);
            SCVhdlNode item = new SCVhdlIdentifier(this, c);
            items.add(item);
        }
    }
    
    public ArrayList<SCVhdlNode> getItems() {
        return items;
    }

    public String postToString() {
        String ret = "";        
        return ret;
    }
}

/**
 * <dl> if_statement ::=
 *   <dd> [ <i>if_</i>label : ]
 *   <ul> <b>if</b> condition <b>then</b>
 *   <ul>  sequence_of_statements
 *   </ul> { <b>elsif</b> condition <b>then</b>
 *   <ul> sequence_of_statements }
 *   </ul> [ <b>else</b>
 *   <ul> sequence_of_statements ]
 *   </ul> <b>end</b> <b>if</b> [ <i>if_</i>label ] ; </ul>
 */
class SCVhdlIf_statement extends SCVhdlNode {
    class ConPair {
        SCVhdlNode condition = null;
        SCVhdlNode seq_statements = null;
    }
    ConPair if_pair = new ConPair();
    ArrayList<ConPair> elsif_pair = new ArrayList<ConPair>();
    ConPair else_pair = null;
    public SCVhdlIf_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTIF_STATEMENT);
        int i = 0;
        while(i < node.getChildrenNum()) {
            ASTNode c = (ASTNode)node.getChild(i);
            String image = "";
            switch(c.getId())
            {
            case ASTVOID:
                image = c.firstTokenImage();
                c = (ASTNode)node.getChild(i+1);
                if(image.equalsIgnoreCase(tokenImage[IF])) {
                    if_pair.condition = new SCVhdlCondition(this, c);
                    c = (ASTNode)node.getChild(i+2);
                    if_pair.seq_statements = new SCVhdlSequence_of_statements(this, c);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSIF])) {
                    ConPair pair = new ConPair();
                    pair.condition = new SCVhdlCondition(this, c);
                    c = (ASTNode)node.getChild(i+2);
                    pair.seq_statements = new SCVhdlSequence_of_statements(this, c);
                    elsif_pair.add(pair);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSE])) {
                    else_pair = new ConPair();
                    else_pair.seq_statements = new SCVhdlSequence_of_statements(this, c);
                    i += 2;
                }
                break;

            default:
                i ++;
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += intent() + "if(" + if_pair.condition.postToString() + ")\r\n";
        ret += intent() + "{\r\n";
        ret += if_pair.seq_statements.postToString() + "\r\n";
        if(elsif_pair.size() > 0) {
            ret += intent() + "}\r\n";
            for(int i = 0; i < elsif_pair.size(); i++) {
                ConPair pair = elsif_pair.get(i);
                ret += intent() + "else if(" + pair.condition.postToString() + ")\r\n";
                ret += intent() + "{\r\n";
                ret += pair.seq_statements.postToString() + "\r\n";
            }
        }
        
        if(else_pair != null) {
            ret += intent() + "}\r\n";
            ret += intent() + "else\r\n";
            ret += intent() + "{\r\n";
            ret += else_pair.seq_statements.postToString() + "\r\n";
        }
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> incomplete_type_declaration ::=
 *   <dd> <b>type</b> identifier ;
 */
class SCVhdlIncomplete_type_declaration extends SCVhdlNode {
    public SCVhdlIncomplete_type_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINCOMPLETE_TYPE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> index_constraint ::=
 *   <dd> ( discrete_range { , discrete_range } )
 */
class SCVhdlIndex_constraint extends SCVhdlNode {
    ArrayList<SCVhdlNode> ranges = new ArrayList<SCVhdlNode>();
    public SCVhdlIndex_constraint(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINDEX_CONSTRAINT);
    }

    public String getMin() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((SCVhdlDiscrete_range)ranges.get(0)).getMin();
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((SCVhdlDiscrete_range)ranges.get(0)).getMax();
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = true;
        if(ranges.size() > 0) {
            ret = ((SCVhdlDiscrete_range)ranges.get(0)).isDownto();
        }
        return ret;
    }
    
    public String postToString() {
        String ret = "";
        return ret;
    }
}

/**
 * <dl> index_specification ::=
 *   <dd> discrete_range
 *   <br> | <i>static_</i>expression
 */
class SCVhdlIndex_specification extends SCVhdlNode {
    public SCVhdlIndex_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINDEX_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> index_subtype_definition ::=
 *   <dd> type_mark <b>range</b> <>
 */
class SCVhdlIndex_subtype_definition extends SCVhdlNode {
    public SCVhdlIndex_subtype_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINDEX_SUBTYPE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> indexed_name ::=
 *   <dd> prefix ( expression { , expression } )
 */
class SCVhdlIndexed_name extends SCVhdlNode {
    public SCVhdlIndexed_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINDEXED_NAME);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> instantiated_unit ::=
 *   <dd> [ <b>component</b> ] <i>component_</i>name
 *   <br> | <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 */
class SCVhdlInstantiated_unit extends SCVhdlNode {
    public SCVhdlInstantiated_unit(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINSTANTIATED_UNIT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> instantiation_list ::=
 *   <dd> <i>instantiation_</i>label { , <i>instantiation_</i>label }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class SCVhdlInstantiation_list extends SCVhdlNode {
    public SCVhdlInstantiation_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINSTANTIATION_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> integer_type_definition ::=
 *   <dd> range_constraint
 */
class SCVhdlInteger_type_definition extends SCVhdlNode {
    SCVhdlRange_constraint range = null;
    public SCVhdlInteger_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTEGER_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new SCVhdlRange_constraint(this, c);
            break;
        default:
            break;
        }
    }
    
    public String getMin() {
        return range.getMin();
    }
    
    public String getMax() {
        return range.getMax();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_constant_declaration ::=
 *   <dd> [ <b>constant</b> ] identifier_list : [ <b>in</b> ] subtype_indication [ := <i>static_</i>expression ]
 */
class SCVhdlInterface_constant_declaration extends SCVhdlNode {
    public SCVhdlInterface_constant_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_CONSTANT_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_declaration ::=
 *   <dd> interface_constant_declaration
 *   <br> | interface_signal_declaration
 *   <br> | interface_variable_declaration
 *   <br> | interface_file_declaration
 *   <br> | interface_terminal_declaration
 *   <br> | interface_quantity_declaration
 */
class SCVhdlInterface_declaration extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlInterface_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_CONSTANT_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINTERFACE_SIGNAL_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINTERFACE_VARIABLE_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINTERFACE_FILE_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINTERFACE_TERMINAL_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        case ASTINTERFACE_QUANTITY_DECLARATION:
            item = new SCVhdlRange_constraint(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> interface_element ::=
 *   <dd> interface_declaration
 */
class SCVhdlInterface_element extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlInterface_element(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_ELEMENT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_DECLARATION:
            item = new SCVhdlInterface_declaration(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> interface_file_declaration ::=
 *   <dd> <b>file</b> identifier_list : subtype_indication
 */
class SCVhdlInterface_file_declaration extends SCVhdlNode {
    public SCVhdlInterface_file_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_FILE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_list ::=
 *   <dd> interface_element { ; interface_element }
 */
class SCVhdlInterface_list extends SCVhdlNode {
    public SCVhdlInterface_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : [ <b>in</b> | <b>out</b> ] subtype_indication [ := <i>static_</i>expression ]
 */
class SCVhdlInterface_quantity_declaration extends SCVhdlNode {
    public SCVhdlInterface_quantity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_QUANTITY_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_signal_declaration ::=
 *   <dd> [ <b>signal</b> ] identifier_list : [ mode ] subtype_indication [ <b>bus</b> ] [ := <i>static_</i>expression ]
 */
class SCVhdlInterface_signal_declaration extends SCVhdlNode {
    public SCVhdlInterface_signal_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_SIGNAL_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication
 */
class SCVhdlInterface_terminal_declaration extends SCVhdlNode {
    public SCVhdlInterface_terminal_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_TERMINAL_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> interface_variable_declaration ::=
 *   <dd> [ <b>variable</b> ] identifier_list : [ mode ] subtype_indication [ := <i>static_</i>expression ]
 */
class SCVhdlInterface_variable_declaration extends SCVhdlNode {
    public SCVhdlInterface_variable_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTINTERFACE_VARIABLE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> iteration_scheme ::=
 *   <dd> <b>while</b> condition
 *   <br> | <b>for</b> <i>loop_</i>parameter_specification
 */
class SCVhdlIteration_scheme extends SCVhdlNode {
    SCVhdlCondition condition = null;
    SCVhdlParameter_specification param = null;
    public SCVhdlIteration_scheme(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTITERATION_SCHEME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new SCVhdlCondition(this, c);
                break;
            case ASTPARAMETER_SPECIFICATION:
                param = new SCVhdlParameter_specification(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(condition != null) {
            ret += "while(" + condition.postToString() + ")";
        }else {
            ret += "for(";
            if(param.isDownto()) {
                ret += param.identifier + " = " + param.getMax() + "; ";
                ret += param.identifier + " >= " + param.getMin() + "; ";
                ret += param.identifier + "--";
            }else {
                ret += param.identifier + " = " + param.getMin() + "; ";
                ret += param.identifier + " <= " + param.getMax() + "; ";
                ret += param.identifier + "++";
            }
            ret += ")";
        }
        return ret;
    }
}

/**
 * <dl> label ::=
 *   <dd> identifier
 */
class SCVhdlLabel extends SCVhdlNode {
    SCVhdlNode identifier = null;
    public SCVhdlLabel(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTLABEL);
        identifier = new SCVhdlIdentifier(this, node);
    }

    public String postToString() {
        return identifier.postToString();
    }
}

/**
 * <dl> letter ::=
 *   <dd> upper_case_letter | lower_case_letter
 */
//class SCVhdlLetter extends SCVhdlNode {
//    public SCVhdlLetter(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTLETTER);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> letter_or_digit ::=
 *   <dd> letter | digit
 */
//class SCVhdlLetter_or_digit extends SCVhdlNode {
//    public SCVhdlLetter_or_digit(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTLETTER_OR_DIGIT);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> library_clause ::=
 *   <dd> <b>library</b> logical_name_list ;
 */
class SCVhdlLibrary_clause extends SCVhdlNode {
    SCVhdlLogical_name_list logical_name_list = null;
    public SCVhdlLibrary_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTLIBRARY_CLAUSE);
        logical_name_list = new SCVhdlLogical_name_list(this, (ASTNode)node.getChild(0));
    }
    
    public ArrayList<SCVhdlNode> getNames() {
        return logical_name_list.getNames();
    } 

    public String postToString() {
        return "";
        //return logical_name_list.postToString() + ";";
    }
}

/**
 * <dl> library_unit ::=
 *   <dd> primary_unit
 *   <br> | secondary_unit
 */
class SCVhdlLibrary_unit extends SCVhdlNode {
    SCVhdlNode unit = null;
    public SCVhdlLibrary_unit(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTLIBRARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTPRIMARY_UNIT:
            unit = new SCVhdlPrimary_unit(this, c);
            break;
        case ASTSECONDARY_UNIT:
            unit = new SCVhdlSecondary_unit(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return unit.postToString();
    }
}

/**
 * <dl> literal ::=
 *   <dd> numeric_literal
 *   <br> | enumeration_literal
 *   <br> | string_literal
 *   <br> | bit_string_literal
 *   <br> | <b>null</b>
 */
class SCVhdlLiteral extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlLiteral(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTLITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int kind  = 0;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                item = new SCVhdlEnumeration_literal(this, c);
                break;
            case ASTNUMERIC_LITERAL:
                item = new SCVhdlNumeric_literal(this, c);
                break;
            case ASTVOID:
                kind = c.getFirstToken().kind;
                switch(kind)
                {
                case NULL:
                    item = new SCVhdlToken(this, c);    // TODO convert null
                    break;
                case string_literal:
                    item = new SCVhdlString_literal(this, c);
                    break;
                case bit_string_literal:
                    item = new SCVhdlBit_string_literal(this, c);
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> logical_name ::=
 *   <dd> identifier
 */
class SCVhdlLogical_name extends SCVhdlNode {
    SCVhdlNode identifier = null;
    public SCVhdlLogical_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTLOGICAL_NAME);
        identifier = new SCVhdlIdentifier(this, node);
    }

    public String postToString() {
        return identifier.postToString();
    }
}

/**
 * <dl> logical_name_list ::=
 *   <dd> logical_name { , logical_name }
 */
class SCVhdlLogical_name_list extends SCVhdlNode {
    ArrayList<SCVhdlNode> names = new ArrayList<SCVhdlNode>();
    public SCVhdlLogical_name_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTLOGICAL_NAME_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                newNode = new SCVhdlLogical_name(this, c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<SCVhdlNode> getNames() {
        return names;
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> logical_operator ::=
 *   <dd> <b>and</b> | <b>or</b> | <b>nand</b> | <b>nor</b> | <b>xor</b> | <b>xnor</b>
 */
//class SCVhdlLogical_operator extends SCVhdlNode {
//    public SCVhdlLogical_operator(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTLOGICAL_OPERATOR);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> loop_statement ::=
 *   <dd> [ <i>loop_</i>label : ]
 *   <ul> [ iteration_scheme ] <b>loop</b>
 *   <ul> sequence_of_statements
 *   </ul> <b>end</b> <b>loop</b> [ <i>loop_</i>label ] ; </ul>
 */
class SCVhdlLoop_statement extends SCVhdlNode {
    SCVhdlIteration_scheme iteration = null;
    SCVhdlNode seq_statements = null;
    public SCVhdlLoop_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTLOOP_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTITERATION_SCHEME:
                iteration = new SCVhdlIteration_scheme(this, c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new SCVhdlSequence_of_statements(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += intent() + iteration.postToString() + "\r\n";
        ret += intent() + "{\r\n";
        ret += seq_statements.postToString();
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> miscellaneous_operator ::=
 *   <dd> ** | <b>abs</b> | <b>not</b>
 */
class SCVhdlMiscellaneous_operator extends SCVhdlNode {
    SCVhdlToken token = null;
    public SCVhdlMiscellaneous_operator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTMISCELLANEOUS_OPERATOR);
        token = new SCVhdlToken(this, node);
    }

    public String postToString() {
        return token.postToString();
    }
}

/**
 * <dl> mode ::=
 *   <dd> <b>in</b> | <b>out</b> | <b>inout</b> | <b>buffer</b> | <b>linkage</b>
 */
class SCVhdlMode extends SCVhdlNode {
    String token = "";
    public SCVhdlMode(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTMODE);
        token = node.firstTokenImage();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> multiplying_operator ::=
 *   <dd> * | / | <b>mod</b> | <b>rem</b>
 */
class SCVhdlMultiplying_operator extends SCVhdlNode {
    String token = "";
    public SCVhdlMultiplying_operator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTMULTIPLYING_OPERATOR);
        token = node.firstTokenImage(); 
    }

    public String postToString() {
        return getReplaceOperator(token);
    }
}

/**
 * <dl> name ::=
 *   <dd> simple_name
 *   <br> | operator_symbol
 *   <br> | selected_name
 *   <br> | indexed_name
 *   <br> | slice_name
 *   <br> | attribute_name
 */
class SCVhdlName extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlName(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNAME);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new SCVhdlSimple_name(this, c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new SCVhdlOperator_symbol(this, c);
            break;
        case ASTSELECTED_NAME:
            item = new SCVhdlSelected_name(this, c);
            break;
        case ASTINDEXED_NAME:
            item = new SCVhdlIndexed_name(this, c);
            break;
        case ASTSLICE_NAME:
            item = new SCVhdlSlice_name(this, c);
            break;
        case ASTATTRIBUTE_NAME:
            item = new SCVhdlAttribute_name(this, c);
            break;
        default:
            break;
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        if(item instanceof SCVhdlSelected_name) {
            segments.addAll(((SCVhdlSelected_name)item).getNameSegments());
        }else {
            segments.add(item.postToString());
        }
        return segments;
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> nature_declaration ::=
 *   <dd> <b>nature</b> identifier <b>is</b> nature_definition ;
 */
class SCVhdlNature_declaration extends SCVhdlNode {
    public SCVhdlNature_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNATURE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> nature_definition ::=
 *   <dd> scalar_nature_definition
 *   <br> | composite_nature_definition
 */
class SCVhdlNature_definition extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlNature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNATURE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_NATURE_DEFINITION:
            item = new SCVhdlScalar_nature_definition(this, c);
            break;
        case ASTCOMPOSITE_NATURE_DEFINITION:
            item = new SCVhdlComposite_nature_definition(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> nature_element_declaration ::=
 *   <dd> identifier_list : element_subnature_definition
 */
class SCVhdlNature_element_declaration extends SCVhdlNode {
    public SCVhdlNature_element_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNATURE_ELEMENT_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> nature_mark ::=
 *   <dd> <i>nature_</i>name | <i>subnature_</i>name
 */
class SCVhdlNature_mark extends SCVhdlNode {
    public SCVhdlNature_mark(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNATURE_MARK);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> next_statement ::=
 *   <dd> [ label : ] <b>next</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
 */
class SCVhdlNext_statement extends SCVhdlNode {
    SCVhdlCondition condition = null;
    public SCVhdlNext_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNEXT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new SCVhdlCondition(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "if(" + condition.postToString() + ")\r\n";
            ret += intent(level+1) + "continue;";
        }else {
            ret += intent() + "continue;";
        }
        return ret;
    }
}

/**
 * <dl> null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class SCVhdlNull_statement extends SCVhdlNode {
    public SCVhdlNull_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNULL_STATEMENT);
    }

    public String postToString() {
        warning("null statement ignore");
        return ";";
    }
}

/**
 * <dl> numeric_literal ::=
 *   <dd> abstract_literal
 *   <br> | physical_literal
 */
class SCVhdlNumeric_literal extends SCVhdlNode {
    SCVhdlNode literal = null;
    public SCVhdlNumeric_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTNUMERIC_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTABSTRACT_LITERAL:
                literal = new SCVhdlAbstract_literal(this, c);
                break;
            case ASTPHYSICAL_LITERAL:
                literal = new SCVhdlPhysical_literal(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return literal.postToString();
    }
}

/**
 * <dl> object_declaration ::=
 *   <dd> constant_declaration
 *   <br> | signal_declaration
 *   <br> | variable_declaration
 *   <br> | file_declaration
 *   <br> | terminal_declaration
 *   <br> | quantity_declaration
 */
class SCVhdlObject_declaration extends SCVhdlNode {
    public SCVhdlObject_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTOBJECT_DECLARATION);
        // no use
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> operator_symbol ::=
 *   <dd> string_literal
 */
class SCVhdlOperator_symbol extends SCVhdlNode {
    SCVhdlNode string_literal = null;
    public SCVhdlOperator_symbol(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTOPERATOR_SYMBOL);
        ASTNode c = (ASTNode)node.getChild(0);
        string_literal = new SCVhdlString_literal(this, c);
    }

    public String postToString() {
        return string_literal.postToString();
    }
}

/**
 * <dl> options ::=
 *   <dd> [ <b>guarded</b> ] [ delay_mechanism ]
 */
class SCVhdlOptions extends SCVhdlNode {
    public SCVhdlOptions(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTOPTIONS);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> package_body ::=
 *   <dd> <b>package body</b> <i>package_</i>simple_name <b>is</b>
 *   <ul> package_body_declarative_part
 *   </ul> <b>end</b> [ <b>package body</b> ] [ <i>package_</i>simple_name ] ;
 */
class SCVhdlPackage_body extends SCVhdlNode {
    public SCVhdlPackage_body(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPACKAGE_BODY);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> package_body_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | <i>shared_</i>variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class SCVhdlPackage_body_declarative_item extends SCVhdlNode {
    public SCVhdlPackage_body_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTPACKAGE_BODY_DECLARATIVE_ITEM);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> package_body_declarative_part ::=
 *   <dd> { package_body_declarative_item }
 */
class SCVhdlPackage_body_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlPackage_body_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPACKAGE_BODY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlPackage_body_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> package_declaration ::=
 *   <dd> <b>package</b> identifier <b>is</b>
 *   <ul> package_declarative_part
 *   </ul> <b>end</b> [ <b>package</b> ] [ <i>package_</i>simple_name ] ;
 */
class SCVhdlPackage_declaration extends SCVhdlNode {
    SCVhdlNode identifier = null;
    SCVhdlNode declarative_part = null;
    public SCVhdlPackage_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPACKAGE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTPACKAGE_DECLARATIVE_PART:
                declarative_part = new SCVhdlPackage_declarative_part(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getName() {
        return identifier.postToString();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> package_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | signal_declaration
 *   <br> | <i>shared_</i>variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | component_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | disconnection_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 *   <br> | nature_declaration
 *   <br> | subnature_declaration
 *   <br> | terminal_declaration
 */
class SCVhdlPackage_declarative_item extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlPackage_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTPACKAGE_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new SCVhdlSubprogram_declaration(this, node);
            break;
        case ASTTYPE_DECLARATION:
            item = new SCVhdlType_declaration(this, node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new SCVhdlSubtype_declaration(this, node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new SCVhdlConstant_declaration(this, node);
            break;
        case ASTSIGNAL_DECLARATION:
            item = new SCVhdlSignal_declaration(this, node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVhdlVariable_declaration(this, node);
            break;
        case ASTFILE_DECLARATION:
            item = new SCVhdlFile_declaration(this, node);
            break;
        case ASTALIAS_DECLARATION:
            item = new SCVhdlAlias_declaration(this, node);
            break;
        case ASTCOMPONENT_DECLARATION:
            item = new SCVhdlComponent_declaration(this, node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new SCVhdlAttribute_declaration(this, node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new SCVhdlAttribute_specification(this, node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            item = new SCVhdlDisconnection_specification(this, node);
            break;
        case ASTUSE_CLAUSE:
            item = new SCVhdlUse_clause(this, node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new SCVhdlGroup_template_declaration(this, node);
            break;
        case ASTGROUP_DECLARATION:
            item = new SCVhdlGroup_declaration(this, node);
            break;
        case ASTNATURE_DECLARATION:
            item = new SCVhdlNature_declaration(this, node);
            break;
        case ASTSUBNATURE_DECLARATION:
            item = new SCVhdlSubnature_declaration(this, node);
            break;
        case ASTTERMINAL_DECLARATION:
            item = new SCVhdlTerminal_declaration(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> package_declarative_part ::=
 *   <dd> { package_declarative_item }
 */
class SCVhdlPackage_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlPackage_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPACKAGE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlPackage_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> parameter_specification ::=
 *   <dd> identifier <b>in</b> discrete_range
 */
class SCVhdlParameter_specification extends SCVhdlNode {
    SCVhdlNode identifier = null;
    SCVhdlDiscrete_range discrete_range = null;
    public SCVhdlParameter_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPARAMETER_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTDISCRETE_RANGE:
                discrete_range = new SCVhdlDiscrete_range(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getMin() {
        return discrete_range.getMin();
    }
    
    public String getMax() {
        return discrete_range.getMax();
    }
    
    public boolean isDownto() {
        return discrete_range.isDownto();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> physical_literal ::=
 *   <dd> [ abstract_literal ] <i>unit_</i>name
 */
class SCVhdlPhysical_literal extends SCVhdlNode {
    SCVhdlNode abstract_literal = null;
    String time_unit_name = "ns";
    public SCVhdlPhysical_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPHYSICAL_LITERAL);
        abstract_literal = new SCVhdlAbstract_literal(this, (ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }

    public String postToString() {
        return abstract_literal.postToString();
    }
}

/**
 * <dl> physical_type_definition ::=
 *   <dd> range_constraint
 *   <ul> <b>units</b>
 *   <ul> primary_unit_declaration
 *   <br> { secondary_unit_declaration }
 *   </ul> <b>end</b> <b>units</b> [ <i>physical_type_</i>simple_name ] </ul>
 */
class SCVhdlPhysical_type_definition extends SCVhdlNode {
    SCVhdlRange_constraint range = null;
    SCVhdlNode primary = null;
    ArrayList<SCVhdlNode> secondaries = new ArrayList<SCVhdlNode>();
    public SCVhdlPhysical_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPHYSICAL_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTRANGE_CONSTRAINT:
                range = new SCVhdlRange_constraint(this, c);
                break;
            case ASTPRIMARY_UNIT_DECLARATION:
                primary = new SCVhdlPrimary_unit_declaration(this, c);
                break;
            case ASTSECONDARY_UNIT_DECLARATION:
                newNode =  new SCVhdlSecondary_unit_declaration(this, c);
                secondaries.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        warning("physical type definition not support");
        return "";
    }
}

/**
 * <dl> port_clause ::=
 *   <dd> <b>port</b> ( port_list ) ;
 */
class SCVhdlPort_clause extends SCVhdlNode {
    public SCVhdlPort_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPORT_CLAUSE);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> port_list ::=
 *   <dd> <i>port_</i>interface_list
 */
class SCVhdlPort_list extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlPort_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPORT_LIST);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_LIST:
            item = new SCVhdlInterface_list(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> port_map_aspect ::=
 *   <dd> <b>port</b> <b>map</b> ( <i>port_</i>association_list )
 */
class SCVhdlPort_map_aspect extends SCVhdlNode {
    public SCVhdlPort_map_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPORT_MAP_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> prefix ::=
 *   <dd> name
 *   <br> | function_call
 */
class SCVhdlPrefix extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlPrefix(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPREFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new SCVhdlName(this, c);
            break;
        case ASTFUNCTION_CALL:
            item = new SCVhdlFunction_call(this, c);
            break;
        default:
            break;
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        if(item instanceof SCVhdlName) {
            segments.addAll(((SCVhdlName)item).getNameSegments());
        }else {
            segments.add(item.postToString());
        }
        return segments;
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> primary ::=
 *   <dd> name
 *   <br> | literal
 *   <br> | aggregate
 *   <br> | function_call
 *   <br> | qualified_expression
 *   <br> | type_conversion
 *   <br> | allocator
 *   <br> | ( expression )
 */
class SCVhdlPrimary extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlPrimary(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPRIMARY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                item = new SCVhdlName(this, c);
                break;
            case ASTLITERAL:
                item = new SCVhdlLiteral(this, c);
                break;
            case ASTAGGREGATE:
                item = new SCVhdlAggregate(this, c);
                break;
            case ASTQUALIFIED_EXPRESSION:
                item = new SCVhdlQualified_expression(this, c);
                break;
            case ASTALLOCATOR:
                item = new SCVhdlAllocator(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> primary_unit ::=
 *   <dd> entity_declaration
 *   <br> | configuration_declaration
 *   <br> | package_declaration
 */
class SCVhdlPrimary_unit extends SCVhdlNode {
    SCVhdlNode declaration = null;
    public SCVhdlPrimary_unit(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPRIMARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENTITY_DECLARATION:
            declaration = new SCVhdlEntity_declaration(this, c);
            break;
        case ASTCONFIGURATION_DECLARATION:
            declaration = new SCVhdlConfiguration_declaration(this, c);
            break;
        case ASTPACKAGE_DECLARATION:
            declaration = new SCVhdlPackage_declaration(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return declaration.postToString();
    }
}

/**
 * <dl> primary_unit_declaration ::=
 *   <dd> identifier ;
 */
class SCVhdlPrimary_unit_declaration extends SCVhdlNode {
    SCVhdlNode identifier = null;
    public SCVhdlPrimary_unit_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPRIMARY_UNIT_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTIDENTIFIER);
        identifier = new SCVhdlIdentifier(this, c);
    }

    public String postToString() {
        String ret = "";
        ret += identifier.postToString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> procedural_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | variable_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class SCVhdlProcedural_declarative_item extends SCVhdlNode {
    public SCVhdlProcedural_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_ITEM);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> procedural_declarative_part ::=
 *   <dd> { procedural_declarative_item }
 */
class SCVhdlProcedural_declarative_part extends SCVhdlNode {
    public SCVhdlProcedural_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> procedural_statement_part ::=
 *   <dd> { sequential_statement }
 */
class SCVhdlProcedural_statement_part extends SCVhdlNode {
    public SCVhdlProcedural_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCEDURAL_STATEMENT_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> procedure_call ::=
 *   <dd> <i>procedure_</i>name [ ( actual_parameter_part ) ]
 */
class SCVhdlProcedure_call extends SCVhdlNode {
    public SCVhdlProcedure_call(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCEDURE_CALL);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> procedure_call_statement ::=
 *   <dd> [ label : ] procedure_call ;
 */
class SCVhdlProcedure_call_statement extends SCVhdlNode {
    SCVhdlNode procedure_call = null;
    public SCVhdlProcedure_call_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCEDURE_CALL_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPROCEDURE_CALL:
                procedure_call = new SCVhdlProcedure_call(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += procedure_call.postToString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> process_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class SCVhdlProcess_declarative_item extends SCVhdlNode {
    public SCVhdlProcess_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTPROCESS_DECLARATIVE_ITEM);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> process_declarative_part ::=
 *   <dd> { process_declarative_item }
 */
class SCVhdlProcess_declarative_part extends SCVhdlNode {
    public SCVhdlProcess_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCESS_DECLARATIVE_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> process_statement ::=
 *   <dd> [ <i>process_</i>label : ]
 *   <ul> [ <b>postponed</b> ] <b>process</b> [ ( sensitivity_list ) ] [ <b>is</b> ]
 *   <ul> process_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> process_statement_part
 *   </ul> <b>end</b> [ <b>postponed</b> ] <b>process</b> [ <i>process_</i>label ] ; </ul>
 */
class SCVhdlProcess_statement extends SCVhdlNode {
    public SCVhdlProcess_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCESS_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> process_statement_part ::=
 *   <dd> { sequential_statement }
 */
class SCVhdlProcess_statement_part extends SCVhdlNode {
    public SCVhdlProcess_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTPROCESS_STATEMENT_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> qualified_expression ::=
 *   <dd> type_mark ' ( expression )
 *   <br> | type_mark ' aggregate
 */
class SCVhdlQualified_expression extends SCVhdlNode {
    SCVhdlNode type_mark = null;
    SCVhdlNode aggregate = null;
    public SCVhdlQualified_expression(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTQUALIFIED_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            
            default:
                break;
            }
        }
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> quantity_declaration ::=
 *   <dd> free_quantity_declaration
 *   <br> | branch_quantity_declaration
 *   <br> | source_quantity_declaration
 */
class SCVhdlQuantity_declaration extends SCVhdlNode {
    public SCVhdlQuantity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTQUANTITY_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> quantity_list ::=
 *   <dd> <i>quantity_</i>name { , <i>quantity_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class SCVhdlQuantity_list extends SCVhdlNode {
    public SCVhdlQuantity_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTQUANTITY_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> quantity_specification ::=
 *   <dd> quantity_list : type_mark
 */
class SCVhdlQuantity_specification extends SCVhdlNode {
    public SCVhdlQuantity_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTQUANTITY_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> range ::=
 *   <dd> <i>range_</i>attribute_name
 *   <br> | simple_expression direction simple_expression
 */
class SCVhdlRange extends SCVhdlNode {
    SCVhdlAttribute_name attribute_name = null;
    SCVhdlNode simple_exp1 = null;
    SCVhdlDirection direction = null;
    SCVhdlNode simple_exp2 = null;
    public SCVhdlRange(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRANGE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTATTRIBUTE_NAME:
                attribute_name = new SCVhdlAttribute_name(this, c);
                break;
            case ASTSIMPLE_EXPRESSION:
                newNode = new SCVhdlSimple_expression(this, c);
                if(simple_exp1 == null) {
                    simple_exp1 = newNode;
                }else {
                    simple_exp2 = newNode;
                }
                break;
            case ASTDIRECTION:
                direction = new SCVhdlDirection(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getMin() {
        String ret = "0";
        if(attribute_name == null) {
            if(direction.dir.equalsIgnoreCase(RANGE_TO)) {
                ret = simple_exp1.postToString();
            }else {
                ret = simple_exp2.postToString();
            }
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(attribute_name != null) {
            ret = attribute_name.designator.postToString();
        }else {
            if(direction.dir.equalsIgnoreCase(RANGE_TO)) {
                ret = simple_exp2.postToString();
            }else {
                ret = simple_exp1.postToString();
            }
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = false;
        if(direction != null &&
                direction.dir.equalsIgnoreCase(RANGE_DOWNTO)) {
            ret = true;
        }
        return ret;
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> range_constraint ::=
 *   <dd> <b>range</b> range
 */
class SCVhdlRange_constraint extends SCVhdlNode {
    SCVhdlRange range = null;
    public SCVhdlRange_constraint(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRANGE_CONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTRANGE);
        range = new SCVhdlRange(this, c);
    }
    
    public String getMin() {
        return range.getMin();
    }
    
    public String getMax() {
        return range.getMax();
    }
    
    public boolean isDownto() {
        return range.isDownto();
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> record_nature_definition ::=
 *   <dd> <b>record</b>
 *   <ul> nature_element_declaration
 *   <br> { nature_element_declaration }
 *   </ul><b>end</b> <b>record</b> [ <i>record_nature_</i>simple_name ]
 */
class SCVhdlRecord_nature_definition extends SCVhdlNode {
    public SCVhdlRecord_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRECORD_NATURE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> record_type_definition ::=
 *   <dd> <b>record</b>
 *   <ul> element_declaration
 *   <br> { element_declaration }
 *   </ul><b>end</b> <b>record</b> [ <i>record_type_</i>simple_name ]
 */
class SCVhdlRecord_type_definition extends SCVhdlNode {
    ArrayList<SCVhdlNode> elements = new ArrayList<SCVhdlNode>();
    public SCVhdlRecord_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRECORD_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlShift_expression newNode = null;
            switch(c.getId())
            {
            case ASTELEMENT_DECLARATION:
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        //ret += "struct"
        return ret;
    }
}

/**
 * <dl> relation ::=
 *   <dd> shift_expression [ relational_operator shift_expression ]
 */
class SCVhdlRelation extends SCVhdlNode {
    SCVhdlShift_expression l_exp = null;
    SCVhdlNode operator = null;
    SCVhdlShift_expression r_exp = null;
    public SCVhdlRelation(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRELATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlShift_expression newNode = null;
            switch(c.getId())
            {
            case ASTSHIFT_EXPRESSION:
                newNode = new SCVhdlShift_expression(this, c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new SCVhdlRelational_operator(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        String tmp = l_exp.postToString();
        if(l_exp.r_exp != null) {
            ret = "(" + tmp + ")";
        }
        if(r_exp != null) {
            ret += " " + getReplaceOperator(operator.postToString()) + " ";
            tmp += r_exp.postToString();
            if(r_exp.r_exp != null) {
                ret += "(" + tmp + ")";
            }else {
                ret += tmp;
            }
            
        }
        return ret;
    }
}

/**
 * <dl> relational_operator ::=
 *   <dd> = | /= | < | <= | > | >=
 */
class SCVhdlRelational_operator extends SCVhdlNode {
    SCVhdlToken op = null;
    public SCVhdlRelational_operator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTRELATIONAL_OPERATOR);
        op = new SCVhdlToken(this, node);
    }

    public String postToString() {
        return op.postToString();
    }
}

/**
 * <dl> report_statement ::=
 *   <dd>  [ label : ]
 *   <ul> <b>report</b> expression
 *   <ul> [ <b>severity</b> expression ] ; </ul></ul>
 */
class SCVhdlReport_statement extends SCVhdlNode {
    SCVhdlNode report_exp = null;
    SCVhdlNode severity_exp = null;
    public SCVhdlReport_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTREPORT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTVOID:
                {
                    String image = c.firstTokenImage();
                    i++;
                    c = (ASTNode)node.getChild(i);
                    if(image.equalsIgnoreCase(tokenImage[REPORT])) {
                        report_exp = new SCVhdlExpression(this, c);
                    }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                        severity_exp = new SCVhdlExpression(this, c);
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        warning("report statement ignored");
        return ret;
    }
}

/**
 * <dl> return_statement ::=
 *   <dd> [ label : ] <b>return</b> [ expression ] ;
 */
class SCVhdlReturn_statement extends SCVhdlNode {
    SCVhdlNode expression = null;
    public SCVhdlReturn_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTRETURN_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += intent() + "return";
        if(expression != null) {
            ret += " " + expression.postToString();
        }
        ret += ";";
        return ret;
    }
}

/**
 * <dl> scalar_nature_definition ::=
 *   <dd> type_mark <b>across</b>
 *   <dd> type_mark <b>through</b>
 *   <dd> identifier <b>reference</b>
 */
class SCVhdlScalar_nature_definition extends SCVhdlNode {
    public SCVhdlScalar_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSCALAR_NATURE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> scalar_type_definition ::=
 *   <dd> enumeration_type_definition
 *   <br> | integer_type_definition
 *   <br> | floating_type_definition
 *   <br> | physical_type_definition
 */
class SCVhdlScalar_type_definition extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlScalar_type_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSCALAR_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENUMERATION_TYPE_DEFINITION:
            item = new SCVhdlEnumeration_type_definition(this, c);
            break;
        case ASTINTEGER_TYPE_DEFINITION:    // the same as float type
            item = new SCVhdlInteger_type_definition(this, c);
            break;
        case ASTPHYSICAL_TYPE_DEFINITION:
            item = new SCVhdlPhysical_type_definition(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> secondary_unit ::=
 *   <dd> architecture_body
 *   <br> | package_body
 */
class SCVhdlSecondary_unit extends SCVhdlNode {
    public SCVhdlSecondary_unit(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSECONDARY_UNIT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> secondary_unit_declaration ::=
 *   <dd> identifier = physical_literal ;
 */
class SCVhdlSecondary_unit_declaration extends SCVhdlNode {
    SCVhdlNode identifier = null;
    SCVhdlNode phy_literal = null;
    public SCVhdlSecondary_unit_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSECONDARY_UNIT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new SCVhdlIdentifier(this, c);
                break;
            case ASTPHYSICAL_LITERAL:
                phy_literal = new SCVhdlPhysical_literal(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += identifier.postToString();
        ret += " = ";
        ret += phy_literal.postToString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> selected_name ::=
 *   <dd> prefix . suffix
 */
class SCVhdlSelected_name extends SCVhdlNode {
    SCVhdlPrefix prefix = null;
    SCVhdlSuffix suffix = null;
    public SCVhdlSelected_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSELECTED_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new SCVhdlPrefix(this, c);
                break;
            case ASTSUFFIX:
                suffix = new SCVhdlSuffix(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        segments.addAll(prefix.getNameSegments());
        segments.add(suffix.postToString());
        return segments;
    }

    public String postToString() {
        String ret = "";
        ret += prefix.postToString();
        ret += ".";
        ret += suffix.postToString();
        return ret;
    }
}

/**
 * <dl> selected_signal_assignment ::=
 *   <dd> <b>with</b> expression <b>select</b>
 *   <ul> target <= options selected_waveforms ; </ul>
 */
class SCVhdlSelected_signal_assignment extends SCVhdlNode {
    public SCVhdlSelected_signal_assignment(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSELECTED_SIGNAL_ASSIGNMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> selected_waveforms ::=
 *   <dd> { waveform <b>when</b> choices , }
 *   <br> waveform <b>when</b> choices
 */
class SCVhdlSelected_waveforms extends SCVhdlNode {
    public SCVhdlSelected_waveforms(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSELECTED_WAVEFORMS);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> sensitivity_clause ::=
 *   <dd> <b>on</b> sensitivity_list
 */
class SCVhdlSensitivity_clause extends SCVhdlNode {
    SCVhdlSensitivity_list sensitivity_list = null;
    public SCVhdlSensitivity_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSENSITIVITY_CLAUSE);
        sensitivity_list = new SCVhdlSensitivity_list(this, (ASTNode)node.getChild(0));
    }

    public String postToString() {
        return "";
    }
    
    ArrayList<String> getSensitiveList() {
        return sensitivity_list.getList();
    }
}

/**
 * <dl> sensitivity_list ::=
 *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
 */
class SCVhdlSensitivity_list extends SCVhdlNode {
    ArrayList<String> items = new ArrayList<String>();
    public SCVhdlSensitivity_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSENSITIVITY_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlName(this, c);
            items.add(item.postToString());
        }
    }
    
    public ArrayList<String> getList() {
        return items;
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> sequence_of_statements ::=
 *   <dd> { sequential_statement }
 */
class SCVhdlSequence_of_statements extends SCVhdlNode {
    public SCVhdlSequence_of_statements(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSEQUENCE_OF_STATEMENTS);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> sequential_statement ::=
 *   <dd> wait_statement
 *   <br> | assertion_statement
 *   <br> | report_statement
 *   <br> | signal_assignment_statement
 *   <br> | variable_assignment_statement
 *   <br> | procedure_call_statement
 *   <br> | if_statement
 *   <br> | case_statement
 *   <br> | loop_statement
 *   <br> | next_statement
 *   <br> | exit_statement
 *   <br> | return_statement
 *   <br> | null_statement
 *   <br> | break_statement
 */
class SCVhdlSequential_statement extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlSequential_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTSEQUENTIAL_STATEMENT);
        switch(node.getId())
        {
        case ASTWAIT_STATEMENT:
            item = new SCVhdlWait_statement(this, node);
            break;
        case ASTASSERTION_STATEMENT:
            item = new SCVhdlAssertion_statement(this, node);
            break;
        case ASTREPORT_STATEMENT:
            item = new SCVhdlReport_statement(this, node);
            break;
        case ASTSIGNAL_ASSIGNMENT_STATEMENT:
            item = new SCVhdlSignal_assignment_statement(this, node);
            break;
        case ASTVARIABLE_ASSIGNMENT_STATEMENT:
            item = new SCVhdlVariable_assignment_statement(this, node);
            break;
        case ASTPROCEDURE_CALL_STATEMENT:
            item = new SCVhdlProcedure_call_statement(this, node);
            break;
        case ASTIF_STATEMENT:
            item = new SCVhdlIf_statement(this, node);
            break;
        case ASTCASE_STATEMENT:
            item = new SCVhdlCase_statement(this, node);
            break;
        case ASTLOOP_STATEMENT:
            item = new SCVhdlLoop_statement(this, node);
            break;
        case ASTNEXT_STATEMENT:
            item = new SCVhdlNext_statement(this, node);
            break;
        case ASTEXIT_STATEMENT:
            item = new SCVhdlExit_statement(this, node);
            break;
        case ASTRETURN_STATEMENT:
            item = new SCVhdlReturn_statement(this, node);
            break;
        case ASTNULL_STATEMENT:
            item = new SCVhdlNull_statement(this, node);
            break;
        case ASTBREAK_STATEMENT:
            item = new SCVhdlBreak_statement(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> shift_expression ::=
 *   <dd> simple_expression [ shift_operator simple_expression ]
 */
class SCVhdlShift_expression extends SCVhdlNode {
    SCVhdlSimple_expression l_exp = null;
    SCVhdlNode operator = null;
    SCVhdlSimple_expression r_exp = null;
    public SCVhdlShift_expression(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSHIFT_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlSimple_expression newNode = null;
            switch(c.getId())
            {
            case ASTSHIFT_EXPRESSION:
                newNode = new SCVhdlSimple_expression(this, c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new SCVhdlShift_operator(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        String tmp = l_exp.postToString();
        if(l_exp.items.size() > 2) {
            ret += "(" + tmp + ")";
        }else {
            ret += tmp;
        }

        if(r_exp != null) {
            ret += " " + getReplaceOperator(operator.postToString()) + " ";
            tmp = r_exp.postToString();
            if(r_exp.items.size() > 2) {
                ret += "(" + tmp + ")";
            }else {
                ret += tmp;
            }
        }
        return ret;
    }
}

/**
 * <dl> shift_operator ::=
 *   <dd> <b>sll</b> | <b>srl</b> | <b>sla</b> | <b>sra</b> | <b>rol</b> | <b>ror</b>
 */
class SCVhdlShift_operator extends SCVhdlNode {
    SCVhdlToken op = null;
    public SCVhdlShift_operator(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTSHIFT_OPERATOR);
        op = new SCVhdlToken(this, node);
    }

    public String postToString() {
        return op.postToString();
    }
}

/**
 * <dl> sign ::=
 *   <dd> + | -
 */
class SCVhdlSign extends SCVhdlNode {
    public SCVhdlSign(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGN);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> signal_assignment_statement ::=
 *   <dd> [ label : ] target <= [ delay_mechanism ] waveform ;
 */
class SCVhdlSignal_assignment_statement extends SCVhdlNode {
    SCVhdlNode target = null;
    SCVhdlNode waveform = null;
    SCVhdlNode delay = null;
    public SCVhdlSignal_assignment_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGNAL_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new SCVhdlTarget(this, c);
                break;
            case ASTDELAY_MECHANISM:
                delay = new SCVhdlDelay_mechanism(this, c);
                break;
            case ASTWAVEFORM:
                waveform = new SCVhdlWaveform(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        String pre = "";
        pre = target.postToString();
        if(delay != null) {
            warning("delay mechanism ignore");
        }
        ArrayList<SCVhdlNode> elements = ((SCVhdlWaveform)waveform).getElements();
        if(elements.size() > 1) {
            warning("multi-source of signal assignment not support");
        }
        for(int i = 0; i < elements.size(); i++) {
            SCVhdlWaveform_element ele = (SCVhdlWaveform_element)elements.get(i);
            SCVhdlExpression delayTime = (SCVhdlExpression)ele.getTime();
            if(delayTime != null) {
                String unit = ele.getTimeUnit();
                ret += intent() + "next_triger(" + delayTime + ", " + getSCTime(unit) + ");\r\n";                
            }
            ret += intent() + pre + ".write(" + ele.getValue() + ")";
            if(i < elements.size() - 1) {
                ret += ";\r\n";
            }
        }
        ret += ";";
        return ret;
    }
}

/**
 * <dl> signal_declaration ::=
 *   <dd> <b>signal</b> identifier_list : subtype_indication [ signal_kind ] [ := expression ] ;
 */
class SCVhdlSignal_declaration extends SCVhdlNode {
    public SCVhdlSignal_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGNAL_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> signal_kind ::=
 *   <dd> <b>register</b> | <b>bus</b>
 */
class SCVhdlSignal_kind extends SCVhdlNode {
    public SCVhdlSignal_kind(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGNAL_KIND);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> signal_list ::=
 *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class SCVhdlSignal_list extends SCVhdlNode {
    public SCVhdlSignal_list(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGNAL_LIST);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> signature ::=
 *   <dd> [ [ type_mark { , type_mark } ] [ <b>return</b> type_mark ] ]
 */
class SCVhdlSignature extends SCVhdlNode {
    public SCVhdlSignature(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIGNATURE);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simple_expression ::=
 *   <dd> [ sign ] term { adding_operator term }
 */
class SCVhdlSimple_expression extends SCVhdlNode {
    SCVhdlSign sign = null;
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlSimple_expression(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMPLE_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTTERM:
                newNode = new SCVhdlTerm(this, c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new SCVhdlAdding_operator(this, c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(sign != null) {
            ret += sign.postToString();
        }
        SCVhdlTerm term = (SCVhdlTerm)items.get(0);
        String tmp = term.postToString();
        if(term.items.size() > 2) {
            ret += "(" + tmp + ")";
        }else {
            ret += tmp;
        }
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            ret += " " + getReplaceOperator(items.get(i).postToString()) + " ";
            term = (SCVhdlTerm)items.get(i+1);
            tmp = term.postToString();
            if(term.items.size() > 2) {
                ret += "(" + tmp + ")";
            }else {
                ret += tmp;
            }
        }
        return ret;
    }
}

/**
 * <dl> simple_name ::=
 *   <dd> identifier
 */
class SCVhdlSimple_name extends SCVhdlNode {
    SCVhdlNode identifier = null;
    public SCVhdlSimple_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTSIMPLE_NAME);
        assert(node.getId() == ASTIDENTIFIER);
        identifier = new SCVhdlIdentifier(this, node);
    }

    public String postToString() {
        return identifier.postToString();
    }
}

/**
 * <dl> simple_simultaneous_statement ::=
 *   <dd> [ label : ] simple_expression == simple_expression [ tolerance_aspect ] ;
 */
class SCVhdlSimple_simultaneous_statement extends SCVhdlNode {
    public SCVhdlSimple_simultaneous_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMPLE_SIMULTANEOUS_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> simultaneous_statement_part </ul>
 */
class SCVhdlSimultaneous_alternative extends SCVhdlNode {
    public SCVhdlSimultaneous_alternative(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_ALTERNATIVE);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_case_statement ::=
 *   <dd> [ <i>case_</i>label : ]
 *   <ul> <b>case</b> expression <b>use</b>
 *   <ul> simultaneous_alternative
 *   <br> { simultaneous_alternative }
 *   </ul> <b>end</b> <b>case</b> [ <i>case_</i>label ] ; </ul>
 */
class SCVhdlSimultaneous_case_statement extends SCVhdlNode {
    public SCVhdlSimultaneous_case_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_CASE_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_if_statement ::=
 *   <dd> [ <i>if_</i>label : ]
 *   <ul> <b>if</b> condition <b>use</b>
 *   <ul>  simultaneous_statement_part
 *   </ul> { <b>elsif</b> condition <b>then</b>
 *   <ul> simultaneous_statement_part }
 *   </ul> [ <b>else</b>
 *   <ul> simultaneous_statement_part ]
 *   </ul> <b>end</b> <b>use</b> [ <i>if_</i>label ] ; </ul>
 */
class SCVhdlSimultaneous_if_statement extends SCVhdlNode {
    public SCVhdlSimultaneous_if_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_IF_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class SCVhdlSimultaneous_null_statement extends SCVhdlNode {
    public SCVhdlSimultaneous_null_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_NULL_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_procedural_statement ::=
 *   <dd> [ <i>procedural_</i>label : ]
 *   <ul> <b>procedural</b> [ <b>is</b> ]
 *   <ul> procedural_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> procedural_statement_part
 *   </ul> <b>end</b> <b>procedural</b> [ <i>procedural_</i>label ] ; </ul>
 */
class SCVhdlSimultaneous_procedural_statement extends SCVhdlNode {
    public SCVhdlSimultaneous_procedural_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_PROCEDURAL_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_statement ::=
 *   <dd> simple_simultaneous_statement
 *   <br> | simultaneous_if_statement
 *   <br> | simultaneous_case_statement
 *   <br> | simultaneous_procedural_statement
 *   <br> | simultaneous_null_statement
 */
class SCVhdlSimultaneous_statement extends SCVhdlNode {
    public SCVhdlSimultaneous_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> simultaneous_statement_part ::=
 *   <dd> { simultaneous_statement }
 */
class SCVhdlSimultaneous_statement_part extends SCVhdlNode {
    public SCVhdlSimultaneous_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT_PART);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> slice_name ::=
 *   <dd> prefix ( discrete_range )
 */
class SCVhdlSlice_name extends SCVhdlNode {
    public SCVhdlSlice_name(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSLICE_NAME);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> source_aspect ::=
 *   <dd> <b>spectrum</b> <i>magnitude_</i>simple_expression , <i>phase_</i>simple_expression
 *   <br> | <b>noise</b> <i>power_</i>simple_expression
 */
class SCVhdlSource_aspect extends SCVhdlNode {
    public SCVhdlSource_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSOURCE_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> source_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication source_aspect ;
 */
class SCVhdlSource_quantity_declaration extends SCVhdlNode {
    public SCVhdlSource_quantity_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSOURCE_QUANTITY_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> step_limit_specification ::=
 *   <dd> <b>limit</b> quantity_specification <b>with</b> <i>real_</i>expression ;
 */
class SCVhdlStep_limit_specification extends SCVhdlNode {
    public SCVhdlStep_limit_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSTEP_LIMIT_SPECIFICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> subnature_declaration ::=
 *   <dd> <b>subnature</b> identifier <b>is</b> subnature_indication ;
 */
class SCVhdlSubnature_declaration extends SCVhdlNode {
    public SCVhdlSubnature_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBNATURE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> subnature_indication ::=
 *   <dd> nature_mark [ index_constraint ] [ <b>tolerance</b> <i>string_</i>expression <b>across</b> <i>string_</i>expression <b>through</b> ]
 */
class SCVhdlSubnature_indication extends SCVhdlNode {
    public SCVhdlSubnature_indication(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBNATURE_INDICATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> string_literal ::=
 *   <dd> " { graphic_character } "
 */
class SCVhdlString_literal extends SCVhdlNode {
    String str = "";
    public SCVhdlString_literal(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTSTRING_LITERAL);
        str = node.firstTokenImage();
    }

    public String postToString() {
        return str;
    }
}

/**
 * <dl> subprogram_body ::=
 *   <dd> subprogram_specification <b>is</b>
 *   <ul> subprogram_declarative_part
 *   </ul> <b>begin</b>
 *   <ul> subprogram_statement_part
 *   </ul> <b>end</b> [ subprogram_kind ] [ designator ] ;
 */
class SCVhdlSubprogram_body extends SCVhdlNode {
    SCVhdlNode spec = null;
    SCVhdlNode declarative_part = null;
    SCVhdlNode statement_part = null;
    public SCVhdlSubprogram_body(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBPROGRAM_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBPROGRAM_SPECIFICATION:
                spec = new SCVhdlSubprogram_specification(this, c);
                break;
            case ASTSUBPROGRAM_DECLARATIVE_PART:
                declarative_part = new SCVhdlSubprogram_declarative_part(this, c);
                break;
            case ASTSUBPROGRAM_STATEMENT_PART:
                statement_part = new SCVhdlSubprogram_statement_part(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += spec.postToString() + "\r\n";
        ret += "{\r\n";
        ret += declarative_part.postToString() + "\r\n";
        ret += statement_part.postToString() + "\r\n";
        ret += "}\r\n";
        return "";
    }
}

/**
 * <dl> subprogram_declaration ::=
 *   <dd> subprogram_specification ;
 */
class SCVhdlSubprogram_declaration extends SCVhdlNode {
    SCVhdlNode spec = null;
    public SCVhdlSubprogram_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATION);
        spec = new SCVhdlSubprogram_specification(this, (ASTNode)node.getChild(0));
    }

    public String postToString() {
        return spec.postToString() + ";";
    }
}

/**
 * <dl> subprogram_declarative_item ::=
 *   <dd> subprogram_declaration
 *   <br> | subprogram_body
 *   <br> | type_declaration
 *   <br> | subtype_declaration
 *   <br> | constant_declaration
 *   <br> | variable_declaration
 *   <br> | file_declaration
 *   <br> | alias_declaration
 *   <br> | attribute_declaration
 *   <br> | attribute_specification
 *   <br> | use_clause
 *   <br> | group_template_declaration
 *   <br> | group_declaration
 */
class SCVhdlSubprogram_declarative_item extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlSubprogram_declarative_item(SCVhdlNode p, ASTNode node) {
        super(p, node);
        //assert(node.getId() == ASTSUBPROGRAM_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new SCVhdlSubprogram_declaration(this, node);
            break;
        case ASTSUBPROGRAM_BODY:
            item = new SCVhdlSubprogram_body(this, node);
            break;
        case ASTTYPE_DECLARATION:
            item = new SCVhdlType_declaration(this, node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new SCVhdlSubtype_declaration(this, node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new SCVhdlConstant_declaration(this, node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVhdlVariable_declaration(this, node);
            break;
        case ASTFILE_DECLARATION:
            item = new SCVhdlFile_declaration(this, node);
            break;
        case ASTALIAS_DECLARATION:
            item = new SCVhdlAlias_declaration(this, node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new SCVhdlAttribute_declaration(this, node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new SCVhdlAttribute_specification(this, node);
            break;
        case ASTUSE_CLAUSE:
            item = new SCVhdlUse_clause(this, node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new SCVhdlGroup_template_declaration(this, node);
            break;
        case ASTGROUP_DECLARATION:
            item = new SCVhdlGroup_declaration(this, node);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> subprogram_declarative_part ::=
 *   <dd> { subprogram_declarative_item }
 */
class SCVhdlSubprogram_declarative_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlSubprogram_declarative_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlSubprogram_declarative_item(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> subprogram_kind ::=
 *   <dd> <b>procedure</b> | <b>function</b>
 */
//class SCVhdlSubprogram_kind extends SCVhdlNode {
//    public SCVhdlSubprogram_kind(SCVhdlNode p, ASTNode node) {
//        super(p, node);
//        assert(node.getId() == ASTSUBPROGRAM_KIND);
//    }
//
//    public String postToString() {
//        return "";
//    }
//}

/**
 * <dl> subprogram_specification ::=
 *   <dd> <b>procedure</b> designator [ ( formal_parameter_list ) ]
 *   <br> | [ <b>pure</b> | <b>impure</b> ] <b>function</b> designator [ ( formal_parameter_list ) ]
 *   <ul> <b>return</b> type_mark </ul>
 */
class SCVhdlSubprogram_specification extends SCVhdlNode {
    boolean isFunction = false;
    SCVhdlNode designator = null;
    SCVhdlNode parameter_list = null;
    SCVhdlNode type_mark = null;
    public SCVhdlSubprogram_specification(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBPROGRAM_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDESIGNATOR:
                designator = new SCVhdlDesignator(this, c);
                break;
            case ASTVOID:
                if(c.firstTokenImage().equalsIgnoreCase(tokenImage[PROCEDURE])) {
                    isFunction = false;
                }else {
                    isFunction = true;
                }
                break;
            case ASTFORMAL_PARAMETER_LIST:
                parameter_list = new SCVhdlFormal_parameter_list(this, c);
                break;
            case ASTTYPE_MARK:
                type_mark = new SCVhdlType_mark(this, c);
                isFunction = true;
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        if(type_mark != null) {     // type_mark appear only in function
            ret += type_mark.postToString() + " ";
        }else {
            ret += "void ";
        }
        ret += designator.postToString();
        ret += "(";
        if(parameter_list != null) {
            ret += parameter_list.postToString();
        }
        ret += ")";
        return ret;
    }
}

/**
 * <dl> subprogram_statement_part ::=
 *   <dd> { sequential_statement }
 */
class SCVhdlSubprogram_statement_part extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlSubprogram_statement_part(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBPROGRAM_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode item = new SCVhdlSequential_statement(this, c);
            items.add(item);
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i);
            if(i < items.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> subtype_declaration ::=
 *   <dd> <b>subtype</b> identifier <b>is</b> subtype_indication ;
 */
class SCVhdlSubtype_declaration extends SCVhdlNode {
    public SCVhdlSubtype_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBTYPE_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> subtype_indication ::=
 *   <dd> [ <i>resolution_function_</i>name ] type_mark [ constraint ] [ tolerance_aspect ]
 */
class SCVhdlSubtype_indication extends SCVhdlNode {
    SCVhdlName name = null;
    SCVhdlType_mark type_mark = null;
    SCVhdlConstraint constraint = null;
    SCVhdlTolerance_aspect tolerance = null;
    public SCVhdlSubtype_indication(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUBTYPE_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new SCVhdlName(this, c);
                break;
            case ASTTYPE_MARK:
                type_mark = new SCVhdlType_mark(this, c);
                break;
            case ASTCONSTRAINT:
                constraint = new SCVhdlConstraint(this, c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance = new SCVhdlTolerance_aspect(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    public String postToString() {
        return "";
    }
}

/**
 * <dl> suffix ::=
 *   <dd> simple_name
 *   <br> | character_literal
 *   <br> | operator_symbol
 *   <br> | <b>all</b>
 */
class SCVhdlSuffix extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlSuffix(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTSUFFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new SCVhdlSimple_name(this, c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new SCVhdlOperator_symbol(this, c);
            break;
        case ASTVOID:
            item = new SCVhdlToken(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> target ::=
 *   <dd> name
 *   <br> | aggregate
 */
class SCVhdlTarget extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlTarget(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTARGET);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new SCVhdlName(this, c);
            break;
        case ASTAGGREGATE:
            item = new SCVhdlAggregate(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> term ::=
 *   <dd> factor { multiplying_operator factor }
 */
class SCVhdlTerm extends SCVhdlNode {
    ArrayList<SCVhdlNode> items = new ArrayList<SCVhdlNode>();
    public SCVhdlTerm(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTERM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode newNode = null;
            switch(c.getId())
            {
            case ASTFACTOR:
                newNode = new SCVhdlFactor(this, c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new SCVhdlMultiplying_operator(this, c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        SCVhdlFactor factor = (SCVhdlFactor)items.get(0);
        ret += factor.postToString();
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            ret += " " + getReplaceOperator(items.get(i).postToString()) + " ";
            factor = (SCVhdlFactor)items.get(i+1);
            ret += factor.postToString();
        }
        return ret;
    }
}

/**
 * <dl> terminal_aspect ::=
 *   <dd> <i>plus_terminal_</i>name [ <b>to</b> <i>minus_terminal_</i>name ]
 */
class SCVhdlTerminal_aspect extends SCVhdlNode {
    public SCVhdlTerminal_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTERMINAL_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication ;
 */
class SCVhdlTerminal_declaration extends SCVhdlNode {
    public SCVhdlTerminal_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTERMINAL_DECLARATION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> through_aspect ::=
 *   <dd> identifier_list [ tolerance_aspect ] [ := expression ] <b>through</b>
 */
class SCVhdlThrough_aspect extends SCVhdlNode {
    public SCVhdlThrough_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTHROUGH_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> timeout_clause ::=
 *   <dd> <b>for</b> <i>time_or_real_</i>expression
 */
class SCVhdlTimeout_clause extends SCVhdlNode {
    SCVhdlNode expression = null;
    String time_unit_name = "ns";
    public SCVhdlTimeout_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTIMEOUT_CLAUSE);
        expression = new SCVhdlExpression(this, (ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }

    public String postToString() {
        return expression.postToString();
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }
}

/**
 * <dl> tolerance_aspect ::=
 *   <dd> <b>tolerance</b> <i>string_</i>expression
 */
class SCVhdlTolerance_aspect extends SCVhdlNode {
    public SCVhdlTolerance_aspect(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTOLERANCE_ASPECT);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> type_conversion ::=
 *   <dd> type_mark ( expression )
 */
class SCVhdlType_conversion extends SCVhdlNode {
    public SCVhdlType_conversion(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTYPE_CONVERSION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> type_declaration ::=
 *   <dd> full_type_declaration
 *   <br> | incomplete_type_declaration
 */
class SCVhdlType_declaration extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlType_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTYPE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTFULL_TYPE_DECLARATION:
            item = new SCVhdlFull_type_declaration(this, c);
            break;
        case ASTINCOMPLETE_TYPE_DECLARATION:
            item = new SCVhdlIncomplete_type_declaration(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> type_definition ::=
 *   <dd> scalar_type_definition
 *   <br> | composite_type_definition
 *   <br> | access_type_definition
 *   <br> | file_type_definition
 */
class SCVhdlType_definition extends SCVhdlNode {
    SCVhdlNode item = null;
    public SCVhdlType_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_TYPE_DEFINITION:
            item = new SCVhdlScalar_type_definition(this, c);
            break;
        case ASTCOMPOSITE_TYPE_DEFINITION:
            item = new SCVhdlComposite_type_definition(this, c);
            break;
        case ASTACCESS_TYPE_DEFINITION:
            item = new SCVhdlAccess_type_definition(this, c);
            break;
        case ASTFILE_TYPE_DEFINITION:
            item = new SCVhdlFile_type_definition(this, c);
            break;
        default:
            break;
        }
    }

    public String postToString() {
        return item.postToString();
    }
}

/**
 * <dl> type_mark ::=
 *   <dd> <i>type_</i>name
 *   <br> | <i>subtype_</i>name
 */
class SCVhdlType_mark extends SCVhdlNode {
    public SCVhdlType_mark(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTTYPE_MARK);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> unconstrained_array_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> <i>element_</i>subtype_indication </ul>
 */
class SCVhdlUnconstrained_array_definition extends SCVhdlNode {
    public SCVhdlUnconstrained_array_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTUNCONSTRAINED_ARRAY_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> unconstrained_nature_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> subnature_indication </ul>
 */
class SCVhdlUnconstrained_nature_definition extends SCVhdlNode {
    public SCVhdlUnconstrained_nature_definition(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTUNCONSTRAINED_NATURE_DEFINITION);
    }

    public String postToString() {
        return "";
    }
}

/**
 * <dl> use_clause ::=
 *   <dd> <b>use</b> selected_name { , selected_name } ;
 */
class SCVhdlUse_clause extends SCVhdlNode {
    static final String IEEE = "ieee";
    static final String STD_LOGIC_1164 = "std_logic_1164";
    static final String STD = "std";
    static final String TEXTIO = "textio";
    static final String WORK = "work";
    
    ArrayList<SCVhdlSelected_name> names = new ArrayList<SCVhdlSelected_name>();
    
    public SCVhdlUse_clause(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTUSE_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlSelected_name newNode = null;
            switch(c.getId())
            {
            case ASTSELECTED_NAME:
                newNode = new SCVhdlSelected_name(this, c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        for(int i = 0; i < names.size(); i++) {
            ArrayList<String> segs = names.get(i).getNameSegments();
            if(segs.size() == 0 || segs.get(0).equalsIgnoreCase(IEEE) 
                    || segs.get(0).equalsIgnoreCase(STD)) {
                //TODO do something
                continue;
            }
            
            String tmp = "#include \"";
            for(int j = 0; j < segs.size(); j++) {
                String seg = segs.get(j);
                if(seg.equalsIgnoreCase(WORK)) { continue; }
                tmp += seg;
                if(j == segs.size()-1 || 
                    (j == segs.size()-2 && seg.equalsIgnoreCase(tokenImage[ALL])))
                    tmp += ".h\"";
                else
                    tmp += "/";
            }
            
            ret += tmp;
            if(i < names.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> variable_assignment_statement ::=
 *   <dd> [ label : ] target := expression ;
 */
class SCVhdlVariable_assignment_statement extends SCVhdlNode {
    SCVhdlNode target = null;
    SCVhdlNode expression = null;
    public SCVhdlVariable_assignment_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTVARIABLE_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new SCVhdlTarget(this, c);
                break;
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = "";
        ret += target.postToString();
        ret += " = ";
        ret += expression.postToString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> variable_declaration ::=
 *   <dd> [ <b>shared</b> ] <b>variable</b> identifier_list : subtype_indication [ := expression ] ;
 */
class SCVhdlVariable_declaration extends SCVhdlNode {
    SCVhdlIdentifier_list identifier_list = null;
    SCVhdlSubtype_indication subtype_indication = null;
    SCVhdlExpression expression = null;
    public SCVhdlVariable_declaration(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTVARIABLE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER_LIST:
                identifier_list = new SCVhdlIdentifier_list(this, c);
                break;
            case ASTSUBTYPE_INDICATION:
                subtype_indication = new SCVhdlSubtype_indication(this, c);
                break;
            case ASTEXPRESSION:
                expression = new SCVhdlExpression(this, c);
                break;
            default:
                break;
            }
        }
    }

    public String postToString() {
        String ret = intent();
        ret += subtype_indication.postToString();
        
        ArrayList<SCVhdlNode> items = identifier_list.getItems();
        for(int i = 0; i < items.size(); i++) {
            ret += " " + items.get(i).postToString();
            if(expression != null) {
                ret += " " + expression.postToString();
            }
            if(i < items.size() - 1) {
                ret += ",";
            }
        }
        ret += ";";
        return ret;
    }
}

/**
 * <dl> wait_statement ::=
 *   <dd> [ label : ] <b>wait</b> [ sensitivity_clause ] 
 *                [ condition_clause ] [ timeout_clause ] ;
 */
class SCVhdlWait_statement extends SCVhdlNode {
    SCVhdlSensitivity_clause sensitivity = null;
    SCVhdlCondition_clause condition = null;
    SCVhdlTimeout_clause timeout = null;
    public SCVhdlWait_statement(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTWAIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSENSITIVITY_CLAUSE:
                sensitivity = new SCVhdlSensitivity_clause(this, c);
                break;
            case ASTCONDITION_CLAUSE:
                condition = new SCVhdlCondition_clause(this, c);
                break;
            case ASTTIMEOUT_CLAUSE:
                timeout = new SCVhdlTimeout_clause(this, c);
                break;
            default:
                break;
            }
        }
    }
    
    private String getWaitString() {
        String ret = "";
        ret += "next_trigger(";
        if(timeout != null) {
            ret += timeout.postToString();
            ret += ", ";
            ret += getSCTime(timeout.getTimeUnitName());
        }
        
        if(sensitivity != null) {
            ret += ", ";
            ArrayList<String> sensList = sensitivity.getSensitiveList();
            String strSens = "";
            for(int i = 0; i < sensList.size(); i++) {
                strSens += sensList.get(i);
                if(i < sensList.size()) {
                    strSens += " | ";
                }
            }
        }
        ret += ")";
        return ret;
    }

    public String postToString() {
        String ret = "";
        if(condition != null) {
            ret += "do {\r\n";
            ret += getWaitString();
            ret += "\r\n}while(";
            ret += condition.postToString();
            ret += ")";
        }else {
            ret += getWaitString();
        }
        ret += ";";
        return ret;
    }
}

/**
 * <dl> waveform ::=
 *   <dd> waveform_element { , waveform_element }
 *   <br> <b>| unaffected</b>
 */
class SCVhdlWaveform extends SCVhdlNode {
    ArrayList<SCVhdlNode> elements = new ArrayList<SCVhdlNode>();
    boolean isUnaffected = false;
    public SCVhdlWaveform(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTWAVEFORM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode ele = null; 
            switch(c.getId())
            {
            case ASTVOID:   // unaffected
                isUnaffected = true;
                break;
            case ASTWAVEFORM_ELEMENT:
                ele = new SCVhdlWaveform_element(this, c);
                elements.add(ele);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<SCVhdlNode> getElements() {
        return elements;
    }

    public String postToString() {
        if(isUnaffected) {
            warning("unaffected ignore");
        }
        return "";
    }
}

/**
 * <dl> waveform_element ::=
 *   <dd> <i>value_</i>expression [ <b>after</b> <i>time_</i>expression ]
 *   <br> | <b>null</b> [ <b>after</b> <i>time_</i>expression ]
 */
class SCVhdlWaveform_element extends SCVhdlNode {
    SCVhdlNode value_exp = null;
    SCVhdlNode time_exp = null;
    boolean isNull = false;
    String timeUnit = "";
    public SCVhdlWaveform_element(SCVhdlNode p, ASTNode node) {
        super(p, node);
        assert(node.getId() == ASTWAVEFORM_ELEMENT);
        boolean bAfter = false;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            SCVhdlNode ele = null; 
            switch(c.getId())
            {
            case ASTVOID:
                if(c.firstTokenImage().equalsIgnoreCase(tokenImage[NULL])) {
                    isNull = true;
                }else if(c.firstTokenImage().equalsIgnoreCase(tokenImage[AFTER])) {
                    bAfter = true;
                }
                break;
            case ASTEXPRESSION:
                ele = new SCVhdlExpression(this, c);
                if(!bAfter) {
                    value_exp = ele;
                }else {
                    time_exp = ele;
                    timeUnit = c.getLastToken().image;
                }
                break;
            default:
                break;
            }
        }
    }
    
    public SCVhdlNode getValue() {
        return value_exp;
    }
    
    public SCVhdlNode getTime() {
        return time_exp;
    }
    
    public String getTimeUnit() {
        return timeUnit;
    }

    public String postToString() {
        String ret = "";
        if(isNull) {
            warning("null ignore");
        }
        return ret;
    }
}
