package controller;

import static database.Query.queryReport1;
import static database.Query.queryReport2;
import static database.Query.queryReport3;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.List;
import static model.List.retrieveReport1;
import static model.List.retrieveReport2;
import static model.List.retrieveReport3;

/**
 * FXML Controller class
 *
 * @author krish
 */
public class ReportsController implements Initializable {
    
    Stage stage;
    Parent scene;
    

    @FXML
    private TextArea appointmentsTxt;

    @FXML
    private TextArea scheduleTxt;

    @FXML
    private TextArea labelTxt;

    @FXML
    void onActionLog(ActionEvent event) {

    }

    @FXML   //Switches back to the Main Screen.
    void onActionMain(ActionEvent event) throws IOException {
        
        List.clearCustomer();
        List.clearAppointment();
        List.clearReport1();
        List.clearReport2();
        List.clearReport3();
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        //Retrieves the Appointment report from the database and populates the Text Area.
        try {
            queryReport1();
                    } catch (Exception ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        appointmentsTxt.setText(retrieveReport1());
        
        
        //Retrieves the Schedule report from the database and populates the Text Area.
        try {
            queryReport2();
                    } catch (Exception ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        scheduleTxt.setText(retrieveReport2());
        
        
        //Retrieves the Address report from the database and populates the Text Area.
        try {
            queryReport3();
                    } catch (Exception ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        labelTxt.setText(retrieveReport3());
        
    }    
 
}
