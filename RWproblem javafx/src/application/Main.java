package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
//import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;




public class Main extends Application {
	private static Stage primaryStage;
	private static BorderPane home;
	MediaPlayer mediaplayer;


	@Override
	public void start(Stage primaryStage) {
		Main.primaryStage= primaryStage;
		Main.primaryStage.setTitle("RW problem");
		showHome();
		Media BGM = new Media("file:///C:/Users/ASUS/Desktop/106�W�Ǵ�/jazz.mp3");
		mediaplayer = new MediaPlayer(BGM);
		mediaplayer.setAutoPlay(true);

	}
	public static void showHome() {
		try {
            // Load Home from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Home.fxml"));
            home = (BorderPane) loader.load();

            // Show the scene containing the home.
            Scene scene = new Scene(home);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static void showprocess() {
		try {
		FXMLLoader loader= new FXMLLoader();
		loader.setLocation(Main.class.getResource("process/RWprocess.fxml"));
		BorderPane rwprocess = loader.load();
		home.setCenter(rwprocess);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
	public static void main(String[] args) {
		launch(args);
	}
}
