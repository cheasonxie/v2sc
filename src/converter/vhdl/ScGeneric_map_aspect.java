package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generic_map_aspect ::=
 *   <dd> <b>generic</b> <b>map</b> ( <i>generic_</i>association_list )
 */
class ScGeneric_map_aspect extends ScVhdl {
    public ScGeneric_map_aspect(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERIC_MAP_ASPECT);
    }

    public String scString() {
        return "";
    }
}
