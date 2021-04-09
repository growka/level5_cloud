package common.old;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter


public class FileMsg implements Serializable {

    private StringProperty fileName;
    private StringProperty filePath;
    private StringProperty fileSize;
    private BooleanProperty folder;
    private StringProperty exchangeDate;
    private boolean isFolder;

    public FileMsg(String fileName, String filePath, String fileSize, boolean folder, String exchangeDate) {

        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.folder = new SimpleBooleanProperty(folder);
        this.exchangeDate = new SimpleStringProperty(exchangeDate);
    }

    @Override
    public String toString() {
        return getFileName().getValue();
    }
}
