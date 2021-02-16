package transport;

/**
 * @author luxinfeng
 * @date 2021/2/13 11:45 上午
 */
public interface ResponseCallBackListener {
    void onResponse(Object response);
    void onTimeout();
    void onException(Exception e);
}
