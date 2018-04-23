package psifx;

import org.problets.lib.comm.rmi.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author himankvats
 */
public class PuzzleController extends MainController implements Initializable {

    public static final String URL_FXML = "puzzle.fxml";
    private ObservableList<String> itemsTemp;
    private static Text txt = new Text();
    private static List<Integer> userSpecifiedOrder = new ArrayList<Integer>();
    private static Map<String, Integer> indexWithCodeMapping = new HashMap<String, Integer>();
    List<String> list = new ArrayList<String>();
    private static List<Integer> OriginalOrder = new ArrayList<Integer>();

    double stepcount = 0;
    double attempt = 0;
    long startTime;
    long totalTimeStart;
    long totalTimeEnd;
    long totaltime;
    long endTime;
    boolean giveUp = false;
    Date elapsed;
    long elapsedMillis;
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    DateFormat timeFormat = new SimpleDateFormat("mm:ss");

    @FXML
    private TextArea instructiontext;
    @FXML
    ListView<String> problemlistview;
    @FXML
    ListView<String> trashlistview;
    @FXML
    ListView<String> answerlistview;
    @FXML
    ListView<String> feedbacklistview;
    @FXML
    private Label uname;
    @FXML
    private Label hname;
    @FXML
    private Label port;

    List<String> answercheck;
    private String un, hn, pn;

    private ParsonsPuzzle puzzle = null;

    private ParsonsBrokerProxy proxy;
    @FXML
    Button quitButton;
    @FXML
    Button submitButton;
    @FXML
    Button nextButton;
    @FXML
    private Button exit;
    @FXML
    private TextArea Popup;

    private int genotypeHashValue;
    long retValue = 0;
    private long userID = 0;
    private ParsonsRelayClient rmiClient;
    private Object client = null;
    int intport;
    boolean active = false;
    int i = 1;
    List<Fragment> fragments;
    String[] description;
    String[] title;
    String[] distractors;
    ObservableList<String> answerWindowCode = null;
    ObservableList<String> problemCode = null;
    ObservableList<String> trashWindowCode = null;
    ObservableList<String> feedbackWindowCode = null;
    int countpuzzles = 0;

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
    private int minpuzzle;
    private int mintime;
    private boolean minpuzzleset;
    private boolean mintimeset;

    public void addconfig() {
        try {
            Properties prop = new Properties();
//            String propFileName = "/Users/himankvats/NetBeansProjects/AppPSI/src/apppsi/config.properties";

            String localDir = System.getProperty("user.dir");
            String propFileName = localDir + "/trunk/psifx/config.properties";
            System.out.println("!!!!!!!!!propFileName~~~~~~~~~~~~~~~~" + propFileName);
            InputStream is = new FileInputStream(propFileName);
            prop.load(is);
            System.out.println("value 1:" + prop.getProperty("requiredpuzzle"));
            System.out.println("value 1:" + prop.getProperty("leastpuzzle"));
            System.out.println("value 1:" + prop.getProperty("requiredtime"));
            System.out.println("value 1:" + prop.getProperty("leasttime"));
            minpuzzle = Integer.parseInt(prop.getProperty("leastpuzzle"));
            mintime = Integer.parseInt(prop.getProperty("leasttime"));
            minpuzzleset = Boolean.parseBoolean(prop.getProperty("requiredpuzzle"));
            mintimeset = Boolean.parseBoolean(prop.getProperty("requiredtime"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void waitOrExit() {
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        }
    }

    private void connector() {

        System.out.println("THIS IS CHECKING FOR PUZZLE REQUIRED: " +minpuzzle +" " +minpuzzleset);
        System.out.println("THIS IS CHECKING FOR TIME REQUIRED: " +mintime +" " +mintimeset);
        intport = Integer.valueOf(getPn());
        proxy = new ParsonsBrokerProxy(hn, intport, "", 0);
        System.out.println("Proxy Object::::::::::::" + proxy);
        long studentID = proxy.setStudentID(un);

        System.out.println("USERID::::::::@@@@########" + studentID);

        if (studentID != 0) {
            puzzle = proxy.getParsonsPuzzle();
            System.out.println("Got ParsonsPuzzle");
            DisplayPuzzle(puzzle);
        } else {
            Alert alert;
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Please check the details and try again!!!");
            alert.showAndWait();
            try {
                AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
            } catch (Exception ex) {
                Logger.getLogger(PuzzleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("\n \n \n \n AT PuzzleController -initialize \n \n \n \n");
        submitButton.setDisable(true);
        nextButton.setDisable(true);
    }

    private void createMap() {
        int i = 0;
        for (String s : list) {
            indexWithCodeMapping.put(s, ++i);
        }
    }

    @FXML
    private void handleQuitButtonAction(ActionEvent event) {
        //attempt++;
        giveUp = true;
        endTime = System.currentTimeMillis();
        proxy.setParsonsEvaluation(stepcount, elapsedMillis, giveUp);
        stepcount = 0;
        // Load new Puzzle
        System.out.println("Total step count: " + stepcount);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Gave Up");
        alert.setContentText("Try new Puzzle by clicking on OK");
        alert.setHeaderText(null);
        alert.showAndWait();
        DisplayPuzzle(puzzle);
    }
private int countingpuzzle=0;
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {

        attempt++;
        boolean flag = false;
//        System.out.println("userSpecifiedOrder: ____()()()()()()():"+userSpecifiedOrder);
        System.out.println("Listcheck SIZE: " + listcheck.size() + "  AnswerWindow.size " + (answerWindowCode.size() - 1));
//        listcheck.size();
        if ((listcheck.size()) == (answerWindowCode.size() - 1)) {
            for (int k = 0; k < listcheck.size(); k++) {
                String tmp = (String) listcheck.get(k);
                String usrtmp = answerWindowCode.get(k);
                if (tmp.equals(usrtmp)) {
                    System.out.println("EQUAL" + k);
                    System.out.println("STRORDER   :" + listcheck.get(k));
                    System.out.println("AnswerCode :" + answerWindowCode.get(k));
                    flag = true;
                } else {
                    System.out.println("UNEQUAL" + k);
                    System.out.println("STRORDER   :" + listcheck.get(k));
                    System.out.println("AnswerCode :" + answerWindowCode.get(k));
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                countingpuzzle++;
                System.out.println("RIGHT answer");
                feedbackWindowCode.add("You answer is right. Click on Next to load new Puzzle.");
                feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
                endTime = System.currentTimeMillis();
                elapsedMillis = endTime - startTime;
                elapsed = new Date(elapsedMillis);
                submitButton.setDisable(true);
                nextButton.setDisable(false);
                proxy.setParsonsEvaluation(stepcount, elapsedMillis, giveUp);
                attempt = 0;
            } else {
                System.out.println("Wrong Answer");
                feedbackWindowCode.add("Incorrect Answer. ");
                feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);

            }

        } else {
            System.out.println("Wrong Answer, few trash in answer window");
            feedbackWindowCode.add("Incorrect Answer. ");
            feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
        }
    }
    String[] strorder;
    List listcheck = new ArrayList<String>();
    int showpuzzlenumber = minpuzzle;

    @FXML
    private void handleNextButtonAction(ActionEvent event) {

        if (minpuzzleset == true){
                
            if(minpuzzle>countingpuzzle){
            DisplayPuzzle(puzzle);
            nextButton.setDisable(true);    
        } 
        else  {
            minpuzzleset = false;
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Completed  " + showpuzzlenumber + "puzzles");
            alert.setHeaderText("Completed required number of puzzles successfully");
            alert.setContentText("If you want to continue to solve more puzzles click \"OK\" button, if you want dont wish to continue than click \"Exit\".");
            ButtonType buttonTypeOk = new ButtonType("OK");
            ButtonType buttonTypeExit = new ButtonType("Exit");
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeExit);
            Optional result = alert.showAndWait();
            if (result.get() == buttonTypeOk) {
                DisplayPuzzle(puzzle);
                nextButton.setDisable(true);
            } else  {
                totalTimeEnd = System.currentTimeMillis();
                long totaltimeMillies = totalTimeEnd - totalTimeStart;
                totaltime = TimeUnit.MILLISECONDS.toMinutes(totaltimeMillies);

                //Moving to 3rd view
                ReportController view3;
                try {
                    view3 = (ReportController) AppPSI.getNavigation().load(ReportController.URL_FXML);
                    view3.setStname(un);
                    view3.setHoname(hn);
                    view3.setPno(pn);
                    view3.setTime(Long.toString(totaltime));
//                    view3.setStcount(Integer.toString(stepcount));
                    view3.setTotalpuzzle(Integer.toString(countpuzzles));
                    view3.Show();
                } catch (Exception ex) {
                    Logger.getLogger(PuzzleController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        }
        else {
            DisplayPuzzle(puzzle);
            nextButton.setDisable(true);
        }
            

    }

    public void DisplayPuzzle(ParsonsPuzzle puzzle) {
//        userSpecifiedOrder.clear();
        stepcount = 0; //setting the fittness as 0 for each puzzle
        System.out.println("MAKING STEPCOUNT 000000000000");
        // Requesting new ParsonsPuzzle from broker
        System.out.println("PORT: " + intport + " UserID: " + userID + " hostname: " + hn + "RMICLIENT2 " + rmiClient);
        System.out.println("got puzzle");
        genotypeHashValue = puzzle.getHashValue();
        instructiontext.clear();
        title = puzzle.getTitle();
        description = puzzle.getDescription();
        fragments = null;
        fragments = puzzle.getPuzzleFragments();
        distractors = puzzle.getDistractorStrings();
        instructiontext.appendText("Question: ");
        strorder = puzzle.getOriginalStrings();
//        System.out.println("ANSWERCHECK length<<<<<<<<" + strorder.length);

        System.out.println("____________++++++++++THE ORGINAL STRING VALUE:+++++++++!!!!!!!!!!");
        for (int i = 0; i < strorder.length; i++) {
            System.out.println(strorder[i]);
        }

        for (String tmpinst : title) {
            instructiontext.appendText(tmpinst);
        }
        instructiontext.appendText("\n");
        for (String tmpinst : description) {
            instructiontext.appendText(tmpinst);
        }
        int b = 1;
        list.clear();
        System.out.println("&&&&&&&&&&&&&&&THIS IS FRAGMENT&&&&&&&&&&&&&&&");
        for (int i = 0; i < fragments.size(); i++) {
            System.out.println(fragments.get(i).getLine());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("^^^^^^^^^^^^THIS is Distractor^^^^^^^^^^^^");
        for (int i = 0; i < distractors.length; i++) {
            System.out.println(distractors[i]);
        }
        System.out.println("");
        System.out.println("");

        for (Fragment s : fragments) {
            list.add(s.getLine().toString()); // tostring
            System.out.println(b++ + "\t" + s.getLine());
        }
        System.out.println("List Added successfully " + list.toString());

        createMap();

        int count = 0;

        for (int i = 0; i < fragments.size(); i++) {

            if (fragments.get(i).getLineNum() != null) {
                count++;
            }
        }
//        listcheck=new ArrayList<String>();
        OriginalOrder.clear();
        listcheck.clear();
        for (int i = 0; i < count; i++) {
            for (int orderCheck = 0; orderCheck < fragments.size(); orderCheck++) {
                if (fragments.get(orderCheck).getOriginalOrder() == i + 1) {
                    OriginalOrder.add(orderCheck + 1);
                    listcheck.add(list.get(orderCheck));
                    System.out.print((orderCheck + 1) + " ");
                }
            }
        }

        System.out.println("LISTCHECK::::::::::::<<<<<>>>>>::::::::::");
        for (int i = 0; i < count; i++) {
            System.out.println(listcheck.get(i));
        }
        countpuzzles++;
        problemCode = FXCollections.observableArrayList(list);

        System.out.println("PROBLEMCODE:  " + problemCode.toString());
        answerWindowCode = FXCollections.observableArrayList(new String());
        trashWindowCode = FXCollections.observableArrayList(new String());
        feedbackWindowCode = FXCollections.observableArrayList();
        problemlistview.setItems(null);
        answerlistview.setItems(null);
        trashlistview.setItems(null);
        feedbacklistview.setItems(null);
        problemlistview.setItems(problemCode);
        System.out.println("PROBLEMCODESIZE)))))))))))))))" + problemlistview.getItems().size());

        answerlistview.setItems(answerWindowCode);
        trashlistview.setItems(trashWindowCode);
        feedbacklistview.setItems(feedbackWindowCode);
        totalTimeStart = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        System.out.println(String.format("Evaluation start time: %s", dateFormat.format(startTime)));
        itemsTemp = FXCollections.observableArrayList();
        problemlistview.setCellFactory(param -> new LISTCell());
        answerlistview.setCellFactory(param -> new LISTCell());
        trashlistview.setCellFactory(param -> new LISTCell());

    }

    public void PreShowing() {
        super.PreShowing();
        uname.setText(un);
        hname.setText(hn);
        port.setText(pn);
        addconfig();
        connector();

    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) throws Exception {

        totalTimeEnd = System.currentTimeMillis();
        long totaltimeMillies = totalTimeEnd - totalTimeStart;
        totaltime = TimeUnit.MILLISECONDS.toMinutes(totaltimeMillies);

        //Moving to 3rd view
        ReportController view3 = (ReportController) AppPSI.getNavigation().load(ReportController.URL_FXML);
        view3.setStname(un);
        view3.setHoname(hn);
        view3.setPno(pn);
        view3.setTime(Long.toString(totaltime));
//        view3.setStcount(Integer.toString(stepcount));
        view3.setTotalpuzzle(Integer.toString(countpuzzles));
        view3.Show();

    }

    private class LISTCell extends ListCell<String> {

        public LISTCell() {

            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                getListView().requestFocus();
                if (getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
                    getListView().getSelectionModel().clearSelection();
                }
                event.consume();
            });

            setOnDragDetected(event -> {
                if (getItem() == null || getItem().isEmpty()) {
                    submitButton.setDisable(false);
                    return;
                }
                itemsTemp = getListView().getItems();
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());
                dragboard.setContent(content);

                event.consume();
            });

            setOnDragOver(event -> {
                if (event.getDragboard().hasString() && !problemlistview.getItems().equals(getListView().getItems())) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            });

            setOnDragEntered(event -> {
                if (event.getDragboard().hasString()) {
                    setOpacity(0.3);
                }
            });

            setOnDragExited(event -> {
                if (event.getDragboard().hasString()) {
                    setOpacity(1);
                }
            });

            setOnDragDropped(event -> {
                txt.setText("");
                nextButton.setDisable(true);
                System.out.println("DragDropppp");
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    ObservableList<String> items = getListView().getItems();
                    int draggedIdx = itemsTemp.indexOf(db.getString());
                    int thisIdx = items.indexOf(getItem());
                    if (thisIdx == -1) {
                        thisIdx = items.size() - 1;
                    }
                    if (getListView().getItems().equals(answerlistview.getItems())) {
                        int convertedID = thisIdx;
                        if (items.size() > 2 && thisIdx >= (items.size() - 1)) {
                            convertedID = userSpecifiedOrder.size();
                        }
//                        userSpecifiedOrder.add(convertedID, indexWithCodeMapping.get(db.getString()));
//                        System.out.println("ADDED to userSpecifiedOrder at : " +convertedID +"Value" +indexWithCodeMapping.get(db.getString()));
                    }

                    if (items.equals(itemsTemp)) {
                        if (getItem().isEmpty()) {
                            return;
                        } else {
                            items.remove(draggedIdx);
                            items.add(thisIdx, db.getString());
//                            System.out.println("Removed from userSpecifiedOrder" +userSpecifiedOrder.remove(draggedIdx) +" from " +draggedIdx);
                            feedbackWindowCode.add("Reorder Done");
                            feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
                        }
                    } else {
                        items.add(thisIdx, db.getString());
                        itemsTemp.remove(draggedIdx);
                        feedbackWindowCode.add("Drag Done");
                        feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
                    }
                    if (problemlistview.getItems().isEmpty()) {
                        submitButton.setDisable(false);
                        feedbackWindowCode.add("You can submit answer now.");
                        feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
                    } else {
                        submitButton.setDisable(true);

                    }
                    success = true;
                }

                event.setDropCompleted(success);
                stepcount++;
            });
            setOnDragDone(DragEvent::consume);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            } else {
                setText(item);
            }
        }
    }
}
