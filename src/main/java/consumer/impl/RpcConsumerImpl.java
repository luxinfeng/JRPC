package consumer.impl;

import consumer.ConsumerHook;
import consumer.RpcConsumer;
import transport.*;
import transport.protocol.RpcRequest;
import transport.protocol.RpcResponse;
import util.Tool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author luxinfeng
 * @date 2021/1/30 10:06 下午
 */
public class RpcConsumerImpl extends RpcConsumer implements InvocationHandler {
    private static AtomicLong callTimes = new AtomicLong(0L);
    private RpcConnection connection;
    private List<RpcConnection> connectionList;
    private Map<String, ResponseCallBackListener> asyncMethods;
    private Class<?> interfaceClazz;
    private String version;
    private Integer timeout;
    private ConsumerHook hook;

    public Class<?> getInterfaceClazz(){
        return this.interfaceClazz;
    }

    public String getVersion(){
        return this.version;
    }

    public Integer getTimeout(){
        return this.timeout;
    }

    public ConsumerHook getHook(){
        return this.hook;
    }

    RpcConnection select(){
        int d = (int) (callTimes.getAndIncrement()%(connectionList.size()+1));
        if(d == 0){
            return connection;
        }else{
            return connectionList.get(d-1);
        }

    }

    public RpcConsumerImpl(){
        String ip = System.getProperty("SIP");
        this.asyncMethods = new HashMap<>();
        this.connection = new NettyRpcConnection(ip, 8888);
        this.connection.connect();
        connectionList = new ArrayList<>();
        int num = Runtime.getRuntime().availableProcessors()/3 - 2;
        for(int i=0;i<num;i++){
            connectionList.add(new NettyRpcConnection(ip, 8888));
        }
        for(RpcConnection connection : connectionList){
            connection.connect();
        }
    }

    public void destroy() throws Throwable{
        if(connection != null){
            connection.close();
        }
    }

    public <T> T proxy(Class<T> interfaceClass) throws Throwable {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName()
                    + " is not an interface");
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass }, this);
    }

    @Override
    public RpcConsumer interfaceClass(Class<?> interfaceClass) {
        this.interfaceClazz = interfaceClass;
        return this;
    }

    @Override
    public RpcConsumer version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public RpcConsumer clientTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public RpcConsumer hook(ConsumerHook hook) {
        this.hook = hook;
        return this;
    }

    @Override
    public Object instance() {
        try{
            return proxy(this.interfaceClazz);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public void asyncCall(String methodName) {
        asyncCall(methodName, null);
    }

    @Override
    public <T extends ResponseCallBackListener> void asyncCall(String methodName, T callbackListener) {
        this.asyncMethods.put(methodName, callbackListener);
        this.connection.setAsyncMethod(asyncMethods);
        for(RpcConnection connection : connectionList){
            connection.setAsyncMethod(asyncMethods);
        }
    }

    @Override
    public void cancelAsync(String methodName) {
        this.asyncMethods.remove(methodName);
        this.connection.setAsyncMethod(asyncMethods);
        for(RpcConnection connection : connectionList){
            connection.setAsyncMethod(asyncMethods);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> parameterTypes = new LinkedList<>();
        for(Class<?> parameterType : method.getParameterTypes()){
            parameterTypes.add(parameterType.getName());
        }
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParamterTypes(method.getParameterTypes());
        if(hook != null){
            hook.before(request);
        }
        RpcResponse response = null;
        try{
            request.setContext(RpcContext.props);
            response = (RpcResponse) select().send(request, asyncMethods.containsKey(request.getMethodName()));
            if(hook != null){
                hook.after(request);
            }
            if(!asyncMethods.containsKey(request.getMethodName()) && response.getException() != null){
                Throwable e = (Throwable) Tool.deserialize(response.getException(),response.getClazz());
                throw e.getCause();
            }
        }catch (Throwable t){
            throw t;
        }
        if(response == null){
            return null;
        }else if(response.getErrorMsg() != null){
            return response.getErrorMsg();
        }else{
            return response.getAppResponse();
        }

    }
}
