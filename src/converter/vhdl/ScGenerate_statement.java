package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generate_statement ::=
 *   <dd> <i>generate_</i>label :
 *   <ul> generation_scheme <b>generate</b>
 *   <ul> [ {block_declarative_item }
 *   </ul> <b>begin</b> ]
 *   <ul> { architecture_statement }
 *   </ul> <b>end</b> <b>generate</b> [ <i>generate_</i>label ] ; </ul>
 */
class ScGenerate_statement extends ScVhdl {
    public ScGenerate_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERATE_STATEMENT);
    }

    public String scString() {
        return "";
    }
}
