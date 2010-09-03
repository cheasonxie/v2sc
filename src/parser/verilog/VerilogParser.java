package parser.verilog;

import java.io.Reader;

import parser.CommentBlock;
import parser.IASTNode;
import parser.IParser;
import parser.ISymbol;
import parser.ParserException;

public class VerilogParser implements IParser
{
    @Override
    public CommentBlock[] getComment() {
        return null;
    }

    @Override
    public IASTNode getRoot() {
        return null;
    }

    @Override
    public ISymbol getSymbol(IASTNode node, String name) {
        return null;
    }

    @Override
    public IASTNode parse(String path) throws ParserException {
        return null;
    }

    @Override
    public IASTNode parse(Reader reader) throws ParserException {
        return null;
    }

    @Override
    public ISymbol getSymbol(IASTNode node, String[] names) {
        return null;
    }

}
