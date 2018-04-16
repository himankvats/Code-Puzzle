/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author himankvats
 */
public class AppPSI extends Application {
    private static Navigation navigation;
    
    public static Navigation getNavigation() throws Exception {
        return navigation;
    }
    @Override
    public void start(Stage stage) throws Exception {
       
        navigation = new Navigation(stage);
        stage.setTitle("Puzzle Coder");
        stage.setFullScreen(true);
        stage.setHeight(600);
        stage.setWidth(800);
        stage.show();
        AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
