package transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import transport.protocol.RpcResponse;
import util.ByteObjConverter;
import transport.serialize.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/2/13 11:36 上午
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    private static byte[] cacheName = null;
    private static Object cacheValue = null;
    private RpcConnection connection;
    private Throwable cause;
    KryoSerialization kryo = new KryoSerialization();
    private Map<String, ResponseCallBackListener> listenerMap;
    RpcClientHandler() {}

    private static boolean cacheEqual(byte[] data1, byte[] data2){
        if(data1 == null && data2 == null){
            return true;
        }else if(data1 !=null && data2 != null){
            if(data1.length != data2.length){
                return false;
            }else{
                for(int i=0;i<data1.length;i++){
                    if(data1[i] != data2[i]){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public RpcClientHandler(RpcConnection connection){
        this.connection = connection;
        listenerMap = new HashMap<String, ResponseCallBackListener>();
    }

    private void notifyListenerResponse(String method, Object result){
        if(listenerMap != null && listenerMap.containsKey(method) && listenerMap.get(method)!=null){
            listenerMap.get(method).onResponse(result);
            System.out.println("notify:" + method);
        }
    }

    private void notifyListenerException(String method, Throwable cause){
        if(listenerMap != null && listenerMap.containsKey(method) && listenerMap.get(method)!=null){
            listenerMap.get(method).onResponse(cause);
            System.out.println("notify:" + method);
        }
    }

    public void setAsyncMethod(Map<String, ResponseCallBackListener> map){
        this.listenerMap.putAll(map);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("connected on server:"+ctx.channel().localAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        super.channelRead(ctx, msg);
        RpcResponse response = (RpcResponse) msg;
        String requestId = response.getRequestId();
        if(connection.containsFuture(requestId)){
            InvokeFuture<Object> future = connection.removeFuture(requestId);
            if(future == null){
                return;
            }
            if(this.cause != null){
                future.setResult(cause);
                notifyListenerException(future.getMethod(), cause);
                cause.printStackTrace();
            }else{
                byte[] data = (byte[]) response.getAppResponse();
                if(data != null){
                    if(cacheName != null && cacheEqual(data, cacheName)){
                        response.setAppResponse(cacheValue);
                    }else{
                        response.setAppResponse(ByteObjConverter.ByteToObject(data));
                        cacheName=data;
                        cacheValue=ByteObjConverter.ByteToObject(data);
                    }
                }
                future.setResult(response);
                this.connection.setResult(response);
                notifyListenerResponse(future.getMethod(), response.getAppResponse());

                for (InvokeFuture<Object> f : connection.getFutures(future.getMethod())) {
                    f.setResult(response);
                }
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        this.cause = cause;
    }
}
