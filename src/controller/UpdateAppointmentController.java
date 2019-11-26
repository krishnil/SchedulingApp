package controller;

import database.connection;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Customer;
import model.List;
import static model.List.checkTime;
import static model.List.getHours;
import static model.List.getType;
import static model.Log.log;
import model.User;
import static time.Time.ldtToUTC;

/**
 * FXML Controller class
 *
 * @author krish
 */
public class UpdateAppointmentController implements Initializable {
    
    Stage stage;
    Parent scene;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField cusIdTxt;

    @FXML
    private DatePicker datePick;

    @FXML
    private ComboBox<String> hourCombo;
    
    @FXML
    private TextField idTxt;

    @FXML
    private TableView<Customer> cusTable;

    @FXML
    private TableColumn<Customer, Integer> cusIdCol;

    @FXML
    private TableColumn<Customer, String> cusNameCol;

    @FXML   //Links the selected customer to the appointment.
    void linkCustomer(ActionEvent event) {
        
        try{
            
            cusIdTxt.setText(String.valueOf(cusTable.getSelectionModel().getSelectedItem().getCustomerId()));
            
        }
        
        catch(NullPointerException e){
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please select a Customer.");
            alert.showAndWait();
            
        }

    }

    @FXML   //Updates the appointment in the Database. Uses Try-Catch blocks to handle exceptions.
    void onActionUpdate(ActionEvent event) {
        
        try {
            
            if (datePick.getValue() == null || hourCombo.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Form Incomplete");
                alert.setContentText("Please select Date and Time");
                alert.setGraphic(null);
                alert.showAndWait();
            } 

            if (typeComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Form Incomplete");
                alert.setContentText("Please select Appointment Type");
                alert.setGraphic(null);
                alert.showAndWait();
            } 

            LocalDate date = datePick.getValue();
            String hour = hourCombo.getValue();
            LocalDateTime ldts = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), Integer.parseInt(hour), 00);
            if (checkTime(ldts) == true){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Clash");
                alert.setContentText("An appointment already exists at this time, please select another time.");
                alert.setGraphic(null);
                alert.showAndWait();
            }
            
            else{
                
            LocalDateTime ldte = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), Integer.parseInt(hour) + 1, 00);
            Timestamp start = ldtToUTC(ldts);
            Timestamp end = ldtToUTC(ldte);

            String type = typeComboBox.getValue();
            Timestamp lastUpdate = ldtToUTC(LocalDateTime.now());
            String lastUpdateBy = User.getUserName();

            PreparedStatement ps = null;
            int customerId = Integer.parseInt(cusIdTxt.getText());
            int appointmentId = Integer.parseInt(idTxt.getText());

            String sql = "update appointment set customerId = ?, type = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ?\n" +
                         "where appointmentId = ?";

            ps = connection.getConnection().prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setString(2, type);
            ps.setTimestamp(3, start);
            ps.setTimestamp(4, end);
            ps.setTimestamp(5, lastUpdate);
            ps.setString(6, lastUpdateBy);
            ps.setInt(7, appointmentId);
            int res = ps.executeUpdate();
            if (res == 1) {//one row was affected; namely teh one that was inserted!
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed");
            }
            connection.closeConnection();
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information!");
            alert.setHeaderText("Update!");
            alert.setContentText("Appointment Updated!");

            //Uses Lambda expression to notify the user of successful update and switches back to main screen.
            alert.showAndWait().ifPresent((resp -> {
                    if (resp == ButtonType.OK) {
                        Parent main = null;
                        try {
                            log(User.getUserName() + " updated an Appointment at " + LocalDateTime.now().toString());
                            hourCombo.getItems().clear();
                            typeComboBox.getItems().clear();
                            List.clearCustomer();
                            List.clearAppointment();
                            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } 
                        
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }));  
            
            }
            
        }
            
        
        catch (NullPointerException n){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete.");
            alert.showAndWait();
        }

        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete.");
            alert.showAndWait();
        } 
        
        catch  (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete, Pick Customer!");
            alert.showAndWait();
        }
        
        
        catch (Exception ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @FXML   //Switches back to main screen after confirmation from the user.
    void onActionMain(ActionEvent event) throws IOException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Data will not be saved, continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            
            hourCombo.getItems().clear();
            typeComboBox.getItems().clear();
            List.clearCustomer();
            List.clearAppointment();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }

    }
    
    //Disables the Saturday and Sunday fields in the DatePicker.
    private Callback<DatePicker, DateCell> getDayCellFactory() {
 
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
 
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
 
                        // Disable Saturday and Sunday.
                        if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY ) {
                            setDisable(true);
                            setStyle("-fx-background-color: #096291;");
                        }
                    }
                };
            }
        };
        return dayCellFactory;
    }
    
    //Pre-Populates the fields with data from the selected appointment.
    public void setAppointment(Appointment app){
        
        typeComboBox.getSelectionModel().select(app.getType());
        cusIdTxt.setText(String.valueOf(app.getCustomerId()));
        idTxt.setText(String.valueOf(app.getAppointmentId()));
        datePick.setValue(app.getStart().toLocalDate());
        hourCombo.getSelectionModel().select(String.valueOf(app.getStart().getHour()));
        
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
       //Populates the Combo Boxes.
       hourCombo.setItems(getHours());
       typeComboBox.setItems(getType());
       
       //Populates the Customer TableView.
       cusTable.setItems(List.getAllCustomers());
        
       cusIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
       cusNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
    }    

}
