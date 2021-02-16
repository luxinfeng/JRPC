package demo.service.impl;

import demo.dao.RaceDO;
import demo.exception.RaceException;

import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/2/16 1:54 下午
 */
public interface RaceTestService {
    public Map<String, Object> getMap();

    public String getString();

    public RaceDO getDO();

    public boolean longTimeMethod();

    public Integer throwException() throws RaceException;
}
