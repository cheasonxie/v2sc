package converter.vhdl;

import parser.vhdl.ASTNode;


/**
 * <dl> generation_scheme ::=
 *   <dd> <b>for</b> <i>generate_</i>parameter_specification
 *   <br> | <b>if</b> condition
 */
class ScGeneration_scheme extends ScVhdl {
    public ScGeneration_scheme(ASTNode node) {
        super(node);
        assert(node.getId() == ASTGENERATION_SCHEME);
    }

    public String scString() {
        return "";
    }
}
