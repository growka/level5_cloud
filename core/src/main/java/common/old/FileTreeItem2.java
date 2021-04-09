package common.old;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class FileTreeItem2 implements Serializable {
    @NonNull
    private String name;
    @NonNull
    private String path;

    @Override
    public String toString() {
        return name;
    }

    public FileTreeItem2(@NonNull String name, @NonNull String path) {
        this.name = name;
        this.path = path.replaceAll("\\\\", "/");
    }

}
