/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import org.problets.lib.comm.rmi.Fragment;
import org.problets.lib.comm.rmi.ParsonsBrokerProxy;
import org.problets.lib.comm.rmi.ParsonsPuzzle;
//import org.problets.lib.comm.rmi.ParsonsRelayClient;
import org.problets.lib.comm.rmi.ParsonsRelayServerInterface;

/**
 * FXML Controller class
 *
 * @author himankvats
 */
public class PuzzleController extends MainController implements Initializable  {

    private ObservableList<String> itemsTemp;
    private static Text txt = new Text();
    private static List<Integer> userSpecifiedOrder = new ArrayList<Integer>();
    private static Map<String, Integer> indexWithCodeMapping = new HashMap<String, Integer>();
    List<String> list = new ArrayList<String>();
    private static List<Integer> OriginalOrder = new ArrayList<Integer>();

    int attempt = 0;
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

    public static final String URL_FXML = "puzzle.fxml";
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
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.exit(0);
        }
    }

    private void connector() {
//       try{
//           System.out.println("/n /n /n /n AT PuzzleController -connector /n /n /n /n");
//       
//        int intport = Integer.valueOf(getPn());
//        proxy = new ParsonsBrokerProxy(hn, intport, "", 0);
//        System.out.println("\n \n Before SetStudentID \n \n");
//        proxy.setStudentID(un);
//        System.out.println("\n \n After SetStudentID \n \n");
//        }
//       catch(Exception e){
//          
//           System.out.println("AT CATCH ERROR");
//           e.printStackTrace();
//       }
//        int intport = Integer.valueOf(getPn());
//        while (proxy==null){
//            try{
//                System.out.println("AT try");
//                proxy = new ParsonsBrokerProxy(hn, intport, "", 0);
//                proxy.setStudentID(un);
//                System.out.println("Before wait or exit ");
//                waitOrExit();
//                
//            }
//            catch(Exception e){
//                System.out.println("AT catch");
//            }
//        }
//
//    
    }

    long retValue = 0;
    private long userID = 1;
    private ParsonsRelayClient rmiClient;
    private Object client = null;

    private void connector1() throws Exception {
        int intport = Integer.valueOf(getPn());
        userID = 0;
        try {
            rmiClient = new ParsonsRelayClient();
            userID=rmiClient.getStudentID(un, hn, intport);
        } catch (RemoteException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Please check the details and try again!!!");
            alert.showAndWait();
            AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
        }
    }

//    private void initClient(String hostName, int portNum, String serviceName, int maxConnectAttempts) {
//        int connectAttempts = 0;
//        client = null;
//        while (client == null && connectAttempts < maxConnectAttempts) {
//            try {
//                Registry r = LocateRegistry.getRegistry(hostName, portNum);
//                connectAttempts++;
//                client = (Object) r.lookup(serviceName);
//
//            } catch (RemoteException e) {
//
//            } catch (NotBoundException e) {
//                e.printStackTrace();
//                waitOrExit();
//                client = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//                waitOrExit();
//                client = null;
//            }
//        }
//    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("\n \n \n \n AT PuzzleController -initialize \n \n \n \n");
        submitButton.setDisable(true);
        nextButton.setDisable(true);
        quitButton.setDisable(true);
//        startconnection();

        //        try{
//            synchronized(proxy){
//                System.out.println("Proxy value "+proxy);
//                active=true;
//                if (proxy!=null){
//                    System.out.println("Connection success");
//                }
//                else{
//                    
//                    Alert alert = new Alert(AlertType.ERROR);
//                    alert.setTitle("Connection Error");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Please check the details and try again!!!");
//                    alert.showAndWait();
//                    AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
//                }
//            } 
//        }
//        catch(Throwable e){
//            e.printStackTrace();
//        }
//        try {
//        } catch (Throwable ex) {
//            if(proxy==null){Alert alert = new Alert(AlertType.ERROR);
//            alert.setTitle("Connection Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Please check the details and try again!!!");
//            alert.showAndWait();
//            AppPSI.getNavigation().load(LoginController.URL_FXML).Show();
//            }
//        }
    }

    private void createMap() {
        int i = 0;
        for (String s : list) {
            indexWithCodeMapping.put(s, ++i);
        }
    }

    @FXML
    private void handleQuitButtonAction(ActionEvent event) {
//        System.exit(0);
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        System.out.println("\n Original Order " + OriginalOrder.toString());
        System.out.println("\nBefore editing userSpecifiedOrder: " + userSpecifiedOrder);
        System.out.println("\n userSpecified Order " + userSpecifiedOrder.toString());
        attempt++;
        if (OriginalOrder.equals(userSpecifiedOrder)) {
            System.out.println("Equal");
            endTime = System.currentTimeMillis();
            elapsedMillis = endTime - startTime;
//                        elapsed = new Date(elapsedMillis);
            System.out.println(String.format("\nEvaluation end time: %s", dateFormat.format(endTime)));
//                        System.out.println(String.format("\nEvaluation elapsed time: %s", timeFormat.format(elapsed)));
            submitButton.setDisable(true);
            nextButton.setDisable(false);
            System.out.println("Attempts: " + attempt);

            proxy.setParsonsEvaluation((double) attempt, elapsedMillis, giveUp);
            attempt = 0;
            txt.setText("Correct. Please click Next for another round");

        } else {

            txt.setText("Please retry");
            System.out.println("NotEqual");
        }
    }

    @FXML
    private void handleNextButtonAction(ActionEvent event) {
        nextButton.setDisable(true);
//        startconnection();
    }

    public void startconnection() {
//        int intport = Integer.valueOf(pn);
////        System.out.println(intport);
//        proxy = new ParsonsBrokerProxy(hn, intport, "", 0);
//        proxy.setStudentID(un);
//        while (true) {
//            // Requesting new ParsonsPuzzle from broker
//            puzzle = proxy.getParsonsPuzzle();
//            System.out.println("got puzzle");
//            DisplayPuzzle(puzzle);
//            break;
//        }
    }

    public void DisplayPuzzle(ParsonsPuzzle o) {
//        attempt++;
        int co = 0;
        co++;
        instructiontext.clear();
        String[] title = o.getTitle();
        String[] description = o.getDescription();
        List<Fragment> fragments = o.getPuzzleFragments();
        String[] distractors = o.getDistractorStrings();
        instructiontext.appendText("Question: ");
        for (String tmpinst : title) {
            instructiontext.appendText(tmpinst);
        }
        instructiontext.appendText("\n");
        for (String tmpinst : description) {
            instructiontext.appendText(tmpinst);
        }
        int b = 1;
        for (Fragment s : fragments) {
            list.add(s.getLine().toString());
            System.out.println("List Added successfully " + list.toString());
            System.out.println(b++ + "\t" + s.getLine());
        }
        createMap();

        int count = 0;

        for (int i = 0; i < fragments.size(); i++) {

            if (fragments.get(i).getLineNum() != null) {
                count++;
            }
        }

        for (int i = 0; i < count; i++) {
            for (int orderCheck = 0; orderCheck < fragments.size(); orderCheck++) {
                if (fragments.get(orderCheck).getOriginalOrder() == i + 1) {
                    OriginalOrder.add(orderCheck + 1);
                    System.out.print((orderCheck + 1) + " ");
                }
            }
        }
        ObservableList<String> answerWindowCode;
        ObservableList<String> trashWindowCode;
        ObservableList<String> problemCode;
        problemCode = FXCollections.observableArrayList(list);
        answerWindowCode = FXCollections.observableArrayList(new String());
        trashWindowCode = FXCollections.observableArrayList(new String());
        problemlistview.setItems(problemCode);
        answerlistview.setItems(answerWindowCode);
        trashlistview.setItems(trashWindowCode);
        totalTimeStart = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        System.out.println(String.format("Evaluation start time: %s", dateFormat.format(startTime)));
//        answerlistview.setItems(itemsTemp);
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

        try {
            connector1();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Something Went wrong close the app and re-try \n NOTE THE ERROR: 400-BAD REQUEST");
            alert.showAndWait();
        }

        while (true) {
            // Requesting new ParsonsPuzzle from broker
            puzzle = proxy.getParsonsPuzzle();
            System.out.println("got puzzle");
            DisplayPuzzle(puzzle);
            break;
        }
    }

    public void handleKeyReleased() {
        boolean disableButtons = (problemlistview.getAccessibleText().isEmpty());
        {
//            submitButton.setDisable(disableButtons);
//            nextButton.setDisable(disableButtons);
        }

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
//                    submitButton.setDisable(false);
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
//                if(getListView().getItems().size() ==1 ) {
//            		System.out.println("hello");
//            	}
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

//                    if(itemsTemp.equals(answerlistview.getItems())) {
//                       	userSpecifiedOrder.remove(draggedIdx);
//                    }
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
                        }
                        items.add(thisIdx, db.getString());
                        items.remove(draggedIdx);
                    } else {
                        items.add(thisIdx, db.getString());
                        itemsTemp.remove(draggedIdx);
                    }
                    if (problemlistview.getItems().isEmpty()) {
                        submitButton.setDisable(false);
                    } else {
                        submitButton.setDisable(true);
                    }

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            });

            setOnDragDone(DragEvent::consume);

        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            } else {
                setText(item);

            }
        }

//public LISTCell() {
//            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//	                getListView().requestFocus();
//	                    if (getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
//	                    	getListView().getSelectionModel().clearSelection();
//	                    }
//	                    event.consume();
//	            });
//            
//            setOnDragDetected(event -> {
//                if (getItem() == null || getItem().isEmpty()) {
//                    return;
//                }
//                itemsTemp =getListView().getItems();
//                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
//                ClipboardContent content = new ClipboardContent();
//                content.putString(getItem());
//                dragboard.setContent(content);
//
//                event.consume();
//            });
//
//            setOnDragOver(event -> {
//                if (event.getDragboard().hasString() && !problemlistview.getItems().equals(getListView().getItems())) {
//                    event.acceptTransferModes(TransferMode.MOVE);
//                }
//            });
//
//            setOnDragEntered(event -> {
//                if (event.getDragboard().hasString()) {
//                    setOpacity(0.3);
//                }
//            });
//
//            setOnDragExited(event -> {
//                if (event.getDragboard().hasString()) {
//                    setOpacity(1);
//                }
//            });
//
//            setOnDragDropped(event -> {
//		txt.setText("");
//		nextButton.setDisable(true);
//            	System.out.println("DragDropppp");
//                Dragboard db = event.getDragboard();
//                boolean success = false;
//
//                if (db.hasString()) {
//                    ObservableList<String> items = getListView().getItems();
//                    int draggedIdx = itemsTemp.indexOf(db.getString());
//                    int thisIdx = items.indexOf(getItem());
//                    if(thisIdx ==-1)
//                    		thisIdx = items.size()-1;
//
//                    if(itemsTemp.equals(answerlistview.getItems())) {
//                       	userSpecifiedOrder.remove(draggedIdx);
//                    }
//                 if(getListView().getItems().equals(answerlistview.getItems())) {
//                    	int convertedID = thisIdx;
//                    	if(items.size() >2 && thisIdx >= (items.size()-1)) {
//                    		convertedID = userSpecifiedOrder.size();
//                    	}
//                    	userSpecifiedOrder.add(convertedID, indexWithCodeMapping.get(db.getString()));
//                    }
//
//                    if(items.equals(itemsTemp)) {
//                    	if(getItem().isEmpty())
//                    		return;
//                    	items.add(thisIdx,db.getString());
//                    	items.remove(draggedIdx);
//                    } else {
//                    	items.add(thisIdx, db.getString());
//                    	itemsTemp.remove(draggedIdx);
//                    }
//                    if(problemlistview.getItems().size() == 0) {
//                    	submitButton.setDisable(false);
//                    }
//                    else {
//                    	submitButton.setDisable(true);
//                    }
//
//                    success = true;
//                }
//                event.setDropCompleted(success);
//
//                event.consume();
//            });
//
//            setOnDragDone(DragEvent::consume);
//			 
//        }
//        @Override
//        protected void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//            if (empty || item == null) {
//                setGraphic(null);
//		return;
//            } else {
//		setText(item);
//
//            }
//        }
//
    }
}
