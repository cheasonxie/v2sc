package parser.verilog;

/**
 * compiler directives
 */
public interface CompilerDirectives
{
    static final String CD_DEFINE               = "`define";
    static final String CD_DEFAULT_NETTYPE      = "`default_nettype";
    static final String CD_UNCONNECTED_DRIVE    = "`unconnected_drive";
    static final String CD_NONUNCONNECTED_DRIVE = "`nounconnected_drive";
    static final String CD_RESETALL             = "`resetall";
    static final String CD_TIMESCALE            = "`timescale";
}
