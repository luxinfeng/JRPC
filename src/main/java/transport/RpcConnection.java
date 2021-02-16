package transport;

import protocol.RpcRequest;

import javax.xml.ws.spi.Invoker;
import java.util.List;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/1/30 10:12 下午
 */
public interface RpcConnection {
    void init();
    void connect();
    void connect(String host, Integer port);
    Object send(RpcRequest rpcRequest, boolean async);
    void close();
    boolean isConnected();
    boolean isClosed();
    public boolean containsFuture(String key);
    public InvokeFuture<Object> removeFuture(String key);
    public void setResult(Object str);
    public void setTimeOut(long timeOut);

    public default void setAsyncMethod(Map<String, ResponseCallBackListener> map) {
    }

    public List<InvokeFuture<Object>> getFutures(String method);
}
