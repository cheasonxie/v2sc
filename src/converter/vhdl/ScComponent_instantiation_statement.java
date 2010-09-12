package converter.vhdl;

import java.util.ArrayList;

import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;
import parser.vhdl.SymbolTable;


/**
 * <dl> component_instantiation_statement ::=
 *   <dd> <i>instantiation_</i>label :
 *   <ul> instantiated_unit
 *   <ul> [ generic_map_aspect ]
 *   <br> [ port_map_aspect ] ; </ul></ul>
 */
class ScComponent_instantiation_statement extends ScCommonIdentifier implements IStatement {
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
            case ASTIDENTIFIER:
                identifier = c.firstTokenImage();
                break;
            default:
                break;
            }
        }
        if(identifier.isEmpty())
            identifier = String.format("line%d", node.getFirstToken().beginLine);
    }

    private String getName() {
        return "process_comp_" + identifier;
    }
    private String getSpec() {
        return intent() + "void " + getName() + "(void)";
    }
    
    public String scString() {
        return "";
    }

    @Override
    public String getDeclaration() {
        String ret = "";
        if(port_map != null) {
            String name = instantiated_unit.name.scString();
            ret += intent() + name;
            if(generic_map != null) {
                ret += "<" + generic_map.mapString(name) + ">";
            }
            name = " comp_" + name;
            ret += name + "(\"" + name + "\");\r\n";
        }
        ret += getSpec() + ";";
        return ret;
    }

    @Override
    public String getImplements() {
        String ret = getSpec() + "\r\n";
        ret += intent() + "{\r\n";
        startIntentBlock();
        if(port_map != null) {
            String name = instantiated_unit.name.scString();
            ret += port_map.mapString(name, "comp_" + name);
        }
        endIntentBlock();
        ret += intent() + "}\r\n";
        return ret;
    }

    @Override
    public String getInitCode()
    {
        return getName() + "();";
    }
}
