package converter.vhdl;

import java.util.ArrayList;

import common.printFileAndLineNumber;

import parser.IASTNode;
import parser.IParser;
import parser.vhdl.ASTNode;
import parser.vhdl.IVhdlType;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlTokenConstants;

public class ScVhdl implements SCVhdlConstants, VhdlTokenConstants, 
                        VhdlASTConstants, IVhdlType
{
    protected static IParser parser = null;
    
    protected ASTNode curNode = null;
    protected int beginLine = 0;
    protected int endLine = 0;
    
    protected int level = 0;    // intent level
    
    /**
     * constructor
     */
    public ScVhdl(IParser parser) {
        ScVhdl.parser = parser;
        if(parser != null) {
            curNode = (ASTNode)parser.getRoot();
            init();
        }
    }
    
    public ScVhdl(ASTNode node) {
        curNode = node;
        init();
    }
    
    protected void init() {
       if(curNode != null) {
            beginLine = curNode.getFirstToken().beginLine;
            endLine = curNode.getLastToken().endLine;
        } 
    }
    
    public String scString() {
        return "";
    }
    
    protected void warning(String msg) {
        System.out.println("line--" + beginLine + ": warning: " + msg);
    }
    
    protected void error() {
        System.err.println("line--" + beginLine + ": ");
        new printFileAndLineNumber("=========not support========");
    }
    
    protected void error(String msg) {
        System.err.println("line--" + beginLine + ": ");
        new printFileAndLineNumber(msg);
    }
    
    // intent
    protected static String intent(int lv)
    {
        String ret = "";
        for (int i = 0; i <= lv; i++)
            for (int j = 0; j < tabSize; j++)
                ret += " ";
        return ret;
    }
    
    protected String intent()
    {
        return intent(level);
    }
    
    String getReplaceOperator(String token)
    {
        String ret = token;
        for (int i = 0; i < vhdlOperators.length; i++) {
            if (token.equalsIgnoreCase(vhdlOperators[i])) {
                if (curNode.isLogic())
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

class ScToken extends ScVhdl {
    String image = null;
    public ScToken(ASTNode node) {
        super(node);
        assert(node.getId() == ASTVOID);
        image = node.firstTokenImage();
    }

    public String scString() {
        return image;
    }
}

/**
 * <dl> abstract_literal ::=
 *   <dd> decimal_literal | based_literal
 */
class ScAbstract_literal extends ScVhdl {
    ScVhdl item = null;
    public ScAbstract_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTABSTRACT_LITERAL);
        int kind = node.getFirstToken().kind;
        if(kind == decimal_literal) {
            item = new ScDecimal_literal(node);
        }else if(kind == based_literal) {
            item = new ScBased_literal(node);
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> access_type_definition ::=
 *   <dd> <b>access</b> subtype_indication
 */
class ScAccess_type_definition extends ScVhdl {
    ScVhdl sub = null;
    public ScAccess_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACCESS_TYPE_DEFINITION);
        sub = new ScSubtype_indication((ASTNode)node.getChild(0));
    }

    public String scString() {
        warning("token access ignored");
        return sub.scString();
    }
}

/**
 * <dl> across_aspect ::=
 *   <dd> identifier_list
 *   [ tolerance_aspect ]
 *   [ := expression ] <b>across</b>
 */
class ScAcross_aspect extends ScVhdl {
    ScVhdl idlist = null;
    ScVhdl tolerance_aspect = null;
    ScVhdl expression = null;
    public ScAcross_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACROSS_ASPECT);
        int i;
        for(i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTIDENTIFIER_LIST:
                idlist = new ScIdentifier_list((ASTNode)c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance_aspect = new ScTolerance_aspect((ASTNode)c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression((ASTNode)c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        assert(idlist != null);
        String ret = idlist.scString();
        if(tolerance_aspect != null) {
            warning("tolerance_aspect ignored");
            ret += tolerance_aspect.scString();
        }
        if(expression != null) {
            ret += " = ";
            ret += expression.scString();
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
class ScActual_designator extends ScVhdl {
    ScVhdl subNode = null;
    boolean isOpen = false;
    public ScActual_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACTUAL_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTEXPRESSION:
            subNode = new ScExpression(c);
            break;
        case ASTVOID:
            isOpen = true;
            break;
        case ASTNAME:
            subNode = new ScName(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        String ret = "";
        if(subNode != null)
            ret = subNode.scString();
        return ret;
    }
}

/**
 * <dl> actual_parameter_part ::=
 *   <dd> <i>parameter_</i>association_list
 */
class ScActual_parameter_part extends ScVhdl {
    ScVhdl paramList = null;
    public ScActual_parameter_part(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTACTUAL_PARAMETER_PART);
        paramList = new ScAssociation_list(node);
    }

    public String scString() {
        return paramList.scString();
    }
}

/**
 * <dl> actual_part ::=
 *   <dd> actual_designator
 *   <br> | <i>function_</i>name ( actual_designator )
 *   <br> | type_mark ( actual_designator )
 */
class ScActual_part extends ScVhdl {
    ScVhdl name = null;
    ScVhdl designator = null;
    public ScActual_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTACTUAL_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTACTUAL_DESIGNATOR:
                designator = new ScActual_designator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(((ScActual_designator)designator).isOpen) {
            warning("token open ignored");
        }
        if(name != null) {
            ret += name.scString();
            ret += "(" + designator.scString() + ")";
        }else {
            ret += designator.scString();
        }
        return ret;
    }
}

/**
 * <dl> adding_operator ::=
 *   <dd> + | - | &
 */
class ScAdding_operator extends ScVhdl {
    ScToken token = null;
    public ScAdding_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTADDING_OPERATOR);
        token = new ScToken(node);
    }

    public String scString() {
        return token.scString();
    }
}

/**
 * <dl> aggregate ::=
 *   <dd> ( element_association { , element_association } )
 */
class ScAggregate extends ScVhdl {
    ArrayList<ScVhdl> elementList = new ArrayList<ScVhdl>();
    public ScAggregate(ASTNode node) {
        super(node);
        assert(node.getId() == ASTAGGREGATE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            ScVhdl n = new ScElement_association((ASTNode)c);
            elementList.add(n);
        }
    }

    public String scString() {
        String ret = "(";
        for(int i = 0; i < elementList.size(); i++) {
            ret += elementList.get(i).scString();
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
class ScAlias_declaration extends ScVhdl {
    ScVhdl designator = null;
    ScVhdl indication = null;
    ScVhdl name = null;
    ScVhdl signature = null;
    public ScAlias_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALIAS_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTALIAS_DESIGNATOR:
                designator = new ScAlias_designator(c);
                break;
            case ASTALIAS_INDICATION:
                indication = new ScAlias_indication(c);
                break;
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTSIGNATURE:
                signature = new ScSignature(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "define ";
        ret += designator.scString();
        if(indication != null) {
            warning("alias indication ignored");
        }
        ret += " " + name.scString();
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
class ScAlias_designator extends ScVhdl {
    ScVhdl subNode = null;
    public ScAlias_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALIAS_DESIGNATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        if(id != ASTIDENTIFIER) {
            warning("only support identifier alias");
        }
        switch(id)
        {
        case ASTIDENTIFIER:
            subNode = new ScIdentifier(c);
            break;
        case ASTVOID:   // character literal
            subNode = new ScToken(c);
            break;
        case ASTOPERATOR_SYMBOL:
            subNode = new ScOperator_symbol(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
    }
}

/**
 * <dl> alias_indication ::=
 *   <dd> subtype_indication
 *   <br> | subnature_indication
 */
class ScAlias_indication extends ScVhdl {
    ScVhdl subNode = null;
    public ScAlias_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALIAS_INDICATION);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new ScSubtype_indication(c);
            break;
        case ASTSUBNATURE_INDICATION:
            subNode = new ScSubnature_indication(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
    }
}

/**
 * <dl> allocator ::=
 *   <dd> <b>new</b> subtype_indication
 *   <br> | <b>new</b> qualified_expression
 */
class ScAllocator extends ScVhdl {
    ScVhdl subNode = null;
    public ScAllocator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTALLOCATOR);
        ASTNode c = (ASTNode)node.getChild(0);
        int id = c.getId();
        switch(id)
        {
        case ASTSUBTYPE_INDICATION:
            subNode = new ScSubtype_indication(c);
            break;
        case ASTQUALIFIED_EXPRESSION:
            subNode = new ScQualified_expression(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return subNode.scString();
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
class ScArchitecture_body extends ScVhdl {
    ScVhdl identifier = null;
    ScVhdl entity_name = null;
    ScVhdl delarative_part = null;
    ScVhdl statement_part = null;
    public ScArchitecture_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            switch(id)
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTNAME:
                entity_name = new ScName(c);
                break;
            case ASTARCHITECTURE_DECLARATIVE_PART:
                delarative_part = new ScArchitecture_declarative_part(c);
                break;
            case ASTARCHITECTURE_STATEMENT_PART:
                statement_part = new ScArchitecture_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = delarative_part.scString() + "\r\n";
        ret += statement_part.scString();
        return ret;
    }
}

/**
 * <dl> architecture_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class ScArchitecture_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> itemList = new ArrayList<ScVhdl>();
    public ScArchitecture_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = new ScBlock_declarative_item(c);
            itemList.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).scString();
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
class ScArchitecture_statement extends ScVhdl {
    ScVhdl itemNode = null;
    public ScArchitecture_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTARCHITECTURE_STATEMENT);
        switch(node.getId())
        {
        case ASTSIMULTANEOUS_STATEMENT:
            itemNode = new ScSimultaneous_statement(node);
            break;
        case ASTCONCURRENT_STATEMENT:
            itemNode = new ScConcurrent_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return itemNode.toString();
    }
}

/**
 * <dl> architecture_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScArchitecture_statement_part extends ScVhdl {
    ArrayList<ScVhdl> itemList = new ArrayList<ScVhdl>();
    public ScArchitecture_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARCHITECTURE_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = new ScArchitecture_statement(c);
            itemList.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < itemList.size(); i++) {
            ret += itemList.get(i).scString();
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
class ScArray_nature_definition extends ScVhdl {
    ScVhdl itemNode = null;
    public ScArray_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARRAY_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTUNCONSTRAINED_NATURE_DEFINITION:
                itemNode = new ScUnconstrained_nature_definition(c);
                break;
            case ASTCONSTRAINED_NATURE_DEFINITION:
                itemNode = new ScConstrained_nature_definition(c);
                break;
            default:
                break;
            }            
        }

    }

    public String scString() {
        return itemNode.scString();
    }
}

/**
 * <dl> array_type_definition ::=
 *   <dd> unconstrained_array_definition | constrained_array_definition
 */
class ScArray_type_definition extends ScVhdl {
    ScVhdl itemNode = null;
    public ScArray_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTARRAY_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTUNCONSTRAINED_ARRAY_DEFINITION:
            itemNode = new ScUnconstrained_array_definition(node);
            break;
        case ASTCONSTRAINED_ARRAY_DEFINITION:
            itemNode = new ScConstrained_array_definition(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return itemNode.scString();
    }
}

/**
 * <dl> assertion ::=
 *   <dd> <b>assert</b> condition
 *   <ul> [ <b>report</b> expression ]
 *   <br> [ <b>severity</b> expression ] </ul>
 */
class ScAssertion extends ScVhdl {
    ScVhdl condition = null;
    ScVhdl report_exp = null;
    ScVhdl severity_exp = null;
    public ScAssertion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSERTION);
        for(int i = 0; i < node.getChildrenNum(); i += 2) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTVOID);  // the first must be token
            String image = c.firstTokenImage(); 
            c = (ASTNode)node.getChild(i+1);
            if(image.equalsIgnoreCase(tokenImage[ASSERT])) {
                condition = new ScCondition(c);
            }else if(image.equalsIgnoreCase(tokenImage[REPORT])) {
                report_exp = new ScExpression(c);
            }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                severity_exp = new ScExpression(c);
            }
        }
    }

    public String scString() {
        String ret = "";
        if(report_exp != null) {
            ret += intent() + "if(!(" + condition.scString() + "))\r\n";
            ret += intent(level+1) + "printf(\"";
            ret += report_exp.scString() + "\");\r\n";
        }
        ret += "assert(" + condition.scString() + ");";
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
class ScAssertion_statement extends ScVhdl {
    ScVhdl label = null;
    ScVhdl assertion = null;
    public ScAssertion_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSERTION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                label = new ScIdentifier(c);
                break;
            case ASTASSERTION:
                assertion = new ScAssertion(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(label != null) {
            warning("label " + label.scString() + " ignored");
        }
        ret += assertion.scString() + ";";
        return ret;
    }
}

/**
 * <dl> association_element ::=
 *   <dd> [ formal_part => ] actual_part
 */
class ScAssociation_element extends ScVhdl {
    ScVhdl formal_part = null;
    ScVhdl actual_part = null;
    public ScAssociation_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSOCIATION_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTFORMAL_PART:
                formal_part = new ScFormal_part(c);
                break;
            case ASTACTUAL_PART:
                actual_part = new ScActual_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(formal_part != null) {
            ret += formal_part.scString();
            ret += "(" + actual_part.scString() + ")";   //TODO write() ??
        }else {
            ret += actual_part.scString();
        }
        return ret;
    }
}

/**
 * <dl> association_list ::=
 *   <dd> association_element { , association_element }
 */
class ScAssociation_list extends ScVhdl {
    ArrayList<ScVhdl> elements = new ArrayList<ScVhdl>();
    public ScAssociation_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTASSOCIATION_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++){
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTASSOCIATION_ELEMENT);
            ScVhdl newNode = new ScAssociation_element(c);
            elements.add(newNode);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < elements.size(); i++) {
            ret += elements.get(i).scString();
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
class ScAttribute_declaration extends ScVhdl {
    public ScAttribute_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTATTRIBUTE_DECLARATION);
    }

    public String scString() {
        error("user defined attribute not support!");
        return "";
    }
}

/**
 * <dl> attribute_designator ::=
 *   <dd> <i>attribute_</i>simple_name
 */
class ScAttribute_designator extends ScVhdl {
    ScVhdl name = null;
    public ScAttribute_designator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTATTRIBUTE_DESIGNATOR);
        name = new ScSimple_name(node);
    }

    public String scString() {
        return name.scString();
    }
}

/**
 * <dl> attribute_name ::=
 *   <dd> prefix [ signature ] ' attribute_designator [ ( expression { , expression } ) ]
 */
class ScAttribute_name extends ScVhdl {
    ScVhdl prefix = null;
    ScVhdl signature = null;
    ScVhdl designator = null;
    ArrayList<ScVhdl> expressions = new ArrayList<ScVhdl>();
    public ScAttribute_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTATTRIBUTE_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTSIGNATURE:
                signature = new ScSignature(c);
                break;
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new ScAttribute_designator(c);
                break;
            case ASTEXPRESSION:
                {
                    ScVhdl exp  = new ScExpression(c);
                    expressions.add(exp);
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += prefix.scString();
        if(signature != null) {
            warning("signature ignored");
        }
        ret += "." + designator.scString();
        ret += "(";
        if(expressions.size() > 0) {
            for(int i = 0; i < expressions.size(); i++) {
                ret += expressions.get(i).scString();
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
class ScAttribute_specification extends ScVhdl {
    ScVhdl designator = null;
    ScVhdl entity = null;
    ScVhdl expression = null;
    public ScAttribute_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTATTRIBUTE_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTATTRIBUTE_DESIGNATOR:
                designator = new ScAttribute_designator(c);
                break;
            case ASTENTITY_SPECIFICATION:
                entity = new ScEntity_specification(c);
                break;
            case ASTEXPRESSION:
                expression  = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error("user define attribute not support");
        return "";
    }
}

/**
 * <dl> based_literal ::=
 *   <dd> base # based_integer [ . based_integer ] # [ exponent ]
 */
class ScBased_literal extends ScVhdl {
    String base = "10";
    String based_integer = "0";
    String fract_based_integer = "";
    String exponent = "";
    public ScBased_literal(ASTNode node) {
        super(node);
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

    public String scString() {
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
//class ScBasic_character extends SCVhdl {
//    public ScBasic_character(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTBASIC_CHARACTER);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> basic_graphic_character ::=
 *   <dd> upper_case_letter | digit | special_character | space_character
 */
//class ScBasic_graphic_character extends SCVhdl {
//    public ScBasic_graphic_character(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTBASIC_GRAPHIC_CHARACTER);
//    }
//
//    public String scString() {
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
class ScBinding_indication extends ScVhdl {
    ScVhdl entity = null;
    ScVhdl generic_map = null;
    ScVhdl port_map = null;
    public ScBinding_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBINDING_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTENTITY_ASPECT:
                entity = new ScEntity_aspect(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new ScPort_map_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        error();
        return ret;
    }
}

/**
 * <dl> bit_string_literal ::=
 *   <dd> base_specifier " [ bit_value ] "
 */
class ScBit_string_literal extends ScVhdl {
    int base = 2;
    String bit_value = "";
    public ScBit_string_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTBIT_STRING_LITERAL);
        String image = node.firstTokenImage();
        base = getBase_specifier(image.substring(0, 1));
        int index = image.indexOf('\"');
        int index1 = image.lastIndexOf('\"');
        bit_value = image.substring(index+1, index1).trim();
    }

    public String scString() {
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
class ScBlock_configuration extends ScVhdl {
    ScVhdl spec = null;
    ScVhdl use_clause = null;
    ScVhdl cfg_item = null;
    public ScBlock_configuration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_SPECIFICATION:
                spec = new ScBlock_specification(c);
                break;
            case ASTUSE_CLAUSE:
                use_clause = new ScUse_clause(c);
                break;
            case ASTCONFIGURATION_ITEM:
                cfg_item = new ScConfiguration_item(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
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
class ScBlock_declarative_item extends ScVhdl {
    ScVhdl itemNode = null;
    public ScBlock_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTBLOCK_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            itemNode = new ScSubprogram_declaration(node);
            break;
        case ASTSUBPROGRAM_BODY:
            itemNode = new ScSubprogram_body(node);
            break;
        case ASTTYPE_DECLARATION:
            itemNode = new ScType_declaration(node);
            break;
        case ASTSUBTYPE_DECLARATION:
            itemNode = new ScSubtype_declaration(node);
            break;
        case ASTCONSTANT_DECLARATION:
            itemNode = new ScConstant_declaration(node);
            break;
        case ASTSIGNAL_DECLARATION:
            itemNode = new ScSignal_declaration(node);
            break;
        case ASTVARIABLE_DECLARATION:
            itemNode = new SCVariable_declaration(node);
            break;
        case ASTFILE_DECLARATION:
            itemNode = new ScFile_declaration(node);
            break;
        case ASTALIAS_DECLARATION:
            itemNode = new ScAlias_declaration(node);
            break;
        case ASTCOMPONENT_DECLARATION:
            itemNode = new ScComponent_declaration(node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            itemNode = new ScAttribute_declaration(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            itemNode = new ScAttribute_specification(node);
            break;
        case ASTCONFIGURATION_SPECIFICATION:
            itemNode = new ScConfiguration_specification(node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            itemNode = new ScDisconnection_specification(node);
            break;
        case ASTSTEP_LIMIT_SPECIFICATION:
            itemNode = new ScStep_limit_specification(node);
            break;
        case ASTUSE_CLAUSE:
            itemNode = new ScUse_clause(node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            itemNode = new ScGroup_template_declaration(node);
            break;
        case ASTGROUP_DECLARATION:
            itemNode = new ScGroup_declaration(node);
            break;
        case ASTNATURE_DECLARATION:
            itemNode = new ScNature_declaration(node);
            break;
        case ASTSUBNATURE_DECLARATION:
            itemNode = new ScSubnature_declaration(node);
            break;
        case ASTQUANTITY_DECLARATION:
            itemNode = new ScQuantity_declaration(node);
            break;
        case ASTTERMINAL_DECLARATION:
            itemNode = new ScTerminal_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        String ret = "";
        if(itemNode != null) {
            ret = intent() + itemNode.scString();
        }
        return ret;
    }
}

/**
 * <dl> block_declarative_part ::=
 *   <dd> { block_declarative_item }
 */
class ScBlock_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScBlock_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScBlock_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScBlock_header extends ScVhdl {
    ScVhdl generic = null;
    ScVhdl generic_map = null;
    ScVhdl port = null;
    ScVhdl port_map = null;
    public ScBlock_header(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new ScGeneric_clause(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
                break;
            case ASTPORT_CLAUSE:
                port = new ScPort_clause(c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new ScPort_map_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(generic != null) {
            ret += generic.scString();
            if(generic_map != null) {
                ret += "\r\n" + generic_map.scString();
            }
        }
        if(!ret.isEmpty()) {
            ret += "\r\n";
        }
        if(port != null) {
            ret += port.scString();
            if(port_map != null) {
                ret += "\r\n" + port.scString();
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
class ScBlock_specification extends ScVhdl {
    ScVhdl name = null;
    ScVhdl index_spec = null;
    public ScBlock_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTIDENTIFIER:
                name = new ScLabel(c);
                break;
            case ASTINDEX_SPECIFICATION:
                index_spec = new ScIndex_specification(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += name.scString();
        if(index_spec != null) {
            ret += "(" + index_spec.scString() + ")";   //TODO: modify here
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
class ScBlock_statement extends ScVhdl {
    ScVhdl header = null;
    ScVhdl declarative_part = null;
    ScVhdl statement_part = null;
    public ScBlock_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBLOCK_HEADER:
                header = new ScBlock_header(c);
                break;
            case ASTBLOCK_DECLARATIVE_PART:
                declarative_part = new ScBlock_declarative_part(c);
                break;
            case ASTBLOCK_STATEMENT_PART:
                statement_part = new ScBlock_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String tab = intent(level+1);
        ret += intent() + "{";
        ret += tab + header.scString() + "\r\n";
        ret += tab + declarative_part.scString() + "\r\n";
        ret += tab + statement_part.scString() + "\r\n";
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> block_statement_part ::=
 *   <dd> { architecture_statement }
 */
class ScBlock_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScBlock_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBLOCK_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScArchitecture_statement(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScBranch_quantity_declaration extends ScVhdl {
    ScVhdl across = null;
    ScVhdl through = null;
    ScVhdl terminal = null;
    public ScBranch_quantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBRANCH_QUANTITY_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTACROSS_ASPECT:
                across = new ScAcross_aspect(c);
                break;
            case ASTTHROUGH_ASPECT:
                through = new ScThrough_aspect(c);
                break;
            case ASTTERMINAL_ASPECT:
                terminal = new ScTerminal_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error();
        return "";
    }
}

/**
 * <dl> break_element ::=
 *   <dd> [ break_selector_clause ] <i>quantity_</i>name => expression
 */
class ScBreak_element extends ScVhdl {
    ScVhdl selector = null;
    ScVhdl name = null;
    ScVhdl expression = null;
    public ScBreak_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_ELEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTBREAK_SELECTOR_CLAUSE:
                selector = new ScBreak_selector_clause(c);
                break;
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        error();
        return "";
    }
}

/**
 * <dl> break_list ::=
 *   <dd> break_element { , break_element }
 */
class ScBreak_list extends ScVhdl {
    ArrayList<ScVhdl> elements = new ArrayList<ScVhdl>();
    public ScBreak_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTBREAK_ELEMENT);
            ScVhdl ele = new ScBreak_element(c);
            elements.add(ele);
        }
    }

    public String scString() {
        error();
        return "";
    }
}

/**
 * <dl> break_selector_clause ::=
 *   <dd> <b>for</b> <i>quantity_</i>name <b>use</b>
 */
class ScBreak_selector_clause extends ScVhdl {
    ScName name = null;
    public ScBreak_selector_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_SELECTOR_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            default:
                break;
            }
        }        
    }

    public String scString() {
        error();
        return "";
    }
}

/**
 * <dl> break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ <b>when</b> condition ] ;
 */
class ScBreak_statement extends ScVhdl {
    ScBreak_list break_list = null;
    ScCondition condition = null;
    public ScBreak_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTBREAK_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            case ASTBREAK_LIST:
                break_list = new ScBreak_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
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
class ScCase_statement extends ScVhdl {
    ScVhdl expression = null;
    ArrayList<ScVhdl> statement_alt = new ArrayList<ScVhdl>();
    public ScCase_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCASE_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            case ASTCASE_STATEMENT_ALTERNATIVE:
                newNode = new ScCase_statement_alternative(c);
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
            ScCase_statement_alternative alt = 
                (ScCase_statement_alternative)statement_alt.get(i);
            ScChoices choices = alt.getChoices();
            if(choices.hasRange()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public String scString() {
        String ret = "";
        String val = expression.scString();
        if(hasRange()) {
            for(int i = 0; i < statement_alt.size(); i++) {
                ScCase_statement_alternative alt = 
                    (ScCase_statement_alternative)statement_alt.get(i);
                ScChoices choices = alt.getChoices();
                ArrayList<ScChoice> items = choices.getItems();
                String tmp = "";
                boolean isElse = false;
                for(int j = 0; j < items.size(); j++) {
                    ScChoice choice = items.get(j);
                    if(choice.isRange()) {
                        ScDiscrete_range range = (ScDiscrete_range)choice.item;
                        tmp += "(" + val + " >= " + range.getMin() + " && ";
                        tmp += val + " <= " + range.getMax() + ")";
                    }else if(choice.isOthers()) {
                        isElse = true;
                        break;
                    }else {
                        tmp += val + " == " + choice.scString();
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
                ret += intent(level+1) + alt.seq_statements.scString() + "\r\n";
                ret += "}\r\n";
            }
        }else {
            ret += intent() + "switch(" + expression.scString() + ")\r\n";
            ret += intent() + "{\r\n";
            for(int i = 0; i < statement_alt.size(); i++) {
                ret += statement_alt.get(i).scString() + "\r\n";
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
class ScCase_statement_alternative extends ScVhdl {
    ScChoices choices = null;
    ScVhdl seq_statements = null;
    public ScCase_statement_alternative(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCASE_STATEMENT_ALTERNATIVE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCHOICES:
                choices = new ScChoices(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new ScSequence_of_statements(c);
                break;
            default:
                break;
            }
        }
    }
    
    public ScChoices getChoices() {
        return choices;
    }
    
    public String statementsString() {
        return seq_statements.scString();
    }

    public String scString() {
        String ret = "";
        ArrayList<ScChoice> items = choices.getItems();
        for(int i = 0; i < items.size(); i++) {
            ScChoice item = items.get(i);
            if(item.isOthers()){
                ret += intent() + "default:\r\n";
            }else {
                ret += intent() + "case " + item.scString();
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
class ScCharacter_literal extends ScVhdl {
    String str = "";
    public ScCharacter_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCHARACTER_LITERAL);
        str = node.firstTokenImage();
    }

    public String scString() {
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
class ScChoice extends ScVhdl {
    ScVhdl item = null;
    public ScChoice(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHOICE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSIMPLE_EXPRESSION:
            item = new ScSimple_expression(c);
            break;
        case ASTDISCRETE_RANGE:
            item = new ScDiscrete_range(c);
            break;
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTVOID:
            item = new ScToken(c);
            break;
        default:
            break;
        }
    }
    
    public boolean isRange() {
        return (item instanceof ScDiscrete_range);
    }
    
    public boolean isOthers() {
        return (item instanceof ScToken);
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> choices ::=
 *   <dd> choice { | choice }
 */
class ScChoices extends ScVhdl {
    ArrayList<ScChoice> items = new ArrayList<ScChoice>();
    public ScChoices(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCHOICES);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ScChoice item = new ScChoice((ASTNode)node.getChild(i));
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
    
    ArrayList<ScChoice> getItems() {
        return items;
    }

    public String scString() {
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
class ScComponent_configuration extends ScVhdl {
    ScVhdl spec = null;
    ScVhdl binding = null;
    ScVhdl block = null;
    public ScComponent_configuration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_CONFIGURATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCOMPONENT_SPECIFICATION:
                spec = new ScComponent_specification(c);
                break;
            case ASTBINDING_INDICATION:
                binding = new ScBinding_indication(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                block = new ScBlock_configuration(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
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
class ScComponent_declaration extends ScVhdl {
    public ScComponent_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_DECLARATION);
    }

    public String scString() {
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
class ScComponent_instantiation_statement extends ScVhdl {
    public ScComponent_instantiation_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_INSTANTIATION_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> component_specification ::=
 *   <dd> instantiation_list : <i>component_</i>name
 */
class ScComponent_specification extends ScVhdl {
    public ScComponent_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> composite_nature_definition ::=
 *   <dd> array_nature_definition
 *   <br> | record_nature_definition
 */
class ScComposite_nature_definition extends ScVhdl {
    ScVhdl item = null;
    public ScComposite_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPOSITE_NATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTARRAY_NATURE_DEFINITION:
                item = new ScArray_nature_definition(c);
                break;
            case ASTRECORD_NATURE_DEFINITION:
                item = new ScRecord_nature_definition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> composite_type_definition ::=
 *   <dd> array_type_definition
 *   <br> | record_type_definition
 */
class ScComposite_type_definition extends ScVhdl {
    ScVhdl item = null;
    public ScComposite_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPOSITE_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTARRAY_TYPE_DEFINITION:
            item = new ScArray_type_definition(c);
            break;
        case ASTRECORD_TYPE_DEFINITION:
            item = new ScRecord_type_definition(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> concurrent_assertion_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] assertion ;
 */
class ScConcurrent_assertion_statement extends ScVhdl {
    public ScConcurrent_assertion_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_ASSERTION_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> concurrent_break_statement ::=
 *   <dd> [ label : ] <b>break</b> [ break_list ] [ sensitivity_clause ] [ <b>when</b> condition ] ;
 */
class ScConcurrent_break_statement extends ScVhdl {
    public ScConcurrent_break_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_BREAK_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> concurrent_procedure_call_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] procedure_call ;
 */
class ScConcurrent_procedure_call_statement extends ScVhdl {
    public ScConcurrent_procedure_call_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_PROCEDURE_CALL_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> concurrent_signal_assignment_statement ::=
 *   <dd> [ label : ] [ <b>postponed</b> ] conditional_signal_assignment
 *   <br> | [ label : ] [ <b>postponed</b> ] selected_signal_assignment
 */
class ScConcurrent_signal_assignment_statement extends ScVhdl {
    public ScConcurrent_signal_assignment_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_SIGNAL_ASSIGNMENT_STATEMENT);
    }

    public String scString() {
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
class ScConcurrent_statement extends ScVhdl {
    public ScConcurrent_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONCURRENT_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> condition ::=
 *   <dd> <i>boolean_</i>expression
 */
class ScCondition extends ScVhdl {
    ScVhdl expression = null;
    public ScCondition(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONDITION);
        expression = new ScExpression(node);
    }

    public String scString() {
        return expression.scString();
    }
}

/**
 * <dl> condition_clause ::=
 *   <dd> <b>until</b> condition
 */
class ScCondition_clause extends ScVhdl {
    public ScCondition_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITION_CLAUSE);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> conditional_signal_assignment ::=
 *   <dd> target <= options conditional_waveforms ;
 */
class ScConditional_signal_assignment extends ScVhdl {
    public ScConditional_signal_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITIONAL_SIGNAL_ASSIGNMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> conditional_waveforms ::=
 *   <dd> { waveform <b>when</b> condition <b>else</b> }
 *   <br> waveform [ <b>when</b> condition ]
 */
class ScConditional_waveforms extends ScVhdl {
    public ScConditional_waveforms(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONDITIONAL_WAVEFORMS);
    }

    public String scString() {
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
class ScConfiguration_declaration extends ScVhdl {
    public ScConfiguration_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> configuration_declarative_item ::=
 *   <dd> use_clause
 *   <br> | attribute_specification
 *   <br> | group_declaration
 */
class ScConfiguration_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScConfiguration_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONFIGURATION_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> configuration_declarative_part ::=
 *   <dd> { configuration_declarative_item }
 */
class ScConfiguration_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScConfiguration_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScConfiguration_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScConfiguration_item extends ScVhdl {
    public ScConfiguration_item(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_ITEM);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> configuration_specification ::=
 *   <dd> <b>for</b> component_specification binding_indication ;
 */
class ScConfiguration_specification extends ScVhdl {
    public ScConfiguration_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> constant_declaration ::=
 *   <dd> <b>constant</b> identifier_list : subtype_indication [ := expression ] ;
 */
class ScConstant_declaration extends ScVhdl {
    public ScConstant_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTANT_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> constrained_array_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> <i>element_</i>subtype_indication
 */
class ScConstrained_array_definition extends ScVhdl {
    public ScConstrained_array_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINED_ARRAY_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> constrained_nature_definition ::=
 *   <dd> <b>array</b> index_constraint <b>of</b> subnature_indication
 */
class ScConstrained_nature_definition extends ScVhdl {
    public ScConstrained_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINED_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> constraint ::=
 *   <dd> range_constraint
 *   <br> | index_constraint
 */
class ScConstraint extends ScVhdl {
    ScRange_constraint range = null;
    ScIndex_constraint index = null;
    public ScConstraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new ScRange_constraint(c);
            break;
        case ASTINDEX_CONSTRAINT:
            index = new ScIndex_constraint(c);
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> context_clause ::=
 *   <dd> { context_item }
 */
class ScContext_clause extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScContext_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONTEXT_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScContext_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScContext_item extends ScVhdl {
    ScVhdl item = null;
    public ScContext_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTCONTEXT_ITEM);
        switch(node.getId())
        {
        case ASTLIBRARY_CLAUSE:
            item = new ScLibrary_clause(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        String ret = "";
        ret += item.scString();
        return ret;
    }
}

/**
 * <dl> decimal_literal ::=
 *   <dd> integer [ . integer ] [ exponent ]
 */
class ScDecimal_literal extends ScVhdl {
    String literal = "";
    public ScDecimal_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTDECIMAL_LITERAL);
        literal = node.firstTokenImage();
    }

    public String scString() {
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
class ScDeclaration extends ScVhdl {
    public ScDeclaration(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTDECLARATION);
        // no use by others module
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> delay_mechanism ::=
 *   <dd> <b>transport</b>
 *   <br> | [ <b>reject</b> <i>time_</i>expression ] <b>inertial</b>
 */
class ScDelay_mechanism extends ScVhdl {
    public ScDelay_mechanism(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDELAY_MECHANISM);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> design_file ::=
 *   <dd> design_unit { design_unit }
 */
class ScDesign_file extends ScVhdl {
    ArrayList<ScVhdl> units = new ArrayList<ScVhdl>(); 
    public ScDesign_file(IParser parser) {
        super(parser);
        assert(curNode.getId() == ASTDESIGN_FILE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            assert(c.getId() == ASTDESIGN_UNIT);
            ScVhdl unit = new ScDesign_unit(c);
            units.add(unit);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < units.size(); i++) {
            ret += units.get(i).scString();
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
class ScDesign_unit extends ScVhdl {
    ScVhdl context_clause = null;
    ScVhdl library_unit = null;
    public ScDesign_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESIGN_UNIT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTCONTEXT_CLAUSE:
                context_clause = new ScContext_clause(c);
                break;
            case ASTLIBRARY_UNIT:
                library_unit = new ScLibrary_unit(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += context_clause.scString();
        ret += "\r\n";
        ret += library_unit.scString();
        return ret;
    }
}

/**
 * <dl> designator ::=
 *   <dd> identifier | operator_symbol
 */
class ScDesignator extends ScVhdl {
    public ScDesignator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDESIGNATOR);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> direction ::=
 *   <dd> <b>to</b> | <b>downto</b>
 */
class ScDirection extends ScVhdl {
    String dir = "to";
    public ScDirection(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDIRECTION);
        dir = node.firstTokenImage();
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> disconnection_specification ::=
 *   <dd> <b>disconnect</b> guarded_signal_specification <b>after</b> <i>time_</i>expression ;
 */
class ScDisconnection_specification extends ScVhdl {
    public ScDisconnection_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDISCONNECTION_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> discrete_range ::=
 *   <dd> <i>discrete_</i>subtype_indication | range
 */
class ScDiscrete_range extends ScVhdl {
    ScRange range = null;
    ScSubtype_indication subtype = null;
    public ScDiscrete_range(ASTNode node) {
        super(node);
        assert(node.getId() == ASTDISCRETE_RANGE);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE:
            range = new ScRange(c);
            break;
        case ASTSUBTYPE_INDICATION:
            subtype = new ScSubtype_indication(c);
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> element_association ::=
 *   <dd> [ choices => ] expression
 */
class ScElement_association extends ScVhdl {
    public ScElement_association(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_ASSOCIATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> element_declaration ::=
 *   <dd> identifier_list : element_subtype_definition ;
 */
class ScElement_declaration extends ScVhdl {
    public ScElement_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> element_subnature_definition ::=
 *   <dd> subnature_indication
 */
class ScElement_subnature_definition extends ScVhdl {
    ScSubnature_indication item = null;
    public ScElement_subnature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_SUBNATURE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBNATURE_INDICATION:
                item = new ScSubnature_indication(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> element_subtype_definition ::=
 *   <dd> subtype_indication
 */
class ScElement_subtype_definition extends ScVhdl {
    ScSubtype_indication item = null;
    public ScElement_subtype_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTELEMENT_SUBTYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBNATURE_INDICATION:
                item = new ScSubtype_indication(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> entity_aspect ::=
 *   <dd> <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 *   <br> | <b>open</b>
 */
class ScEntity_aspect extends ScVhdl {
    public ScEntity_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_ASPECT);
    }

    public String scString() {
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
class ScEntity_class extends ScVhdl {
    String image = "";
    public ScEntity_class(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTENTITY_CLASS);
        image = node.firstTokenImage();
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> entity_class_entry ::=
 *   <dd> entity_class [ <> ]
 */
class ScEntity_class_entry extends ScVhdl {
    public ScEntity_class_entry(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> entity_class_entry_list ::=
 *   <dd> entity_class_entry { , entity_class_entry }
 */
class ScEntity_class_entry_list extends ScVhdl {
    public ScEntity_class_entry_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_CLASS_ENTRY_LIST);
    }

    public String scString() {
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
class ScEntity_declaration extends ScVhdl {
    SCVhdlArchitecture_body body = null;
    ScVhdl identifier = null;
    ScVhdl header = null;
    ScVhdl declarative_part = null;
    ScVhdl statement_part = null;
    public ScEntity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DECLARATION);
        level = 0;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTENTITY_HEADER:
                header = new ScEntity_header(c);
                break;
            case ASTENTITY_DECLARATIVE_PART:
                declarative_part = new ScEntity_declarative_part(c);
                break;
            case ASTENTITY_STATEMENT_PART:
                statement_part = new ScEntity_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(body == null) {
            return "";  //TODO no entity body, ignore
        }
        ret += "SC_MODULE(" + getName() + ")\r\n{\r\n";
        ret += header.scString() + "\r\n";
        ret += declarative_part.scString() + "\r\n";
        if(statement_part != null) {
            ret += statement_part.scString();
        }
        ret += "}\r\n";
        return ret;
    }
    
    public void setArchitectureBody(SCVhdlArchitecture_body body) {
        this.body = body;
    }
    
    public String getName() {
        return identifier.scString();
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
class ScEntity_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScEntity_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTENTITY_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new ScSubprogram_declaration(node);
            break;
        case ASTSUBPROGRAM_BODY:
            item = new ScSubprogram_body(node);
            break;
        case ASTTYPE_DECLARATION:
            item = new ScType_declaration(node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new ScSubtype_declaration(node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new ScConstant_declaration(node);
            break;
        case ASTSIGNAL_DECLARATION:
            item = new ScSignal_declaration(node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVariable_declaration(node);
            break;
        case ASTFILE_DECLARATION:
            item = new ScFile_declaration(node);
            break;
        case ASTALIAS_DECLARATION:
            item = new ScAlias_declaration(node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new ScAttribute_declaration(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            item = new ScDisconnection_specification(node);
            break;
        case ASTSTEP_LIMIT_SPECIFICATION:
            item = new ScStep_limit_specification(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new ScGroup_template_declaration(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        case ASTNATURE_DECLARATION:
            item = new ScNature_declaration(node);
            break;
        case ASTSUBNATURE_DECLARATION:
            item = new ScSubnature_declaration(node);
            break;
        case ASTQUANTITY_DECLARATION:
            item = new ScQuantity_declaration(node);
            break;
        case ASTTERMINAL_DECLARATION:
            item = new ScTerminal_declaration(node);
            break;

        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> entity_declarative_part ::=
 *   <dd> { entity_declarative_item }
 */
class ScEntity_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEntity_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScEntity_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScEntity_designator extends ScVhdl {
    public ScEntity_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_DESIGNATOR);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> entity_header ::=
 *   <dd> [ <i>formal_</i>generic_clause ]
 *   <br> [ <i>formal_</i>port_clause ]
 */
class ScEntity_header extends ScVhdl {
    ScVhdl generic = null;
    ScVhdl port = null;
    public ScEntity_header(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_HEADER);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTGENERIC_CLAUSE:
                generic = new ScGeneric_clause(c);
                break;
            case ASTPORT_CLAUSE:
                port = new ScPort_clause(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(generic != null) {
            ret += generic.scString();
        }
        if(!ret.isEmpty()) {
            ret += "\r\n";
        }
        if(port != null) {
            ret += port.scString();
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
class ScEntity_name_list extends ScVhdl {
    public ScEntity_name_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_NAME_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> entity_specification ::=
 *   <dd> entity_name_list : entity_class
 */
class ScEntity_specification extends ScVhdl {
    public ScEntity_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> entity_statement ::=
 *   <dd> concurrent_assertion_statement
 *   <br> | <i>passive_</i>concurrent_procedure_call_statement
 *   <br> | <i>passive_</i>process_statement
 */
class ScEntity_statement extends ScVhdl {
    ScVhdl item = null;
    public ScEntity_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTENTITY_STATEMENT);
        switch(node.getId())
        {
        case ASTCONCURRENT_ASSERTION_STATEMENT:
            item = new ScConcurrent_assertion_statement(node);
            break;
        case ASTCONCURRENT_PROCEDURE_CALL_STATEMENT:
            item = new ScConcurrent_procedure_call_statement(node);
            break;
        case ASTPROCESS_STATEMENT:
            item = new ScProcess_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> entity_statement_part ::=
 *   <dd> { entity_statement }
 */
class ScEntity_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEntity_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScEntity_statement(c);
            items.add(item);
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
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
class ScEntity_tag extends ScVhdl {
    public ScEntity_tag(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENTITY_TAG);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> enumeration_literal ::=
 *   <dd> identifier | character_literal
 */
class ScEnumeration_literal extends ScVhdl {
    ScVhdl item = null;
    public ScEnumeration_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENUMERATION_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                item = new ScIdentifier(c);
                break;
            case ASTVOID:
                item = new ScCharacter_literal(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> enumeration_type_definition ::=
 *   <dd> ( enumeration_literal { , enumeration_literal } )
 */
class ScEnumeration_type_definition extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScEnumeration_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTENUMERATION_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                newNode = new ScEnumeration_literal(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
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
class ScExit_statement extends ScVhdl {
    ScCondition condition = null;
    public ScExit_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "if(" + condition.scString() + ")\r\n";
            ret += intent(level+1) + "break;";  //FIXME: may break in switch
        }else {
            ret += intent() + "break;";
        }
        if(curNode.getParent().getId() == ASTCASE_STATEMENT_ALTERNATIVE) {
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
class ScExpression extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScExpression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTEXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTRELATION:
                newNode = new ScRelation(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScToken(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += items.get(0).scString();
        for(int i = 1; i < items.size() - 1; i += 2){
            ret += " " + getReplaceOperator(items.get(i).scString()) + " ";
            ret += items.get(i+1).scString();
        }
        return ret;
    }
}

/**
 * <dl> extended_digit ::=
 *   <dd> digit | letter
 */
//class ScExtended_digit extends SCVhdl {
//    String image = 
//    public ScExtended_digit(ASTNode node) {
//        super(node);
//        //assert(node.getId() == ASTEXTENDED_DIGIT);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> extended_identifier ::=
 *   <dd> \ graphic_character { graphic_character } \
 */
//class ScExtended_identifier extends SCVhdl {
//    public ScExtended_identifier(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTEXTENDED_IDENTIFIER);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> factor ::=
 *   <dd> primary [ ** primary ]
 *   <br> | <b>abs</b> primary
 *   <br> | <b>not</b> primary
 */
class ScFactor extends ScVhdl {
    ScPrimary primary0 = null;
    ScVhdl operator = null;
    ScPrimary primary1 = null;
    public ScFactor(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFACTOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScPrimary newNode = null;
            switch(c.getId())
            {
            case ASTPRIMARY:
                newNode = new ScPrimary(c);
                if(primary0 == null) {
                    primary0 = newNode;
                }else {
                    primary1 = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScMiscellaneous_operator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(operator != null
            && operator.scString().equalsIgnoreCase(vhdlOperators[VHDL_ABS])) {
            String tmp = getReplaceOperator(operator.scString());
            ret += tmp + "(" + primary0.scString() + ")";
        }else if(operator != null
            && operator.scString().equalsIgnoreCase(vhdlOperators[VHDL_NOT])) {
            String tmp = getReplaceOperator(operator.scString());
            ret += tmp + primary0.scString();
        }else {
            if(primary1 == null) {
                ret += primary0.scString();
            }else {
                ret += "(";
                ret += primary0.scString();
                ret += getReplaceOperator(operator.scString());
                ret += primary1.scString();
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
class ScFile_declaration extends ScVhdl {
    public ScFile_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> file_logical_name ::=
 *   <dd> <i>string_</i>expression
 */
class ScFile_logical_name extends ScVhdl {
    public ScFile_logical_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_LOGICAL_NAME);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> file_open_information ::=
 *   <dd> [ <b>open</b> <i>file_open_kind_</i>expression ] <b>is</b> file_logical_name
 */
class ScFile_open_information extends ScVhdl {
    public ScFile_open_information(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_OPEN_INFORMATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> file_type_definition ::=
 *   <dd> <b>file of</b> type_mark
 */
class ScFile_type_definition extends ScVhdl {
    public ScFile_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFILE_TYPE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> floating_type_definition ::=
 *   <dd> range_constraint
 */
//class ScFloating_type_definition extends SCVhdl {
//    public ScFloating_type_definition(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTFLOATING_TYPE_DEFINITION);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> formal_designator ::=
 *   <dd> <i>generic_</i>name
 *   <br> | <i>port_</i>name
 *   <br> | <i>parameter_</i>name
 */
class ScFormal_designator extends ScVhdl {
    ScName name = null;
    public ScFormal_designator(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_DESIGNATOR);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return name.scString();
    }
}

/**
 * <dl> formal_parameter_list ::=
 *   <dd> <i>parameter_</i>interface_list
 */
class ScFormal_parameter_list extends ScVhdl {
    public ScFormal_parameter_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_PARAMETER_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> formal_part ::=
 *   <dd> formal_designator
 *   <br> | <i>function_</i>name ( formal_designator )
 *   <br> | type_mark  ( formal_designator )
 */
class ScFormal_part extends ScVhdl {
    public ScFormal_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFORMAL_PART);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> free_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication [ := expression ] ;
 */
class ScFree_quantity_declaration extends ScVhdl {
    public ScFree_quantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFREE_QUANTITY_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> full_type_declaration ::=
 *   <dd> <b>type</b> identifier <b>is</b> type_definition ;
 */
class ScFull_type_declaration extends ScVhdl {
    ScVhdl identifier = null;
    ScVhdl type_def = null;
    public ScFull_type_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFULL_TYPE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTTYPE_DEFINITION:
                type_def = new ScType_definition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += "typedef ";
        ret += type_def.scString() + " ";
        ret += identifier.scString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> function_call ::=
 *   <dd> <i>function_</i>name [ ( actual_parameter_part ) ]
 */
class ScFunction_call extends ScVhdl {
    public ScFunction_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTFUNCTION_CALL);
    }

    public String scString() {
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
class ScGenerate_statement extends ScVhdl {
    public ScGenerate_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERATE_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> generation_scheme ::=
 *   <dd> <b>for</b> <i>generate_</i>parameter_specification
 *   <br> | <b>if</b> condition
 */
class ScGeneration_scheme extends ScVhdl {
    public ScGeneration_scheme(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERATION_SCHEME);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> generic_clause ::=
 *   <dd> <b>generic</b> ( generic_list ) ;
 */
class ScGeneric_clause extends ScVhdl {
    public ScGeneric_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERIC_CLAUSE);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> generic_list ::=
 *   <dd> <i>generic_</i>interface_list
 */
class ScGeneric_list extends ScVhdl {
    ScVhdl list = null;
    public ScGeneric_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERIC_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINTERFACE_LIST:
                list = new ScInterface_list(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return list.scString();
    }
}

/**
 * <dl> generic_map_aspect ::=
 *   <dd> <b>generic</b> <b>map</b> ( <i>generic_</i>association_list )
 */
class ScGeneric_map_aspect extends ScVhdl {
    public ScGeneric_map_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERIC_MAP_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> graphic_character ::=
 *   <dd> basic_graphic_character | lower_case_letter | other_special_character
 */
//class ScGraphic_character extends SCVhdl {
//    public ScGraphic_character(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTGRAPHIC_CHARACTER);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> group_constituent ::=
 *   <dd> name | character_literal
 */
class ScGroup_constituent extends ScVhdl {
    public ScGroup_constituent(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_CONSTITUENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> group_constituent_list ::=
 *   <dd> group_constituent { , group_constituent }
 */
class ScGroup_constituent_list extends ScVhdl {
    public ScGroup_constituent_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_CONSTITUENT_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> group_declaration ::=
 *   <dd> <b>group</b> identifier : <i>group_template_</i>name ( group_constituent_list ) ;
 */
class ScGroup_declaration extends ScVhdl {
    public ScGroup_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> group_template_declaration ::=
 *   <dd> <b>group</b> identifier <b>is</b> ( entity_class_entry_list ) ;
 */
class ScGroup_template_declaration extends ScVhdl {
    public ScGroup_template_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGROUP_TEMPLATE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> guarded_signal_specification ::=
 *   <dd> <i>guarded_</i>signal_list : type_mark
 */
class ScGuarded_signal_specification extends ScVhdl {
    public ScGuarded_signal_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGUARDED_SIGNAL_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> identifier ::=
 *   <dd> basic_identifier | extended_identifier
 */
class ScIdentifier extends ScVhdl {
    String str = "";
    public ScIdentifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER);
        ASTNode c = (ASTNode)node.getChild(0);
        str = c.firstTokenImage();
    }

    public String scString() {
        return str;
    }
}

/**
 * <dl> identifier_list ::=
 *   <dd> identifier { , identifier }
 */
class ScIdentifier_list extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScIdentifier_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            assert(c.getId() == ASTIDENTIFIER);
            ScVhdl item = new ScIdentifier(c);
            items.add(item);
        }
    }
    
    public ArrayList<ScVhdl> getItems() {
        return items;
    }

    public String scString() {
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
class ScIf_statement extends ScVhdl {
    class ConPair {
        ScVhdl condition = null;
        ScVhdl seq_statements = null;
    }
    ConPair if_pair = new ConPair();
    ArrayList<ConPair> elsif_pair = new ArrayList<ConPair>();
    ConPair else_pair = null;
    public ScIf_statement(ASTNode node) {
        super(node);
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
                    if_pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    if_pair.seq_statements = new ScSequence_of_statements(c);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSIF])) {
                    ConPair pair = new ConPair();
                    pair.condition = new ScCondition(c);
                    c = (ASTNode)node.getChild(i+2);
                    pair.seq_statements = new ScSequence_of_statements(c);
                    elsif_pair.add(pair);
                    i += 3;
                }else if(image.equalsIgnoreCase(tokenImage[ELSE])) {
                    else_pair = new ConPair();
                    else_pair.seq_statements = new ScSequence_of_statements(c);
                    i += 2;
                }
                break;

            default:
                i ++;
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "if(" + if_pair.condition.scString() + ")\r\n";
        ret += intent() + "{\r\n";
        ret += if_pair.seq_statements.scString() + "\r\n";
        if(elsif_pair.size() > 0) {
            ret += intent() + "}\r\n";
            for(int i = 0; i < elsif_pair.size(); i++) {
                ConPair pair = elsif_pair.get(i);
                ret += intent() + "else if(" + pair.condition.scString() + ")\r\n";
                ret += intent() + "{\r\n";
                ret += pair.seq_statements.scString() + "\r\n";
            }
        }
        
        if(else_pair != null) {
            ret += intent() + "}\r\n";
            ret += intent() + "else\r\n";
            ret += intent() + "{\r\n";
            ret += else_pair.seq_statements.scString() + "\r\n";
        }
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> incomplete_type_declaration ::=
 *   <dd> <b>type</b> identifier ;
 */
class ScIncomplete_type_declaration extends ScVhdl {
    public ScIncomplete_type_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINCOMPLETE_TYPE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> index_constraint ::=
 *   <dd> ( discrete_range { , discrete_range } )
 */
class ScIndex_constraint extends ScVhdl {
    ArrayList<ScVhdl> ranges = new ArrayList<ScVhdl>();
    public ScIndex_constraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_CONSTRAINT);
    }

    public String getMin() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).getMin();
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).getMax();
        }
        return ret;
    }
    
    public boolean isDownto() {
        boolean ret = true;
        if(ranges.size() > 0) {
            ret = ((ScDiscrete_range)ranges.get(0)).isDownto();
        }
        return ret;
    }
    
    public String scString() {
        String ret = "";
        return ret;
    }
}

/**
 * <dl> index_specification ::=
 *   <dd> discrete_range
 *   <br> | <i>static_</i>expression
 */
class ScIndex_specification extends ScVhdl {
    public ScIndex_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> index_subtype_definition ::=
 *   <dd> type_mark <b>range</b> <>
 */
class ScIndex_subtype_definition extends ScVhdl {
    public ScIndex_subtype_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEX_SUBTYPE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> indexed_name ::=
 *   <dd> prefix ( expression { , expression } )
 */
class ScIndexed_name extends ScVhdl {
    public ScIndexed_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINDEXED_NAME);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> instantiated_unit ::=
 *   <dd> [ <b>component</b> ] <i>component_</i>name
 *   <br> | <b>entity</b> <i>entity_</i>name [ ( <i>architecture_</i>identifier ) ]
 *   <br> | <b>configuration</b> <i>configuration_</i>name
 */
class ScInstantiated_unit extends ScVhdl {
    public ScInstantiated_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINSTANTIATED_UNIT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> instantiation_list ::=
 *   <dd> <i>instantiation_</i>label { , <i>instantiation_</i>label }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScInstantiation_list extends ScVhdl {
    public ScInstantiation_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINSTANTIATION_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> integer_type_definition ::=
 *   <dd> range_constraint
 */
class ScInteger_type_definition extends ScVhdl {
    ScRange_constraint range = null;
    public ScInteger_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTEGER_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTRANGE_CONSTRAINT:
            range = new ScRange_constraint(c);
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> interface_constant_declaration ::=
 *   <dd> [ <b>constant</b> ] identifier_list : [ <b>in</b> ] 
 *          subtype_indication [ := <i>static_</i>expression ]
 */
class ScInterface_constant_declaration extends ScVhdl {
    ScIdentifier_list idList = null;
    boolean isIn = false;
    ScSubtype_indication subtype = null;
    ScExpression expression = null;
    public ScInterface_constant_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_CONSTANT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode child = (ASTNode)node.getChild(i);
            switch(child.getId())
            {
            case ASTIDENTIFIER_LIST:
                idList = new ScIdentifier_list(child);
                break;
                
            case ASTSUBTYPE_INDICATION:
                subtype = new ScSubtype_indication(child);
                break;
                
            case ASTEXPRESSION:
                expression = new ScExpression(child);
                break;
                
            case ASTVOID:
                isIn = true;
                break;
                
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "const";
        ret += " " + subtype.scString();
        if(isIn) {
            warning("token in ignored");
        }
        ret += " " + idList.scString();
        
        return ret;
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
class ScInterface_declaration extends ScVhdl {
    ScVhdl item = null;
    public ScInterface_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_CONSTANT_DECLARATION:
            item = new ScInterface_constant_declaration(c);
            break;
        case ASTINTERFACE_SIGNAL_DECLARATION:
            item = new ScInterface_signal_declaration(c);
            break;
        case ASTINTERFACE_VARIABLE_DECLARATION:
            item = new ScInterface_variable_declaration(c);
            break;
        case ASTINTERFACE_FILE_DECLARATION:
            item = new ScInterface_file_declaration(c);
            break;
        case ASTINTERFACE_TERMINAL_DECLARATION:
            item = new ScInterface_terminal_declaration(c);
            break;
        case ASTINTERFACE_QUANTITY_DECLARATION:
            item = new ScInterface_quantity_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> interface_element ::=
 *   <dd> interface_declaration
 */
class ScInterface_element extends ScVhdl {
    ScVhdl item = null;
    public ScInterface_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_ELEMENT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_DECLARATION:
            item = new ScInterface_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> interface_file_declaration ::=
 *   <dd> <b>file</b> identifier_list : subtype_indication
 */
class ScInterface_file_declaration extends ScVhdl {
    public ScInterface_file_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_FILE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> interface_list ::=
 *   <dd> interface_element { ; interface_element }
 */
class ScInterface_list extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScInterface_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode child = (ASTNode)node.getChild(i);
            ScVhdl item = null;
            switch(child.getId())
            {
            case ASTINTERFACE_ELEMENT:
                item = new ScInterface_element(child);
                items.add(item);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        for(int i = 0; i < items.size(); i++) {
            ret += items.get(i).scString();
            if(i < items.size() - 1) {
                ret += ";\r\n";
            }
        }
        return ret;
    }
}

/**
 * <dl> interface_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : [ <b>in</b> | <b>out</b> ] subtype_indication [ := <i>static_</i>expression ]
 */
class ScInterface_quantity_declaration extends ScVhdl {
    public ScInterface_quantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_QUANTITY_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> interface_signal_declaration ::=
 *   <dd> [ <b>signal</b> ] identifier_list : [ mode ] subtype_indication [ <b>bus</b> ] [ := <i>static_</i>expression ]
 */
class ScInterface_signal_declaration extends ScVhdl {
    public ScInterface_signal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_SIGNAL_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> interface_terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication
 */
class ScInterface_terminal_declaration extends ScVhdl {
    public ScInterface_terminal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_TERMINAL_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> interface_variable_declaration ::=
 *   <dd> [ <b>variable</b> ] identifier_list : [ mode ] subtype_indication [ := <i>static_</i>expression ]
 */
class ScInterface_variable_declaration extends ScVhdl {
    public ScInterface_variable_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTINTERFACE_VARIABLE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> iteration_scheme ::=
 *   <dd> <b>while</b> condition
 *   <br> | <b>for</b> <i>loop_</i>parameter_specification
 */
class ScIteration_scheme extends ScVhdl {
    ScCondition condition = null;
    ScParameter_specification param = null;
    public ScIteration_scheme(ASTNode node) {
        super(node);
        assert(node.getId() == ASTITERATION_SCHEME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            case ASTPARAMETER_SPECIFICATION:
                param = new ScParameter_specification(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += "while(" + condition.scString() + ")";
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
class ScLabel extends ScVhdl {
    ScVhdl identifier = null;
    public ScLabel(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTLABEL);
        identifier = new ScIdentifier(node);
    }

    public String scString() {
        return identifier.scString();
    }
}

/**
 * <dl> letter ::=
 *   <dd> upper_case_letter | lower_case_letter
 */
//class ScLetter extends SCVhdl {
//    public ScLetter(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTLETTER);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> letter_or_digit ::=
 *   <dd> letter | digit
 */
//class ScLetter_or_digit extends SCVhdl {
//    public ScLetter_or_digit(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTLETTER_OR_DIGIT);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> library_clause ::=
 *   <dd> <b>library</b> logical_name_list ;
 */
class ScLibrary_clause extends ScVhdl {
    ScLogical_name_list logical_name_list = null;
    public ScLibrary_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIBRARY_CLAUSE);
        logical_name_list = new ScLogical_name_list((ASTNode)node.getChild(0));
    }
    
    public ArrayList<ScVhdl> getNames() {
        return logical_name_list.getNames();
    } 

    public String scString() {
        return "";
        //return logical_name_list.scString() + ";";
    }
}

/**
 * <dl> library_unit ::=
 *   <dd> primary_unit
 *   <br> | secondary_unit
 */
class ScLibrary_unit extends ScVhdl {
    ScVhdl unit = null;
    public ScLibrary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLIBRARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTPRIMARY_UNIT:
            unit = new ScPrimary_unit(c);
            break;
        case ASTSECONDARY_UNIT:
            unit = new ScSecondary_unit(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return unit.scString();
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
class ScLiteral extends ScVhdl {
    ScVhdl item = null;
    public ScLiteral(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int kind  = 0;
            switch(c.getId())
            {
            case ASTENUMERATION_LITERAL:
                item = new ScEnumeration_literal(c);
                break;
            case ASTNUMERIC_LITERAL:
                item = new ScNumeric_literal(c);
                break;
            case ASTVOID:
                kind = c.getFirstToken().kind;
                switch(kind)
                {
                case NULL:
                    item = new ScToken(c);    // TODO convert null
                    break;
                case string_literal:
                    item = new ScString_literal(c);
                    break;
                case bit_string_literal:
                    item = new ScBit_string_literal(c);
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

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> logical_name ::=
 *   <dd> identifier
 */
class ScLogical_name extends ScVhdl {
    ScVhdl identifier = null;
    public ScLogical_name(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTLOGICAL_NAME);
        identifier = new ScIdentifier(node);
    }

    public String scString() {
        return identifier.scString();
    }
}

/**
 * <dl> logical_name_list ::=
 *   <dd> logical_name { , logical_name }
 */
class ScLogical_name_list extends ScVhdl {
    ArrayList<ScVhdl> names = new ArrayList<ScVhdl>();
    public ScLogical_name_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLOGICAL_NAME_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                newNode = new ScLogical_name(c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<ScVhdl> getNames() {
        return names;
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> logical_operator ::=
 *   <dd> <b>and</b> | <b>or</b> | <b>nand</b> | <b>nor</b> | <b>xor</b> | <b>xnor</b>
 */
//class ScLogical_operator extends SCVhdl {
//    public ScLogical_operator(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTLOGICAL_OPERATOR);
//    }
//
//    public String scString() {
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
class ScLoop_statement extends ScVhdl {
    ScIteration_scheme iteration = null;
    ScVhdl seq_statements = null;
    public ScLoop_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTLOOP_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTITERATION_SCHEME:
                iteration = new ScIteration_scheme(c);
                break;
            case ASTSEQUENCE_OF_STATEMENTS:
                seq_statements = new ScSequence_of_statements(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + iteration.scString() + "\r\n";
        ret += intent() + "{\r\n";
        ret += seq_statements.scString();
        ret += intent() + "}";
        return ret;
    }
}

/**
 * <dl> miscellaneous_operator ::=
 *   <dd> ** | <b>abs</b> | <b>not</b>
 */
class ScMiscellaneous_operator extends ScVhdl {
    ScToken token = null;
    public ScMiscellaneous_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTMISCELLANEOUS_OPERATOR);
        token = new ScToken(node);
    }

    public String scString() {
        return token.scString();
    }
}

/**
 * <dl> mode ::=
 *   <dd> <b>in</b> | <b>out</b> | <b>inout</b> | <b>buffer</b> | <b>linkage</b>
 */
class ScMode extends ScVhdl {
    String token = "";
    public ScMode(ASTNode node) {
        super(node);
        assert(node.getId() == ASTMODE);
        token = node.firstTokenImage();
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> multiplying_operator ::=
 *   <dd> * | / | <b>mod</b> | <b>rem</b>
 */
class ScMultiplying_operator extends ScVhdl {
    String token = "";
    public ScMultiplying_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTMULTIPLYING_OPERATOR);
        token = node.firstTokenImage(); 
    }

    public String scString() {
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
class ScName extends ScVhdl {
    ScVhdl item = null;
    public ScName(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNAME);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new ScOperator_symbol(c);
            break;
        case ASTSELECTED_NAME:
            item = new ScSelected_name(c);
            break;
        case ASTINDEXED_NAME:
            item = new ScIndexed_name(c);
            break;
        case ASTSLICE_NAME:
            item = new ScSlice_name(c);
            break;
        case ASTATTRIBUTE_NAME:
            item = new ScAttribute_name(c);
            break;
        default:
            break;
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        if(item instanceof ScSelected_name) {
            segments.addAll(((ScSelected_name)item).getNameSegments());
        }else {
            segments.add(item.scString());
        }
        return segments;
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> nature_declaration ::=
 *   <dd> <b>nature</b> identifier <b>is</b> nature_definition ;
 */
class ScNature_declaration extends ScVhdl {
    public ScNature_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> nature_definition ::=
 *   <dd> scalar_nature_definition
 *   <br> | composite_nature_definition
 */
class ScNature_definition extends ScVhdl {
    ScVhdl item = null;
    public ScNature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_NATURE_DEFINITION:
            item = new ScScalar_nature_definition(c);
            break;
        case ASTCOMPOSITE_NATURE_DEFINITION:
            item = new ScComposite_nature_definition(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> nature_element_declaration ::=
 *   <dd> identifier_list : element_subnature_definition
 */
class ScNature_element_declaration extends ScVhdl {
    public ScNature_element_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_ELEMENT_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> nature_mark ::=
 *   <dd> <i>nature_</i>name | <i>subnature_</i>name
 */
class ScNature_mark extends ScVhdl {
    public ScNature_mark(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNATURE_MARK);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> next_statement ::=
 *   <dd> [ label : ] <b>next</b> [ <i>loop_</i>label ] [ <b>when</b> condition ] ;
 */
class ScNext_statement extends ScVhdl {
    ScCondition condition = null;
    public ScNext_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNEXT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                condition = new ScCondition(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "if(" + condition.scString() + ")\r\n";
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
class ScNull_statement extends ScVhdl {
    public ScNull_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNULL_STATEMENT);
    }

    public String scString() {
        warning("null statement ignore");
        return ";";
    }
}

/**
 * <dl> numeric_literal ::=
 *   <dd> abstract_literal
 *   <br> | physical_literal
 */
class ScNumeric_literal extends ScVhdl {
    ScVhdl literal = null;
    public ScNumeric_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTNUMERIC_LITERAL);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTABSTRACT_LITERAL:
                literal = new ScAbstract_literal(c);
                break;
            case ASTPHYSICAL_LITERAL:
                literal = new ScPhysical_literal(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return literal.scString();
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
class ScObject_declaration extends ScVhdl {
    public ScObject_declaration(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTOBJECT_DECLARATION);
        // no use
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> operator_symbol ::=
 *   <dd> string_literal
 */
class ScOperator_symbol extends ScVhdl {
    ScVhdl string_literal = null;
    public ScOperator_symbol(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOPERATOR_SYMBOL);
        ASTNode c = (ASTNode)node.getChild(0);
        string_literal = new ScString_literal(c);
    }

    public String scString() {
        return string_literal.scString();
    }
}

/**
 * <dl> options ::=
 *   <dd> [ <b>guarded</b> ] [ delay_mechanism ]
 */
class ScOptions extends ScVhdl {
    public ScOptions(ASTNode node) {
        super(node);
        assert(node.getId() == ASTOPTIONS);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> package_body ::=
 *   <dd> <b>package body</b> <i>package_</i>simple_name <b>is</b>
 *   <ul> package_body_declarative_part
 *   </ul> <b>end</b> [ <b>package body</b> ] [ <i>package_</i>simple_name ] ;
 */
class ScPackage_body extends ScVhdl {
    public ScPackage_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_BODY);
    }

    public String scString() {
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
class ScPackage_body_declarative_item extends ScVhdl {
    public ScPackage_body_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPACKAGE_BODY_DECLARATIVE_ITEM);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> package_body_declarative_part ::=
 *   <dd> { package_body_declarative_item }
 */
class ScPackage_body_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScPackage_body_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_BODY_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScPackage_body_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
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
class ScPackage_declaration extends ScVhdl {
    ScVhdl identifier = null;
    ScVhdl declarative_part = null;
    public ScPackage_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTPACKAGE_DECLARATIVE_PART:
                declarative_part = new ScPackage_declarative_part(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String getName() {
        return identifier.scString();
    }

    public String scString() {
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
class ScPackage_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScPackage_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPACKAGE_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new ScSubprogram_declaration(node);
            break;
        case ASTTYPE_DECLARATION:
            item = new ScType_declaration(node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new ScSubtype_declaration(node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new ScConstant_declaration(node);
            break;
        case ASTSIGNAL_DECLARATION:
            item = new ScSignal_declaration(node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVariable_declaration(node);
            break;
        case ASTFILE_DECLARATION:
            item = new ScFile_declaration(node);
            break;
        case ASTALIAS_DECLARATION:
            item = new ScAlias_declaration(node);
            break;
        case ASTCOMPONENT_DECLARATION:
            item = new ScComponent_declaration(node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new ScAttribute_declaration(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTDISCONNECTION_SPECIFICATION:
            item = new ScDisconnection_specification(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new ScGroup_template_declaration(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        case ASTNATURE_DECLARATION:
            item = new ScNature_declaration(node);
            break;
        case ASTSUBNATURE_DECLARATION:
            item = new ScSubnature_declaration(node);
            break;
        case ASTTERMINAL_DECLARATION:
            item = new ScTerminal_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> package_declarative_part ::=
 *   <dd> { package_declarative_item }
 */
class ScPackage_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScPackage_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPACKAGE_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScPackage_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
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
class ScParameter_specification extends ScVhdl {
    ScVhdl identifier = null;
    ScDiscrete_range discrete_range = null;
    public ScParameter_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPARAMETER_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTDISCRETE_RANGE:
                discrete_range = new ScDiscrete_range(c);
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> physical_literal ::=
 *   <dd> [ abstract_literal ] <i>unit_</i>name
 */
class ScPhysical_literal extends ScVhdl {
    ScVhdl abstract_literal = null;
    String time_unit_name = "ns";
    public ScPhysical_literal(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPHYSICAL_LITERAL);
        abstract_literal = new ScAbstract_literal((ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }

    public String scString() {
        return abstract_literal.scString();
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
class ScPhysical_type_definition extends ScVhdl {
    ScRange_constraint range = null;
    ScVhdl primary = null;
    ArrayList<ScVhdl> secondaries = new ArrayList<ScVhdl>();
    public ScPhysical_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPHYSICAL_TYPE_DEFINITION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTRANGE_CONSTRAINT:
                range = new ScRange_constraint(c);
                break;
            case ASTPRIMARY_UNIT_DECLARATION:
                primary = new ScPrimary_unit_declaration(c);
                break;
            case ASTSECONDARY_UNIT_DECLARATION:
                newNode =  new ScSecondary_unit_declaration(c);
                secondaries.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        warning("physical type definition not support");
        return "";
    }
}

/**
 * <dl> port_clause ::=
 *   <dd> <b>port</b> ( port_list ) ;
 */
class ScPort_clause extends ScVhdl {
    public ScPort_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_CLAUSE);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> port_list ::=
 *   <dd> <i>port_</i>interface_list
 */
class ScPort_list extends ScVhdl {
    ScVhdl item = null;
    public ScPort_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_LIST);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTINTERFACE_LIST:
            item = new ScInterface_list(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> port_map_aspect ::=
 *   <dd> <b>port</b> <b>map</b> ( <i>port_</i>association_list )
 */
class ScPort_map_aspect extends ScVhdl {
    public ScPort_map_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_MAP_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> prefix ::=
 *   <dd> name
 *   <br> | function_call
 */
class ScPrefix extends ScVhdl {
    ScVhdl item = null;
    public ScPrefix(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPREFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new ScName(c);
            break;
        case ASTFUNCTION_CALL:
            item = new ScFunction_call(c);
            break;
        default:
            break;
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        if(item instanceof ScName) {
            segments.addAll(((ScName)item).getNameSegments());
        }else {
            segments.add(item.scString());
        }
        return segments;
    }

    public String scString() {
        return item.scString();
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
class ScPrimary extends ScVhdl {
    ScVhdl item = null;
    public ScPrimary(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                item = new ScName(c);
                break;
            case ASTLITERAL:
                item = new ScLiteral(c);
                break;
            case ASTAGGREGATE:
                item = new ScAggregate(c);
                break;
            case ASTQUALIFIED_EXPRESSION:
                item = new ScQualified_expression(c);
                break;
            case ASTALLOCATOR:
                item = new ScAllocator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> primary_unit ::=
 *   <dd> entity_declaration
 *   <br> | configuration_declaration
 *   <br> | package_declaration
 */
class ScPrimary_unit extends ScVhdl {
    ScVhdl declaration = null;
    public ScPrimary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY_UNIT);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENTITY_DECLARATION:
            declaration = new ScEntity_declaration(c);
            break;
        case ASTCONFIGURATION_DECLARATION:
            declaration = new ScConfiguration_declaration(c);
            break;
        case ASTPACKAGE_DECLARATION:
            declaration = new ScPackage_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return declaration.scString();
    }
}

/**
 * <dl> primary_unit_declaration ::=
 *   <dd> identifier ;
 */
class ScPrimary_unit_declaration extends ScVhdl {
    ScVhdl identifier = null;
    public ScPrimary_unit_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPRIMARY_UNIT_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTIDENTIFIER);
        identifier = new ScIdentifier(c);
    }

    public String scString() {
        String ret = "";
        ret += identifier.scString();
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
class ScProcedural_declarative_item extends ScVhdl {
    public ScProcedural_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_ITEM);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> procedural_declarative_part ::=
 *   <dd> { procedural_declarative_item }
 */
class ScProcedural_declarative_part extends ScVhdl {
    public ScProcedural_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURAL_DECLARATIVE_PART);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> procedural_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScProcedural_statement_part extends ScVhdl {
    public ScProcedural_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURAL_STATEMENT_PART);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> procedure_call ::=
 *   <dd> <i>procedure_</i>name [ ( actual_parameter_part ) ]
 */
class ScProcedure_call extends ScVhdl {
    public ScProcedure_call(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURE_CALL);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> procedure_call_statement ::=
 *   <dd> [ label : ] procedure_call ;
 */
class ScProcedure_call_statement extends ScVhdl {
    ScVhdl procedure_call = null;
    public ScProcedure_call_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCEDURE_CALL_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPROCEDURE_CALL:
                procedure_call = new ScProcedure_call(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += procedure_call.scString();
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
class ScProcess_declarative_item extends ScVhdl {
    public ScProcess_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTPROCESS_DECLARATIVE_ITEM);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> process_declarative_part ::=
 *   <dd> { process_declarative_item }
 */
class ScProcess_declarative_part extends ScVhdl {
    public ScProcess_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_DECLARATIVE_PART);
    }

    public String scString() {
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
class ScProcess_statement extends ScVhdl {
    public ScProcess_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> process_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScProcess_statement_part extends ScVhdl {
    public ScProcess_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPROCESS_STATEMENT_PART);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> qualified_expression ::=
 *   <dd> type_mark ' ( expression )
 *   <br> | type_mark ' aggregate
 */
class ScQualified_expression extends ScVhdl {
    ScVhdl type_mark = null;
    ScVhdl aggregate = null;
    public ScQualified_expression(ASTNode node) {
        super(node);
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> quantity_declaration ::=
 *   <dd> free_quantity_declaration
 *   <br> | branch_quantity_declaration
 *   <br> | source_quantity_declaration
 */
class ScQuantity_declaration extends ScVhdl {
    public ScQuantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> quantity_list ::=
 *   <dd> <i>quantity_</i>name { , <i>quantity_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScQuantity_list extends ScVhdl {
    public ScQuantity_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> quantity_specification ::=
 *   <dd> quantity_list : type_mark
 */
class ScQuantity_specification extends ScVhdl {
    public ScQuantity_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTQUANTITY_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> range ::=
 *   <dd> <i>range_</i>attribute_name
 *   <br> | simple_expression direction simple_expression
 */
class ScRange extends ScVhdl {
    ScAttribute_name attribute_name = null;
    ScVhdl simple_exp1 = null;
    ScDirection direction = null;
    ScVhdl simple_exp2 = null;
    public ScRange(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTATTRIBUTE_NAME:
                attribute_name = new ScAttribute_name(c);
                break;
            case ASTSIMPLE_EXPRESSION:
                newNode = new ScSimple_expression(c);
                if(simple_exp1 == null) {
                    simple_exp1 = newNode;
                }else {
                    simple_exp2 = newNode;
                }
                break;
            case ASTDIRECTION:
                direction = new ScDirection(c);
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
                ret = simple_exp1.scString();
            }else {
                ret = simple_exp2.scString();
            }
        }
        return ret;
    }
    
    public String getMax() {
        String ret = "0";
        if(attribute_name != null) {
            ret = attribute_name.designator.scString();
        }else {
            if(direction.dir.equalsIgnoreCase(RANGE_TO)) {
                ret = simple_exp2.scString();
            }else {
                ret = simple_exp1.scString();
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

    public String scString() {
        return "";
    }
}

/**
 * <dl> range_constraint ::=
 *   <dd> <b>range</b> range
 */
class ScRange_constraint extends ScVhdl {
    ScRange range = null;
    public ScRange_constraint(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRANGE_CONSTRAINT);
        ASTNode c = (ASTNode)node.getChild(0);
        assert(c.getId() == ASTRANGE);
        range = new ScRange(c);
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

    public String scString() {
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
class ScRecord_nature_definition extends ScVhdl {
    public ScRecord_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRECORD_NATURE_DEFINITION);
    }

    public String scString() {
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
class ScRecord_type_definition extends ScVhdl {
    ArrayList<ScVhdl> elements = new ArrayList<ScVhdl>();
    public ScRecord_type_definition(ASTNode node) {
        super(node);
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

    public String scString() {
        String ret = "";
        //ret += "struct"
        return ret;
    }
}

/**
 * <dl> relation ::=
 *   <dd> shift_expression [ relational_operator shift_expression ]
 */
class ScRelation extends ScVhdl {
    ScShift_expression l_exp = null;
    ScVhdl operator = null;
    ScShift_expression r_exp = null;
    public ScRelation(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRELATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScShift_expression newNode = null;
            switch(c.getId())
            {
            case ASTSHIFT_EXPRESSION:
                newNode = new ScShift_expression(c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScRelational_operator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String tmp = l_exp.scString();
        if(l_exp.r_exp != null) {
            ret = "(" + tmp + ")";
        }
        if(r_exp != null) {
            ret += " " + getReplaceOperator(operator.scString()) + " ";
            tmp += r_exp.scString();
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
class ScRelational_operator extends ScVhdl {
    ScToken op = null;
    public ScRelational_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTRELATIONAL_OPERATOR);
        op = new ScToken(node);
    }

    public String scString() {
        return op.scString();
    }
}

/**
 * <dl> report_statement ::=
 *   <dd>  [ label : ]
 *   <ul> <b>report</b> expression
 *   <ul> [ <b>severity</b> expression ] ; </ul></ul>
 */
class ScReport_statement extends ScVhdl {
    ScVhdl report_exp = null;
    ScVhdl severity_exp = null;
    public ScReport_statement(ASTNode node) {
        super(node);
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
                        report_exp = new ScExpression(c);
                    }else if(image.equalsIgnoreCase(tokenImage[SEVERITY])) {
                        severity_exp = new ScExpression(c);
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        warning("report statement ignored");
        return ret;
    }
}

/**
 * <dl> return_statement ::=
 *   <dd> [ label : ] <b>return</b> [ expression ] ;
 */
class ScReturn_statement extends ScVhdl {
    ScVhdl expression = null;
    public ScReturn_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTRETURN_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += intent() + "return";
        if(expression != null) {
            ret += " " + expression.scString();
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
class ScScalar_nature_definition extends ScVhdl {
    public ScScalar_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_NATURE_DEFINITION);
    }

    public String scString() {
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
class ScScalar_type_definition extends ScVhdl {
    ScVhdl item = null;
    public ScScalar_type_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSCALAR_TYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTENUMERATION_TYPE_DEFINITION:
            item = new ScEnumeration_type_definition(c);
            break;
        case ASTINTEGER_TYPE_DEFINITION:    // the same as float type
            item = new ScInteger_type_definition(c);
            break;
        case ASTPHYSICAL_TYPE_DEFINITION:
            item = new ScPhysical_type_definition(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> secondary_unit ::=
 *   <dd> architecture_body
 *   <br> | package_body
 */
class ScSecondary_unit extends ScVhdl {
    public ScSecondary_unit(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSECONDARY_UNIT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> secondary_unit_declaration ::=
 *   <dd> identifier = physical_literal ;
 */
class ScSecondary_unit_declaration extends ScVhdl {
    ScVhdl identifier = null;
    ScVhdl phy_literal = null;
    public ScSecondary_unit_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSECONDARY_UNIT_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER:
                identifier = new ScIdentifier(c);
                break;
            case ASTPHYSICAL_LITERAL:
                phy_literal = new ScPhysical_literal(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += identifier.scString();
        ret += " = ";
        ret += phy_literal.scString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> selected_name ::=
 *   <dd> prefix . suffix
 */
class ScSelected_name extends ScVhdl {
    ScPrefix prefix = null;
    ScSuffix suffix = null;
    public ScSelected_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSELECTED_NAME);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTPREFIX:
                prefix = new ScPrefix(c);
                break;
            case ASTSUFFIX:
                suffix = new ScSuffix(c);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<String> getNameSegments() {
        ArrayList<String> segments = new ArrayList<String>();
        segments.addAll(prefix.getNameSegments());
        segments.add(suffix.scString());
        return segments;
    }

    public String scString() {
        String ret = "";
        ret += prefix.scString();
        ret += ".";
        ret += suffix.scString();
        return ret;
    }
}

/**
 * <dl> selected_signal_assignment ::=
 *   <dd> <b>with</b> expression <b>select</b>
 *   <ul> target <= options selected_waveforms ; </ul>
 */
class ScSelected_signal_assignment extends ScVhdl {
    public ScSelected_signal_assignment(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSELECTED_SIGNAL_ASSIGNMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> selected_waveforms ::=
 *   <dd> { waveform <b>when</b> choices , }
 *   <br> waveform <b>when</b> choices
 */
class ScSelected_waveforms extends ScVhdl {
    public ScSelected_waveforms(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSELECTED_WAVEFORMS);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> sensitivity_clause ::=
 *   <dd> <b>on</b> sensitivity_list
 */
class ScSensitivity_clause extends ScVhdl {
    ScSensitivity_list sensitivity_list = null;
    public ScSensitivity_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSENSITIVITY_CLAUSE);
        sensitivity_list = new ScSensitivity_list((ASTNode)node.getChild(0));
    }

    public String scString() {
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
class ScSensitivity_list extends ScVhdl {
    ArrayList<String> items = new ArrayList<String>();
    public ScSensitivity_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSENSITIVITY_LIST);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScName(c);
            items.add(item.scString());
        }
    }
    
    public ArrayList<String> getList() {
        return items;
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> sequence_of_statements ::=
 *   <dd> { sequential_statement }
 */
class ScSequence_of_statements extends ScVhdl {
    public ScSequence_of_statements(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSEQUENCE_OF_STATEMENTS);
    }

    public String scString() {
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
class ScSequential_statement extends ScVhdl {
    ScVhdl item = null;
    public ScSequential_statement(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSEQUENTIAL_STATEMENT);
        switch(node.getId())
        {
        case ASTWAIT_STATEMENT:
            item = new ScWait_statement(node);
            break;
        case ASTASSERTION_STATEMENT:
            item = new ScAssertion_statement(node);
            break;
        case ASTREPORT_STATEMENT:
            item = new ScReport_statement(node);
            break;
        case ASTSIGNAL_ASSIGNMENT_STATEMENT:
            item = new ScSignal_assignment_statement(node);
            break;
        case ASTVARIABLE_ASSIGNMENT_STATEMENT:
            item = new ScVariable_assignment_statement(node);
            break;
        case ASTPROCEDURE_CALL_STATEMENT:
            item = new ScProcedure_call_statement(node);
            break;
        case ASTIF_STATEMENT:
            item = new ScIf_statement(node);
            break;
        case ASTCASE_STATEMENT:
            item = new ScCase_statement(node);
            break;
        case ASTLOOP_STATEMENT:
            item = new ScLoop_statement(node);
            break;
        case ASTNEXT_STATEMENT:
            item = new ScNext_statement(node);
            break;
        case ASTEXIT_STATEMENT:
            item = new ScExit_statement(node);
            break;
        case ASTRETURN_STATEMENT:
            item = new ScReturn_statement(node);
            break;
        case ASTNULL_STATEMENT:
            item = new ScNull_statement(node);
            break;
        case ASTBREAK_STATEMENT:
            item = new ScBreak_statement(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> shift_expression ::=
 *   <dd> simple_expression [ shift_operator simple_expression ]
 */
class ScShift_expression extends ScVhdl {
    ScSimple_expression l_exp = null;
    ScVhdl operator = null;
    ScSimple_expression r_exp = null;
    public ScShift_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSHIFT_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScSimple_expression newNode = null;
            switch(c.getId())
            {
            case ASTSHIFT_EXPRESSION:
                newNode = new ScSimple_expression(c);
                if(l_exp == null) {
                    l_exp = newNode;
                }else {
                    r_exp = newNode;
                }
                break;
            case ASTVOID:
                operator = new ScShift_operator(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String tmp = l_exp.scString();
        if(l_exp.items.size() > 2) {
            ret += "(" + tmp + ")";
        }else {
            ret += tmp;
        }

        if(r_exp != null) {
            ret += " " + getReplaceOperator(operator.scString()) + " ";
            tmp = r_exp.scString();
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
class ScShift_operator extends ScVhdl {
    ScToken op = null;
    public ScShift_operator(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSHIFT_OPERATOR);
        op = new ScToken(node);
    }

    public String scString() {
        return op.scString();
    }
}

/**
 * <dl> sign ::=
 *   <dd> + | -
 */
class ScSign extends ScVhdl {
    public ScSign(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGN);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> signal_assignment_statement ::=
 *   <dd> [ label : ] target <= [ delay_mechanism ] waveform ;
 */
class ScSignal_assignment_statement extends ScVhdl {
    ScVhdl target = null;
    ScVhdl waveform = null;
    ScVhdl delay = null;
    public ScSignal_assignment_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTDELAY_MECHANISM:
                delay = new ScDelay_mechanism(c);
                break;
            case ASTWAVEFORM:
                waveform = new ScWaveform(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        String pre = "";
        pre = target.scString();
        if(delay != null) {
            warning("delay mechanism ignore");
        }
        ArrayList<ScVhdl> elements = ((ScWaveform)waveform).getElements();
        if(elements.size() > 1) {
            warning("multi-source of signal assignment not support");
        }
        for(int i = 0; i < elements.size(); i++) {
            ScWaveform_element ele = (ScWaveform_element)elements.get(i);
            ScExpression delayTime = (ScExpression)ele.getTime();
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
class ScSignal_declaration extends ScVhdl {
    public ScSignal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> signal_kind ::=
 *   <dd> <b>register</b> | <b>bus</b>
 */
class ScSignal_kind extends ScVhdl {
    public ScSignal_kind(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_KIND);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> signal_list ::=
 *   <dd> <i>signal_</i>name { , <i>signal_</i>name }
 *   <br> | <b>others</b>
 *   <br> | <b>all</b>
 */
class ScSignal_list extends ScVhdl {
    public ScSignal_list(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNAL_LIST);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> signature ::=
 *   <dd> [ [ type_mark { , type_mark } ] [ <b>return</b> type_mark ] ]
 */
class ScSignature extends ScVhdl {
    public ScSignature(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIGNATURE);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> simple_expression ::=
 *   <dd> [ sign ] term { adding_operator term }
 */
class ScSimple_expression extends ScVhdl {
    ScSign sign = null;
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSimple_expression(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMPLE_EXPRESSION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTTERM:
                newNode = new ScTerm(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScAdding_operator(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(sign != null) {
            ret += sign.scString();
        }
        ScTerm term = (ScTerm)items.get(0);
        String tmp = term.scString();
        if(term.items.size() > 2) {
            ret += "(" + tmp + ")";
        }else {
            ret += tmp;
        }
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            ret += " " + getReplaceOperator(items.get(i).scString()) + " ";
            term = (ScTerm)items.get(i+1);
            tmp = term.scString();
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
class ScSimple_name extends ScVhdl {
    ScVhdl identifier = null;
    public ScSimple_name(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSIMPLE_NAME);
        assert(node.getId() == ASTIDENTIFIER);
        identifier = new ScIdentifier(node);
    }

    public String scString() {
        return identifier.scString();
    }
}

/**
 * <dl> simple_simultaneous_statement ::=
 *   <dd> [ label : ] simple_expression == simple_expression [ tolerance_aspect ] ;
 */
class ScSimple_simultaneous_statement extends ScVhdl {
    public ScSimple_simultaneous_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMPLE_SIMULTANEOUS_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> simultaneous_alternative ::=
 *   <dd> <b>when</b> choices =>
 *   <ul> simultaneous_statement_part </ul>
 */
class ScSimultaneous_alternative extends ScVhdl {
    public ScSimultaneous_alternative(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_ALTERNATIVE);
    }

    public String scString() {
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
class ScSimultaneous_case_statement extends ScVhdl {
    public ScSimultaneous_case_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_CASE_STATEMENT);
    }

    public String scString() {
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
class ScSimultaneous_if_statement extends ScVhdl {
    public ScSimultaneous_if_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_IF_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> simultaneous_null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class ScSimultaneous_null_statement extends ScVhdl {
    public ScSimultaneous_null_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_NULL_STATEMENT);
    }

    public String scString() {
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
class ScSimultaneous_procedural_statement extends ScVhdl {
    public ScSimultaneous_procedural_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_PROCEDURAL_STATEMENT);
    }

    public String scString() {
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
class ScSimultaneous_statement extends ScVhdl {
    public ScSimultaneous_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> simultaneous_statement_part ::=
 *   <dd> { simultaneous_statement }
 */
class ScSimultaneous_statement_part extends ScVhdl {
    public ScSimultaneous_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_STATEMENT_PART);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> slice_name ::=
 *   <dd> prefix ( discrete_range )
 */
class ScSlice_name extends ScVhdl {
    public ScSlice_name(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSLICE_NAME);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> source_aspect ::=
 *   <dd> <b>spectrum</b> <i>magnitude_</i>simple_expression , <i>phase_</i>simple_expression
 *   <br> | <b>noise</b> <i>power_</i>simple_expression
 */
class ScSource_aspect extends ScVhdl {
    public ScSource_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSOURCE_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> source_quantity_declaration ::=
 *   <dd> <b>quantity</b> identifier_list : subtype_indication source_aspect ;
 */
class ScSource_quantity_declaration extends ScVhdl {
    public ScSource_quantity_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSOURCE_QUANTITY_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> step_limit_specification ::=
 *   <dd> <b>limit</b> quantity_specification <b>with</b> <i>real_</i>expression ;
 */
class ScStep_limit_specification extends ScVhdl {
    public ScStep_limit_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSTEP_LIMIT_SPECIFICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> subnature_declaration ::=
 *   <dd> <b>subnature</b> identifier <b>is</b> subnature_indication ;
 */
class ScSubnature_declaration extends ScVhdl {
    public ScSubnature_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBNATURE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> subnature_indication ::=
 *   <dd> nature_mark [ index_constraint ] [ <b>tolerance</b> <i>string_</i>expression <b>across</b> <i>string_</i>expression <b>through</b> ]
 */
class ScSubnature_indication extends ScVhdl {
    public ScSubnature_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBNATURE_INDICATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> string_literal ::=
 *   <dd> " { graphic_character } "
 */
class ScString_literal extends ScVhdl {
    String str = "";
    public ScString_literal(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSTRING_LITERAL);
        str = node.firstTokenImage();
    }

    public String scString() {
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
class ScSubprogram_body extends ScVhdl {
    ScVhdl spec = null;
    ScVhdl declarative_part = null;
    ScVhdl statement_part = null;
    public ScSubprogram_body(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_BODY);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSUBPROGRAM_SPECIFICATION:
                spec = new ScSubprogram_specification(c);
                break;
            case ASTSUBPROGRAM_DECLARATIVE_PART:
                declarative_part = new ScSubprogram_declarative_part(c);
                break;
            case ASTSUBPROGRAM_STATEMENT_PART:
                statement_part = new ScSubprogram_statement_part(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += spec.scString() + "\r\n";
        ret += "{\r\n";
        ret += declarative_part.scString() + "\r\n";
        ret += statement_part.scString() + "\r\n";
        ret += "}\r\n";
        return "";
    }
}

/**
 * <dl> subprogram_declaration ::=
 *   <dd> subprogram_specification ;
 */
class ScSubprogram_declaration extends ScVhdl {
    ScVhdl spec = null;
    public ScSubprogram_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATION);
        spec = new ScSubprogram_specification((ASTNode)node.getChild(0));
    }

    public String scString() {
        return spec.scString() + ";";
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
class ScSubprogram_declarative_item extends ScVhdl {
    ScVhdl item = null;
    public ScSubprogram_declarative_item(ASTNode node) {
        super(node);
        //assert(node.getId() == ASTSUBPROGRAM_DECLARATIVE_ITEM);
        switch(node.getId())
        {
        case ASTSUBPROGRAM_DECLARATION:
            item = new ScSubprogram_declaration(node);
            break;
        case ASTSUBPROGRAM_BODY:
            item = new ScSubprogram_body(node);
            break;
        case ASTTYPE_DECLARATION:
            item = new ScType_declaration(node);
            break;
        case ASTSUBTYPE_DECLARATION:
            item = new ScSubtype_declaration(node);
            break;
        case ASTCONSTANT_DECLARATION:
            item = new ScConstant_declaration(node);
            break;
        case ASTVARIABLE_DECLARATION:
            item = new SCVariable_declaration(node);
            break;
        case ASTFILE_DECLARATION:
            item = new ScFile_declaration(node);
            break;
        case ASTALIAS_DECLARATION:
            item = new ScAlias_declaration(node);
            break;
        case ASTATTRIBUTE_DECLARATION:
            item = new ScAttribute_declaration(node);
            break;
        case ASTATTRIBUTE_SPECIFICATION:
            item = new ScAttribute_specification(node);
            break;
        case ASTUSE_CLAUSE:
            item = new ScUse_clause(node);
            break;
        case ASTGROUP_TEMPLATE_DECLARATION:
            item = new ScGroup_template_declaration(node);
            break;
        case ASTGROUP_DECLARATION:
            item = new ScGroup_declaration(node);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> subprogram_declarative_part ::=
 *   <dd> { subprogram_declarative_item }
 */
class ScSubprogram_declarative_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSubprogram_declarative_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_DECLARATIVE_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScSubprogram_declarative_item(c);
            items.add(item);
        }
    }

    public String scString() {
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
//class ScSubprogram_kind extends SCVhdl {
//    public ScSubprogram_kind(ASTNode node) {
//        super(node);
//        assert(node.getId() == ASTSUBPROGRAM_KIND);
//    }
//
//    public String scString() {
//        return "";
//    }
//}

/**
 * <dl> subprogram_specification ::=
 *   <dd> <b>procedure</b> designator [ ( formal_parameter_list ) ]
 *   <br> | [ <b>pure</b> | <b>impure</b> ] <b>function</b> designator [ ( formal_parameter_list ) ]
 *   <ul> <b>return</b> type_mark </ul>
 */
class ScSubprogram_specification extends ScVhdl {
    boolean isFunction = false;
    ScVhdl designator = null;
    ScVhdl parameter_list = null;
    ScVhdl type_mark = null;
    public ScSubprogram_specification(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_SPECIFICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTDESIGNATOR:
                designator = new ScDesignator(c);
                break;
            case ASTVOID:
                if(c.firstTokenImage().equalsIgnoreCase(tokenImage[PROCEDURE])) {
                    isFunction = false;
                }else {
                    isFunction = true;
                }
                break;
            case ASTFORMAL_PARAMETER_LIST:
                parameter_list = new ScFormal_parameter_list(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                isFunction = true;
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        if(type_mark != null) {     // type_mark appear only in function
            ret += type_mark.scString() + " ";
        }else {
            ret += "void ";
        }
        ret += designator.scString();
        ret += "(";
        if(parameter_list != null) {
            ret += parameter_list.scString();
        }
        ret += ")";
        return ret;
    }
}

/**
 * <dl> subprogram_statement_part ::=
 *   <dd> { sequential_statement }
 */
class ScSubprogram_statement_part extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScSubprogram_statement_part(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBPROGRAM_STATEMENT_PART);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl item = new ScSequential_statement(c);
            items.add(item);
        }
    }

    public String scString() {
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
class ScSubtype_declaration extends ScVhdl {
    public ScSubtype_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBTYPE_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> subtype_indication ::=
 *   <dd> [ <i>resolution_function_</i>name ] type_mark [ constraint ] [ tolerance_aspect ]
 */
class ScSubtype_indication extends ScVhdl {
    ScName name = null;
    ScType_mark type_mark = null;
    ScConstraint constraint = null;
    ScTolerance_aspect tolerance = null;
    public ScSubtype_indication(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUBTYPE_INDICATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTNAME:
                name = new ScName(c);
                break;
            case ASTTYPE_MARK:
                type_mark = new ScType_mark(c);
                break;
            case ASTCONSTRAINT:
                constraint = new ScConstraint(c);
                break;
            case ASTTOLERANCE_ASPECT:
                tolerance = new ScTolerance_aspect(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
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
class ScSuffix extends ScVhdl {
    ScVhdl item = null;
    public ScSuffix(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSUFFIX);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTIDENTIFIER:
            item = new ScSimple_name(c);
            break;
        case ASTOPERATOR_SYMBOL:
            item = new ScOperator_symbol(c);
            break;
        case ASTVOID:
            item = new ScToken(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> target ::=
 *   <dd> name
 *   <br> | aggregate
 */
class ScTarget extends ScVhdl {
    ScVhdl item = null;
    public ScTarget(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTARGET);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTNAME:
            item = new ScName(c);
            break;
        case ASTAGGREGATE:
            item = new ScAggregate(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> term ::=
 *   <dd> factor { multiplying_operator factor }
 */
class ScTerm extends ScVhdl {
    ArrayList<ScVhdl> items = new ArrayList<ScVhdl>();
    public ScTerm(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl newNode = null;
            switch(c.getId())
            {
            case ASTFACTOR:
                newNode = new ScFactor(c);
                items.add(newNode);
                break;
            case ASTVOID:
                newNode = new ScMultiplying_operator(c);
                items.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ScFactor factor = (ScFactor)items.get(0);
        ret += factor.scString();
        
        for(int i = 1; i < items.size() - 1; i += 2) {
            ret += " " + getReplaceOperator(items.get(i).scString()) + " ";
            factor = (ScFactor)items.get(i+1);
            ret += factor.scString();
        }
        return ret;
    }
}

/**
 * <dl> terminal_aspect ::=
 *   <dd> <i>plus_terminal_</i>name [ <b>to</b> <i>minus_terminal_</i>name ]
 */
class ScTerminal_aspect extends ScVhdl {
    public ScTerminal_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERMINAL_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> terminal_declaration ::=
 *   <dd> <b>terminal</b> identifier_list : subnature_indication ;
 */
class ScTerminal_declaration extends ScVhdl {
    public ScTerminal_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTERMINAL_DECLARATION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> through_aspect ::=
 *   <dd> identifier_list [ tolerance_aspect ] [ := expression ] <b>through</b>
 */
class ScThrough_aspect extends ScVhdl {
    public ScThrough_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTHROUGH_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> timeout_clause ::=
 *   <dd> <b>for</b> <i>time_or_real_</i>expression
 */
class ScTimeout_clause extends ScVhdl {
    ScVhdl expression = null;
    String time_unit_name = "ns";
    public ScTimeout_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTIMEOUT_CLAUSE);
        expression = new ScExpression((ASTNode)node.getChild(0));
        time_unit_name = node.getLastToken().image;
    }

    public String scString() {
        return expression.scString();
    }
    
    public String getTimeUnitName() {
        return time_unit_name;
    }
}

/**
 * <dl> tolerance_aspect ::=
 *   <dd> <b>tolerance</b> <i>string_</i>expression
 */
class ScTolerance_aspect extends ScVhdl {
    public ScTolerance_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTOLERANCE_ASPECT);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> type_conversion ::=
 *   <dd> type_mark ( expression )
 */
class ScType_conversion extends ScVhdl {
    public ScType_conversion(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_CONVERSION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> type_declaration ::=
 *   <dd> full_type_declaration
 *   <br> | incomplete_type_declaration
 */
class ScType_declaration extends ScVhdl {
    ScVhdl item = null;
    public ScType_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_DECLARATION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTFULL_TYPE_DECLARATION:
            item = new ScFull_type_declaration(c);
            break;
        case ASTINCOMPLETE_TYPE_DECLARATION:
            item = new ScIncomplete_type_declaration(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> type_definition ::=
 *   <dd> scalar_type_definition
 *   <br> | composite_type_definition
 *   <br> | access_type_definition
 *   <br> | file_type_definition
 */
class ScType_definition extends ScVhdl {
    ScVhdl item = null;
    public ScType_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_DEFINITION);
        ASTNode c = (ASTNode)node.getChild(0);
        switch(c.getId())
        {
        case ASTSCALAR_TYPE_DEFINITION:
            item = new ScScalar_type_definition(c);
            break;
        case ASTCOMPOSITE_TYPE_DEFINITION:
            item = new ScComposite_type_definition(c);
            break;
        case ASTACCESS_TYPE_DEFINITION:
            item = new ScAccess_type_definition(c);
            break;
        case ASTFILE_TYPE_DEFINITION:
            item = new ScFile_type_definition(c);
            break;
        default:
            break;
        }
    }

    public String scString() {
        return item.scString();
    }
}

/**
 * <dl> type_mark ::=
 *   <dd> <i>type_</i>name
 *   <br> | <i>subtype_</i>name
 */
class ScType_mark extends ScVhdl {
    public ScType_mark(ASTNode node) {
        super(node);
        assert(node.getId() == ASTTYPE_MARK);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> unconstrained_array_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> <i>element_</i>subtype_indication </ul>
 */
class ScUnconstrained_array_definition extends ScVhdl {
    public ScUnconstrained_array_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNCONSTRAINED_ARRAY_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> unconstrained_nature_definition ::=
 *   <dd> <b>array</b> ( index_subtype_definition { , index_subtype_definition } )
 *   <ul> <b>of</b> subnature_indication </ul>
 */
class ScUnconstrained_nature_definition extends ScVhdl {
    public ScUnconstrained_nature_definition(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUNCONSTRAINED_NATURE_DEFINITION);
    }

    public String scString() {
        return "";
    }
}

/**
 * <dl> use_clause ::=
 *   <dd> <b>use</b> selected_name { , selected_name } ;
 */
class ScUse_clause extends ScVhdl {
    static final String IEEE = "ieee";
    static final String STD_LOGIC_1164 = "std_logic_1164";
    static final String STD = "std";
    static final String TEXTIO = "textio";
    static final String WORK = "work";
    
    ArrayList<ScSelected_name> names = new ArrayList<ScSelected_name>();
    
    public ScUse_clause(ASTNode node) {
        super(node);
        assert(node.getId() == ASTUSE_CLAUSE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScSelected_name newNode = null;
            switch(c.getId())
            {
            case ASTSELECTED_NAME:
                newNode = new ScSelected_name(c);
                names.add(newNode);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
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
class ScVariable_assignment_statement extends ScVhdl {
    ScVhdl target = null;
    ScVhdl expression = null;
    public ScVariable_assignment_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTVARIABLE_ASSIGNMENT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTTARGET:
                target = new ScTarget(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = "";
        ret += target.scString();
        ret += " = ";
        ret += expression.scString();
        ret += ";";
        return ret;
    }
}

/**
 * <dl> variable_declaration ::=
 *   <dd> [ <b>shared</b> ] <b>variable</b> identifier_list : subtype_indication [ := expression ] ;
 */
class SCVariable_declaration extends ScVhdl {
    ScIdentifier_list identifier_list = null;
    ScSubtype_indication subtype_indication = null;
    ScExpression expression = null;
    public SCVariable_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTVARIABLE_DECLARATION);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTIDENTIFIER_LIST:
                identifier_list = new ScIdentifier_list(c);
                break;
            case ASTSUBTYPE_INDICATION:
                subtype_indication = new ScSubtype_indication(c);
                break;
            case ASTEXPRESSION:
                expression = new ScExpression(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        String ret = intent();
        ret += subtype_indication.scString();
        
        ArrayList<ScVhdl> items = identifier_list.getItems();
        for(int i = 0; i < items.size(); i++) {
            ret += " " + items.get(i).scString();
            if(expression != null) {
                ret += " " + expression.scString();
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
class ScWait_statement extends ScVhdl {
    ScSensitivity_clause sensitivity = null;
    ScCondition_clause condition = null;
    ScTimeout_clause timeout = null;
    public ScWait_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSENSITIVITY_CLAUSE:
                sensitivity = new ScSensitivity_clause(c);
                break;
            case ASTCONDITION_CLAUSE:
                condition = new ScCondition_clause(c);
                break;
            case ASTTIMEOUT_CLAUSE:
                timeout = new ScTimeout_clause(c);
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
            ret += timeout.scString();
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

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += "do {\r\n";
            ret += getWaitString();
            ret += "\r\n}while(";
            ret += condition.scString();
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
class ScWaveform extends ScVhdl {
    ArrayList<ScVhdl> elements = new ArrayList<ScVhdl>();
    boolean isUnaffected = false;
    public ScWaveform(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAVEFORM);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl ele = null; 
            switch(c.getId())
            {
            case ASTVOID:   // unaffected
                isUnaffected = true;
                break;
            case ASTWAVEFORM_ELEMENT:
                ele = new ScWaveform_element(c);
                elements.add(ele);
                break;
            default:
                break;
            }
        }
    }
    
    public ArrayList<ScVhdl> getElements() {
        return elements;
    }

    public String scString() {
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
class ScWaveform_element extends ScVhdl {
    ScVhdl value_exp = null;
    ScVhdl time_exp = null;
    boolean isNull = false;
    String timeUnit = "";
    public ScWaveform_element(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAVEFORM_ELEMENT);
        boolean bAfter = false;
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            ScVhdl ele = null; 
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
                ele = new ScExpression(c);
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
    
    public ScVhdl getValue() {
        return value_exp;
    }
    
    public ScVhdl getTime() {
        return time_exp;
    }
    
    public String getTimeUnit() {
        return timeUnit;
    }

    public String scString() {
        String ret = "";
        if(isNull) {
            warning("null ignore");
        }
        return ret;
    }
}
