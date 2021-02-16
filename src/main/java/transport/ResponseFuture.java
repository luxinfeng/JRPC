package transport;

import protocol.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author luxinfeng
 * @date 2021/2/16 10:28 上午
 */
public class ResponseFuture {
    public static ThreadLocal<Future<Object>> futureThreadLocal = new ThreadLocal<>();

    public static Object getResponse(long timeout) throws InterruptedException {
        if(futureThreadLocal.get() == null){
            throw new RuntimeException("Thread[ " + Thread.currentThread() + "] have not set the response future");
        }

        try{
            RpcResponse response = (RpcResponse) (futureThreadLocal.get().get(timeout, TimeUnit.MILLISECONDS));
            if(response.isError()){
                throw new RuntimeException(response.getErrorMsg());
            }
            return response.getAppResponse();
        }catch (ExecutionException e){
            throw new RuntimeException(e);
        }catch (TimeoutException e){
            throw new RuntimeException(e);
        }
    }

    public static void setFuture(Future<Object> future){
        futureThreadLocal.set(future);
    }
}
