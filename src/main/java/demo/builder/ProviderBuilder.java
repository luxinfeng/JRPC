package demo.builder;

import demo.service.impl.RaceTestService;
import demo.service.impl.RaceTestServiceImpl;
import provider.RpcProvider;

/**
 * @author luxinfeng
 * @date 2021/2/16 2:57 下午
 */
public class ProviderBuilder {

    public static void buildProvider(){
        publish();
    }


    private static void publish(){
        RpcProvider rpcProvider = null;
        try{
            rpcProvider = (RpcProvider) getProviderImplClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if(rpcProvider == null){
            System.out.println("start rpc provider failed");
            System.exit(1);
        }

        rpcProvider.serviceInterface(RaceTestService.class)
                .impl(new RaceTestServiceImpl())
                .version("1.0.0.api")
                .timeout(3000)
                .serializeType("java").publish();
        try{
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getProviderImplClass(){
        try {
            return Class.forName("org.example.rpc.provider.impl.RpcProviderImpl");
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot found the class which must exist and override all RpcProvider's methods");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
