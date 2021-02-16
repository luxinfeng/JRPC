package demo.dao;

import demo.exception.RaceException;

import java.io.Serializable;

/**
 * @author luxinfeng
 * @date 2021/2/16 2:16 下午
 */
public class RaceChildrenDO implements Serializable {
    private static final long serialVersionUID = -4364586436171722421L;
    private Integer childNum;
    private Long longValue;
    private char[] chars;

    public RaceChildrenDO(){
        chars = new char[]{'r','p','c'};
        childNum = 7;
        longValue = new Long(3000);
    }

    public Integer getChildNum(){
        return this.childNum;
    }

    public Long getLongValue(){
        return this.longValue;
    }

    public char[] getChars(){
        return this.chars;
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
        RaceChildrenDO other = (RaceChildrenDO) obj;

        return (this.getChars() == null ? other.getChars() == null : this.getChars().equals(other.getChars())
        && (this.getChildNum() == null ? other.getChildNum() == null : this.getChildNum().equals(other.getChildNum()))
        && (this.getLongValue() == null ? other.getLongValue() == null : this.getLongValue().equals(other.getLongValue())));
    }
}
