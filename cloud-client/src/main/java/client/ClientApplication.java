package client;

import controllers.LoginController;
import controllers.MainWindowController;
import handler.ClientAppHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Network;

import java.io.IOException;

public class ClientApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(ClientApplication.class);

    private Stage loginStage;

    @Override
    public void start(Stage stage) throws Exception {

        stage.setOnCloseRequest(value -> Network.getInstance().shutdown());

        FXMLLoader loginLoader = new FXMLLoader();

        loginLoader.setLocation(getClass().getResource("/fxml/loginwindow.fxml"));
        Parent loginPanel = loginLoader.load();
        loginStage = new Stage();

        loginStage.setTitle("Client Cloud Storage v0.1");
        loginStage.initModality(Modality.WINDOW_MODAL);
        loginStage.initOwner(stage);
        loginStage.setResizable(false);

        Scene scene = new Scene(loginPanel);
        loginStage.setScene(scene);
        loginStage.setOnCloseRequest(value -> Network.getInstance().shutdown());
        loginStage.show();

        LoginController loginController = loginLoader.getController();
        loginController.prepare(this);

        loginStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

    }

    public void openMainWindow() {
        try {
            loginStage.hide();

            FXMLLoader mainWindowLoader = new FXMLLoader();

            mainWindowLoader.setLocation(getClass().getResource("/fxml/mainwindow.fxml"));
            Parent mainWindowPanel = mainWindowLoader.load();
            Stage mainWindowStage = new Stage();

            mainWindowStage.setTitle(ClientAppHandler.getInstance().getLogin());
            mainWindowStage.initModality(Modality.NONE);
            mainWindowStage.setResizable(true);
            Scene scene = new Scene(mainWindowPanel);
            mainWindowStage.setScene(scene);
            mainWindowStage.setOnCloseRequest(event -> Network.getInstance().shutdown());

            mainWindowStage.show();

            MainWindowController mainWindowController = mainWindowLoader.getController();

            mainWindowStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
