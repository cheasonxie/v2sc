package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> component_instantiation_statement ::=
 *   <dd> <i>instantiation_</i>label :
 *   <ul> instantiated_unit
 *   <ul> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ] ; </ul></ul>
 */
class ScComponent_instantiation_statement extends ScVhdl {
    ScInstantiated_unit instantiated_unit = null;
    ScGeneric_map_aspect generic_map = null;
    ScPort_map_aspect port_map = null;
    public ScComponent_instantiation_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCOMPONENT_INSTANTIATION_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTINSTANTIATED_UNIT:
                instantiated_unit = new ScInstantiated_unit(c);
                break;
            case ASTGENERIC_MAP_ASPECT:
                generic_map = new ScGeneric_map_aspect(c);
                break;
            case ASTPORT_MAP_ASPECT:
                port_map = new ScPort_map_aspect(c);
                break;
            default:
                break;
            }
        }
    }

    public String scString() {
        return "";
    }
}
