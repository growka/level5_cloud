package handler;

import common.Command;
import controllers.MainWindowController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);

    private MainWindowController mainWindowController;

    public MessageHandler(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOG.debug("Пришло сообщение!!");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

        LOG.debug(msg.toString());
        mainWindowController.commandProcessor((Command) msg);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Command error (client)", cause);
    }
}
