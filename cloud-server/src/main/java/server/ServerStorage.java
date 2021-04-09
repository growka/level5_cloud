package server;

import common.*;
import handler.RequestHandler;
import handler.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

public class ServerStorage {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStorage.class);

    public static DirectoryTree createServerDirectoryTree(Path path, DirectoryTree dirList) throws IOException {
        dirList.clear();
        Path fullPath = path;
        Files.walkFileTree(fullPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir != fullPath) {
                            dirList.add(new FileItem(
                                    dir.getFileName().toString(),
                                    path.relativize(dir).toString(),
                                    dir.toAbsolutePath().toString(),
                                    attrs.size() + " байт",
                                    attrs.isDirectory(),
                                    attrs.creationTime().toString()
                            ));

                        }
                        return super.preVisitDirectory(dir, attrs);
                    }
                }
        );
        return dirList;
    }

    public static FileList getFilesFromDir(Path path, Command command) throws IOException {

        FileList fileList = new FileList();
        Path fullPath;
        if (command.getData()!= null) {

            LOG.debug("Path: " + path + " , data: " + ((FileItem) command.getData()).getPath());
            LOG.debug(command.getData().toString());

            if (!((FileItem) command.getData()).getPath().contains("Сервер")) {

                fullPath = Paths.get(path + "/" + ((FileItem) command.getData()).getPath());

            } else {
                fullPath = path;
            }

            Files.walkFileTree(fullPath, new HashSet<>(), 1, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //if (!Files.isDirectory(file)) {
                    fileList.add(new FileItem(
                            file.getFileName().toString(),
                            file.relativize(fullPath).toString(),
                            file.toAbsolutePath().toString(),
                            attrs.size() + " байт",
                            attrs.isDirectory(),
                            attrs.creationTime().toString()));
                    //}
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
            return fileList;


        }

        return fileList;
    }

    public static boolean writeFilePart(UserEntity user, FilePart filePart) {
        if (filePart.isEnd()) {
            return true;
        }
        try {
            Path filePath = getUserFolder(user).resolve(filePart.getFileName());
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, filePart.getData(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOG.info("FIle part " + filePart.getPartNumber());
            return true;
        } catch (IOException e) {
            LOG.error(e.toString());
            return false;
        }

    }

    public static Path getUserFolder(UserEntity user) {

        Path rootPath = Paths.get(ServerProperties.getInstance().getProperties("root.directory"));

        return rootPath.resolve(user.getFolder());
    }

    public static Path getFilePath(UserEntity user, String path) {
        Path userPath = getUserFolder(user);
        return userPath.resolve(path);
    }
}

