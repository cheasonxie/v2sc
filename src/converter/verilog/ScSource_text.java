package converter.verilog;

import java.util.ArrayList;

import converter.IScFile;

import parser.IParser;
import parser.verilog.ASTNode;

/**
 *  source_text  <br>
 *     ::= { description } 
 */
class ScSource_text extends ScVerilog implements IScFile {
    public ScSource_text(IParser parser) {
        this(parser, false);
    }
    
    /**
     * convert dividually: in order to decrease memory, <br>
     * when one standalone <b>entity</b> or <b>package</b> can be converted,<br>
     * it will be converted immediately and then it's memory will be free.<br>
     * <br>
     * "can be converted" means that:
     * <li> <b>entity</b> meets all of it's architecture body & configuration
     * <li> <b>package</b> meets it's package body
     */
    boolean convDividual = false;
    public ScSource_text(IParser parser, boolean dividual) {
        super(parser);
        convDividual = dividual;
    }

    public String scString() {
        String ret = "";
        return ret;
    }

    @Override
    public String getDeclaration() {
        return "";
    }

    @Override
    public String getImplements() {
        return "";
    }

    @Override
    public String getInclude() {
        return "";
    }

    @Override
    public void getIndividualString(StringBuffer strInclude,
            StringBuffer strDeclaration, StringBuffer strImplements) {
    }
}
