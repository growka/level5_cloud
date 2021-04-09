package controllers;

import client.ClientApplication;
import common.AuthMessage;
import handler.AuthHandler;
import handler.ClientAppHandler;
import handler.MessageHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.event.ActionEvent;
import service.Network;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private String authString;


    @FXML
    private AnchorPane loginAnchorPane;

    private ClientApplication clientApplication;
    private AuthHandler authHandler;
    private AuthMessage authMessage;

    public Button loginButton;
    public Button regButton;

    public TextField ipTextField;
    public TextField userTextField;
    public TextField passwordTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ipTextField.setText("localhost");
        ipTextField.setEditable(false);
        userTextField.setText("Moki");
        passwordTextField.setText("moki");
        regButton.setDisable(true);

    }

    public void prepare(ClientApplication clientApplication) {

        this.clientApplication = clientApplication;

        Network.getInstance();

    }

    public void login(ActionEvent actionEvent) throws InterruptedException {

        ClientAppHandler.getInstance().setServerHost(ipTextField.getText());
        ClientAppHandler.getInstance().setServerPort(8189);

        authMessage = AuthMessage.builder().username(userTextField.getText()).password(passwordTextField.getText()).build();

        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Network.getInstance().connect();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        connection.start();

        while (Network.getInstance().getChannel() == null) {

        }

        if ((authMessage != null) && (Network.getInstance().getChannel() != null)) {
            Network.getInstance().getChannel().writeAndFlush(authMessage);
        }

        Thread.sleep(1000);

        if (ClientAppHandler.getInstance().isAuth()) {
            clientApplication.openMainWindow();
        }
    }
}
