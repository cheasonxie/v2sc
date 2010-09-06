/*
 * reg_uint: inheritance of sc_signal and sc_uint_base
 * operate the same as sc_uint, but can be used as signal
 */

#ifndef __REG_UINT_H__
#define __REG_UINT_H__

#include <systemc.h>

using sc_dt::sc_generic_base;
using sc_dt::sc_uint_subref_r;
using sc_dt::uint_type;

template<int W>
class reg_uint : public sc_signal<sc_uint<W> >, public sc_uint_base
{
public:

    // constructors

    reg_uint()
    : sc_uint_base( W )
    {}

    reg_uint( uint_type v )
    : sc_uint_base( v, W )
    {}

    reg_uint( const reg_uint<W>& a )
    : sc_uint_base( a )
    {}

    reg_uint( const sc_uint_base& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( const sc_uint_subref_r& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    template< class T >
    reg_uint( const sc_generic_base<T>& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( const sc_signed& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( const sc_unsigned& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

#ifdef SC_INCLUDE_FX

    explicit reg_uint( const sc_fxval& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    explicit reg_uint( const sc_fxval_fast& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    explicit reg_uint( const sc_fxnum& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    explicit reg_uint( const sc_fxnum_fast& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

#endif

    reg_uint( const sc_bv_base& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( const sc_lv_base& a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( const char* a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( unsigned long a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( long a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( unsigned int a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( int a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( int64 a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }

    reg_uint( double a )
    : sc_uint_base( W )
    { sc_uint_base::operator = ( a ); }


    // assignment operators

    reg_uint<W>& operator = ( uint_type v )
    { sc_uint_base::operator = ( v ); return *this; }

    reg_uint<W>& operator = ( const sc_uint_base& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_uint_subref_r& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const reg_uint<W>& a )
    { m_val = a.m_val; return *this; }

    template<class T>
    reg_uint<W>& operator = ( const sc_generic_base<T>& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_signed& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_unsigned& a )
    { sc_uint_base::operator = ( a ); return *this; }

#ifdef SC_INCLUDE_FX

    reg_uint<W>& operator = ( const sc_fxval& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_fxval_fast& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_fxnum& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_fxnum_fast& a )
    { sc_uint_base::operator = ( a ); return *this; }

#endif

    reg_uint<W>& operator = ( const sc_bv_base& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const sc_lv_base& a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( const char* a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( unsigned long a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( long a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( unsigned int a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( int a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( int64 a )
    { sc_uint_base::operator = ( a ); return *this; }

    reg_uint<W>& operator = ( double a )
    { sc_uint_base::operator = ( a ); return *this; }


    // arithmetic assignment operators

    reg_uint<W>& operator += ( uint_type v )
    { sc_uint_base::operator += ( v ); return *this; }

    reg_uint<W>& operator -= ( uint_type v )
    { sc_uint_base::operator -= ( v ); return *this; }

    reg_uint<W>& operator *= ( uint_type v )
    { sc_uint_base::operator *= ( v ); return *this; }

    reg_uint<W>& operator /= ( uint_type v )
    { sc_uint_base::operator /= ( v ); return *this; }

    reg_uint<W>& operator %= ( uint_type v )
    { sc_uint_base::operator %= ( v ); return *this; }


    // bitwise assignment operators

    reg_uint<W>& operator &= ( uint_type v )
    { sc_uint_base::operator &= ( v ); return *this; }

    reg_uint<W>& operator |= ( uint_type v )
    { sc_uint_base::operator |= ( v ); return *this; }

    reg_uint<W>& operator ^= ( uint_type v )
    { sc_uint_base::operator ^= ( v ); return *this; }


    reg_uint<W>& operator <<= ( uint_type v )
    { sc_uint_base::operator <<= ( v ); return *this; }

    reg_uint<W>& operator >>= ( uint_type v )
    { sc_uint_base::operator >>= ( v ); return *this; }


    // prefix and postfix increment and decrement operators

    reg_uint<W>& operator ++ () // prefix
    { sc_uint_base::operator ++ (); return *this; }

    const reg_uint<W> operator ++ ( int ) // postfix
    { return reg_uint<W>( sc_uint_base::operator ++ ( 0 ) ); }

    reg_uint<W>& operator -- () // prefix
    { sc_uint_base::operator -- (); return *this; }

    const reg_uint<W> operator -- ( int ) // postfix
    { return reg_uint<W>( sc_uint_base::operator -- ( 0 ) ); }

    // implement signal trace
    inline friend void sc_trace(sc_trace_file *tf, const reg_uint<W> & v,
            const std::string& name )
    {
        sc_trace(tf, v.m_val, name + ".m_val");
    }

    // implement interface of signal
    // write the new value
    virtual void write( uint_type a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}

    virtual void write( const sc_uint_base& a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}

    virtual void write( const sc_uint_subref_r& a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}

    virtual void write( const reg_uint<W>& a )
    { sc_signal<sc_uint<W> >::write(a); m_val = a.m_val;}

    template<class T>
    void write( const sc_generic_base<T>& a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}

    virtual void write( const sc_signed& a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}

    virtual void write( const sc_unsigned& a )
    { sc_signal<sc_uint<W> >::write(a); sc_uint_base::operator = ( a );}
};

#endif /* __REG_UINT_H__ */
