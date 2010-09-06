
#include <systemc.h>
#include "reg_uint.h"
#include "reg_bool.h"

using sc_dt::sc_uint_bitref;

//typedef reg_uint<1> reg_bool;

struct ahb_dma_in_type
{
    reg_uint<32>    address;
    reg_uint<32>    wdata;
    reg_bool        start;
    reg_bool        burst;
    reg_bool        write;
    reg_bool        busy;
    reg_bool        irq;
    reg_uint<2>     size;

    inline bool operator == (const ahb_dma_in_type& v) const
    {
        return ((address == v.address)
                && (wdata == v.wdata)
                && (start == v.start)
                && (burst == v.burst)
                && (write == v.write)
                && (busy == v.busy)
                && (irq == v.irq)
                && (size == v.size));
    }

    inline bool operator != (const ahb_dma_in_type& v) const
    {
        return (! operator == (v));
    }

    inline ahb_dma_in_type& operator = (const ahb_dma_in_type& v)
    {
        address = v.address;
        wdata = v.wdata;
        start = v.start;
        burst = v.burst;
        write = v.write;
        busy = v.busy;
        irq = v.irq;
        size = v.size;
        return *this;
    }

    inline friend void sc_trace(sc_trace_file *tf, const ahb_dma_in_type& v,
            const std::string& name)
    {
        sc_trace(tf, v.address, name + ".address");
        sc_trace(tf, v.wdata, name + ".wdata");
        sc_trace(tf, v.start, name + ".start");
        sc_trace(tf, v.burst, name + ".burst");
        sc_trace(tf, v.write, name + ".write");
        sc_trace(tf, v.busy, name + ".busy");
        sc_trace(tf, v.irq, name + ".irq");
        sc_trace(tf, v.size, name + ".size");
    }

    inline friend ostream& operator << (ostream& os, const ahb_dma_in_type & v)
    {
        os << "(" << std::dec << v.address << ", ";
        os << v.wdata << ", ";
        os << std::boolalpha << v.start << ", ";
        os << v.burst << ", ";
        os << v.write << ", ";
        os << v.busy << ", ";
        os << v.irq << ", ";
        os << std::dec << v.size << ")";
        return os;
    }
};

struct ahb_dma_out_type
{
    reg_bool        start;
    reg_bool        active;
    reg_bool        ready;
    reg_bool        retry;
    reg_bool        mexc;
    reg_uint<10>    haddr;
    reg_uint<32>    rdata;

    inline bool operator == (const ahb_dma_out_type& v) const
    {
        return ((start == v.start)
                && (active == v.active)
                && (ready == v.ready)
                && (retry == v.retry)
                && (mexc == v.mexc)
                && (haddr == v.haddr)
                && (rdata == v.rdata));
    }

    inline bool operator != (const ahb_dma_out_type& v) const
    {
        return (! operator == (v));
    }

    inline ahb_dma_out_type& operator = (const ahb_dma_out_type& v)
    {
        start = v.start;
        active = v.active;
        ready = v.ready;
        retry = v.retry;
        mexc = v.mexc;
        haddr = v.haddr;
        rdata = v.rdata;
        return *this;
    }

    inline friend void sc_trace(sc_trace_file *tf, const ahb_dma_out_type& v,
            const std::string& name)
    {
        sc_trace(tf, v.start, name + ".start");
        sc_trace(tf, v.active, name + ".active");
        sc_trace(tf, v.ready, name + ".ready");
        sc_trace(tf, v.retry, name + ".retry");
        sc_trace(tf, v.mexc, name + ".mexc");
        sc_trace(tf, v.haddr, name + ".haddr");
        sc_trace(tf, v.rdata, name + ".rdata");
    }

    inline friend ostream& operator << (ostream& os, const ahb_dma_out_type& v)
    {
        os << "(" << std::boolalpha << v.start << ", ";
        os << v.active << ", ";
        os << v.ready << ", ";
        os << v.retry << ", ";
        os << v.mexc << ", ";
        os << std::dec << v.haddr << ", ";
        os << v.rdata << ")";
        return os;
    }
};

SC_MODULE(ahbdma)
{
    sc_in<bool>             rst;
    sc_in<bool>             clk;
    sc_in<ahb_dma_in_type>  dmai;
    sc_out<ahb_dma_out_type> dmao;

    sc_signal<ahb_dma_out_type>    ldmao;
    void control()
    {
        ahb_dma_out_type vdmao;
        sc_uint<32> aaa = 10;
        vdmao = ldmao.read();

        if(rst == 1)
        {
            vdmao.active = 0;
            vdmao.haddr = 0;
        }
        else
        {
            if(dmai.read().irq == 1)
                vdmao.active = 1;
            if(dmai.read().start == 1)
                vdmao.rdata = dmai.read().address;
            if(dmai.read().address == 0xff)
                vdmao.mexc = 0;
        }

        ldmao.write(vdmao);
    }

    SC_CTOR(ahbdma)
    {
        SC_METHOD(control);
        sensitive << rst << clk.pos();
    }

};

SC_MODULE(counter32)
{
    sc_in<bool> clk;
    sc_in<bool> rst;
    sc_in<bool> load;
    sc_in<sc_uint<32> > d;
    sc_out<sc_uint<32> > q;

    sc_signal<sc_uint<32> > iq;

    void control()
    {
        sc_uint<32> iiq = iq.read();
        if(rst == 1)
        {
            iiq = 0;
        }
        else if(load)
        {
            iiq = d;
        }
        else
        {
            iiq ++;
        }
        iq.write(iiq);
    }

    void comb_assignement()
    {
        q.write(iq);
    }

    SC_CTOR(counter32)
    {
        SC_METHOD(control);
        sensitive << clk.pos() << rst << load;
    }
};

int sc_main(int argc, char *argv[])
{
    sc_signal<bool> clk, rst, load;
    sc_signal<sc_uint<32> >  d, q;
    sc_signal<ahb_dma_in_type> dmai;
    sc_signal<ahb_dma_out_type> dmao;
    reg_bool aaa;
    bool aaa0;
    reg_uint<32> bbb;
    sc_uint<32> ccc = 11;
    sc_uint<32> ddd = 22;
    sc_uint<32> eee;

    int a0 = 0;
    long a1 = 0;
    double a2 = 0;
    char a3 = 0;
    char *a4 = "a";

    bbb = ccc;
    bbb.write(ddd);
    d.write(ddd);
    d = ccc;
    eee = bbb.read();
    aaa = 1;
    aaa0 = 1;
    bbb.range(4, 0) = (aaa, aaa, 1, aaa, aaa);
    cout << "b[0]:" << bbb[0] << ", b[1]:" << bbb[1] << ", b[2]:" << bbb[2];
    cout << ", b[3]:" << bbb[3] << ", b[4]:" << bbb[4] << endl;
    bbb.range(4, 0) = (clk.read(), aaa0, aaa, aaa, 1);
    cout << "b[0]:" << bbb[0] << ", b[1]:" << bbb[1] << ", b[2]:" << bbb[2];
    cout << ", b[3]:" << bbb[3] << ", b[4]:" << bbb[4] << endl;
    ccc.range(4, 0) = ((reg_bool)aaa0, aaa, (reg_bool)aaa0, (reg_bool)aaa0,
                        (reg_bool)aaa0);
    cout << "c[0]:" << ccc[0] << ", c[1]:" << ccc[1] << ", c[2]:" << ccc[2];
    cout << ", c[3]:" << ccc[3] << ", c[4]:" << ccc[4] << endl;
    aaa = ccc[2];
    cout << "aaa:" << aaa << endl;
    aaa = bbb.range(2, 2);
    cout << "aaa:" << aaa << endl;
    aaa = a0;
    cout << "aaa:" << aaa << endl;
    aaa = a1;
    cout << "aaa:" << aaa << endl;
    aaa = a2;
    cout << "aaa:" << aaa << endl;
    aaa = a3;
    cout << "aaa:" << aaa << endl;
    aaa = a4;
    cout << "aaa:" << aaa << endl;

    cout << "bbb:" << bbb << ", bbb.read():" << bbb.read() << ", d:" << d << endl;
    cout << "ddd:" << ddd << endl;
    cout << "eee:" << eee << endl;

    counter32 cnt32("counter32");
    cnt32.clk(clk);
    cnt32.rst(rst);
    cnt32.load(load);
    cnt32.d(dmai.read().address);
    cnt32.q(bbb);

    ahbdma dma("ahbdma");
    dma.clk(aaa);
    dma.rst(dmai.read().irq);
    dma.dmai(dmai);
    dma.dmao(dmao);

    return 0;
}

