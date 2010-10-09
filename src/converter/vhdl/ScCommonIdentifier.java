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
                return getWidth(sym.typeRange[0], sym.typeRange[2]);
            }
        }
        return 0;
    }
    
    public String scString() {
        return identifier;
    }
}
