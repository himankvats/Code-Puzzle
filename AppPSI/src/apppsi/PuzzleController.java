package apppsi;

//import java.net.MalformedURLException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.problets.lib.comm.rmi.Fragment;
import org.problets.lib.comm.rmi.ParsonsBrokerProxy;
import org.problets.lib.comm.rmi.ParsonsEvaluation;
import org.problets.lib.comm.rmi.ParsonsPuzzle;
//import org.problets.lib.comm.rmi.ParsonsRelayClient;
import org.problets.lib.comm.rmi.ParsonsRelayServerInterface;

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

    int stepcount = 0;
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

    boolean active = false;

    private void waitOrExit() {
        try {
//            ProgressIndicator progressIndicator = new ProgressIndicator();
//            uname.getScene().setCursor(Cursor.WAIT);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.exit(0);
        }
    }

    long retValue = 0;
    private long userID = 1;
    private ParsonsRelayClient rmiClient;
    private Object client = null;
    int intport;

    private void connector() {
        intport = Integer.valueOf(getPn());
        userID = 0;
        int countcatch = 0;
        Alert alert;
//        if (countcatch < 1) 

//        {
//            Popup.setText("Sending request to server, this might take sometime.");
//            Popup.setVisible(true);
        while (rmiClient == null) {
            try {
                countcatch++;
                if (countcatch == 10) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please check the details and try again!!!");
                    alert.showAndWait();
                    AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
                    break;
                }
                else{
                System.out.println("NUMBER__________-----------------" + countcatch);
                rmiClient = new ParsonsRelayClient();
                System.out.println("RMICLIENT1: " + rmiClient);
                userID = rmiClient.getStudentID(un, hn, intport);
                System.out.println("USERID1: " + userID);
            }
            }
            catch (ConnectException ex){
                ex.printStackTrace();
//                waitOrExit();
//                rmiClient = null;
            }catch (RemoteException ex) {
                ex.printStackTrace();
                waitOrExit();
                rmiClient = null;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                waitOrExit();
                rmiClient = null;
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
        attempt++;
        giveUp = true;
        endTime = System.currentTimeMillis();
        setevaluvationdata(attempt, elapsedMillis, giveUp);
        // Load new Puzzle
        System.out.println("Total step count: " + stepcount);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Gave Up");
        alert.setContentText("Try new Puzzle by clicking on OK");
        alert.setHeaderText(null);
        alert.showAndWait();
        DisplayPuzzle();
    }

    private void setevaluvationdata(double someValues, long timeInMillis, boolean gaveUp) {
        DateFormat timeFormat = new SimpleDateFormat("mm:ss");
        ParsonsEvaluation eval = new ParsonsEvaluation();
        eval.setStudentID(userID);
        eval.setHashValue(genotypeHashValue);
        eval.setFitness(someValues);
        eval.setTimeTaken(timeInMillis);
        eval.setGaveUp(gaveUp);
       
        try {
            rmiClient.setParsonsEvaluation(eval, hn, intport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        System.out.println("\n Original Order " + OriginalOrder.toString());
        System.out.println("\nBefore editing userSpecifiedOrder: " + userSpecifiedOrder);
        System.out.println("\n userSpecified Order " + userSpecifiedOrder.toString());
        System.out.println("Total step count: " + stepcount);
        attempt++;
        if (OriginalOrder.equals(userSpecifiedOrder)) {
//            System.out.println("Total step count: " +stepcount);
            feedbackWindowCode.add("You answer is right. Click on Next to load new Puzzle.");
            feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
//            System.out.println("Equal");
            endTime = System.currentTimeMillis();
            elapsedMillis = endTime - startTime;
            elapsed = new Date(elapsedMillis);
            System.out.println(String.format("\nEvaluation end time: %s", dateFormat.format(endTime)));
            System.out.println(String.format("\nEvaluation elapsed time: %s", timeFormat.format(elapsed)));
            submitButton.setDisable(true);
            nextButton.setDisable(false);
            System.out.println("Attempts: " + attempt);
            setevaluvationdata(attempt, elapsedMillis, giveUp);
            attempt = 0;
        } else {
            feedbackWindowCode.add("Incorrect Answer. ");
            feedbacklistview.scrollTo(feedbackWindowCode.size() - 1);
            System.out.println("NotEqual");
        }
    }

    @FXML
    private void handleNextButtonAction(ActionEvent event) {
        DisplayPuzzle();
        nextButton.setDisable(true);
    }

    List<Fragment> fragments;
    String[] description;
    String[] title;
    String[] distractors;
    ObservableList<String> answerWindowCode = null;
    ObservableList<String> problemCode = null;
    ObservableList<String> trashWindowCode = null;
    ObservableList<String> feedbackWindowCode = null;
    int countpuzzles = 0;

    public void DisplayPuzzle() {
        userSpecifiedOrder.clear();

        while (true) {
            try {
                // Requesting new ParsonsPuzzle from broker
                System.out.println("PORT: " + intport + " UserID: " + userID + " hostname: " + hn + "RMICLIENT2 " + rmiClient);
                puzzle = (ParsonsPuzzle) rmiClient.getParsonsPuzzle(userID, hn, intport);
                System.out.println("got puzzle");
                genotypeHashValue = puzzle.getHashValue();
            } catch (RemoteException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(null);
                alert.setContentText("Something Went wrong close the app and re-try \n NOTE THE ERROR: 400-BAD REQUEST");
                alert.showAndWait();
            }
            break;
        }
        instructiontext.clear();
        title = puzzle.getTitle();
        description = puzzle.getDescription();
        fragments = null;
        fragments = puzzle.getPuzzleFragments();
        distractors = puzzle.getDistractorStrings();
        instructiontext.appendText("Question: ");
        for (String tmpinst : title) {
            instructiontext.appendText(tmpinst);
        }
        instructiontext.appendText("\n");
        for (String tmpinst : description) {
            instructiontext.appendText(tmpinst);
        }
        int b = 1;
        list.clear();
        for (Fragment s : fragments) {
            list.add(b + " " + s.getLine().toString()); // tostring
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
        OriginalOrder.clear();
        for (int i = 0; i < count; i++) {
            for (int orderCheck = 0; orderCheck < fragments.size(); orderCheck++) {
                if (fragments.get(orderCheck).getOriginalOrder() == i + 1) {
                    OriginalOrder.add(orderCheck + 1);
                    System.out.print((orderCheck + 1) + " ");
                }
            }
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

//    private int intport;
//    private long userID = 1;
//    private ParsonsRelayClient rmiClient;
//    
//    public void setRmiClient(ParsonsRelayClient rmiClient) {
//        this.rmiClient = rmiClient;
//    }
//
//    public void setIntport(int intport) {
//        this.intport = intport;
//    }
//
//    public void setUserID(long userID) {
//        this.userID = userID;
//    }
    public void PreShowing() {
        super.PreShowing();
        uname.setText(un);
        hname.setText(hn);
        port.setText(pn);
        
        try {
            connector();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Something Went wrong close the app and re-try \n NOTE THE ERROR: 400-BAD REQUEST");
            alert.showAndWait();
        }
        DisplayPuzzle();
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
        view3.setStcount(Integer.toString(stepcount));
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
//                int draggedIdx=itemsTemp.indexOf(db.getString());

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
                        userSpecifiedOrder.add(convertedID, indexWithCodeMapping.get(db.getString()));
                    }

                    if (items.equals(itemsTemp)) {
                        if (getItem().isEmpty()) {
                            return;
                        } else {
                            items.remove(draggedIdx);
                            items.add(thisIdx, db.getString());
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
