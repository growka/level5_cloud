package service;

import common.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);
    private static CommandHandler instance;
    private CommandHandler commandHandler;

    public CommandHandler() { }

    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    public void commandProcessor(Command command, ChannelHandlerContext ctx) throws InterruptedException {

        switch (command.getAction()) {
            case CONNECT:
                //Network.getInstance().connect();
            case GET_TREE:
                //
        }

    }

}
