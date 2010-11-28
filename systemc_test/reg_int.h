/*
 * reg_int: inheritance of sc_signal and sc_int_base
 * operate the same as sc_int, but can be used as signal
 */

#ifndef __REG_INT_H__
#define __REG_INT_H__

#include <systemc.h>

template<int W>
class reg_int : public sc_signal<sc_int<W> >
{
};

#endif /* __REG_INT_H__ */
