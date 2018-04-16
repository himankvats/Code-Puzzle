/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author himankvats
 */
public class ReportController extends MainController implements Initializable {
    public static final String URL_FXML="Report.fxml";
    @FXML
    private Button done;
    @FXML
    private Text studentname;
    @FXML
    private Text hostname;
    @FXML
    private Text port;
    @FXML
    private Text totaltime;
   
    
    private String stname,honame,pno,time;

    public String getStname() {
        return stname;
    }

    public void setStname(String stname) {
        this.stname = stname;
    }

    public String getHoname() {
        return honame;
    }

    public void setHoname(String honame) {
        this.honame = honame;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleDoneButtonAction(ActionEvent event) {
        System.exit(0);
    }

    public void PreShowing(){
        studentname.setText(stname);
        hostname.setText(honame);
        port.setText(pno);
        totaltime.setText(time+" Minutes");
    }
    
}
