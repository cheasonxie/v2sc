/*
 * reg_bool: inheritance of sc_signal and sc_concat_bool
 * operate the same as bool, but can be used as signal
 */

#ifndef __REG_BOOL_H__
#define __REG_BOOL_H__

#include <systemc.h>

using sc_dt::sc_concat_bool;

class reg_bool : public sc_signal<bool>, public sc_concat_bool
{
public:

    // constructors
    reg_bool() {m_value = 0;}

    reg_bool( bool v ) {m_value = v; update_signal();}

    reg_bool( const reg_bool& a )
    {m_value = a.m_value; update_signal();}


    // assignment operators
    reg_bool& operator = ( const reg_bool& a )
    { m_value = a.m_value; update_signal(); return *this; }

    reg_bool& operator = ( bool a )
    { m_value = a; update_signal(); return *this; }


    // bitwise assignment operators
    reg_bool& operator &= ( bool v )
    { m_value &= v; update_signal(); return *this; }

    reg_bool& operator &= ( reg_bool v )
    { m_value &= v.m_value; update_signal(); return *this; }

    reg_bool& operator |= ( bool v )
    { m_value |= v; update_signal(); return *this; }

    reg_bool& operator |= ( reg_bool v )
    { m_value |= v.m_value; update_signal(); return *this; }

    reg_bool& operator ^= ( bool v )
    { m_value ^= v; update_signal(); return *this; }

    reg_bool& operator ^= ( reg_bool v )
    { m_value ^= v.m_value; update_signal(); return *this; }

    // implement signal trace
    inline friend void sc_trace(sc_trace_file *tf, const reg_bool & v,
            const std::string& name )
    {
        sc_trace(tf, v.m_value, name + ".m_value");
    }

    // implement interface of signal
    // write the new value
    virtual void write( reg_bool& a )
    { reg_bool::operator = (a); update_signal();}

    virtual void write( const bool& a )
    { reg_bool::operator = (a); update_signal();}

    // iostream
    void print( ::std::ostream& os = ::std::cout ) const
    {
        os << m_value;
    }
    void scan( ::std::istream& is = ::std::cin )
    {
        std::string s;
        is >> s;
        *this = s.c_str();
    }

protected:

    void update_signal() {sc_signal<bool>::write(m_value);}
};

inline
::std::ostream&
operator << ( ::std::ostream& os, const reg_bool& a )
{
    a.print( os );
    return os;
}

inline
::std::istream&
operator >> ( ::std::istream& is, reg_bool& a )
{
    a.scan( is );
    return is;
}

#endif /* __REG_BOOL_H__ */
