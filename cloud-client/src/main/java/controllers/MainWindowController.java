package controllers;

import common.*;
import handler.ClientAppHandler;
import handler.MessageHandler;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Network;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public TableView<FileItem> clientTableView;
    @FXML
    public ListView<String> serverView;
    @FXML
    public Button download;
    @FXML
    public Button upload;
    @FXML
    public Button open;
    @FXML
    public TableColumn<FileItem,String> fName;
    @FXML
    public TableColumn<FileItem,String> fExt;
    @FXML
    public TableColumn<FileItem,String> fSize;
    @FXML
    public TableColumn<FileItem,String> fDate;
    @FXML
    public TableColumn<FileItem,String> fAttrib;
    @FXML
    public TreeView<FileItem> clientTreeView;
    @FXML
    public TreeView<FileItem> serverTreeView;
    @FXML
    public ProgressBar progressBar;

    private StringProperty currentFile;

    private Path currentDir;
    private FileItem currentDirF;

    private int iterator;
    private float fileSize;

    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;

    private Path clientDir = Paths.get("cloud-client/сlientDir").toAbsolutePath();;

    DirectoryTree clientDirectoryTree = new DirectoryTree(new ArrayList<FileItem>());

    ObservableList<FileItem> clientFileList = FXCollections.observableArrayList();
    ObservableList<FileItem> clientDirList = FXCollections.observableArrayList();

    private final Image folderIcon = new Image(getClass().getResourceAsStream("folder.png"));
    //private final Node rootIcon = new ImageView(folderIcon);
    private final TreeItem<FileItem> rootClientNode = new TreeItem<>(new FileItem("Клиент", "", clientDir.toString()),new ImageView(folderIcon));
    private final TreeItem<FileItem> rootServerNode = new TreeItem<>(new FileItem("Сервер", "./", " "),new ImageView(folderIcon));


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Network.getInstance().getChannel().pipeline().removeLast();
        LOG.debug("AuthHandler успешно удалён");
        Network.getInstance().getChannel().pipeline().addLast(new MessageHandler(this));
        LOG.debug("MessageHandler успешно добавлен");
        LOG.debug(Network.getInstance().getChannel().pipeline().toString());

        ClientAppHandler.getInstance().setLocalDir(clientDir);

        fName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<FileItem, String>, ObservableValue<String>>) param -> param.getValue().getName());

        fSize.setCellValueFactory(new PropertyValueFactory<FileItem, String>("fileSize"));
        fSize.setCellValueFactory((Callback<TableColumn.CellDataFeatures<FileItem, String>, ObservableValue<String>>) param -> param.getValue().getFileSize());

        fDate.setCellValueFactory(new PropertyValueFactory<FileItem, String>("exchangeDate"));
        fDate.setCellValueFactory((Callback<TableColumn.CellDataFeatures<FileItem, String>, ObservableValue<String>>) param -> param.getValue().getExchangeDate());

        clientTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentFile = newValue.getName();
            }
            }));

        clientTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //clientTableView.getSelectionModel().clearSelection();
            currentDir = Paths.get(newValue.getValue().getAbsolutePath());
            updateClientView(currentDir, clientFileList);
            updateTableView();
            System.out.println(currentDir);

        });

        serverTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //clientTableView.getSelectionModel().clearSelection();
            currentDirF = newValue.getValue();

            currentDir = Paths.get(newValue.getValue().toString());
            System.out.println(currentDir);
            updateServerTable(currentDir);
        });

        createClientDirectoryTree(clientDir, clientDirectoryTree);
        createTreeFromList(clientDirectoryTree, clientTreeView, rootClientNode);

//        updateClientView(clientDir,clientFileList);
//        updateTableView(clientFileList);
//        createDirectoryTree(clientDir);
        getServerTree();

    }

    private void updateServerTable(Path currentDir) {
        Network.getInstance().getFileList(currentDir);
    }

    public void updateClientView(Path path, ObservableList<FileItem> fileList) {
        fileList.clear();

        try {
            Files.walkFileTree(path,new HashSet<>(), 1, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){

                    if (!path.equals(dir) && Files.isDirectory(path)) {
                    fileList.add(new FileItem(
                            dir.getFileName().toString(),
                            dir.toAbsolutePath().toString(),
                            dir.toAbsolutePath().toString(),
                            attrs.size() + " байт",
                            attrs.isDirectory(),
                            attrs.creationTime().toString()));
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){

                    fileList.add(new FileItem(
                            file.getFileName().toString(),
                            file.toAbsolutePath().toString(),
                            file.toAbsolutePath().toString(),
                            attrs.size() + " байт",
                            attrs.isDirectory(),
                            attrs.creationTime().toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc){
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTableView() {

        clientTableView.getItems().clear();
        if ((clientFileList != null) && (clientDirList!= null)) {
            clientTableView.getItems().addAll(clientFileList);
        }
    }

    private void updateTableView(FileList filesList) {

        clientTableView.getItems().clear();
        if (filesList != null) {
            SortedList<FileItem> observableList = new SortedList<>(
                    FXCollections.observableArrayList(filesList.getFileList())
            );
            clientTableView.getItems().addAll(observableList);
        }
    }

    public static void createClientDirectoryTree(Path path, DirectoryTree dirList) throws IOException {
        dirList.clear();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir != path) {
                            dirList.add(new FileItem(
                                    dir.getFileName().toString(),
                                    path.relativize(dir).toString(),
                                    dir.toAbsolutePath().toString(),
                                    attrs.size() + " байт",
                                    attrs.isDirectory(),
                                    attrs.creationTime().toString()));
                        }
                        return super.preVisitDirectory(dir, attrs);
                    }
                }
        );
    }


    private void getServerTree () {
        Network.getInstance().getDirectoryTree();
    }



    public void commandProcessor(Command command) {
        switch (command.getAction()) {
            case GET_TREE: createTreeFromList((DirectoryTree) command.getData(), serverTreeView, rootServerNode);
            break;
            case GET_FILELIST: updateTableView((FileList)command.getData());
            break;
            case FILE_TRANSFER: writeFilePart((FilePart) command.getData());
            break;
            case FILE_TRANSFER_RESULT : {
                iterator++;
                if (iterator * FilePart.partSize >= fileSize) {
                    progressBar.setProgress(0);
                    //tbFileButtons.setDisable(false);
                    Network.getInstance().getFileList(currentDir);
                } else {
                    float progress = ((float) (FilePart.partSize * iterator)) / fileSize;
                    progressBar.setProgress(progress);
                }
            }
        }
    }

    private void writeFilePart(FilePart data) {

        //if (data.isEnd()) {

        //    Platform.runLater(() -> {
               // tbFileButtons.setDisable(false);
       //         progressBar.setProgress(data.getProgress());
                Network.getInstance().getFileList(currentDir);
         //   });
       // } else {
        Platform.runLater(() -> {
            progressBar.setProgress(data.getProgress());
        });
        Path filePath = ClientAppHandler.getInstance().getLocalDir().resolve(currentDir.getFileName()).resolve(data.getFileName());
        LOG.debug("Path to receive file is " + filePath);
            try {
                Files.write(filePath, data.getData(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOG.error(e.toString());
        //   }
        }

    }

    public void download() throws IOException {
        // выключаем кнопку скачивания, пока процесс однопоточный...
     //   tbFileButtons.setDisable(true);
        Path path = Paths.get(String.valueOf(ClientAppHandler.getInstance().getLocalDir()) + "\\" + currentDir);
        LOG.debug("Downloading path is: " + path.toString());
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        Network.getInstance().getFile(Paths.get((currentDirF.getAbsolutePath() + "/" + currentFile.getValue())).toString());
        LOG.debug("Send command GET_FILE: " + (Paths.get(currentDirF.getAbsolutePath() + "/" + currentFile.getValue())));

    }

    private void createTreeFromList(DirectoryTree directoryTree, TreeView<FileItem> treeView, TreeItem<FileItem> root) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            for (FileItem fileItem : directoryTree.getDirList()) {
                String itemPath = fileItem.getPath();
                String[] pathParts = itemPath.replaceAll("\\\\", "/").split("/");
                for (String s : pathParts) {
                    System.out.print(pathParts.length + " ");
                    System.out.print(s + " ");
                }
                System.out.println();
                if (pathParts.length == 1) {
                    root.getChildren().add(new TreeItem<FileItem>(fileItem, new ImageView(folderIcon)));
                } else {
                    String parentName;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pathParts.length - 1; i++) {
                        sb.append(pathParts[i]).append("/");
                    }
                    sb.deleteCharAt(sb.lastIndexOf("/"));
                    parentName = sb.toString();
                    TreeItem<FileItem> parent = findItemByPath(root, parentName);
                    ObservableList<TreeItem<FileItem>> children;
                    //System.out.println(("Path: " + itemPath + "; parent: " + parentName + " (" + parent + ")"));
                    if (parent != null) {
                        children = parent.getChildren();
                        if (children != null) {
                            children.add(new TreeItem<>(fileItem, new ImageView(folderIcon)));
                        }
                    }
                }
            }
            treeView.setRoot(root);
            treeView.setShowRoot(true);
            treeView.setEditable(false);

        });
    }

    private TreeItem<FileItem> findItemByPath(TreeItem<FileItem> root, String parentName) {
        if (root.getValue().getPath().equals(parentName)) {
            return root;
        }
        for (TreeItem<FileItem> child : root.getChildren()) {
            TreeItem<FileItem> item = findItemByPath(child, parentName);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public void upload(ActionEvent actionEvent) throws IOException {
    }


    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else Platform.runLater(r);
    }

    public void openFile() {
        if (Files.exists(Paths.get(currentDirF.getAbsolutePath() + "\\" + currentFile))) {
            try {
                Desktop.getDesktop().open(Paths.get(currentDirF.getAbsolutePath() + "\\" + currentFile).toFile());
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось открыть файл");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Сперва надо файл скачать");
            alert.show();
        }
    }
}
