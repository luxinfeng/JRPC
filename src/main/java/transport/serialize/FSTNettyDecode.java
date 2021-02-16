package transport.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.nustaq.serialization.FSTConfiguration;

import java.util.List;

/**
 * @author luxinfeng
 * @date 2021/2/13 11:26 上午
 */
public class FSTNettyDecode extends ByteToMessageDecoder {
    private FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(dataLength < 0){
            ctx.close();
        }

        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        Object o = conf.asObject(body);
        out.add(o);

    }
}
