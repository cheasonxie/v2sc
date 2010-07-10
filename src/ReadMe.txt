
目前版本有下面缺点，将来再考虑解决：
1、“**”转换为systemc指数形式时，总是假定以2为底
2、对于有generic的entity，generic值作为std_logic_vector的上下标时，总是使用其generic的默认值
   因为这样无法预先知道std_logic_vector的宽度
3、。。。

