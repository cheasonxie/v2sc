
#include "reg_uint.h"
#include <iostream>

SC_MODULE(testRegUint)
{
    sc_in<bool> clk;
    sc_out<reg_uint<16> > out;

    reg_uint<16> v1, v2;
    int iv1, iv2;
    int count;

    void test()
    {
        count ++;
        if(count > 50)
            count = 0;

        switch(count)
        {
        case 0:
            v1 = iv1;
            v2 = iv2;
            cout << "0-v1: " << v1 << endl;
            break;
        case 1:
            v1 = v2;
            cout << "1-v1: " << v1 << endl;
            break;
        case 2:
            v1 += iv1;
            cout << "2-v1: " << v1 << endl;
            break;
        case 3:
            v1 ++;
            cout << "3-v1: " << v1 << endl;
            break;
        case 4:
            v1 >>= iv1;
            cout << "4-v1: " << v1 << endl;
            break;
        case 5:
            cout << "50-v1: " << v1 << endl;
            cout << "51-v1: " << v1[3] << endl;
            break;
        case 6:
            cout << "6-v1: " << v1.and_reduce() << endl;
            break;
        case 7:
            cout << "7-v1: " << v1.range(3, 1).to_int() << endl;
            break;
        case 8:
            cout << "8-v1: " << v1.to_int() << endl;
            break;
        case 9:
            v1 = ((sc_uint<4>)7, v2.range(3, 0), v1.range(7, 0));
            cout << "9-v1: " << std::hex << v1 << endl;
            break;
        case 10:
            cout << "100-v1: " << std::hex << v1 << endl;
            v1.range(3, 0) = (sc_uint<3>)5;
            cout << "101-v1: " << std::hex << v1 << endl;
            break;
        case 11:
            cout << "11-v1: " << std::hex << v1 << endl;
            break;
        }
     }

    SC_CTOR(testRegUint)
    {
        SC_METHOD(test);
        sensitive << clk.pos();

        count = -1;
        iv1 = 2;
        iv2 = 5;
    }
};

void test_reg_uint()
{
    sc_clock clk("clock", 10, SC_NS);
    sc_signal<reg_uint<16> > regUint;
    reg_uint<16> aaa(20);
    reg_uint<16> bbb(aaa);

    testRegUint testreg("testRegUint");
    testreg.clk(clk);
    testreg.out(regUint);

    cout << "start: test_reg_uint" << endl;
    sc_start(500);

    sc_stop();
}
