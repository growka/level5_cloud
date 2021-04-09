package handler;

import common.AuthMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class AuthHandler extends SimpleChannelInboundHandler<AuthMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);
    private AuthMessage authMessage;
    private boolean isAuth = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        LOG.debug("Подключено к серверу");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthMessage msg) throws Exception {

        LOG.debug(msg.toString());

        if (msg!=null) {
            if (msg.isResult()) {
                ClientAppHandler.getInstance().setAuth(true);
                ClientAppHandler.getInstance().setToken(msg.getToken());
                ClientAppHandler.getInstance().setRemoteDir(Paths.get(msg.getFolder()));
                LOG.debug(new StringBuilder().append("Успешная авторизация! Токен: ")
                        .append(ClientAppHandler.getInstance().getToken())
                        .append(" директория на сервере: ")
                        .append(msg.getFolder())
                        .toString());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
