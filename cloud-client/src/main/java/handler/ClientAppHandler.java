package handler;

import client.ClientApplication;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Getter
@Setter

public class ClientAppHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAppHandler.class);

    private static ClientAppHandler instance;

    private String login;
    private String password;
    private String serverHost;
    @Getter
    @Setter
    private String token;
    private int serverPort;
    private boolean isAuth;
    @Getter
    @Setter
    private Path localDir;
    @Getter
    @Setter
    private Path remoteDir;

    private ClientAppHandler () {

    }

    public static ClientAppHandler getInstance() {
        if (instance == null) {
            instance = new ClientAppHandler();
        }
        return instance;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
