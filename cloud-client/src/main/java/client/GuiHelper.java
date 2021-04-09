package client;

import javafx.application.Platform;

public class GuiHelper {

    public static void uploadUI(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
