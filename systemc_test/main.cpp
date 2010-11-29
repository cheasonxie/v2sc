
#include <systemc.h>
#include "reg_uint.h"
#include "reg_int.h"
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
            vdmao.active.write(0);
            vdmao.haddr.write(0);
        }
        else
        {
            if(dmai.read().irq == (reg_bool)1)
                vdmao.active.write(1);
            if(dmai.read().start == (reg_bool)1)
                vdmao.rdata.write(dmai.read().address);
            if(dmai.read().address.read().to_int() == 0xff)
                vdmao.mexc.write(0);
        }

        ldmao.write(vdmao);

        ((ahb_dma_out_type &)dmao.read()).start.write((bool)0);
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

extern void testSigStrut();
extern void test_reg_uint();
extern void test_reg_int();
extern void test_reg_bool();

enum
{
    SIGSTRUCT = 1,
    REG_UINT,
    REG_INT,
    REG_BOOL,
};
int testId = REG_UINT;

int sc_main(int argc, char *argv[])
{
    sc_signal<bool> clk, rst, load;
    sc_signal<sc_uint<32> >  d, q;
    sc_signal<ahb_dma_in_type> dmai;
    sc_signal<ahb_dma_out_type> dmao;
    reg_bool aaa = 0;
    bool aaa0;
    reg_uint<32> bbb;
    sc_uint<32> ccc = 11;
    sc_uint<32> ddd = 22;
    sc_uint<32> eee;

    reg_uint<5> aaa1;
    reg_int aaa2;

    switch(testId)
    {
    case SIGSTRUCT:
        testSigStrut();
        return 0;
    case REG_UINT:
        test_reg_uint();
        return 0;
    case REG_INT:
        //test_reg_int();
        return 0;
    case REG_BOOL:
        return 0;
    }

    if(aaa == 1)
        printf("equal\n");
    if(1 == aaa)
        printf("equal\n");

    if(aaa1 == 1)
        printf("equal\n");
    if(1 == aaa1)
        printf("equal\n");

    if(aaa2 == 1)
        printf("equal\n");
    if(1 == aaa2)
        printf("equal\n");


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
    bbb.range(4, 0) = (aaa1, aaa1, aaa1, aaa1, aaa2);
    bbb.range(4, 0) = (aaa2, aaa2, aaa2, aaa2, aaa2);
    bbb.range(4, 0) = (aaa2, aaa1, aaa2, aaa1, aaa2);
    bbb.range(4, 0) = (aaa1, aaa2, aaa, aaa2, aaa1);
    bbb.range(4, 0) = (aaa2, aaa1, aaa, aaa1, aaa2);
    cout << "b[0]:" << bbb[0] << ", b[1]:" << bbb[1] << ", b[2]:" << bbb[2];
    cout << ", b[3]:" << bbb[3] << ", b[4]:" << bbb[4] << endl;
    bbb.range(4, 0) = (clk.read(), aaa0, aaa, aaa, (sc_uint<1>)1);
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








struct SigStruct_t
{
    reg_uint<32> aaa;
    reg_bool bbb;

    inline bool operator == (const SigStruct_t& v) const
    {
        return ((aaa == v.aaa)
                && (bbb == v.bbb));
    }

    inline bool operator != (const SigStruct_t& v) const
    {
        return (! operator == (v));
    }

    inline SigStruct_t& operator = (const SigStruct_t& v)
    {
        aaa = v.aaa;
        bbb = v.bbb;
        return *this;
    }

    inline friend void sc_trace(sc_trace_file *tf, const SigStruct_t& v,
            const std::string& name)
    {
        sc_trace(tf, v.aaa, name + ".aaa");
        sc_trace(tf, v.bbb, name + ".bbb");
    }

    inline friend ostream& operator << (ostream& os, const SigStruct_t& v)
    {
        os << "(" << std::boolalpha << v.aaa << ", ";
        os << v.bbb << ", ";
        return os;
    }
};


SC_MODULE(m_testSigStrut)
{
    sc_in<bool> clk;
    sc_out<SigStruct_t> sigstr_o;

    sc_signal<SigStruct_t> sigstr;

    void comp()
    {
        SigStruct_t &sigstr_s = (SigStruct_t &)sigstr.read();
        sigstr_s.aaa.write(sigstr_s.aaa.read().to_int() + 1);
        sigstr_s.bbb.write(!sigstr_s.bbb.read());

        sigstr_o.write(sigstr);

        cout << "old: aaa: " << sigstr.read().aaa;
        cout << ", bbb: " << sigstr.read().bbb << endl;

        cout << "new: aaa: " << sigstr_s.aaa;
        cout << ", bbb: " << sigstr_s.bbb << endl;

        cout << "out: aaa: " << sigstr_o.read().aaa;
        cout << ", bbb: " << sigstr_o.read().bbb << endl;
    }

    SC_CTOR(m_testSigStrut)
    {
        SC_METHOD(comp);
        sensitive << clk.pos();
    }
};

void testSigStrut()
{
    cout << "testSigStrut" << endl;
    sc_clock clk("clock", 20, SC_NS);
    sc_signal<SigStruct_t> sigstr;

    m_testSigStrut testsig("m_testSigStrut");
    testsig.clk(clk);
    testsig.sigstr_o(sigstr);

    cout << "start:" << endl;
    sc_start(200);

    sc_stop();
}


