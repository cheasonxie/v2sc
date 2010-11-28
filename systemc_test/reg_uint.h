/*
 * reg_uint: inheritance of sc_signal and sc_uint_base
 * operate the same as sc_uint, but can be used as signal
 */

#ifndef __REG_UINT_H__
#define __REG_UINT_H__

#include <systemc.h>

template<int W>
class reg_uint : public sc_signal<sc_uint<W> >
{
};

#endif /* __REG_UINT_H__ */
