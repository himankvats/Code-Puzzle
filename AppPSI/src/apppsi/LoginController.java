/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author himankvats
 */
public class LoginController extends MainController implements Initializable {

    public static final String URL_FXML = "login.fxml";

    @FXML
    private Label titlelable;
    @FXML
    private TextField username;
    @FXML
    private TextField hostname;
    @FXML
    private TextField portnumber;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginButton.setDisable(true);
    }

    @FXML
    private void handleKeyReleased(KeyEvent event) {
        boolean disableButtons = username.getText().isEmpty() || username.getText().trim().isEmpty() || hostname.getText().isEmpty() || hostname.getText().trim().isEmpty() || portnumber.getText().isEmpty() || portnumber.getText().trim().isEmpty();
        loginButton.setDisable(disableButtons);
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        PuzzleController view2 = null;
        try{
            System.out.println("Loading view 2");
        view2 = (PuzzleController) AppPSI.getNavigation().load(PuzzleController.URL_FXML);
            System.out.println("Loaded view 2");
        }
        catch(Exception e){
            System.out.println("Could not load view 2");
            e.printStackTrace();
        }
        System.out.println("At LoginController Before view 2 show");
        view2.setHn(hostname.getText());
        view2.setPn(portnumber.getText());
        view2.setUn(username.getText());
        view2.Show();
       
    }

    

   

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        System.exit(0);
    }

}
