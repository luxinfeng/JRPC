package consumer;

import transport.ResponseCallBackListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author luxinfeng
 * @date 2021/1/30 8:15 下午
 */
public class RpcConsumer implements InvocationHandler {
    private Class<?> interfaceClazz;
    public RpcConsumer(){

    }

    private void init(){
        // todo
    }

    public RpcConsumer interfaceClass (Class<?> interfaceClass){
        this.interfaceClazz = interfaceClass;
        return this;
    }


    public RpcConsumer version(String version){
        // todo
        return this;
    }

    public RpcConsumer clientTimeout(int timeout){
        // todo
        return this;
    }

    public RpcConsumer hook(ConsumerHook hook){
        return this;
    }

    public Object instance(){
        // todo
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.interfaceClazz},this);
    }

    public void asyncCall(String methodName){
        asyncCall(methodName,null);
    }

    public <T extends ResponseCallBackListener> void asyncCall(String methodName, T callbackListener){
        // todo
    }

    public void cancelAsync(String methodName){
        // todo
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
