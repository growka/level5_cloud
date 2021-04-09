package service;

import common.Command;
import common.CommandData;
import common.FileItem;
import common.FilePathString;
import handler.AuthHandler;
import handler.ClientAppHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Network {

    private static final Logger LOG = LoggerFactory.getLogger(Network.class);


    private static Network instance;

    private String serverHost;
    private int serverPort;
    @Getter
    private Channel channel;

    private Network() {}

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    public void connect() throws InterruptedException {

        serverHost = ClientAppHandler.getInstance().getServerHost();
        serverPort = ClientAppHandler.getInstance().getServerPort();

        EventLoopGroup workerService = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerService)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new AuthHandler()
                                    //new MessageHandler()
                                    );
                        }
                    });

            channel = bootstrap.connect(serverHost, serverPort)
                    .sync()
                    .channel();
            channel.closeFuture()
                    .sync();

        } finally {

            workerService.shutdownGracefully();
            LOG.debug("Соединение клиента успешно разорвано");

        }

    }

    public void shutdown() {
        LOG.debug("Shutdown client call");
        if (channel != null) {
            channel.close();
        }

    }

    private void writeToken(Command command) {
        command.setToken(ClientAppHandler.getInstance().getToken());
    }

    public void getDirectoryTree() {
        Command command = Command.getTreeCommand();
        writeToken(command);
     //   command.setData(new FileItem("name", "name", "name"));
        LOG.debug(new StringBuilder().append("Отправляем команду: ").append(command.toString()).toString());
        Network.getInstance().getChannel().writeAndFlush(command);
    }

    public void getFileList(Path currentDir) {
        Command command = Command.getFilelistCommand();
        FileItem fileItem = new FileItem(currentDir.getFileName().toString(), currentDir.toString(), currentDir.toAbsolutePath().toString());
        command.setData(fileItem);
        writeToken(command);
        LOG.debug(String.valueOf(new StringBuilder().append("Отправляем команду: ").append((command.getData()).toString())));
        Network.getInstance().getChannel().writeAndFlush(command);
    }

    public void getFile(String file) {
        Command command = Command.getFileCommand(new FilePathString(file));
        writeToken(command);
        Network.getInstance().getChannel().writeAndFlush(command);
    }
}
