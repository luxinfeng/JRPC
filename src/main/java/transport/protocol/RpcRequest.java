package transport.protocol;

import java.io.Serializable;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/1/30 8:37 下午
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5606111910428846773L;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] paramterTypes;

    private Object[] parameters;

    private Map<String, Object> context;

    public String getRequestId(){
        return requestId;
    }

    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    public String getClassName(){
        return className;
    }

    public void setClassName(String className){
        this.className = className;
    }

    public String getMethodName(){
        return methodName;
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
    }

    public Class<?>[] getParamterTypes(){
        return paramterTypes;
    }

    public void setParamterTypes(Class<?>[] paramterTypes){
        this.paramterTypes = paramterTypes;
    }

    public Object[] getParameters(){
        return parameters;
    }

    public void setParameters(Object[] parameters){
        this.parameters = parameters;
    }

    public Map<String,Object> getContext(){
        return context;
    }

    public void setContext(Map<String, Object> context){
        this.context = context;
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
        RpcRequest other = (RpcRequest) obj;
        return (this.getRequestId() == null ? other.getRequestId() == null : this.getRequestId().equals(other.getRequestId())
                &&(this.getContext() == null ? other.getContext() == null : this.getContext().equals(other.getContext()))
                &&(this.getParameters() == null ? other.getParameters() == null : this.getParameters().equals(other.getParameters()))
                &&(this.getParamterTypes() == null ? other.getParamterTypes() == null : this.getParamterTypes().equals(other.getParamterTypes()))
                &&(this.getClassName() == null ? other.getClassName() == null : this.getClassName().equals(other.getClassName()))
                &&(this.getClass() == null ? other.getClass() == null : this.getClass().equals(other.getClass()))
                &&(this.getMethodName() == null ? other.getMethodName() == null : this.getMethodName().equals(other.getMethodName())));
    }
}
