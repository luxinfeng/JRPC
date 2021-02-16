package transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protocol.RpcRequest;
import protocol.RpcResponse;
import util.Tool;

import java.util.List;

/**
 * @author luxinfeng
 * @date 2021/2/10 4:14 下午
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private static byte[] requestCacheName = null;

    private static RpcRequest rpcRequestCacheValue = null;

    private static byte[] responseCacheName = null;

    private static RpcResponse rpcResponseCacheValue = null;

    private Class<?> genericClass;

    private KryoSerialization kryo;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
        kryo = new KryoSerialization();
        kryo.register(genericClass);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int HEAD_LENGTH = 4;
        if(in.readableBytes() < HEAD_LENGTH){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(dataLength < 0){
            ctx.close();
        }
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        if(genericClass.equals(RpcResponse.class)){
            // requestId的长度
            int requestIdLength = in.readInt();

            byte[] requestIdBytes = new byte[requestIdLength];
            in.readBytes(requestIdBytes);
            int bodyLength = dataLength - 4 - requestIdLength;

            byte[] body = new byte[bodyLength];
            in.readBytes(body);
            String requestId = new String(requestIdBytes);

            if(responseCacheName != null && cacheEqual(requestCacheName, body)){
                RpcResponse obj = new RpcResponse();
                obj.setRequestId(requestId);
                obj.setAppResponse(rpcResponseCacheValue.getAppResponse());
                obj.setClazz(rpcResponseCacheValue.getClazz());
                obj.setException(rpcResponseCacheValue.getException());
                out.add(obj);
            }else{
                RpcResponse obj=(RpcResponse) Tool.deserialize(body, genericClass);
                obj.setRequestId(requestId);//设置requestId
                out.add(obj);

                responseCacheName=body;
                rpcResponseCacheValue=new RpcResponse();
                rpcResponseCacheValue.setAppResponse(obj.getAppResponse());
                rpcResponseCacheValue.setClazz(obj.getClazz());
                rpcResponseCacheValue.setException(obj.getException());
            }

        }
    }

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
}
