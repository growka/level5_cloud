package common;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DirectoryTree implements CommandData {



    private final ArrayList<FileItem> dirList;

    public DirectoryTree(ArrayList<FileItem> dirList) {
        this.dirList = dirList;
    }

    public void add(FileItem fileItem) {
        dirList.add(fileItem);
    }

    public void clear() {
        dirList.clear();
    }

    public ArrayList<FileItem> getDirList() {
        return dirList;
    }


    @Override
    public String toString() {
        return "DirectoryTree{" +
                dirList.toString() +
                '}';
    }

}
