package common;

import lombok.Getter;
import lombok.Setter;

public class Command implements CommandData {

    private Action action;


    @Getter
    @Setter
    private CommandData data;
    @Getter
    @Setter
    private String token;

    public static Command getFileCommand(FilePathString filePathString) {
        Command command = new Command();
        command.action = Action.GET_FILE;
        command.data = filePathString;
        return command;
    }

    public static Command getTreeCommand() {
        Command command = new Command();
        command.action = Action.GET_TREE;
        return command;
    }

    public static Command getFilelistCommand() {
        Command command = new Command();
        command.action = Action.GET_FILELIST;
        return command;
    }

    public static Command filePartTransferCommand(String fileName, byte[] fileData, int dataLength, boolean isEnd, double progress, int partNumber) {
        Command command = new Command();
        command.action = Action.FILE_TRANSFER;
        command.data = new FilePart(fileName, fileData, dataLength, isEnd, progress, partNumber);
        return command;
    }

    public static Command fileTransferResult(boolean result) {
        Command command = new Command();
        command.action = Action.FILE_TRANSFER_RESULT;
        command.data = new FileTransferResult(result);
        return command;
    }


    public void setData(CommandData data) {
        this.data = data;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Command{" +
                "action=" + action +
                ", commandData=" + data +
                ", token=" + token +
                '}';
    }
}
