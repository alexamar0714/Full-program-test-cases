package dataBaseLogin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Application driver. First file to run.
 * @author Adrian Mora
 */
public class MountieLib extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //window = stage;
        
        Parent root = FXMLLoader.load(getClass().getResource("MountieLib.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Mountie Library Login");
        stage.getIcons().add(new Image("images/titleIcon.png"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
