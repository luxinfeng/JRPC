package provider;

import transport.protocol.RpcRequest;

/**
 * @author luxinfeng
 * @date 2021/2/16 10:42 上午
 */
public interface ProviderHook {
    public void before(RpcRequest request);

    public void after(RpcRequest request);
}
