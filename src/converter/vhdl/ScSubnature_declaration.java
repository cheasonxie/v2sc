package converter.vhdl;

import parser.vhdl.ASTNode;


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
