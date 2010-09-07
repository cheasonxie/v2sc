package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> port_map_aspect ::=
 *   <dd> <b>port</b> <b>map</b> ( <i>port_</i>association_list )
 */
class ScPort_map_aspect extends ScVhdl {
    public ScPort_map_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTPORT_MAP_ASPECT);
    }

    public String scString() {
        return "";
    }
}
