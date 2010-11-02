package converter.vhdl;

import java.util.ArrayList;

import common.MyDebug;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;
import parser.vhdl.SymbolTable;


/**
 * <dl> port_map_aspect ::=
 *   <dd> <b>port</b> <b>map</b> ( <i>port_</i>association_list )
 */
class ScPort_map_aspect extends ScVhdl {
    ScAssociation_list association_list = null;
    public ScPort_map_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_MAP_ASPECT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTASSOCIATION_LIST:
                association_list = new ScAssociation_list(c);
                break;
            default:
                break;
            }
        }
    }
    
    /**
     * map to component
     * @param componentName: name of component
     * @param entityName: name of entity
     */
    public String mapString(String name, String entityName) {
        String ret = "";
        int i = 0;

        SymbolTable symTab = (SymbolTable)parser.getTableOfSymbol(curNode, name);
        if(symTab == null) {
            MyDebug.printFileLine("component not found:" + name);
            return ret;
        }
        
        symTab = symTab.getChildTable(name);
        if(symTab == null) {
            MyDebug.printFileLine("component table not found:" + name);
            return ret;
        }
        
        Symbol[] syms = symTab.getKindSymbols(PORT);
        if(syms.length == 0) {
            System.out.println();
        }
        ArrayList<ScAssociation_element> elements = association_list.elements;
        
        for(i = 0; i < elements.size(); i++) {
            if(elements.get(i).actual_part.designator.isOpen)   // ignore open port
                continue;
            String portName = "";
            if(elements.get(i).formal_part == null) {
                portName = syms[i].name;
            }else {
                portName = elements.get(i).formal_part.scString();
            }
            ret += intent() + entityName + "." + portName + "(";
            ret += elements.get(i).actual_part.scString();
            ret += ");\r\n";
        }

        return ret;
    }

    public String scString() {
        return "";
    }
}
