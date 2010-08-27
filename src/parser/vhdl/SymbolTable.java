/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

import java.util.HashMap;

public class SymbolTable extends VhdlArrayList<Symbol>
{
    private static final long serialVersionUID = 2819894900029453741L;
    
    SymbolTable parent = null;
    HashMap<String, SymbolTable> children = null;
    
    public SymbolTable() {}
    public SymbolTable(SymbolTable p) {
        parent = p;
    }
    
    /**
     * add one child to children table<br>
     * only package need child table(such as component's port_list/generic_list)
     */
    protected void addChild(String name, SymbolTable child) {
        if(children == null) {
            children = new HashMap<String, SymbolTable>();
        }
        children.put(name, child);
    }
    
    /**
     * copy another's children to my children table<br>
     * only package need child table(such as component's port_list/generic_list)
     */
    protected void copyChild(SymbolTable other, Symbol[] symbols) {
        if(other == null || symbols == null) {
            return;
        }
        if(children == null) {
            children = new HashMap<String, SymbolTable>();
        }
        
        for(int i = 0; i < symbols.length; i++) {
            String name = symbols[i].getName();
            SymbolTable child = other.getChild(name);
            if(child != null) {
                children.put(name, child);
            }
        }
    }
    
    /**
     * get one child table<br>
     * only package need child table(such as component's port_list/generic_list)
     */
    protected SymbolTable getChild(String name) {
        if(children == null) {
            return null;
        }
        return children.get(name);
    }

    /**
     * Get a symbol from the symbol table by name
     */
    public Symbol getSymbol(String name) {
        Symbol ret = get(name);
        if(ret != null)
            return ret;
        
        if(parent != null)
            return parent.getSymbol(name);
        else
            return null;
    }
    
    public SymbolTable getParent() {
        return parent;
    }
    
    public void setParent(SymbolTable sym) {
        parent = sym;
    }
}

