package protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import transport.KryoSerialization;
import transport.RpcContext;
import util.ByteObjConverter;
import util.Tool;

import javax.jws.Oneway;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/2/16 11:19 上午
 */
public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static Map<String, Map<String, Object>> ThreadLocalMap = new HashMap<>();

    private final Map<String, Object> handlerMap;

    KryoSerialization kryo = new KryoSerialization();

    private static Object cacheName = null;

    private static Object cacheValue = null;

    private static RpcRequest methodCacheName = null;

    private static Object methodCacheValue = null;

    private Map<String, FastMethod> methodMap = new HashMap<>();

    public RpcRequestHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("disconnected");
    }
    private void updateRpcContext(String host, Map<String, Object> map){
        if(ThreadLocalMap.containsKey(host)){
            Map<String, Object> local = ThreadLocalMap.get(host);
            local.putAll(map);
            ThreadLocalMap.put(host, local);
            for(Map.Entry<String,Object> entry : map.entrySet()){
                RpcContext.addProp(entry.getKey(), entry.getValue());
            }
        }else{
           ThreadLocalMap.put(host, map);
           for(Map.Entry<String, Object> entry : map.entrySet()){
               RpcContext.addProp(entry.getKey(), entry.getValue());
           }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        String host = ctx.channel().remoteAddress().toString();
        updateRpcContext(host, request.getContext());

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try{
            Object result = handle(request);
            if(cacheName != null && cacheName.equals(result)){
                response.setAppResponse(cacheValue);
            }else{
                response.setAppResponse(ByteObjConverter.ObjectToByte(result));
                cacheName = result;
                cacheValue = ByteObjConverter.ObjectToByte(result);
            }
        }catch (Throwable t){
            response.setException(Tool.serialize(t));
            response.setClazz(t.getClass());
        }
        ctx.writeAndFlush(response);
    }

    private Object handle(RpcRequest request) throws Throwable{

        String className = request.getClassName();
        Object classImpl = handlerMap.get(className);
        Class<?> clazz = classImpl.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParamterTypes();
        Object[] parameters = request.getParameters();

        if(cacheName != null && cacheName.equals(request)){
            return cacheValue;
        }else{
            try{
                methodCacheName = request;
                if(methodMap.containsKey(methodName)){
                    methodCacheValue = methodMap.get(methodName).invoke(classImpl, parameters);
                    return methodCacheValue;
                }else{
                    FastClass serviceFastClass = FastClass.create(clazz);
                    FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
                    methodMap.put(methodName, serviceFastMethod);
                    methodCacheValue = serviceFastMethod.invoke(classImpl, parameters);
                    return methodCacheValue;
                }

            }catch (Throwable e){
                throw e.getCause();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
