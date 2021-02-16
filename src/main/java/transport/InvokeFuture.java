package transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author luxinfeng
 * @date 2021/2/10 3:02 下午
 */
public class InvokeFuture<T> {
    private Semaphore semaphore = new Semaphore(0);

    private Throwable cause;

    private T result;

    private List<InvokeListener<T>> listeners = new ArrayList<>();

    private String method;

    private boolean isRelease = false;

    public String getMethod(){
        return method;
    }

    public void setMethod(String method){
        this.method = method;
    }

    public InvokeFuture(){

    }

    public void setResult(T result){
        this.result = result;
        notifyListeners();
        synchronized (semaphore){
            if(!isRelease){
                semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
                isRelease = true;
            }
        }
    }

    public Object getResult(long timeout, TimeUnit unit){
        try{
            if(!semaphore.tryAcquire(timeout,unit)){
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        if(this.cause != null){
            throw new RuntimeException(this.cause);
        }
        return result;
    }
    public void setCause(Throwable cause) {
        this.cause = cause;
        notifyListeners();
        if(!isRelease)
        {
            semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
            isRelease=true;
        }
    }

    public Throwable getCause() {
        return cause;
    }

    public void addInvokerListener(InvokeListener<T> listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners(){
        for (InvokeListener<T> listener : listeners) {
            if (cause!=null) {
                listener.failure(cause);
            }else{
                listener.success(result);
            }
        }
    }
}
interface InvokeListener<T> {

    void success(T t);

    void failure(Throwable e);

}