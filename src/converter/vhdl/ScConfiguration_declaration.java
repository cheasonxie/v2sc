package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> configuration_declaration ::=
 *   <dd> <b>configuration</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> configuration_declarative_part
 *   <br> block_configuration
 *   </ul><b>end</b> [ <b>configuration</b> ] [ <i>configuration_</i>simple_name ] ;
 */
class ScConfiguration_declaration extends ScVhdl {
    public ScConfiguration_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATION);
    }

    public String scString() {
        return "";
    }
}
