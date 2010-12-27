package converter.verilog;

import parser.verilog.ASTNode;

/**
 *  identifier  <br>
 *     ::=  IDENTIFIER {. IDENTIFIER } <br>
 *     (Note: the period may <b>not</b> be preceded <b>or</b> followed by a space.) 
 */
class ScIdentifier extends ScVerilog {
    public ScIdentifier(ASTNode node) {
        super(node);
        assert(node.getId() == ASTidentifier);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}

/**
 *  IDENTIFIER  <br>
 *     An identifier is any sequence of letters, digits, dollar signs ($), <b>and</b> <br>
 *     underscore (_) symbol, except that the first must be a letter <b>or</b> the <br>
 *     underscore; the first character may <b>not</b> be a digit <b>or</b> $. Upper <b>and</b> lower <b>case</b> <br>
 *     letters are considered to be different. Identifiers may be up to 1024 <br>
 *     characters long. Some Verilog-based tools do <b>not</b> recognize identifier <br>
 *     characters beyond the 1024th as a significant part of the identifier. Escaped <br>
 *     identifiers start with the backslash character (\) <b>and</b> may include any <br>
 *     printable ASCII character. An escaped identifier ends with white space. The <br>
 *     leading backslash character is <b>not</b> considered to be part of the identifier. 
 */
class ScIDENTIFIER extends ScVerilog {
    public ScIDENTIFIER(ASTNode node) {
        super(node);
        assert(node.getId() == ASTIDENTIFIER);
    }

    public String ScString() {
        String ret = "";
        return ret;
    }
}
