package provider;

/**
 * @author luxinfeng
 * @date 2021/2/16 10:43 上午
 */
public class RpcProvider {
    public RpcProvider() {}

    private void init() {}

    public RpcProvider serviceInterface(Class<?> serviceInterface){
        return this;
    }

    public RpcProvider version(String version){
        return this;
    }

    public RpcProvider impl(Object serviceInstance){
        return this;
    }

    public RpcProvider timeout(int timeout){
        return this;
    }

    public RpcProvider serializeType(String serializeType){
        return this;
    }

    public void publish(){

    }
}
