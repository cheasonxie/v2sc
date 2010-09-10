package parser.vhdl;

class PrePkg
{
    String libName = "";
    String pkgName = "";
    Symbol[] syms = null;
    public PrePkg(String libName, String pkgName, Symbol[] syms) {
        this.libName = libName;
        this.pkgName = pkgName;
        this.syms = syms;
    }
}

/**
 * some standard symbols(type, subtype, function, ...)
 */
public class PredefinedPackage implements VhdlTokenConstants
{
    // library name
    static final String IEEE = "ieee";
    static final String STD = "std";
    
    // package name
    static final String STD_LOGIC_1164 = "std_logic_1164";
    static final String TEXTIO = "textio";
    static final String NUMERIC_BIT = "numeric_bit";
    static final String NUMERIC_STD = "numeric_std";
    static final String MATH_REAL = "math_real";
    static final String MATH_COMPLEX = "math_complex";
    
    static final Symbol[] standard_syms = 
    {
        new Symbol("severity_level", TYPE),
    };
    
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
    
    static final Symbol[] textio_syms = 
    {
        new Symbol("line", TYPE, "string"),
        new Symbol("text", TYPE, "string"),
        new Symbol("side", TYPE),
        new Symbol("width", SUBTYPE, "natural"),
        new Symbol("input", FILE, "text"),
        new Symbol("output", FILE, "text"),
        new Symbol("read", PROCEDURE),
        new Symbol("write", PROCEDURE),
        new Symbol("writeline", PROCEDURE),
        new Symbol("readline", PROCEDURE),
    };
    
    static final Symbol[] numeric_bit_syms =
    {
        new Symbol("shift_left", FUNCTION),
        new Symbol("shift_right", FUNCTION),
        new Symbol("rotate_left", FUNCTION),
        new Symbol("rotate_right", FUNCTION),
        new Symbol("resize", FUNCTION),
        new Symbol("to_integer", FUNCTION),
        new Symbol("to_unsigned", FUNCTION),
        new Symbol("to_signed", FUNCTION),
        new Symbol("rising_edge", FUNCTION, "boolean"),
        new Symbol("falling_edge", FUNCTION, "boolean"),
    };
    
    static final Symbol[] numeric_std_syms =
    {
        // part of std_logic_1164
        new Symbol("x01", SUBTYPE, "std_ulogic", new String[]{"X", "to", "1"}),
        new Symbol("x01Z", SUBTYPE, "std_ulogic", new String[]{"X", "to", "Z"}),
        new Symbol("ux01", SUBTYPE, "std_ulogic", new String[]{"U", "to", "1"}),
        new Symbol("ux01z", SUBTYPE, "std_ulogic", new String[]{"U", "to", "Z"}),
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
        
        // addition in numeric_std
        new Symbol("shift_left", FUNCTION),
        new Symbol("shift_right", FUNCTION),
        new Symbol("rotate_left", FUNCTION),
        new Symbol("rotate_right", FUNCTION),
        new Symbol("resize", FUNCTION),
        new Symbol("to_integer", FUNCTION),
        new Symbol("to_unsigned", FUNCTION),
        new Symbol("to_signed", FUNCTION),
        new Symbol("std_match", FUNCTION),
        new Symbol("to_01", FUNCTION),
    };
    
    static final Symbol[] math_real_syms =
    {
        // constant
        new Symbol("MATH_E", CONSTANT, "real"),          //2.71828_18284_59045_23536
        new Symbol("MATH_1_OVER_E", CONSTANT, "real"),    //0.36787_94411_71442_32160
        new Symbol("MATH_PI",  CONSTANT, "real"),         //3.14159_26535_89793_23846
        new Symbol("MATH_2_PI", CONSTANT, "real"),        //6.28318_53071_79586_47693
        new Symbol("MATH_1_OVER_PI", CONSTANT, "real"),   //0.31830_98861_83790_67154
        new Symbol("MATH_PI_OVER_2", CONSTANT, "real"),   //1.57079_63267_94896_61923
        new Symbol("MATH_PI_OVER_3", CONSTANT, "real"),   //1.04719_75511_96597_74615
        new Symbol("MATH_PI_OVER_4", CONSTANT, "real"),   //0.78539_81633_97448_30962
        new Symbol("MATH_3_PI_OVER_2", CONSTANT, "real"), //4.71238_89803_84689_85769
        new Symbol("MATH_LOG_OF_2", CONSTANT, "real"),   //0.69314_71805_59945_30942
        new Symbol("MATH_LOG_OF_10", CONSTANT, "real"),   //2.30258_50929_94045_68402
        new Symbol("MATH_LOG2_OF_E", CONSTANT, "real"),   //1.44269_50408_88963_4074
        new Symbol("MATH_LOG10_OF_E", CONSTANT, "real"),  //0.43429_44819_03251_82765
        new Symbol("MATH_SQRT_2", CONSTANT, "real"),      //1.41421_35623_73095_04880
        new Symbol("MATH_1_OVER_SQRT_2", CONSTANT, "real"),    //0.70710_67811_86547_52440
        new Symbol("MATH_SQRT_PI", CONSTANT, "real"),     //1.77245_38509_05516_02730
        new Symbol("MATH_DEG_TO_RAD", CONSTANT, "real"),  //0.01745_32925_19943_29577
        new Symbol("MATH_RAD_TO_DEG", CONSTANT, "real"),  //57.29577_95130_82320_87680
        
        // function
        new Symbol("sign", FUNCTION),
        new Symbol("ceil", FUNCTION),
        new Symbol("floor", FUNCTION),
        new Symbol("round", FUNCTION),
        new Symbol("trunc", FUNCTION),
        new Symbol("realmax", FUNCTION),
        new Symbol("realmin", FUNCTION),
        new Symbol("uniform", PROCEDURE),
        new Symbol("sort", FUNCTION),
        new Symbol("cbrt", FUNCTION),
        new Symbol("log", FUNCTION),
        new Symbol("log2", FUNCTION),
        new Symbol("log10", FUNCTION),
        new Symbol("sin", FUNCTION),
        new Symbol("cos", FUNCTION),
        new Symbol("tan", FUNCTION),
        new Symbol("arcsin", FUNCTION),
        new Symbol("arccos", FUNCTION),
        new Symbol("arctan", FUNCTION),
        new Symbol("sinh", FUNCTION),
        new Symbol("cosh", FUNCTION),
        new Symbol("tanh", FUNCTION),
        new Symbol("arcsinh", FUNCTION),
        new Symbol("arccosh", FUNCTION),
        new Symbol("arctanh", FUNCTION),
    };
    
    static final Symbol[] math_complex_syms = 
    {
        new Symbol("complex", TYPE, "record"), // record
        
        new Symbol("positive_real", SUBTYPE, "real"),
        new Symbol("principal_value", SUBTYPE, "real"),
        new Symbol("complex_polar", TYPE, "record"),   // record
        new Symbol("MATH_CBASE_1", CONSTANT, "complex"),
        new Symbol("MATH_CBASE_J", CONSTANT, "complex"),
        new Symbol("MATH_CZERO", CONSTANT, "complex"),
        
        new Symbol("cmplx", FUNCTION),
        new Symbol("get_pricipal_value", FUNCTION),
        new Symbol("complex_to_polar", FUNCTION),
        new Symbol("polar_to_complex", FUNCTION),
        new Symbol("arg", FUNCTION),
        new Symbol("conj", FUNCTION),
        new Symbol("sort", FUNCTION),
        new Symbol("log", FUNCTION),
        new Symbol("log2", FUNCTION),
        new Symbol("log10", FUNCTION),
        new Symbol("sin", FUNCTION),
        new Symbol("cos", FUNCTION),
        new Symbol("tan", FUNCTION),
        new Symbol("sinh", FUNCTION),
        new Symbol("cosh", FUNCTION),
        new Symbol("tanh", FUNCTION),
    };
    
    static final PrePkg pkg_std_logic_1164 = new PrePkg(IEEE, STD_LOGIC_1164, std_logic_1164_syms);
    static final PrePkg pkg_textio = new PrePkg(STD, TEXTIO, textio_syms);
    static final PrePkg pkg_numeric_bit = new PrePkg(IEEE, NUMERIC_BIT, numeric_bit_syms);
    static final PrePkg pkg_numeric_std = new PrePkg(IEEE, NUMERIC_STD, numeric_std_syms);
    static final PrePkg pkg_math_real = new PrePkg(IEEE, MATH_REAL, math_real_syms);
    static final PrePkg pkg_math_complex = new PrePkg(IEEE, MATH_COMPLEX, math_real_syms);
    
    static final PrePkg[] predefined_pkgs = 
    {
        pkg_std_logic_1164,
        pkg_textio,
        pkg_numeric_bit,
        pkg_numeric_std,
        pkg_math_real,
        pkg_math_complex,
    };
}
