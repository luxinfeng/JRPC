package transport;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import protocol.RpcRequest;
import protocol.RpcResponse;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author luxinfeng
 * @date 2021/1/30 10:20 下午
 */
public class NettyRpcConnection implements RpcConnection{
    private InetSocketAddress inetAddr;

    private volatile Channel channel;

    private RpcClientHandler handle;

    private static Map<String, InvokeFuture<Object>> futures = new ConcurrentHashMap<>();

    private Map<String, Channel> channels = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private volatile ResultFuture<Object> resultFuture;

    private long timeout = 3000;

    private boolean connected = false;

    NettyRpcConnection(){}

    public NettyRpcConnection(String host, Integer port){
        inetAddr = new InetSocketAddress(host, port);
        handle = new RpcClientHandler(this);
        init();
    }

    private Channel getChannel(String key){
        return channels.get(key);
    }

    @Override
    public void init(){
        try{
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                            ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                            ch.pipeline().addLast(new FSTNettyEncode());
                            ch.pipeline().addLast(new FSTNettyDecode());
                            ch.pipeline().addLast(handle);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try{
            ChannelFuture channelFuture = bootstrap.connect(this.inetAddr).sync();
            channels.put(this.inetAddr.toString(), channelFuture.channel());
            connected = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String host, Integer port) {
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port));
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channels.put(channel.remoteAddress().toString(), channel);
            }
        });
    }

    @Override
    public Object send(RpcRequest rpcRequest, boolean async) {
        if(channel == null){
            channel = channels.get(inetAddr.toString());
        }
        if(channel != null){
            final InvokeFuture<Object> future = new InvokeFuture<>();
            futures.put(rpcRequest.getRequestId(), future);
            future.setMethod(rpcRequest.getMethodName());
            ChannelFuture cfuture = channel.writeAndFlush(rpcRequest);
            cfuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture rfuture) throws Exception {
                    if(!rfuture.isSuccess()){
                        future.setCause(rfuture.cause());
                    }
                }
            });
            resultFuture = new ResultFuture<Object>(timeout);
            resultFuture.setRequestId(rpcRequest.getRequestId());
            try{
                if(async){
                    ResponseFuture.setFuture(resultFuture);
                    return null;
                }
                Object result = future.getResult(timeout, TimeUnit.MILLISECONDS);
                return result;
            } catch (Exception e) {
                throw e;
            }finally {
                if(!async){
                    futures.remove(rpcRequest.getRequestId());
                }
            }
        }else{
            return null;
        }
    }

    @Override
    public void close() {
        if(channel == null){
            try{
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isClosed() {
        return (channel == null || !channel.isOpen() || !channel.isActive() || channel.isWritable());
    }

    @Override
    public boolean containsFuture(String key) {
        return futures.containsKey(key);
    }

    @Override
    public InvokeFuture<Object> removeFuture(String key) {
        if(containsFuture(key)){
            return futures.remove(key);
        }
        return null;
    }

    @Override
    public void setResult(Object str) {
        RpcResponse response = (RpcResponse) str;
        if(response.getRequestId().equals(resultFuture.getRequestId())){
            resultFuture.setResult(str);
        }
    }

    @Override
    public void setTimeOut(long timeOut) {
        this.timeout = timeOut;
    }

    @Override
    public void setAsyncMethod(Map<String, ResponseCallBackListener> map) {
        handle.setAsyncMethod(map);
    }

    @Override
    public List<InvokeFuture<Object>> getFutures(String method) {
        List<InvokeFuture<Object>> list = new ArrayList<>();
        Iterator<Map.Entry<String, InvokeFuture<Object>>> it = futures.entrySet().iterator();
        String methodName = null;
        InvokeFuture<Object> temp = null;
        while(it.hasNext()){
            Map.Entry<String, InvokeFuture<Object>> entry = it.next();

            methodName = entry.getValue().getMethod();
            temp = entry.getValue();

            if(methodName!=null&&methodName.equals(method)&&temp!=null)
            {
                list.add(temp);
                methodName=null;
                temp=null;
            }
        }
        return list;
    }
}
