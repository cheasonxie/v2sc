package converter.vhdl;

import java.util.ArrayList;

import common.printFileAndLineNumber;
import converter.CommentManager;

import parser.CommentBlock;
import parser.IParser;
import parser.Token;
import parser.vhdl.ASTNode;
import parser.vhdl.IVhdlType;
import parser.vhdl.Symbol;
import parser.vhdl.SymbolTable;
import parser.vhdl.VhdlASTConstants;
import parser.vhdl.VhdlTokenConstants;

public class ScVhdl implements ScVhdlConstants, VhdlTokenConstants, 
                        VhdlASTConstants, IVhdlType
{
    protected static IParser parser = null;
    protected static CommentManager commentMgr = null;
    /** entities and packages in this design file */
    protected static ArrayList<ScCommonIdentifier> units = null;
    protected static int curLevel = 0;    // intent level    
    
    protected ASTNode curNode = null;
    protected int beginLine = 0;
    protected int endLine = 0;
    protected boolean isLogic = false;
    protected ArrayList<CommentBlock> myPrevComment = null;
    protected CommentBlock myPostComment = null;
    protected boolean needComment = true;
    
    
    /**
     * constructor
     */
    public ScVhdl(IParser parser) {
        ScVhdl.parser = parser;
        if(parser != null) {
            curNode = (ASTNode)parser.getRoot();
            commentMgr = new CommentManager(parser);
            init();
        }
    }
    
    public ScVhdl(ASTNode node) {
        curNode = node;
        init();
    }
    
    public ScVhdl(ASTNode node, boolean needComment) {
        curNode = node;
        this.needComment = needComment;
        init();
    }
    
    protected void init() {
        if(curNode != null) {
            Token token = curNode.getFirstToken();
            if(token != null) {
                beginLine = token.beginLine;
            }else {
                beginLine = ((ASTNode)curNode.getParent()).getFirstToken().beginLine;
            }
            
            token = curNode.getLastToken();
            if(token != null) {
                endLine = token.beginLine;
            }else {
                endLine = ((ASTNode)curNode.getParent()).getLastToken().beginLine;
            }
        }
        
        if(needComment && commentMgr != null) {
            CommentBlock tmp = commentMgr.getCurrentBlock();
            if(tmp != null) {
                if(tmp.endLine < beginLine) {
                    if(myPrevComment == null)
                        myPrevComment = new ArrayList<CommentBlock>();
                    do {
                        myPrevComment.add(tmp);
                        commentMgr.toNextBlock();
                        tmp = commentMgr.getCurrentBlock();
                    }while(tmp != null && tmp.endLine < beginLine);
                }
                
                if(tmp != null && tmp.startLine == tmp.endLine 
                        && beginLine == endLine 
                        && tmp.startLine == endLine) {
                    // the comment is at the end of this line
                    myPostComment = tmp;
                    commentMgr.toNextBlock();
                }
            }
        }
    }
    
    protected String addPrevComment() {
        if(myPrevComment == null) return "";
        String ret = "";
        String strIntent = intent();
        for(int i = 0; i < myPrevComment.size(); i ++) {
            CommentBlock cb = myPrevComment.get(i);
            for(int j = 0; j < cb.commentLines.size(); j++) {
                ret += strIntent + cb.commentLines.get(j) + "\r\n";
            }
        }
        return ret;
    }
    
    protected String addPostComment() {
        if(myPostComment == null) return "";
        String ret = "    ";
        ret += myPostComment.commentLines.get(0);
        return ret;
    }
    
    
    @Override
    public String toString() {
        String ret = "";
        ret += addPrevComment();
        ret += scString();
        ret += addPostComment();
        return ret;
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
    
    protected static void startIntentBlock() { curLevel ++; }
    protected static void endIntentBlock() { curLevel --; }
    
    // intent
    protected static String intent(int lv)
    {
        String ret = "";
        for (int i = 0; i < lv; i++)
            for (int j = 0; j < tabSize; j++)
                ret += " ";
        return ret;
    }
    
    protected String intent()
    {
        return intent(curLevel);
    }
    
    protected void setLogic(boolean logic) {
        isLogic = logic;
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
    
    boolean isRangeValid(String[] range)
    {
        return ((range != null) && (range[0] != null)
                && (range[0].length() > 0));
    }
    
    /**
     * calculate result of add one
     */
    static String addOne(String input)
    {
        String str, ret;
        int index1 = input.lastIndexOf('-');
        int index2 = input.lastIndexOf('+');
        int index = -1, addition = 0;
        if (index1 > 0) {
            index = index1;
            addition = -1;
        } else if (index2 > 0) {
            index = index2;
            addition = 1;
        }

        int value = 0;
        ret = "";
        if (index > 0) {
            str = input.substring(index + 1);
            str = str.trim();
            if(str.startsWith("0x")) {
                str = str.substring(2);
                value = Integer.parseInt(str, 16) + addition;
            }else {
                value = Integer.parseInt(str, 10) + addition;
            }
            
            if(value == 0) {
                ret = input.substring(0, index).trim();
            }else {
                ret = input.substring(0, index+1) + " ";
                ret += String.format("%d", value);
            }
        }else {
            try {
                if(input.startsWith("0x")) {
                    value = Integer.parseInt(input, 16) + 1;
                }else {
                    value = Integer.parseInt(input) + 1;
                }
                ret = String.format("%d", value);
            }catch(NumberFormatException e) {
                return (input + " + 1");
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
        
        // check whether max is valid
        ret += "<" + addOne(max) + ">";
        return ret;
    }
    
    String getReplaceOperator(String token)
    {
        String ret = token;
        for (int i = 0; i < vhdlOperators.length; i++) {
            if (token.equalsIgnoreCase(vhdlOperators[i])) {
                if (isLogic)
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
    
    String[] getLoopVar() {
        ArrayList<Symbol> syms = curNode.getSymbolTable().getKindSymbols(LOOP);
        String[] ret = new String[syms.size()];
        for(int i = 0; i < syms.size(); i++) {
            ret[i] = syms.get(i).name;
        }
        return ret;
    }
    
    String[] getTypeRange(ASTNode node, String[] names) {
        Symbol sym = (Symbol)parser.getSymbol(node, names);
        if(sym == null) { return null; }
        Symbol sym1 = (Symbol)parser.getSymbol(node, sym.type);
        if(sym1 == null) { return sym.typeRange; }  //TODO: only tow level here
        return sym1.typeRange;
    }
    
    String[] getArrayRange(ASTNode node, String[] names) {
        Symbol sym = (Symbol)parser.getSymbol(node, names);
        if(sym == null) { return null; }
        Symbol sym1 = (Symbol)parser.getSymbol(node, sym.type);
        if(sym1 == null) { return sym.arrayRange; }  //TODO: only tow level here
        return sym1.arrayRange;
    }
    
    
    /** often be used in "<b>others</b>" of aggregate */
    String[] getTargetRange() {
        String[] ret = null;
        ASTNode node = null;
        
        /* check statement */
        if((node = curNode.getAncestor(ASTVARIABLE_ASSIGNMENT_STATEMENT)) != null) {
            ScVariable_assignment_statement var = new ScVariable_assignment_statement(node);
            ret = var.target.getTargetRange();
        }else if((node = curNode.getAncestor(ASTCONDITIONAL_SIGNAL_ASSIGNMENT)) != null) {
            ScConditional_signal_assignment csa = new ScConditional_signal_assignment(node);
            ret = csa.target.getTargetRange();
        }else if((node = curNode.getAncestor(ASTSELECTED_SIGNAL_ASSIGNMENT)) != null) {
            ScSelected_signal_assignment ssa = new ScSelected_signal_assignment(node);
            ret = ssa.target.getTargetRange();
        }else if((node = curNode.getAncestor(ASTSIGNAL_ASSIGNMENT_STATEMENT)) != null) {
            ScSignal_assignment_statement sas = new ScSignal_assignment_statement(node);
            ret = sas.target.getTargetRange();
        }else {
            /* check declaration */
            ScCommonDeclaration cd = null;
            if((node = curNode.getAncestor(ASTCONSTANT_DECLARATION)) != null) {
                cd = new ScConstant_declaration(node);
            }else if((node = curNode.getAncestor(ASTSIGNAL_DECLARATION)) != null) {
                cd = new ScSignal_declaration(node);
            }else if((node = curNode.getAncestor(ASTVARIABLE_DECLARATION)) != null) {
                cd = new ScVariable_declaration(node);
            }
            
            if(cd != null) {
                String[] names = {cd.idList.items.get(0).identifier};
                ret = getTypeRange(node, names);
            }
        }

        return ret;
    }
    
    String[] getTargetArrayRange() {
        String[] ret = null;
        ASTNode node = null;
        
        /* check statement */
        if((node = curNode.getAncestor(ASTVARIABLE_ASSIGNMENT_STATEMENT)) != null) {
            ScVariable_assignment_statement var = new ScVariable_assignment_statement(node);
            ret = var.target.getTargetArrayRange();
        }else if((node = curNode.getAncestor(ASTCONDITIONAL_SIGNAL_ASSIGNMENT)) != null) {
            ScConditional_signal_assignment csa = new ScConditional_signal_assignment(node);
            ret = csa.target.getTargetArrayRange();
        }else if((node = curNode.getAncestor(ASTSELECTED_SIGNAL_ASSIGNMENT)) != null) {
            ScSelected_signal_assignment ssa = new ScSelected_signal_assignment(node);
            ret = ssa.target.getTargetArrayRange();
        }else if((node = curNode.getAncestor(ASTSIGNAL_ASSIGNMENT_STATEMENT)) != null) {
            ScSignal_assignment_statement sas = new ScSignal_assignment_statement(node);
            ret = sas.target.getTargetArrayRange();
        }else {
            /* check declaration */
            ScCommonDeclaration cd = null;
            if((node = curNode.getAncestor(ASTCONSTANT_DECLARATION)) != null) {
                cd = new ScConstant_declaration(node);
            }else if((node = curNode.getAncestor(ASTSIGNAL_DECLARATION)) != null) {
                cd = new ScSignal_declaration(node);
            }else if((node = curNode.getAncestor(ASTVARIABLE_DECLARATION)) != null) {
                cd = new ScVariable_declaration(node);
            }
            
            if(cd != null) {
                String[] names = {cd.idList.items.get(0).identifier};
                ret = getArrayRange(node, names);
            }
        }

        return ret;
    }
    
    int getIntValue(String str) {
        if(str.isEmpty())
            return 0;
        if(Character.isDigit(str.charAt(0))) {
            return Integer.parseInt(str);
        }else {
            Symbol sym = (Symbol)parser.getSymbol(curNode, str);
            //TODO get value
        }
        return 0;
    }
    
    public int getBitWidth() {
        return 0;
    }
    
    protected Symbol[] getComponentChildSymbols(String componentName, int kind) {
        SymbolTable tmpTable = null;
        tmpTable = (SymbolTable)parser.getTableOfSymbol(curNode, componentName);
        if(tmpTable == null) {
            return null;
        }
        tmpTable = tmpTable.getSubtable(componentName);
        if(tmpTable == null)
            return null;
        
        int count = 0;
        for(int i = 0; i < tmpTable.size(); i++) {
            if(tmpTable.get(i).kind == kind) {
                count ++;
            }
        }
        
        Symbol[] ret = new Symbol[count];
        count = 0;
        for(int i = 0; i < tmpTable.size(); i++) {
            if(tmpTable.get(i).kind == kind) {
                ret[count++] = tmpTable.get(i);
            }
        }
        return ret;
    }
    
    /**
     * @return true if current node is assignment(expression) of declaration
     */
    boolean isDeclarationAssignment() {
        if(!(curNode.getId() == ASTEXPRESSION || curNode.isDescendantOf(ASTEXPRESSION)))
            return false;
        if(curNode.isDescendantOf(ASTCONSTANT_DECLARATION) 
                || curNode.isDescendantOf(ASTSIGNAL_DECLARATION)
                || curNode.isDescendantOf(ASTVARIABLE_DECLARATION))
            return true;
        return false;
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

