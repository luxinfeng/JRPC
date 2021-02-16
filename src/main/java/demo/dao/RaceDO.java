package demo.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luxinfeng
 * @date 2021/2/16 1:59 下午
 */

public class RaceDO implements Serializable {
    private static final long serialVersionUID = -4364536336161728421L;

    private int num;

    private String str;

    private List<String> list;

    private RaceChildrenDO child;

    public RaceDO(){
        num = 3;
        str = "rpc";
        list = new ArrayList<>();
        list.add("rpc-list");
        child = new RaceChildrenDO();
    }

    public Integer getNum(){
        return this.num;
    }

    public String getStr(){
        return this.str;
    }

    public List<String> getList(){
        return this.list;
    }

    public RaceChildrenDO getChild() {
        return this.child;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        RaceDO other = (RaceDO) obj;
        return (this.getNum() == null ? other.getNum() == null : this.getNum().equals(other.getNum())
        && (this.getStr() == null ? other.getStr() == null : this.getStr().equals(other.getStr()))
        && (this.getList() == null ? other.getList() == null : this.getList().equals(other.getList()))
        && (this.getChild() == null ? other.getChild() == null : this.getChild().equals(other.getChild())));
    }
}
