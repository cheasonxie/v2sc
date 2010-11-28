/*
 * reg_uint: inheritance of sc_signal and sc_uint_base
 * operate the same as sc_uint, but can be used as signal
 */

#ifndef __REG_UINT_H__
#define __REG_UINT_H__

#include <systemc.h>

using sc_dt::sc_uint_base;
using sc_dt::sc_generic_base;
using sc_dt::sc_uint_subref_r;
using sc_dt::uint_type;

template<int W>
class reg_uint : public sc_signal<sc_uint<W> >
{
public:

    // constructors

    reg_uint()
        : sc_signal<sc_uint<W> >()
    {}

    reg_uint( uint_type v )
        : sc_signal<sc_uint<W> >()
    {sc_signal<sc_uint<W> >::write(v);}

    reg_uint( const reg_uint<W>& a )
        : sc_signal<sc_uint<W> >()
    {sc_signal<sc_uint<W> >::write(a);}


    // assignment operators

    reg_uint<W>& operator = ( uint_type v )
    { sc_signal<sc_uint<W> >::write(v); return *this; }

    reg_uint<W>& operator = ( const sc_uint_base& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_uint_subref_r& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const reg_uint<W>& a )
    { sc_signal<sc_uint<W> >::write(a.read()); return *this; }

    template<class T>
    reg_uint<W>& operator = ( const sc_generic_base<T>& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_signed& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_unsigned& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

#ifdef SC_INCLUDE_FX

    reg_uint<W>& operator = ( const sc_fxval& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_fxval_fast& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_fxnum& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_fxnum_fast& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

#endif

    reg_uint<W>& operator = ( const sc_bv_base& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const sc_lv_base& a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( const char* a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( unsigned long a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( long a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( unsigned int a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( int a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( int64 a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }

    reg_uint<W>& operator = ( double a )
    { sc_signal<sc_uint<W> >::write(a); return *this; }


    // arithmetic assignment operators

    reg_uint<W>& operator += ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value+v); return *this; }

    reg_uint<W>& operator -= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value-v); return *this; }

    reg_uint<W>& operator *= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value*v); return *this; }

    reg_uint<W>& operator /= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value/v); return *this; }

    reg_uint<W>& operator %= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value%v); return *this; }


    // bitwise assignment operators

    reg_uint<W>& operator &= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value&v); return *this; }

    reg_uint<W>& operator |= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value|v); return *this; }

    reg_uint<W>& operator ^= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value^v); return *this; }


    reg_uint<W>& operator <<= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value<<v); return *this; }

    reg_uint<W>& operator >>= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value>>v); return *this; }


    // prefix and postfix increment and decrement operators

    reg_uint<W>& operator ++ () // prefix
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value++); return *this; }

    const reg_uint<W> operator ++ ( int ) // postfix
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(++value); return *this; }

    reg_uint<W>& operator -- () // prefix
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(value--); return *this; }

    const reg_uint<W> operator -- ( int ) // postfix
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_signal<sc_uint<W> >::write(--value); return *this; }

    // compare operators
    const bool operator == (reg_uint<W> &v)
    { return (sc_signal<sc_uint<W> >::read() == v.read()); }

    const bool operator == (uint_type v)
    { return (sc_signal<sc_uint<W> >::read().to_int() == v); }

    // convert to primitive type
    int to_int() const
    { return sc_signal<sc_uint<W> >::read().to_int(); }

    unsigned int to_uint() const
    { return sc_signal<sc_uint<W> >::read().to_uint(); }

    long to_long() const
    { return sc_signal<sc_uint<W> >::read().to_long(); }

    unsigned long to_ulong() const
    { return sc_signal<sc_uint<W> >::read().to_ulong(); }

    int64 to_int64() const
    { return sc_signal<sc_uint<W> >::read().to_int64(); }

    uint64 to_uint64() const
    { return sc_signal<sc_uint<W> >::read().to_uint64(); }

    double to_double() const
    { return sc_signal<sc_uint<W> >::read().to_double(); }

};

#endif /* __REG_UINT_H__ */
