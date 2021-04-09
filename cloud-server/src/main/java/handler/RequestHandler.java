package handler;

import common.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerApplication;
import server.ServerStorage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final ServerApplication serverApplication;

    private static Path clientDir;

    private static String username = null;
    private static String folder = null;

    public RequestHandler(ServerApplication serverApplication) {
        this.serverApplication = serverApplication;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {

        LOG.debug(command.toString());
        UserEntity user = serverApplication.getUser(command.getToken());
        if (user == null) {
            LOG.error("User not found on server");
            channelHandlerContext.writeAndFlush(new Exception("User not found"));
        } else {

            switch (command.getAction()) {
                case GET_TREE: {
                    Command answer;
                    answer = Command.getTreeCommand();
                    DirectoryTree tree = ServerStorage.createServerDirectoryTree(ServerStorage.getUserFolder(user), new DirectoryTree(new ArrayList<>()));
                    answer.setData(tree);
                    answer.setToken(user.getId());
                    LOG.debug(new StringBuilder().append("Отправляем команду: ").append(answer.toString()).toString());
                    channelHandlerContext.writeAndFlush(answer);
                    break;
                }
                case GET_FILELIST: {
                    Command answer;
                    answer = Command.getFilelistCommand();
                    FileList filelist = ServerStorage.getFilesFromDir(ServerStorage.getUserFolder(user), command);
                    answer.setData(filelist);
                    answer.setToken(user.getId());
                    LOG.debug(new StringBuilder().append("Отправляем команду: ").append(answer.toString()).toString());
                    channelHandlerContext.writeAndFlush(answer);
                    break;
                }
                case GET_FILE : {
                    Command answer;
                    LOG.debug("Received command GET_FILE: " + command.getData().toString());
                    //Path filePath = ServerStorage.getFilePath(user, command.getData().toString());
                    Path filePath = Paths.get(command.getData().toString());
                    String fileName = filePath.getFileName().toString();

                    if (Files.exists(filePath)) {
                        LOG.debug("File exist, start transfer...");
                        byte[] buffer = new byte[FilePart.partSize];

                        int read;

                        try (InputStream inputStream = new FileInputStream(filePath.toAbsolutePath().toString())) {
                            float size = inputStream.available();
                            int iterator = 1;
                            while ((read = inputStream.read(buffer)) != -1) {
                                float progress = ((float) (buffer.length * iterator)) / size;
                                answer = Command.filePartTransferCommand(fileName, buffer, read, false, progress, iterator);
                                channelHandlerContext.writeAndFlush(answer);
                                LOG.debug("Answer is: " + answer);
                                LOG.debug("Sending part of file...");
                                iterator++;
                            }
                            answer = Command.filePartTransferCommand(fileName, buffer, read, true, 0, iterator);
                            channelHandlerContext.writeAndFlush(answer);
                            LOG.debug("Answer is: " + answer);
                            LOG.debug("Sending end of file...");
                        } catch (IOException e) {
                            LOG.error(e.toString());
                        }
                    }
                    break;
                }
                case FILE_TRANSFER : {
                    Command answer;
                    LOG.debug("Received command FILE_TRANSFER: " + command.getData().toString());
                    boolean result = ServerStorage.writeFilePart(user, (FilePart) command.getData());
                    answer = Command.fileTransferResult(result);
                    channelHandlerContext.writeAndFlush(answer);
                    LOG.debug("Answer is: " + answer);
                    LOG.debug("Sending answer");
                    break;
                }
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("e: ", cause);
    }
}
