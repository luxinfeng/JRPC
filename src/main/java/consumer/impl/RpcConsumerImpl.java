package consumer.impl;

import consumer.RpcConsumer;
import transport.RpcConnection;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author luxinfeng
 * @date 2021/1/30 10:06 下午
 */
public class RpcConsumerImpl extends RpcConsumer implements InvocationHandler {
    private static AtomicLong callTimes = new AtomicLong(0L);
    private RpcConnection connection;
    private List<RpcConnection> connectionList;


}
