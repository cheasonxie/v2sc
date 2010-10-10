package converter.vhdl;

import parser.vhdl.ASTNode;
import java.util.ArrayList;


/**
 * <dl> wait_statement ::=
 *   <dd> [ label : ] <b>wait</b> [ sensitivity_clause ] 
 *                [ condition_clause ] [ timeout_clause ] ;
 */
class ScWait_statement extends ScVhdl {
    ScSensitivity_clause sensitivity = null;
    ScCondition_clause condition = null;
    ScTimeout_clause timeout = null;
    public ScWait_statement(ASTNode node) {
        super(node);
        assert(node.getId() == ASTWAIT_STATEMENT);
        for(int i = 0; i < node.getChildrenNum(); i++) {
            ASTNode c = (ASTNode)node.getChild(i);
            switch(c.getId())
            {
            case ASTSENSITIVITY_CLAUSE:
                sensitivity = new ScSensitivity_clause(c);
                break;
            case ASTCONDITION_CLAUSE:
                condition = new ScCondition_clause(c);
                break;
            case ASTTIMEOUT_CLAUSE:
                timeout = new ScTimeout_clause(c);
                break;
            default:
                break;
            }
        }
    }
    
    private String getWaitString() {
        String ret = intent();
        ret += "next_trigger(";
        if(timeout != null) {
            ret += timeout.scString();
            ret += ", ";
            ret += getSCTime(timeout.getTimeUnitName());
        }
        
        if(sensitivity != null) {
            ret += ", ";
            ArrayList<String> sensList = sensitivity.getSensitiveList();
            String strSens = "";
            for(int i = 0; i < sensList.size(); i++) {
                strSens += sensList.get(i);
                if(i < sensList.size()) {
                    strSens += " | ";
                }
            }
        }
        ret += ");";
        return ret;
    }

    public String scString() {
        String ret = "";
        if(condition != null) {
            ret += intent() + "do {\r\n";
            startIntentBlock();
            ret += getWaitString();
            endIntentBlock();
            ret += "\r\n" + intent() + "}while(";
            ret += condition.scString();
            ret += ")";
        }else {
            ret += getWaitString();
        }
        ret += ";";
        return ret;
    }
}
