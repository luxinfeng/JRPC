package provider.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import protocol.RpcRequest;
import protocol.RpcRequestHandler;
import protocol.RpcResponse;
import provider.RpcProvider;
import transport.RpcDecoder;
import transport.RpcEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luxinfeng
 * @date 2021/2/16 10:49 上午
 */
public class RpcProviderImpl extends RpcProvider {
    private Map<String, Object> handlerMap = new HashMap<>();

    private Class<?> interfaceClazz;

    private Object classImplement;

    private String version;

    private int timeout;

    private String type;

    public String getVersion(){
        return version;
    }

    public int getTimeout(){
        return timeout;
    }

    public String getType(){
        return type;
    }

    @Override
    public RpcProvider serviceInterface(Class<?> serviceInterface) {
        this.interfaceClazz = serviceInterface;
        return this;
    }

    @Override
    public RpcProvider version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public RpcProvider impl(Object serviceInstance) {
        this.classImplement = serviceInstance;
        return this;
    }

    @Override
    public RpcProvider timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public RpcProvider serializeType(String serializeType) {
        this.type = serializeType;
        return this;
    }

    @Override
    public void publish() {
        handlerMap.put(interfaceClazz.getName(), classImplement);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                            ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                            ch.pipeline().addLast(new RpcRequestHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .option(ChannelOption.SO_RCVBUF, 2048);

            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
