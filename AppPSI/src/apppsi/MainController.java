/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psifx;


import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;


/**
 *
 * @author himankvats
 */
public class MainController implements Controller  {
     private Node view;

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView (Node view){
        this.view = view;
    }

    @Override
    public void Show() {
        PreShowing();
         try {
             AppPSI.getNavigation().Show(this);
         } catch (Exception ex) {
             Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }
        PostShowing();
    }

    public void PreShowing()
    {

    }

    public void PostShowing()
    {

    }
    
    
}
