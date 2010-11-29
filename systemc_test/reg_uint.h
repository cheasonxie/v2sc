/*
 * reg_uint: inheritance of sc_signal
 * operate the same as sc_uint, but can be used as signal
 */

#ifndef __REG_UINT_H__
#define __REG_UINT_H__

#include <systemc.h>

using namespace sc_dt;

template<int W>
class reg_uint : public sc_signal<sc_uint<W> >
{
public:

    // constructors

    reg_uint()
        : sc_signal<sc_uint<W> >()
    { }

    reg_uint( uint_type v )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::write(v); }

    reg_uint( const reg_uint<W>& a )
        : sc_signal<sc_uint<W> >()
    { sc_signal<sc_uint<W> >::write(a); }


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
        value += v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator -= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value -= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator *= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value *= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator /= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value /= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator %= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value %= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }


    // bitwise assignment operators

    reg_uint<W>& operator &= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value &= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator |= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value |= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator ^= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value ^= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }


    reg_uint<W>& operator <<= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value <<= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }

    reg_uint<W>& operator >>= ( uint_type v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value >>= v;
        sc_signal<sc_uint<W> >::write(value); return *this; }


    // prefix and postfix increment and decrement operators

    sc_uint<W>& operator ++ () // prefix
    {
        sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_uint<W> value1(value);
        ++value;
        sc_signal<sc_uint<W> >::write(value);
        return value1;
    }

    const sc_uint<W> operator ++ ( int ) // postfix
    {
        sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_uint<W> value1(value);
        value++;
        sc_signal<sc_uint<W> >::write(value);
        return value;
    }

    sc_uint<W>& operator -- () // prefix
    {
        sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_uint<W> value1(value);
        --value;
        sc_signal<sc_uint<W> >::write(value); return value1;
    }

    const sc_uint<W> operator -- ( int ) // postfix
    {
        sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        sc_uint<W> value1(value);
        value--;
        sc_signal<sc_uint<W> >::write(value);
        return value1;
    }

    // relational operators

    friend bool operator == ( const reg_uint& a, const reg_uint& b )
    { return a.read() == b.read(); }

    friend bool operator != ( const reg_uint& a, const reg_uint& b )
    { return a.read() != b.read(); }

    friend bool operator <  ( const reg_uint& a, const reg_uint& b )
    { return a.read() < b.read(); }

    friend bool operator <= ( const reg_uint& a, const reg_uint& b )
    { return a.read() <= b.read(); }

    friend bool operator >  ( const reg_uint& a, const reg_uint& b )
    { return a.read() > b.read(); }

    friend bool operator >= ( const reg_uint& a, const reg_uint& b )
    { return a.read() >= b.read(); }


    // bit selection

    sc_uint_bitref&         operator [] ( int i )
    { sc_signal<sc_uint<W> >::request_update();
        return sc_signal<sc_uint<W> >::m_new_val[i]; }

    const sc_uint_bitref_r& operator [] ( int i ) const
    { return sc_signal<sc_uint<W> >::read()[i];}

    sc_uint_bitref&         bit( int i )
    { sc_signal<sc_uint<W> >::request_update();
        return sc_signal<sc_uint<W> >::m_new_val.bit(i); }

    const sc_uint_bitref_r& bit( int i ) const
    { return sc_signal<sc_uint<W> >::read().bit(i); }


    // part selection

    sc_uint_subref&         operator () ( int left, int right )
    { sc_signal<sc_uint<W> >::request_update();
        return sc_signal<sc_uint<W> >::m_new_val(left, right); }

    const sc_uint_subref_r& operator () ( int left, int right ) const
    { return sc_signal<sc_uint<W> >::read()(left, right); }

    sc_uint_subref&         range( int left, int right )
    { sc_signal<sc_uint<W> >::request_update();
        return sc_signal<sc_uint<W> >::m_new_val.range(left, right); }

    const sc_uint_subref_r& range( int left, int right ) const
    { return sc_signal<sc_uint<W> >::read().range(left, right); }


    // bit access, without bounds checking or sign extension

    bool test( int i ) const
    { return sc_signal<sc_uint<W> >::read().test(i); }

    void set( int i )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value.set(i);
        sc_signal<sc_uint<W> >::write(value); }

    void set( int i, bool v )
    { sc_uint<W> value = sc_signal<sc_uint<W> >::read();
        value.set(i, v);
        sc_signal<sc_uint<W> >::write(value); }


    // capacity

    int length() const
    { return sc_signal<sc_uint<W> >::read().length(); }

    // reduce methods

    bool and_reduce() const
    { return sc_signal<sc_uint<W> >::read().and_reduce(); }

    bool nand_reduce() const
    { return ( ! and_reduce() ); }

    bool or_reduce() const
    { return sc_signal<sc_uint<W> >::read().or_reduce(); }

    bool nor_reduce() const
    { return ( ! or_reduce() ); }

    bool xor_reduce() const
    { return sc_signal<sc_uint<W> >::read().xor_reduce(); }

    bool xnor_reduce() const
    { return ( ! xor_reduce() ); }


    // implicit conversion to uint_type
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
