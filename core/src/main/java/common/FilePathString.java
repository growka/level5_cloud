package common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@AllArgsConstructor

public class FilePathString implements CommandData {
    private String data;

    @Override
    public String toString() {
        return data;
    }
}
