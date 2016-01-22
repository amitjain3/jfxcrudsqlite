package pcgabssys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public final void start(final Stage primarystage) throws Exception{
       final Parent root = FXMLLoader.load(this.getClass().getResource("sample.fxml"));
       primarystage.setTitle("Hello Absolut System");
       primarystage.setScene(new Scene(root, 500.0, 350.0));
       primarystage.show();
    }


    public static void main(final String[] args) {
        Application.launch(args);
    }


}
