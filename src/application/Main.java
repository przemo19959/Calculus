package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	private SampleController mainCon;
	@Override
	public void start(Stage primaryStage) {
		try {
			Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
	        Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
	        Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);
	        FXMLLoader loader=new FXMLLoader(getClass().getResource("Sample.fxml"));
			BorderPane root =loader.load();
			mainCon=loader.getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			mainCon.init(primaryStage);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
