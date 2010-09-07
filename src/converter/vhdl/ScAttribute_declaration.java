package converter.vhdl;

import parser.vhdl.ASTNode;


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
