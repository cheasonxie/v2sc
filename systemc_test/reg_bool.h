/*
 * reg_bool: inheritance of sc_signal and sc_concat_bool
 * operate the same as bool, but can be used as signal
 */

#ifndef __REG_BOOL_H__
#define __REG_BOOL_H__

#include <systemc.h>

class reg_bool : public sc_signal<bool>
{
};

#endif /* __REG_BOOL_H__ */
