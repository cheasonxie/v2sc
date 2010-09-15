package converter.vhdl;

import converter.IScStatementBlock;
import parser.vhdl.ASTNode;


/**
 * <dl> configuration_declaration ::=
 *   <dd> <b>configuration</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> configuration_declarative_part
 *   <br> block_configuration
 *   </ul><b>end</b> [ <b>configuration</b> ] [ <i>configuration_</i>simple_name ] ;
 */
class ScConfiguration_declaration extends ScVhdl implements IScStatementBlock {
    public ScConfiguration_declaration(ASTNode node) {
        super(node);
        assert(node.getId() == ASTCONFIGURATION_DECLARATION);
    }

    public String scString() {
        return "";
    }

    @Override
    public String getDeclaration()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getImplements()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getInitCode()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
