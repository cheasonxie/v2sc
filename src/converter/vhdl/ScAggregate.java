package converter.vhdl;

import java.util.ArrayList;
import parser.vhdl.ASTNode;
import parser.IASTNode;
import parser.vhdl.SymbolTable;
import parser.vhdl.Symbol;


/**
 * <dl> aggregate ::=
 *   <dd> ( element_association { , element_association } )
 */
class ScAggregate extends ScVhdl {
    ArrayList<ScElement_association> elementList = new ArrayList<ScElement_association>();
    public ScAggregate(ASTNode node) {
        super(node);
        assert(node.getId() == ASTAGGREGATE);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            IASTNode c = node.getChild(i);
            ScElement_association n = new ScElement_association((ASTNode)c);
            elementList.add(n);
        }
    }
    
    public int getBitWidth() {
        int ret = 0;
        for(int i = 0; i < elementList.size(); i++) {
            ret += elementList.get(i).getBitWidth();
        }
        return ret;
    }
    
    public SymbolTable getTargetTypeSymbolTable() {
        SymbolTable ret = null;
        ASTNode node = null;
        ScCommonDeclaration cd = null;
        if((node = curNode.getAncestor(ASTCONSTANT_DECLARATION)) != null) {
            cd = new ScConstant_declaration(node);
        }else if((node = curNode.getAncestor(ASTSIGNAL_DECLARATION)) != null) {
            cd = new ScSignal_declaration(node);
        }else if((node = curNode.getAncestor(ASTVARIABLE_DECLARATION)) != null) {
            cd = new ScVariable_declaration(node);
        }
        
        if(cd != null) {
            Symbol sym = (Symbol)parser.getSymbol(node, cd.idList.items.get(0).identifier);
            if(sym == null) { return null; }
            ret = curNode.getSymbolTable().getTableOfSymbol(sym.type); //TODO: only tow level here
            if(ret != null)
                ret = ret.getSubtable(sym.type);
        }
        return ret;
    }
    
    protected void setLogic(boolean logic) {
        super.setLogic(logic);
        if(elementList.size() < 2)
            elementList.get(0).setLogic(logic);
    }

    public String scString() {
        String[] typeRange = getTargetRange();
        String[] arrayRange = getTargetArrayRange();
        SymbolTable recordTable = getTargetTypeSymbolTable();
        boolean isArray = (arrayRange != null);
        
        String ret = "";
        if(isArray)
            ret += "{";
        else if(elementList.size() > 1)
            ret += "(";
        
        int max = 1;
        if(isArray) {
            int v1 = getIntValue(arrayRange[0]);
            int v2 = getIntValue(arrayRange[2]);
            max = (v1 > v2) ? (v1-v2+1) : (v2-v1+1);
        }else if(typeRange != null) {
            int v1 = getIntValue(typeRange[0]);
            int v2 = getIntValue(typeRange[2]);
            max = (v1 > v2) ? (v1-v2+1) : (v2-v1+1);
        }
        
        int num = 0;
        for(int i = 0; i < elementList.size(); i++) {
            int width = max-num;
            if(recordTable != null && recordTable.get(i).typeRange != null) {
                int v1 = getIntValue(recordTable.get(i).typeRange[0]);
                int v2 = getIntValue(recordTable.get(i).typeRange[2]);
                width = (v1 > v2) ? (v1-v2+1) : (v2-v1+1);
            }
            ret += elementList.get(i).toBitString(width, isArray);
            num += elementList.get(i).getBitWidth();
            if(i < elementList.size() - 1) {
                ret += ", ";
            }
        }
        
        if(isArray)
            ret += "}";
        else if(elementList.size() > 1)
            ret += ")";
        return ret;
    }
}
