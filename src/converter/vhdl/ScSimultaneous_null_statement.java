package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> simultaneous_null_statement ::=
 *   <dd> [ label : ] <b>null</b> ;
 */
class ScSimultaneous_null_statement extends ScCommonIdentifier implements IStatement {
    public ScSimultaneous_null_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTSIMULTANEOUS_NULL_STATEMENT);
    }

    public String scString() {
        return "";
    }

    @Override
    public String getDeclaration()
    {
        return "";
    }

    @Override
    public String getImplements()
    {
        return "";
    }

    @Override
    public String getInitCode()
    {
        return "";
    }
}
