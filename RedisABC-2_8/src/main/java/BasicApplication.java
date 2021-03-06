/**
 * BasicApplication
 *
 * @Description
 * @author yu
 * @Date 2020/4/1
 * @version v1.0
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BasicApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Redis");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
