/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

/**
 *
 * @author himankvats
 */
import javafx.scene.Node;

public interface Controller {
    Node getView();
    void setView(Node view);

    void Show();
}
