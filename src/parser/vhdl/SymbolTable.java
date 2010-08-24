/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package parser.vhdl;

public class SymbolTable extends VhdlArrayList<Symbol>
{
    private static final long serialVersionUID = 2819894900029453741L;
    
    SymbolTable parent = null;    
    
    public SymbolTable() {}
    public SymbolTable(SymbolTable p) {
        parent = p;
    }
    
    @Override
    public boolean add(Symbol e)
    {
        if(e == null) {
            return false;
        }
        for(int i = 0; i < size(); i++) {
            if(e.getName().equalsIgnoreCase(get(i).getName())) {
                return false;
            }
        }
        return super.add(e);
    }

    /**
     * Get a symbol from the symbol table
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

