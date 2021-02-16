package demo.service.impl;

import demo.dao.RaceDO;
import demo.exception.RaceException;
import transport.RpcContext;

import javax.jws.Oneway;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/2/16 2:26 下午
 */
public class RaceTestServiceImpl implements RaceTestService{
    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("race","rpc");
        if(RpcContext.getProps() != null){
            newMap.putAll(RpcContext.getProps());
        }
        return newMap;
    }

    @Override
    public String getString() {
        return "this is a rpc framework";
    }

    @Override
    public RaceDO getDO() {
        return new RaceDO();
    }

    @Override
    public boolean longTimeMethod() {
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Integer throwException() throws RaceException {
        throw new RaceException("just a exception");
    }
}
