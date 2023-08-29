package Part1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    BorderPane root;

    public static GraphModel model = new GraphModel();
    public static GraphView view = new GraphView();
    public static GraphViewController controller = new GraphViewController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        Main.view.setStyle("-fx-background-color: #808080;");
        root.setCenter(Main.view);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Assignment 3 - Part 1");
        primaryStage.show();
    }


}
