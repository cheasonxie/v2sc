package converter.vhdl;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


class ScCommonIdentifier extends ScVhdl {
    String identifier = "";
    public ScCommonIdentifier(ASTNode node) {
        super(node);
    }
    
    public void setIdentifier(String ident) {
        identifier = ident;
    }
    
    public int getBitWidth() {
        Symbol sym = (Symbol)parser.getSymbol(curNode, identifier);
        if(sym != null) {
            String[] range = sym.typeRange;
            if(range != null) {
                int v1 = getIntValue(sym.typeRange[0]);
                int v2 = getIntValue(sym.typeRange[2]);
                return (v1 > v2) ? (v1-v2+1) : (v2-v1+1);
            }
        }
        return 0;
    }
    
    public String scString() {
        return identifier;
    }
}
