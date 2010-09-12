package parser;

public interface ISymbolTable
{
    int getSize();
    ISymbol getSymbol(int i);
    ISymbol getSymbol(String name);
}
