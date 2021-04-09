package common;

import javafx.beans.property.*;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class FileItem implements CommandData {
    @NonNull
    private String name;
    @NonNull
    private String path;
    @NonNull
    private String absolutePath;
    @NonNull
    private String fileSize;
    @NonNull
    private Boolean folder;
    @NonNull
    private String exchangeDate;

    @Override
    public String toString() {
        return name;
    }

    public FileItem(@NonNull String name, @NonNull String path, @NonNull String absolutePath, @NonNull String fileSize, @NonNull Boolean folder, @NonNull String exchangeDate) {
        this.name = name;
        this.path = path;
        this.absolutePath = absolutePath;
        this.fileSize = fileSize;
        this.folder = folder;
        this.exchangeDate = exchangeDate;
    }

    public FileItem(@NonNull String name, @NonNull String path, @NonNull String absolutePath) {
        this.name = name;
        this.path = path.replaceAll("\\\\", "/");
        this.absolutePath = absolutePath;
    }

    public StringProperty getName() {
        return new SimpleStringProperty(name);
    }

    public StringProperty getFileSize() {
        return new SimpleStringProperty(fileSize);
    }

    public StringProperty getExchangeDate() {
        return new SimpleStringProperty(exchangeDate);
    }

}
