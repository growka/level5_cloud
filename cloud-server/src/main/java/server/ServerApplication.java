package server;

import handler.AuthHandler;
import handler.RequestHandler;
import handler.UserEntity;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;

public class ServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);
    private static final String SERVER_HOST = ServerProperties.getInstance().getProperties("host");
    private static final int SERVER_PORT = Integer.parseInt(ServerProperties.getInstance().getProperties("port"));
    private static final ConcurrentHashMap<String, UserEntity> authClients = new ConcurrentHashMap<String, UserEntity>();

    RequestHandler requestHandler = new RequestHandler(this);
    AuthHandler authHandler = new AuthHandler(this);

    public ServerApplication() throws InterruptedException {

        EventLoopGroup authService = new NioEventLoopGroup(1);
        EventLoopGroup workerService = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap =new ServerBootstrap();
            bootstrap.group(authService, workerService)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    authHandler,
                                    requestHandler
                            );
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);
                    ChannelFuture future = bootstrap.bind(SERVER_HOST,SERVER_PORT).sync();
                    LOG.debug("Сервер успешно запущен, адрес: " + SERVER_HOST + " порт: " + SERVER_PORT);
                    future.channel()
                    .closeFuture()
                    .sync();

        } finally {
            authService.shutdownGracefully();
            workerService.shutdownGracefully();
            LOG.debug("Сервер успешно остановлен");

        }

    }

//    private boolean isRootFolderExist(){
//        Path path = Paths.get(ServerProperties.getInstance().getProperties("root.directory"));
//        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
//    }


    public void addUser(UserEntity user, String token) {
        authClients.put(token, user);
    }

    public UserEntity getUser(String token) {return authClients.get(token);}
}
