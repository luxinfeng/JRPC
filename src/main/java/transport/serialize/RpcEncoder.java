package transport.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import transport.protocol.RpcRequest;
import transport.protocol.RpcResponse;
import util.Tool;

/**
 * @author luxinfeng
 * @date 2021/2/13 10:32 上午
 */
public class RpcEncoder extends MessageToByteEncoder {
    private static Object responseCacheName = null;
    private static byte[] responseCacheValue = null;
    private static Object requestCacheName = null;
    private static byte[] requestCacheValue = null;
    private Class<?> genericClass;
    private KryoSerialization kryo;

    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
        kryo = new KryoSerialization();
        kryo.register(genericClass);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(genericClass.equals(RpcResponse.class)){
            RpcResponse response = (RpcResponse) msg;
            String requestId = response.getRequestId();
            response.setRequestId("");

            byte[] requestIdByte = requestId.getBytes();
            byte[] body = null;
            if(responseCacheName != null && requestCacheName.equals(response)){
                body = responseCacheValue;
            }else{
                body = Tool.serialize(msg);
                responseCacheName = response;
                responseCacheValue = body;
            }

            int totalLen = requestIdByte.length + body.length;

            out.writeInt(totalLen);
            out.writeInt(requestIdByte.length);
            out.writeBytes(requestIdByte);
            out.writeBytes(body);
        }else if(genericClass.equals(RpcRequest.class)){
            RpcRequest request = (RpcRequest) msg;
            String requestId = request.getRequestId();
            request.setRequestId("");
            byte[] requestIdByte = requestId.getBytes();

            byte[] body = null;
            if(requestCacheName != null && requestCacheName.equals(request)){
                body = requestCacheValue;
            }else{
                body = Tool.serialize(msg);
                requestCacheName = request;
                requestCacheValue = body;
            }

            int totalLen = requestIdByte.length + body.length;

            out.writeInt(totalLen);
            out.writeInt(requestIdByte.length);
            out.writeBytes(requestIdByte);
            out.writeBytes(body);
        }else{
            byte[] body = Tool.serialize(msg);
            out.writeInt(body.length);
            out.writeBytes(body);
        }
    }
}
