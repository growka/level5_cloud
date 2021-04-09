package service;

import handler.SqlHandler;

public class SqlServiceImpl {

    public String isAuthClient(String login, String password) {
        return SqlHandler.isAuthClient(login, password);
    }

    public void createNewUser(String login, String password, String folder) {
        SqlHandler.createNewUser(login, password, folder);
    }


}
