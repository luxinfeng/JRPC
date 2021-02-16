package consumer;

import protocol.RpcRequest;

/**
 * @author luxinfeng
 * @date 2021/1/30 8:32 下午
 */
public interface ConsumerHook {
    public void before(RpcRequest request);
    public void after(RpcRequest request);
}
