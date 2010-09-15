package converter.vhdl;

import java.util.ArrayList;

import converter.IScFile;
import converter.IncludePath;
import parser.INameObject;
import parser.IParser;
import parser.vhdl.ASTNode;
import parser.vhdl.VhdlArrayList;


/**
 * <dl> design_file ::=
 *   <dd> design_unit { design_unit }
 */
class ScDesign_file extends ScVhdl implements IScFile {
    ArrayList<ScDesign_unit> design_units = new ArrayList<ScDesign_unit>(); 
    public ScDesign_file(IParser parser) {
        super(parser);
        units = new ArrayList<ScCommonIdentifier>();
        assert(curNode.getId() == ASTDESIGN_FILE);
        for(int i = 0; i < curNode.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)curNode.getChild(i);
            assert(c.getId() == ASTDESIGN_UNIT);
            ScDesign_unit dunit = new ScDesign_unit(c);
            design_units.add(dunit);
        }
    }

    public String scString() {
        String ret = "\r\n#include <systemc.h>\r\n";
        for(int i = 0; i < design_units.size(); i++) {
            ret += design_units.get(i).toString();
            if(i < design_units.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        for(int i = 0; i < design_units.size(); i++) {
            ret += design_units.get(i).getDeclaration() + "\r\n";
        }
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        for(int i = 0; i < design_units.size(); i++) {
            ret += design_units.get(i).getImplements() + "\r\n";
        }
        return ret;
    }

    @Override
    public String getInclude()
    {
        String ret = "";
        ret += addPrevComment();
        ret += "\r\n#include <systemc.h>\r\n";
        VhdlArrayList<IncludePath> list = new VhdlArrayList<IncludePath>();
        for(int i = 0; i < design_units.size(); i++) {
            IncludePath[] paths = design_units.get(i).getInclude();
            list.addAll(paths);
        }
        
        for(int i = 0; i < list.size(); i++) {
            ret += list.get(i);
            if(i < list.size() - 1) {
                ret += "\r\n";
            }
        }
        return ret;
    }
}

