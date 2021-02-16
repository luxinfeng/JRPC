package protocol;

import java.io.Serializable;

/**
 * @author luxinfeng
 * @date 2021/2/10 3:39 下午
 */
public class RpcResponse implements Serializable {
    static private final long serialVersionUID = -4364536436151723421L;

    private Class<?> clazz;

    private byte[] exception;

    private String requestId;

    private Throwable errorMsg;

    private Object appResponse;

    public Class<?> getClazz(){
        return clazz;
    }

    public void setClazz(Class<?> clazz){
        this.clazz = clazz;
    }

    public byte[] getException(){
        return exception;
    }

    public void setException(byte[] exception){
        this.exception = exception;
    }

    public String getRequestId(){
        return requestId;
    }

    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    public Throwable getErrorMsg(){
        return errorMsg;
    }

    public void setErrorMsg(Throwable errorMsg){
        this.errorMsg = errorMsg;
    }

    public Object getAppResponse(){
        return appResponse;
    }

    public void setAppResponse(Object appResponse){
        this.appResponse = appResponse;
    }

    public boolean isError(){
        return errorMsg == null ? false : true;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        RpcResponse other = (RpcResponse) obj;
        return (this.getClazz() == null ? other.getClazz() == null : this.getClazz().equals(other.getClazz())
                &&(this.getAppResponse() == null ? other.getAppResponse() == null : this.getAppResponse().equals(other.getAppResponse()))
                &&(this.getErrorMsg() == null ? other.getErrorMsg() == null : this.getErrorMsg().equals(other.getErrorMsg()))
                &&(this.getException() == null ? other.getException() == null : this.getException().equals(other.getException()))
                &&(this.getRequestId() == null ? other.getRequestId() == null : this.getRequestId().equals(other.getRequestId())));
    }
}
