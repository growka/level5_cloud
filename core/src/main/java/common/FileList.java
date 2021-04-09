package common;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FileList implements CommandData {

    private ArrayList<FileItem> fileList = new ArrayList<FileItem>();

    public void add (FileItem fileItem) {fileList.add(fileItem);}
}
