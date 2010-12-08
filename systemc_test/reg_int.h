/*
 * reg_int: inheritance of sc_signal and sc_uint_base
 * operate the same as sc_int, but can be used as signal
 */

#ifndef __REG_INT_H__
#define __REG_INT_H__

#include <systemc.h>

using namespace sc_dt;

class reg_int : public sc_signal<int>
{
public:

    // constructors

    reg_int()
        : sc_signal<int>()
    { }

    reg_int( uint_type v )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = v; }

    reg_int( const reg_int& a )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = a.read(); }

    reg_int( unsigned int a )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = (int)a; }

    reg_int( int a )
        : sc_signal<int>()
    { sc_signal<int>::m_cur_val = a; }



    // assignment operators

    reg_int& operator = ( uint_type v )
    { sc_signal<int>::m_cur_val = v; return *this; }

    reg_int& operator = ( unsigned long a )
    { sc_signal<int>::m_cur_val = a; return *this; }

    reg_int& operator = ( long a )
    { sc_signal<int>::m_cur_val = a; return *this; }

    reg_int& operator = ( unsigned int a )
    { sc_signal<int>::m_cur_val = a; return *this; }

    reg_int& operator = ( int a )
    { sc_signal<int>::m_cur_val = a; return *this; }

    reg_int& operator = ( int64 a )
    { sc_signal<int>::m_cur_val = a; return *this; }

    reg_int& operator = ( double a )
    { sc_signal<int>::m_cur_val = (int)a; return *this; }


    // arithmetic assignment operators

    reg_int& operator += ( uint_type v )
    { sc_signal<int>::m_cur_val += v; return *this; }

    reg_int& operator -= ( uint_type v )
    { sc_signal<int>::m_cur_val -= v; return *this; }

    reg_int& operator *= ( uint_type v )
    { sc_signal<int>::m_cur_val *= v; return *this; }

    reg_int& operator /= ( uint_type v )
    { sc_signal<int>::m_cur_val /= v; return *this; }

    reg_int& operator %= ( uint_type v )
    { sc_signal<int>::m_cur_val %= v; return *this; }


    // bitwise assignment operators

    reg_int& operator &= ( uint_type v )
    { sc_signal<int>::m_cur_val &= v; return *this; }

    reg_int& operator |= ( uint_type v )
    { sc_signal<int>::m_cur_val |= v; return *this; }

    reg_int& operator ^= ( uint_type v )
    { sc_signal<int>::m_cur_val ^= v; return *this; }


    reg_int& operator <<= ( uint_type v )
    { sc_signal<int>::m_cur_val <<= v; return *this; }

    reg_int& operator >>= ( uint_type v )
    { sc_signal<int>::m_cur_val >>= v; return *this; }


    // prefix and postfix increment and decrement operators

    int operator ++ () // prefix
    { return sc_signal<int>::m_cur_val ++; }

    const int operator ++ ( int ) // postfix
    { return ++ sc_signal<int>::m_cur_val; }

    int operator -- () // prefix
    { return sc_signal<int>::m_cur_val --; }

    const int operator -- ( int ) // postfix
    { return -- sc_signal<int>::m_cur_val; }

    // relational operators

    friend bool operator == ( const reg_int& a, const reg_int& b )
    { return a.read() == b.read(); }
    friend bool operator == ( const reg_int& a, const int& b )
    { return a.read() == b; }
    friend bool operator == ( const int& a, const reg_int& b )
    { return a == b.read(); }

    friend bool operator != ( const reg_int& a, const reg_int& b )
    { return a.read() != b.read(); }
    friend bool operator != ( const reg_int& a, const int& b )
    { return a.read() != b; }
    friend bool operator != ( const int& a, const reg_int& b )
    { return a != b.read(); }

    friend bool operator <  ( const reg_int& a, const reg_int& b )
    { return a.read() < b.read(); }
    friend bool operator <  ( const reg_int& a, const int& b )
    { return a.read() < b; }
    friend bool operator <  ( const int& a, const reg_int& b )
    { return a < b.read(); }

    friend bool operator <= ( const reg_int& a, const reg_int& b )
    { return a.read() <= b.read(); }
    friend bool operator <= ( const reg_int& a, const int& b )
    { return a.read() <= b; }
    friend bool operator <= ( const int& a, const reg_int& b )
    { return a <= b.read(); }

    friend bool operator >  ( const reg_int& a, const reg_int& b )
    { return a.read() > b.read(); }
    friend bool operator >  ( const reg_int& a, const int& b )
    { return a.read() > b; }
    friend bool operator >  ( const int& a, const reg_int& b )
    { return a > b.read(); }

    friend bool operator >= ( const reg_int& a, const reg_int& b )
    { return a.read() >= b.read(); }
    friend bool operator >= ( const reg_int& a, const int& b )
    { return a.read() >= b; }
    friend bool operator >= ( const int& a, const reg_int& b )
    { return a >= b.read(); }

    int to_int() const
    { return sc_signal<int>::m_cur_val; }
};

inline
const
sc_dt::sc_concatref& operator , (const reg_int& a, const reg_int& b)
{
    sc_int<32> a_i(a.read());   // integer always 32 bits
    sc_int<32> b_i(b.read());   // integer always 32 bits
    sc_dt::sc_concatref*    result_p; // Proxy for the concatenation.

    result_p = sc_dt::sc_concatref::m_pool.allocate();
    result_p->initialize( a_i, b_i );
    return *result_p;
}

#endif /* __REG_INT_H__ */
