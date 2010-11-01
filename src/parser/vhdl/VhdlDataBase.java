package parser.vhdl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import common.MyDebug;

import parser.vhdl.Symbol;

public class VhdlDataBase
{
    // error string
    private static String JDBC_NOT_CONNECT = "jdbc has not been connect, call init() firstly";
    private static String INVALID_TABLE_NAME = "invalid table name";
    private static String INVALID_SYMBOL_NAME = "invalid symbol name";
    
    // database connection & statement
    private Connection conn = null;
    private Statement stmt = null;
    
    private String normalize(String str) {
        String ret = str.replace('\"', '&');
        ret = ret.replace('\'', '@');
        return ret;
    }
    
    private String restore(String str) {
        String ret = str.replace('&', '\"');
        ret = ret.replace('@', '\'');
        return ret;
    }
    
    public boolean init() {
        try {
            // Loading database driver
            String driverName = "org.sqlite.JDBC";
            Class.forName(driverName);
            conn = DriverManager.getConnection("jdbc:sqlite:vhdlLib.db");
            
            stmt = conn.createStatement();
        }catch (ClassNotFoundException e) {  
            e.printStackTrace();
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean newTable(String tabName, boolean append) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            System.err.println(INVALID_TABLE_NAME);
            return false;
        }
        
        tabName = normalize(tabName);
        
        String[] columns = {
                //"libName text",
                //"pkgName text",
                "name text", 
                "kind integer",
                "type text",
                "range0 text",
                "range1 text",
                "range2 text",
                "arrayRange0 text",
                "arrayRange1 text",
                "arrayRange2 text",
                "typeRange0 text",
                "typeRange1 text",
                "typeRange2 text",
                "mode text",
                "defValue text",
                "paramList text"
        };
        
        String createStr = "";
        try {
            if(!append) {
                stmt.executeUpdate("drop table if exists \"" + tabName + "\";");
            }
            
            createStr = "create table \"" + tabName + "\"(";
            for(int i = 0; i < columns.length; i++) {
                createStr += columns[i];
                if(i < columns.length - 1) {
                    createStr += ", ";
                }
            }
            createStr += ");";
            stmt.executeUpdate(createStr);
        } catch (SQLException e) {
            //System.out.println(e.getMessage() + ": " + createStr);
            //e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean insert(String tabName, Symbol sym) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            System.err.println(INVALID_TABLE_NAME);
            return false;
        }
        tabName = normalize(tabName);
        
        if(sym == null) {
            System.err.println("null parameter");
            return false;
        }
        
        // check whether this symbol is already in table 
        Symbol[] oldSyms = retrive(tabName, sym.name);
        if(oldSyms != null) {
            for(int i = 0; i < oldSyms.length; i++) {
                if(oldSyms[i].equals(sym))
                    return true;
            }
        }
        
        String insertStr = "insert into \"" + tabName + "\" values(" +
                    //"'" + sym.libName + "', " + 
                    //"'" + sym.pkgName + "', " + 
                    "'" + normalize(sym.name) + "', " +
                    sym.kind + ", '" +  normalize(sym.type) + "', ";
        
        if(sym.range != null) {
            insertStr += "'" + normalize(sym.range[0]) + "', " 
                      + "'" + normalize(sym.range[1]) + "', "
                      + "'" + normalize(sym.range[2]) + "', ";
        }else {
            insertStr += "'', '', '', ";
        }
        
        if(sym.arrayRange != null) {
            insertStr += "'" + normalize(sym.arrayRange[0]) + "', "
                      + "'" + normalize(sym.arrayRange[1]) + "', "
                      + "'" + normalize(sym.arrayRange[2]) + "', ";
        }else {
            insertStr += "'', '', '', ";
        }
        
        if(sym.typeRange != null) {
            insertStr += "'" + normalize(sym.typeRange[0]) + "', "
                      + "'" + normalize(sym.typeRange[1]) + "', "
                      + "'" + normalize(sym.typeRange[2]) + "', ";
        }else {
            insertStr += "'', '', '', ";
        }
        
        insertStr += "'" + normalize(sym.mode) + "', ";
        insertStr += "'" + normalize(sym.value) + "', ";
        
        if(sym.paramTypeList != null) {
            insertStr += "'";
            for(int i = 0; i < sym.paramTypeList.size(); i++) {
                insertStr += normalize(sym.paramTypeList.get(i));
                if(i < sym.paramTypeList.size() - 1) {
                    insertStr += "#";
                }
            }
            insertStr += "'";
        }else {
            insertStr += "''";
        }
        insertStr += ");";
        
        try {
            stmt.executeUpdate(insertStr);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean insert(String tabName, Symbol[] syms) {
        if(syms == null) {
            System.err.println("null parameter");
            return false;
        }
        
        for(int i = 0; i < syms.length; i++) {
            if(!insert(tabName, syms[i]))
                return false;
        }
        return true;
    }
    
    public Symbol[] retrive(String tabName) {
        return retriveArray(tabName, null);
    }
    
    public Symbol[] retrive(String tabName, String symName) {
        return retriveArray(tabName, symName);
    }
    
    public int getCount(String tabName) {
        return getCount(tabName, null);
    }
    
    public int getCount(String tabName, String symName) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return 0;
        }
        if(tabName == null || tabName.isEmpty()) {
            System.err.println(INVALID_TABLE_NAME);
            return 0;
        }
        tabName = normalize(tabName);
        symName = normalize(symName);
        
        int ret = 0;
        
        String selStr = "select * from " + tabName;
        if(symName != null && !symName.isEmpty())
            selStr += " " + "where name = '" + symName  + "';";
        try {
            ResultSet rs = stmt.executeQuery(selStr);
            while(rs.next()) {
                ret ++;
            }
        }catch (SQLException e) {
            return 0;
        }
        return ret;
    }
    
    /**
     * find symbol in specified table
     * @param tableName name of table
     * @param symName symbol name
     */
    private Symbol[] retriveArray(String tabName, String symName) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return null;
        }
        if(tabName == null || tabName.isEmpty()) {
            System.err.println(INVALID_TABLE_NAME);
            return null;
        }
        tabName = normalize(tabName);
        symName = normalize(symName);
        
        ArrayList<Symbol> ret = new ArrayList<Symbol>();
        String selStr = "select * from \"" + tabName + "\"";
        if(symName != null && !symName.isEmpty())
            selStr += " " + "where name = '" + symName  + "';";
        try {
            ResultSet rs = stmt.executeQuery(selStr);
            while(rs.next()) {
                Symbol sym = new Symbol();
                sym.name = restore(rs.getString("name"));
                sym.kind = rs.getInt("kind");
                sym.type = restore(rs.getString("type"));
                
                String tmp = rs.getString("range0");
                if(tmp != null && !tmp.isEmpty()) {
                    sym.range = new String[3];
                    sym.range[0] = restore(tmp);
                    sym.range[1] = restore(rs.getString("range1"));
                    sym.range[2] = restore(rs.getString("range2"));
                }
                
                tmp = rs.getString("arrayRange0");
                if(tmp != null && !tmp.isEmpty()) {
                    sym.arrayRange = new String[3];
                    sym.arrayRange[0] = restore(tmp);
                    sym.arrayRange[1] = restore(rs.getString("arrayRange1"));
                    sym.arrayRange[2] = restore(rs.getString("arrayRange2"));
                }
                
                tmp = rs.getString("typeRange0");
                if(tmp != null && !tmp.isEmpty()) {
                    sym.typeRange = new String[3];
                    sym.typeRange[0] = restore(tmp);
                    sym.typeRange[1] = restore(rs.getString("typeRange1"));
                    sym.typeRange[2] = restore(rs.getString("typeRange2"));
                }
                
                sym.mode = restore(rs.getString("mode"));
                sym.value = restore(rs.getString("defValue"));
                
                tmp = rs.getString("paramList");
                if(tmp != null && !tmp.isEmpty()) {
                    StringTokenizer tokenizer = new StringTokenizer(tmp, "#");
                    sym.paramTypeList = new ArrayList<String>();
                    while(tokenizer.hasMoreTokens()) {
                        sym.paramTypeList.add(restore(tokenizer.nextToken()));
                    }
                }
                ret.add(sym);
            }
        }catch (SQLException e) {
            return null;
        }
        if(ret.size() > 0)
            return ret.toArray(new Symbol[ret.size()]);
        else
            return null;
    }
    
    /**
     * get name of table which contain the symbol 
     * @param tableNames name array of tables, if null, then search all tables
     * @param symName symbol name
     */
    public String getTableName(String[] tableNames, String symName) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return "";
        }
        
       if(symName == null || symName.isEmpty()) {
           System.err.println("jdbc:invalid parameter");
           return "";
       }
        
        String ret = "";
        DatabaseMetaData meta;
        try {
            if(tableNames == null) {
                meta = conn.getMetaData();
                ResultSet res = meta.getTables(null, null, null, new String[] {"table"});
                int count = 0;
                while (res.next()) {
                    count ++;
                }
                tableNames = new String[count];
                res.first();
                res.previous();
                count = 0;
                while (res.next()) {
                    tableNames[count++] = (res.getString("table_name"));
                }
            }
            
            if(tableNames.length == 0) {
                return "";
            }
            
            for(int i = 0; i < tableNames.length; i++) {
                if(retrive(tableNames[i], symName) != null) {
                    ret = tableNames[i];
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * clear contents of all tables
     */
    public boolean clearAllTables() {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return false;
        }

        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, null, new String[] {"table"});
            while (res.next()) {
                clearTable(res.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * clear specified table
     * @param tabName
     */
    public boolean clearTable(String tabName) {
        if(conn == null || stmt == null) {
            System.err.println(JDBC_NOT_CONNECT);
            return false;
        }
        
        if(tabName == null || tabName.isEmpty()) {
            System.err.println(INVALID_TABLE_NAME);
            return false;
        }
        tabName = normalize(tabName);
        
        try {
            stmt.executeUpdate("drop table " + tabName + ";");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
   
    /**
     * disconnect jdbc
     */
    public void exit() {
        try {
            if(stmt != null)
                stmt.close();
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    //////////////////////////test////////////////////////////////
    static int SUBTYPE = 92;
    static int FUNCTION = 31;
    static final Symbol[] std_logic_1164_syms = 
    {
        //subtype
        new Symbol("x01", SUBTYPE, "std_ulogic", new String[]{"X", "to", "1"}),
        new Symbol("x01Z", SUBTYPE, "std_ulogic", new String[]{"X", "to", "Z"}),
        new Symbol("ux01", SUBTYPE, "std_ulogic", new String[]{"U", "to", "1"}),
        new Symbol("ux01z", SUBTYPE, "std_ulogic", new String[]{"U", "to", "Z"}),
        
        // function
        new Symbol("to_bit", FUNCTION, "bit"),
        new Symbol("to_bitvector", FUNCTION, "bit_vector"),
        new Symbol("to_stdULogic", FUNCTION, "std_ulogic"),
        new Symbol("to_stdLogicVector", FUNCTION, "std_logic_vector"),
        new Symbol("to_stdULogicVector", FUNCTION, "std_ulogic_vector"),
        new Symbol("to_x01", FUNCTION, "x01"),
        new Symbol("to_x01z", FUNCTION, "x01z"),
        new Symbol("to_ux01", FUNCTION, "ux01"),
        new Symbol("rising_edge", FUNCTION, "boolean"),
        new Symbol("falling_edge", FUNCTION, "boolean"),
        new Symbol("is_x", FUNCTION, "boolean"),
    };
    
    public static void main(String[] args) {
        VhdlDataBase db = new VhdlDataBase();
        String tabName = "grlib#amba";
        //String tabName = "grlib";
        db.init();
        db.clearAllTables();
        //db.clearTable(tabName);
        db.newTable(tabName, false);
        
        // insert symbols
        for(int i = 0; i < std_logic_1164_syms.length; i++) {
            db.insert(tabName, std_logic_1164_syms[i]);
        }
        
        // Retrieve
        for(int i = 0; i < std_logic_1164_syms.length; i++) {
            Symbol[] sym = db.retrive(tabName, std_logic_1164_syms[i].name);
        }
        //db.clearTable(tabName);
        db.exit();
    }
}
