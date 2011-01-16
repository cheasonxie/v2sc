package converter.vhdl;

import java.util.ArrayList;

import common.MyDebug;

import converter.IScStatementBlock;
import parser.ISymbol;
import parser.Token;
import parser.vhdl.ASTNode;
import parser.vhdl.Symbol;


/**
 * <dl> architecture_body ::=
 *   <dd> <b>architecture</b> identifier <b>of</b> <i>entity_</i>name <b>is</b>
 *   <ul> architecture_declarative_part </ul>
 *   <b>begin</b>
 *   <ul> architecture_statement_part </ul>
 *   <b>end</b> [ <b>architecture</b> ] [ <i>architecture_</i>simple_name ] ;
 */
class ScArchitecture_body extends ScCommonIdentifier implements IScStatementBlock {
    ScName entity_name = null;
    ScArchitecture_declarative_part declarative_part = null;
    ScArchitecture_statement_part statement_part = null;
    
    ArrayList<ScVhdl> signalAssignments = new ArrayList<ScVhdl>();
    String assProcessName = "process_comp_assignment";
    public ScArchitecture_body(ASTNode node) {
        super(node, true);
        assert(node.getId() == ASTARCHITECTURE_BODY);
        int i, j;
                
        for(i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            int id = c.getId();
            ScVhdl tmp = null;
            switch(id)
            {
            case ASTIDENTIFIER:
                tmp = new ScIdentifier(c);
                identifier = tmp.scString();
                break;
            case ASTNAME:
                entity_name = new ScName(c);
                for(j = 0; j < units.size(); j++) {
                    ScCommonIdentifier ident = units.get(j);
                    if(ident instanceof ScEntity_declaration
                        && ident.identifier.equalsIgnoreCase(entity_name.scString())) {
                        ((ScEntity_declaration)ident).setArchitectureBody(this);
                        break;
                    }
                }
                if(j == units.size()) {
                    MyDebug.printFileLine("architecture boty has no corresponding entity");
                }
                break;
            case ASTARCHITECTURE_DECLARATIVE_PART:
                declarative_part = new ScArchitecture_declarative_part(c);
                break;
            case ASTARCHITECTURE_STATEMENT_PART:
                statement_part = new ScArchitecture_statement_part(c);
                break;
            default:
                break;
            }
        }
    }
    
    public String scString() {
        String ret = "";
        ret += addLF(declarative_part.toString());
        ret += addLF(statement_part.scString());
        return ret;
    }

    @Override
    public String getDeclaration()
    {
        String ret = "";
        ret += addLF(declarative_part.getDeclaration());
        if(signalAssignments.size() > 0) {
            ret += addLF(intent() + "void " + assProcessName + "();");
        }
        ret += addLF(statement_part.getDeclaration());
        return ret;
    }

    @Override
    public String getImplements()
    {
        String ret = "";
        ret += addLF(declarative_part.getImplements());
        
        if(signalAssignments.size() > 0) {
            ret += intent() + "void " + assProcessName + "()\r\n";
            ret += startIntentBraceBlock();
            for(int i = 0; i < signalAssignments.size(); i++) {
                ret += addLF(signalAssignments.get(i).toString());
            }
            ret += endIntentBraceBlock();
        }
        
        ret += addLF(statement_part.getImplements());
        return ret;
    }

    @Override
    public String getInitCode()
    {
        String ret = "";
        ret += addLF(declarative_part.getInitCode());
        if(signalAssignments.size() > 0) {
            ret += intent() + "SC_METHOD(" + assProcessName + ");\r\n";
            ret += intent() + "sensitive";
            ArrayList<String> senList = getAssignmentSensitiveList();
            for(int i = 0; i < senList.size(); i++) {
                ret += " << " + senList.get(i);
            }
            ret += ";\r\n";
        }
        ret += addLF(statement_part.getInitCode());
        return ret;
    }
    
    private ArrayList<String> getAssignmentSensitiveList() {
        ArrayList<String> senList = new ArrayList<String>();
        for(int i = 0; i < signalAssignments.size(); i++) {
            ScConcurrent_signal_assignment_statement ass = 
                (ScConcurrent_signal_assignment_statement)signalAssignments.get(i);
            ScVhdl sv = null;
            if(ass.signal_assignment instanceof ScConditional_signal_assignment) {
                sv = ((ScConditional_signal_assignment)ass.signal_assignment).waveforms;

            }else {
                // ScSelected_signal_assignment
                sv = ((ScSelected_signal_assignment)ass.signal_assignment).selected_waveforms;
            }
            
            Token tkn = sv.curNode.getFirstToken();
            Symbol sym;
            while(tkn != sv.curNode.getLastToken()) {
                sym = (Symbol)parser.getSymbol(curNode, tkn.image);
                if(sym != null && (sym.kind == SIGNAL || sym.kind == PORT)) {
                    int k = 0;
                    for(k = 0; k < senList.size(); k++) {
                        if(tkn.image.equalsIgnoreCase(senList.get(k))) {
                            break;
                        }
                    }
                    if(k >= senList.size())
                        senList.add(tkn.image);
                }
                tkn = tkn.next;
            }
            
            sym = (Symbol)parser.getSymbol(curNode, tkn.image);
            if(sym != null && (sym.kind == SIGNAL || sym.kind == PORT)) {
                int k = 0;
                for(k = 0; k < senList.size(); k++) {
                    if(tkn.image.equalsIgnoreCase(senList.get(k))) {
                        break;
                    }
                }
                if(k >= senList.size())
                    senList.add(tkn.image);
            }
        }
        return senList;
    }
    
    protected void addSignalAssignment(ScVhdl ass) {
        signalAssignments.add(ass);
    }
}

