import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main.fxml"));
        Parent root = loader.load();

        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Статус");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        MainController mainController = loader.getController();
        primaryStage.setOnCloseRequest(mainController.getCloseEventHandler());
    }


    public static void main(String[] args) {
        launch(args);
    }

}
