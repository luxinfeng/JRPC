package demo;

import transport.ResponseCallBackListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author luxinfeng
 * @date 2021/2/16 2:38 下午
 */
public class RaceServiceListener implements ResponseCallBackListener {
    private CountDownLatch latch = new CountDownLatch(1);

    private Object response;

    public Object getResponse() throws InterruptedException{
        latch.await(10, TimeUnit.SECONDS);
        if(response == null){
            throw new RuntimeException("The response doesn't come back");
        }
        return response;
    }

    @Override
    public void onResponse(Object response) {
        System.out.println("This method is call when response arrived");
        this.response = response;
        latch.countDown();
    }

    @Override
    public void onTimeout() {
        throw new RuntimeException("This call has taken time more than timeout value");
    }

    @Override
    public void onException(Exception e) {
        throw new RuntimeException(e);
    }
}
