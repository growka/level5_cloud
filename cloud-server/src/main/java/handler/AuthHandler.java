package handler;


import common.AuthMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerApplication;


import java.util.UUID;

@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);
    private static boolean auth = false;
    private final ServerApplication serverApplication;
    private AuthMessage authMessage;

    private static final SqlHandler sqlService = new SqlHandler();

    public AuthHandler(ServerApplication serverApplication) {
        this.serverApplication = serverApplication;
        LOG.info("AuthHandler started!!");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("Client accepted!");
        //ctx.writeAndFlush(authMessage.builder().srvMsg("Auth ready").build());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

        if (msg instanceof AuthMessage) {
            LOG.debug("Received message: {}", msg.toString());
            SqlHandler.connect();
            String username = ((AuthMessage) msg).getUsername();
            String password = ((AuthMessage) msg).getPassword();
            String folder = SqlHandler.isAuthClient(username, password);

            if (folder != null) {

                auth = true;
                ((AuthMessage) msg).setPassword(null);
                ((AuthMessage) msg).setFolder(folder);
                UserEntity user = new UserEntity();
                String token = UUID.randomUUID().toString();
                user.setUsername(username);
                user.setId(token);
                user.setFolder(folder);
                serverApplication.addUser(user, token);
                channelHandlerContext.writeAndFlush(AuthMessage.builder().result(true).token(token).folder(folder).build());


            } else {
                channelHandlerContext.writeAndFlush(AuthMessage.builder().result(false).build());
            }
        } else {
            channelHandlerContext.fireChannelRead(msg);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("e: ", cause);
    }


}
