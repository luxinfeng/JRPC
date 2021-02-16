package demo;

import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import consumer.ConsumerHook;
import protocol.RpcRequest;
import transport.RpcContext;

/**
 * @author luxinfeng
 * @date 2021/2/16 2:31 下午
 */
public class RaceConsumerHook implements ConsumerHook {

    @Override
    public void before(RpcRequest request) {
        RpcContext.addProp("hook key","this is a pass by hook");
    }

    @Override
    public void after(RpcRequest request) {
        System.out.println("I have finish rpc calling");
    }
}
