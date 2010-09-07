package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> component_declaration ::=
 *   <dd> <b>component</b> identifier [ <b>is</b> ]
 *   <ul> [ <i>local_</i>generic_clause ]
 *   <br> [ <i>local_</i>port_clause ]
 *   </ul> <b>end</b> <b>component</b> [ <i>component_</i>simple_name ] ;
 */
class ScComponent_declaration extends ScVhdl {
    public ScComponent_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_DECLARATION);
    }

    public String scString() {
        return "";
    }
}
