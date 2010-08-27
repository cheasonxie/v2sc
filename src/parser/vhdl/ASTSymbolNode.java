package parser.vhdl;

import java.util.ArrayList;

import parser.IASTNode;
import parser.Token;

class ASTSymbolNode extends ASTNode 
    implements VhdlTokenConstants, VhdlASTConstants, IVhdlType
{
    // symbol node kind
    protected static final int KIND_SUBPROGRAM = 0;
    protected static final int KIND_TYPE = 1;
    protected static final int KIND_SUBTYPE = 2;
    protected static final int KIND_VARAIBLE = 3;
    protected static final int KIND_INTERFACE_LIST = 4;
    protected static final int KIND_OTHER = 5;
    
    private boolean isParsed = false;

    private SymbolTable mySymTab = new SymbolTable();
    
    public ASTSymbolNode(IASTNode p, int id) {
        super(p, id);
    }
    
    // wild node, only used to parse symbol(must not in ast)
    protected ASTSymbolNode(IASTNode thisNode) {
        super(null, 0);
        children = ((ASTNode)thisNode).children;
        first_token = ((ASTNode)thisNode).first_token;
        last_token = ((ASTNode)thisNode).last_token;
        symTab = ((ASTNode)thisNode).symTab;
    }
    
    /**
     * <b>function</b>, <b>procedure</b>, <b>variable</b>, <b>constant</b>, <b>type</b><br>
     * <b>attribute</b>, <b>alias</b>, <b>subtype</b>, <b>file</b>, <b>group</b><br>
     * <b>signal</b>, <b>component</b>, <b>quantity</b>, <b>nature</b>, <b>terminal</b><br>
     * <b>subnature</b>, <b>generic</b>, <b>port</b>
     */
    private int getNodeKind(int kind) {
        int ret = KIND_OTHER;
        switch(kind) 
        {
        case FUNCTION:
        case PROCEDURE:
            ret = KIND_SUBPROGRAM;
            break;
            
        case VARIABLE:
        case CONSTANT:
        case FILE:
        case SIGNAL:
        case TERMINAL:
        case QUANTITY:
            ret = KIND_VARAIBLE;
            break;
            
        case PORT:
        case GENERIC:
            ret = KIND_INTERFACE_LIST;
            break;
            
        case TYPE:
            ret = KIND_TYPE;
            break;
            
        case SUBTYPE:
            ret = KIND_SUBTYPE;
            break;
            
        case ALIAS:
        case GROUP:
        case ATTRIBUTE:
        case COMPONENT:
        case NATURE:
        case SUBNATURE:
        default:
            break;
        }
        return ret;
    }
    
    private String getSimpleExpression(ASTNode node) {
        String ret = "";
        Token token = node.first_token;
        while(token != node.last_token) {
            ret += token.image;
            token = token.next;
        }
        ret += token.image;
        return ret;
    }
    private String[] getRange(ASTNode rangeNode) {
        String range[] = null;
        if(rangeNode.getChild(0).getId() == ASTSIMPLE_EXPRESSION) {
            range = new String[3];
            range[0] = getSimpleExpression((ASTNode)rangeNode.getChild(0));
            range[1] = ((ASTNode)rangeNode.getChild(1)).getName();
            range[2] = getSimpleExpression((ASTNode)rangeNode.getChild(2));
        }else {
            ASTNode atribute = (ASTNode)rangeNode.getChild(0);
            ASTNode prefix = (ASTNode)atribute.getChild(0);
            ASTNode designator = (ASTNode)atribute.getChildById(ASTATTRIBUTE_DESIGNATOR);
            assert(designator.getName().equalsIgnoreCase("range"));
            range = symTab.getSymbol(prefix.getName()).range.clone();
        }
        return range;
    }
    
    private boolean parseSymbol(int kind) {
        if(isParsed) {
            return true;
        }
        
        int i = 0, j = 0;
        ASTNode tmpNode0 = null, tmpNode1 = null;
        ASTSymbolNode child = null;
        
        int nodeKind = getNodeKind(kind);
        Symbol sym = new Symbol();
        sym.kind = kind;
        switch(nodeKind)
        {
        case KIND_SUBPROGRAM:
            sym.name = getName();
            tmpNode0 = (ASTNode)getDescendant(ASTINTERFACE_LIST);
            if(tmpNode0 == null) {
                break;  // no parameters
            }
            
            sym.paramTypeList = new ArrayList<String>();
            
            // get parameters
            for(i = 0; i < tmpNode0.getChildrenNum(); i++) {
                tmpNode1 = (ASTNode)tmpNode0.getChild(i).getChild(0); // interface_declaration
                child = new ASTSymbolNode(tmpNode1);
                SymbolTable table = child.getParsedSymbolTable(VARIABLE);
                for(j = 0; j < table.size(); j++) {
                    sym.paramTypeList.add(table.get(j).type);
                }
            }
            
            // get return type
            tmpNode0 = (ASTNode)getDescendant(ASTSUBPROGRAM_SPECIFICATION);
            tmpNode0 = (ASTNode)tmpNode0.getChildById(ASTTYPE_MARK);
            if(tmpNode0 != null) {
                sym.type = tmpNode0.getName();
            }
            mySymTab.add(sym);
            break;
            
        case KIND_INTERFACE_LIST:
            tmpNode0 = (ASTNode)getDescendant(ASTINTERFACE_LIST);
            for(i = 0; i < tmpNode0.getChildrenNum(); i++) {
                child = new ASTSymbolNode(tmpNode0.getChild(i));
                mySymTab.addAll(child.getParsedSymbolTable(VARIABLE));
            }
            break;
            
        case KIND_TYPE:
            sym.name = ((ASTNode)getDescendant(ASTIDENTIFIER)).getName();
            tmpNode0 = (ASTNode)getDescendant(ASTCOMPOSITE_TYPE_DEFINITION);
            if(tmpNode0 != null) {
                tmpNode0 = (ASTNode)tmpNode0.getChildById(ASTARRAY_TYPE_DEFINITION);
                if(tmpNode0 != null) {
                    // array type
                    tmpNode0 = (ASTNode)tmpNode0.getChild(0);
                    tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTINDEX_CONSTRAINT);
                    if(tmpNode1 != null) {
                        // constraint array
                        ASTNode rangeNode = (ASTNode)tmpNode1.getDescendant(ASTRANGE);
                        sym.typeRange = getRange(rangeNode);
                    }
                    
                    tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTSUBTYPE_INDICATION);
                    tmpNode1 = (ASTNode)tmpNode1.getChildById(ASTTYPE_MARK);
                    sym.type = tmpNode1.getName();
                    
                }else {
                    // record type
                    sym.type = strVhdlType[TYPE_RECORD];
                }
            }
            mySymTab.add(sym);
            break;
            
        case KIND_SUBTYPE:
            // get names
            sym.name = ((ASTNode)getChildById(ASTIDENTIFIER)).getName();
            
            // get type and range
            tmpNode0 = (ASTNode)getDescendant(ASTSUBTYPE_INDICATION);
            tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTTYPE_MARK);
            sym.type = tmpNode1.getName();
            
            // type range(children of type_mark)
            tmpNode1 = (ASTNode)tmpNode1.getDescendant(ASTRANGE);
            if(tmpNode1 != null) {
                sym.typeRange = getRange(tmpNode1);
            }
            
            // value range
            tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTCONSTRAINT);
            if(tmpNode1 != null) {
                ASTNode rangeNode = (ASTNode)tmpNode1.getDescendant(ASTRANGE);
                sym.range = getRange(rangeNode);
            }
            mySymTab.add(sym);
            break;
            
        case KIND_VARAIBLE:
            
            tmpNode0 = (ASTNode)getDescendant(ASTMODE);
            if(tmpNode0 != null) {
                sym.mode = tmpNode0.firstTokenImage();
            }
            
            // get type and range
            tmpNode0 = (ASTNode)getDescendant(ASTSUBTYPE_INDICATION);
            tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTTYPE_MARK);
            sym.type = tmpNode1.getName();
            
            // type range(children of type_mark)
            tmpNode1 = (ASTNode)tmpNode1.getDescendant(ASTRANGE);
            if(tmpNode1 != null) {
                sym.typeRange = getRange(tmpNode1);
            }
            
            tmpNode1 = (ASTNode)tmpNode0.getChildById(ASTCONSTRAINT);
            if(tmpNode1 != null) {
                ASTNode rangeNode = (ASTNode)tmpNode1.getDescendant(ASTRANGE);
                sym.range = getRange(rangeNode);
            }
            
            // get names
            tmpNode0 = (ASTNode)getDescendant(ASTIDENTIFIER_LIST);
            for(i = 0; i < tmpNode0.getChildrenNum(); i++) {
                ASTNode idNode = (ASTNode)tmpNode0.getChild(i);
                Symbol tmpSym = new Symbol();
                tmpSym.name = idNode.getName();
                tmpSym.mode = sym.mode;
                tmpSym.type = sym.type;
                tmpSym.range = sym.range;
                tmpSym.kind = sym.kind;
                mySymTab.add(tmpSym);
            }
            break;
            
        case KIND_OTHER:
        default:
            sym.name = getName();
            mySymTab.add(sym);
            break;
        }
        isParsed = true;
        return true;
    }
    
    /** add private table to global table */
    public boolean addSymbols(int kind) {
        boolean ret = parseSymbol(kind);
        if(!ret || parent == null || ((ASTNode)parent).symTab == null)
            return false;
        return ((ASTNode)parent).symTab.addAll(mySymTab);
    }
    
    /** parse and save to private table */
    protected SymbolTable getParsedSymbolTable(int kind) {
        if(!parseSymbol(kind))
            return null;
        return mySymTab;
    }
}

