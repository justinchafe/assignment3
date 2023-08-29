package Part2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    BorderPane root;
    public static GraphModel model = new GraphModel();
    public static InteractionModel interact = new InteractionModel();
    public static GraphView view = new GraphView();
    public static GraphViewController controller = new GraphViewController();
    public static ToolBarController tbController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        root = new BorderPane();


        FXMLLoader fxml = new FXMLLoader(new URL(Main.class.getResource("toolbar.fxml").toExternalForm()));
        Pane toolBar = fxml.load();
        toolBar.setStyle("-fx-background-color: #e6e6e6;");
        tbController = fxml.getController();
        tbController.setBlueButton();
        root.setCenter(Main.view);
        root.setTop(toolBar);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Assignment 3 - Part 2");
        primaryStage.show();
    }



}