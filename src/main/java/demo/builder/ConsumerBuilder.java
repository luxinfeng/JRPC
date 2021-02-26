package demo.builder;

import consumer.RpcConsumer;
import demo.RaceConsumerHook;
import demo.dao.RaceDO;
import demo.service.impl.RaceTestService;
/**
 * @author luxinfeng
 * @date 2021/2/16 1:14 下午
 */
public class ConsumerBuilder {
    private static RpcConsumer consumer;

    private static RaceTestService apiService;

    static {
        try{
            consumer = (RpcConsumer) getConsumerImplClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if(consumer == null){
            System.out.println("start rpc consumer failed");
            System.exit(1);
        }
        apiService = (RaceTestService) consumer
                .interfaceClass(RaceTestService.class)
                .version("1.0.0.api")
                .clientTimeout(3000)
                .hook(new RaceConsumerHook()).instance();

    }
    public boolean pressureTest() {
        try {
            RaceDO result = apiService.getDO();
            if (result == null)
                return false;
        } catch (Throwable t) {
            return false;
        }
        return true;
    }



    private static Class<?> getConsumerImplClass(){
        try {
            return Class.forName("org.example.rpc.consumer.impl.RpcConsumerImpl");
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot found the class which must exist and override all RpcProvider's methods");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
