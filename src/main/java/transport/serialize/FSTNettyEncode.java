package transport.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author luxinfeng
 * @date 2021/2/13 11:33 上午
 */
public class FSTNettyEncode extends MessageToByteEncoder {

    private FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 将信息保存为字节数组
        byte[] bytes = conf.asByteArray(msg);
        out.writeInt((bytes.length));
        out.writeBytes(bytes);
    }
}
