package demo.exception;

/**
 * @author luxinfeng
 * @date 2021/2/16 1:56 下午
 */
public class RaceException extends RuntimeException{
    private String flag = "race";
    public RaceException(String message){
        super(message);
    }
    public RaceException(Exception e){
        super(e);
    }
    public String getFlag(){
        return flag;
    }
}
