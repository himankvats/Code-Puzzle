/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author himankvats
 */
public class ConnectorController extends MainController implements Initializable {

    public static final String URL_FXML = "Connector.fxml";
    @FXML
    private AnchorPane AnchorPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("THis is connector class");
    }
    private String un, hn, pn;
    long retValue = 0;
    private long userID = 1;
    private ParsonsRelayClient rmiClient = null;
    private Object client = null;
    int intport;

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }
    @FXML
    private Label uname;
    @FXML
    private Label hname;
    @FXML
    private Label port;

    public void PreShowing() {
        super.PreShowing();
        uname.setText(un);
        hname.setText(hn);
        port.setText(pn);

        AnchorPane ap = new AnchorPane();
//        ringIndicator.setRingWidth(180);
//        ringIndicator.makeIndeterminate();
//        ap.getChildren().add(ringIndicator);
        try {
            connector();
//            ap.getChildren().removeAll(ringIndicator);
        } catch (Exception ex) {
            Logger.getLogger(ConnectorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ActionEvent event = null;

        handleLoginButtonAction(event);
    }
    int countcatch1 = 0;

//    public void setCountcatch1(int countcatch1) {
//        this.countcatch1 = countcatch1;
//    }
    Alert alert;

//RingProgressIndicator ringIndicator = new RingProgressIndicator();
    private void connector() throws Exception  {
        intport = Integer.valueOf(getPn());
        userID = 0;
//        int countcatch1=0;

        if (countcatch1 < 1) {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws RemoteException, Exception{
//                int countcatch = 0;
                        System.out.println("NUMBER__________-----------------" + countcatch1);
                        rmiClient = new ParsonsRelayClient();
                        System.out.println("RMICLIENT1 At ConnectorController: " + rmiClient);
                        userID = rmiClient.getStudentID(un, hn, intport);
                        System.out.println("USERID1:  at ConnectorController : " + userID);
                    
                    updateProgress(countcatch1, 10);

                    return null;
                }

            };
            ProgressIndicator indicator = new ProgressIndicator();
            indicator.progressProperty().bind(task.progressProperty());
            while (rmiClient == null && countcatch1 < 10) {
                try {
                    new Thread(task).start();
                }
//                catch (RemoteException ex) {
//                            ex.printStackTrace();
//                            waitOrExit();
//                            rmiClient = null;
//                        }
                catch (Exception ex) {
                            ex.printStackTrace();
                            waitOrExit();
                            rmiClient = null;
                        }
                
                countcatch1++;
            }
        } //        if (countcatch < 1) {
        //            while (rmiClient == null && countcatch < 10) {
        //                try {
        //                    countcatch++;
        //                    System.out.println("NUMBER__________-----------------" + countcatch);
        //                    rmiClient = new ParsonsRelayClient();
        //                    System.out.println("RMICLIENT1 At ConnectorController: " + rmiClient);
        //                    userID = rmiClient.getStudentID(un, hn, intport);
        //                    System.out.println("USERID1:  at ConnectorController : " + userID); 
        //                    
        //                } catch (RemoteException ex) {
        //                    ex.printStackTrace();
        //                    waitOrExit();
        //                    rmiClient = null;
        //                } catch (Exception ex) {
        //                    ex.printStackTrace();
        //                    waitOrExit();
        //                    rmiClient = null;
        //                }
        //            }
        //            
        //    }
        else {
//            AnchorPane.getChildren().remove(ringIndicator);
//            AnchorPane.getChildren().add(alert);
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
//            alert.
            alert.setContentText("Please check the details and try again!!!");
            alert.showAndWait();
            AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
        }
    }

    private void handleLoginButtonAction(ActionEvent event) {
        System.out.println("TRYING TO LOAD PuzzleController" + rmiClient);
        if (rmiClient == null) {
            System.out.println("Cleint =======" + rmiClient);
        } else {
            System.out.println("AT ELSE OF handleLoginButtonAction" + rmiClient);
            PuzzleController view4 = null;
//                     AnchorPane.getChildren().remove(ringIndicator);
            try {
                System.out.println("Loading view 2 with Client" + rmiClient);
                view4 = (PuzzleController) AppPSI.getNavigation().load(PuzzleController.URL_FXML);
                System.out.println("Loaded view 2");
            } catch (Exception e) {
                System.out.println("Could not load view 2");
                e.printStackTrace();
            }

            System.out.println("At ConnectorController Before view 2 show");
//                    view4.setIntport(intport);
//                    view4.setRmiClient(rmiClient);
//                    view4.setUserID(userID);
            view4.setHn(hn);
            view4.Show();
            System.out.println("Loaded PuzzleController");
        }
    }

    private void waitOrExit() {
        try {
//            ProgressIndicator progressIndicator = new ProgressIndicator();
//            uname.getScene().setCursor(Cursor.WAIT);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.exit(0);
        }
    }

}
