package controller;

import database.Query;
import database.connection;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.List;
import static model.Log.log;
import model.User;

/**
 * FXML Controller class
 *
 * @author krish
 */
public class MainScreenController implements Initializable {
    
    Stage stage;
    Parent scene;

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, Integer> customerAddressCol;

    @FXML
    private TableColumn<Customer, String> customerContactCol;

    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, String> appTypeCol;

    @FXML
    private TableColumn<Appointment, Integer> appCustomerCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appFromCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appTocol;
    
    @FXML
    private ComboBox<String> viewCombo;

    @FXML   //Provides the ability to view the TableView by month and by week.
    void viewChange(ActionEvent event) throws Exception {
        
        if (viewCombo.getSelectionModel().getSelectedItem() == "This Month"){
            
            appointmentTableView.getItems().clear();
            appointmentTableView.setItems(List.getMonthlyAppointments());

            appTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            appCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            appFromCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            appTocol.setCellValueFactory(new PropertyValueFactory<>("end"));
        }
        
        else if (viewCombo.getSelectionModel().getSelectedItem() == "This Week"){

            appointmentTableView.getItems().clear();
            appointmentTableView.setItems(List.getWeeklyAppointments());

            appTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            appCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            appFromCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            appTocol.setCellValueFactory(new PropertyValueFactory<>("end"));
        }

    }

    @FXML   //Switches to the Add Appointment Screen.
    void onActionAddAppointment(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    @FXML   //Switches to the Add Customer Screen.
    void onActionAddCustomer(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    @FXML   //Deletes the selected Appointment from the Database after confirmation from the user.
    void onActionDeleteAppointment(ActionEvent event) throws SQLException, Exception {
        
        try{
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will remove the selected Appointment, continue?");

            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK){
               
               log(User.getUserName() + " removed an appointment at " + LocalDateTime.now().toString());
               String stmt = "DELETE from appointment where appointmentId = " + appointmentTableView.getSelectionModel().getSelectedItem().getAppointmentId();
               connection.makeConnection();
               Query.makeQuery(stmt);
               List.clearAppointment();
               Query.retrieveAppointments();

            }
            
        }
        
        catch(NullPointerException e){
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please select an Appointment.");
            alert.showAndWait();
            
        }

    }

    @FXML   //Deletes the selected Customer from the Database after confirmation from the user.
    void onActionDeleteCustomer(ActionEvent event) throws SQLException, Exception {
       
        try{
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will remove the selected Customer and their Appointments, continue?");

            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK){
                
               log(User.getUserName() + " removed a customer at " + LocalDateTime.now().toString());
               String stmt = "DELETE from customer where customerId = " + customerTableView.getSelectionModel().getSelectedItem().getCustomerId();
               connection.makeConnection();
               Query.makeQuery(stmt);
               List.clearCustomer();
               Query.retrieveCustomers();
               List.clearAppointment();
               Query.retrieveAppointments();

            }
            
        }
        
        catch(NullPointerException e){
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please select a Customer.");
            alert.showAndWait();
            
        }

    }

    @FXML   //Logs out and switches to the Log-in screen.
    void onActionLoginScreen(ActionEvent event) throws IOException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will end this session, continue to Login Screen?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            
            log(User.getUserName() + " logged out at " + LocalDateTime.now().toString());
            List.clearCustomer();
            List.clearAppointment();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/LoginScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }

    }

    @FXML   //Switches to the Reports Screen.
    void onActionReport(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    @FXML   //Switches to the Update Appointment screen and populates the fields with the selected appointment.
    void onActionUpdateAppointment(ActionEvent event) throws IOException {
        try{
    
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            loader.load();

            UpdateAppointmentController UAController = loader.getController();
            UAController.setAppointment(appointmentTableView.getSelectionModel().getSelectedItem());

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        
        }
        
        catch(NullPointerException e){
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please select an Appointment.");
            alert.showAndWait();
            
        }
        
    }

    @FXML   //Switches to the Update Customer screen and populates the fields with the selected customer.
    void onActionUpdateCustomer(ActionEvent event) throws IOException {
        
        try{
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/UpdateCustomer.fxml"));
            loader.load();

            UpdateCustomerController UCController = loader.getController();
            UCController.setCustomer(customerTableView.getSelectionModel().getSelectedItem());

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        
        }
        
        catch(NullPointerException e){
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please select a Customer.");
            alert.showAndWait();
            
        }

    }
    
    ObservableList<String> view = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       //Retrieves Data from the Database 
       try {
            
            Query.retrieveAppointments();
            Query.retrieveCustomers();

            
        } 
        
        catch (Exception ex) {
            
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            
        }
       
       //Populates the View Combo Box.
       view.addAll("This Month", "This Week");
       viewCombo.setItems(view);

       //Populates the Customer Table View.
       customerTableView.setItems(List.getAllCustomers());
        
       customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
       customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
       customerContactCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
       
        
       //Populates the Appointment Table View.
       appointmentTableView.setItems(List.getAllAppointments());
       
       appTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
       appCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
       appFromCol.setCellValueFactory(new PropertyValueFactory<>("start"));
       appTocol.setCellValueFactory(new PropertyValueFactory<>("end"));
       
    }    
    
}
