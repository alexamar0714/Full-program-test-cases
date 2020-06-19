package cn.edu.ruc.iir.rainbow.layout.receiver;

import cn.edu.ruc.iir.rainbow.common.cmd.Receiver;

import java.util.Properties;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.rainbow.cli.receiver
 * @ClassName: ReceiverOrdering
 * @Description: receiver ordering
 * @author: Tao
 * @date: Create in 2017-08-13 11:09
 **/
public class ReceiverOrdering implements Receiver
{
    @Override
    public void progress(double percentage)
    {
        System.out.print("\rORDERING: " + ((int)(percentage * 10000) / 100.0) + "%    ");
    }

    @Override
    public void action(Properties results)
    {
        System.out.println("\nFinish.");
    }
}
