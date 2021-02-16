package transport;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
/**
 * @author luxinfeng
 * @date 2021/2/10 4:22 下午
 */
public class KryoSerialization {
    private Kryo kryo;
    private Registration registration = null;
    private Class<?> t;
    public KryoSerialization() {
        // TODO Auto-generated constructor stub
        kryo = new Kryo();
        kryo.setReferences(true);
    }

    public void register(Class<?> T) {
        //注册类
        t = T;
        registration = kryo.register(t);
    }
    public byte[] Serialize(Object object) {
        Output output = null;
        output = new Output(1, 4096);
        kryo.writeClassAndObject(output, object);
        byte[] bb = output.toBytes();
        output.flush();

        return bb;
    }

    public <t> t Deserialize(byte[] bb) {
        Input input = null;
        input = new Input(bb);
        @SuppressWarnings("unchecked")
        t res = (t) kryo.readClassAndObject(input);
        input.close();
        return res;
    }
}
