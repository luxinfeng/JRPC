package transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author luxinfeng
 * @date 2021/2/10 3:26 下午
 */
public class ResultFuture<T> implements Future<Object> {

    private Semaphore semaphore = new Semaphore(0);

    private Throwable cause;

    private T result;

    private List<ResponseCallBackListener> listeners=new ArrayList<ResponseCallBackListener>();

    private boolean isDone;

    private long timeout;

    private String requestId;

    public ResultFuture(long timeout){
        this.timeout = timeout;
    }

    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    public String getRequestId(){
        return requestId;
    }

    public void setResult(T result) {
        this.result=result;
        this.isDone=true;
        notifyListeners();
        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
        notifyListeners();
        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
    }

    public Throwable getCause() {
        return cause;
    }

    public void addInvokerListener(ResponseCallBackListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners(){
        for (ResponseCallBackListener listener : listeners) {
            if (cause!=null) {
                listener.onException((Exception) cause);
            }else{
                listener.onResponse(result);
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try {
            if (!semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("time out");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("error");
        }

        if (this.cause!=null) {
            throw new RuntimeException(this.cause.toString());
        }
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            if (!semaphore.tryAcquire(timeout, unit)) {
                throw new RuntimeException("time out");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("error");
        }

        if (this.cause!=null) {
            throw new RuntimeException(this.cause.toString());
        }
        return result;
    }
}
