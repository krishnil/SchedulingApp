package controller;

import database.connection;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.List;
import static model.List.getAllCountry;
import static model.List.getCanada;
import static model.List.getCityId;
import static model.List.getJapan;
import static model.List.getUSA;
import static model.Log.log;
import model.User;
import static time.Time.ldtToUTC;

/**
 * FXML Controller class
 *
 * @author krish
 */
public class AddCustomerController implements Initializable {
    
    Stage stage;
    Parent scene;

    
     @FXML
    private TextField cusNameTxt;

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField add2Txt;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private TextField postTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private RadioButton yesSelect;

    @FXML
    private ToggleGroup act;

    @FXML
    private RadioButton noSelect;

    @FXML   //Populates the City combo box after the Country is selected.
    void handleCity(ActionEvent event) {
        
        cityComboBox.getItems().clear();
        if (countryComboBox.getSelectionModel().getSelectedItem() == "USA")
            cityComboBox.setItems(getUSA());
        else if (countryComboBox.getSelectionModel().getSelectedItem() == "Canada")
            cityComboBox.setItems(getCanada());
        else if (countryComboBox.getSelectionModel().getSelectedItem() == "Japan")
            cityComboBox.setItems(getJapan());

    }

    @FXML   //Inserts the customer in the Databases. Uses try-Catch blocks and Throws clause to handle exceptions.
    void onActionCreateCus(ActionEvent event) throws IOException {
        
        String address = addressTxt.getText();
        
            if (address.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("Insert Address.");
                alert.showAndWait();
            }
            
        String address2 = add2Txt.getText();
        Timestamp createDate = ldtToUTC(LocalDateTime.now());
        String createdBy = User.getUserName();
        Timestamp lastUpdate = ldtToUTC(LocalDateTime.now());
        String lastUpdateBy = User.getUserName();

        PreparedStatement ps = null;
        int generatedKey = 0;

        try {

            int postalCode = Integer.parseInt(postTxt.getText());
            int phone = Integer.parseInt(phoneTxt.getText());
            int cityId = getCityId(cityComboBox.getSelectionModel().getSelectedItem());

            String sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, address);
            ps.setString(2, address2);
            ps.setInt(3, cityId);
            ps.setInt(4, postalCode);
            ps.setInt(5, phone);
            ps.setTimestamp(6, createDate);
            ps.setString(7, createdBy);
            ps.setTimestamp(8, lastUpdate);
            ps.setString(9, lastUpdateBy);
            int res = ps.executeUpdate();
            if (res == 1) {
                System.out.println("Insert Success");
            } else {
                System.out.println("Insert Failed");
            }
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) generatedKey = rs.getInt(1);
            connection.closeConnection();
            
        }
            
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Insert Correct Format for Postal Code/ Phone Number.");
            alert.showAndWait();
        }
        
        catch (NullPointerException n){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete. Select Country and City.");
            alert.showAndWait();
        }

        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete.");
            alert.showAndWait();
        } 
        
        
        catch (Exception ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String customerName = cusNameTxt.getText();
        if (customerName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Insert Customer Name.");
            alert.showAndWait();
        }
        int active = 1;
        if (noSelect.isSelected()) active = 0;
        
        try {

            String sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = connection.getConnection().prepareStatement(sql);
            ps.setString(1, customerName);
            ps.setInt(2, generatedKey);
            ps.setInt(3, active);
            ps.setTimestamp(4, createDate);
            ps.setString(5, createdBy);
            ps.setTimestamp(6, lastUpdate);
            ps.setString(7, lastUpdateBy);
            int res = ps.executeUpdate();
            if (res == 1) {
                System.out.println("Insert Success");
            } else {
                System.out.println("Insert Failed");
            }
            connection.closeConnection();
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information!");
            alert.setHeaderText("Insert!");
            alert.setContentText("One Customer Added!");

            //Uses Lambda expression to notify the user of a successful insert and switches back to Main Screen.
            alert.showAndWait().ifPresent((resp -> {
                    if (resp == ButtonType.OK) {
                        Parent main = null;
                        try {
                            log(User.getUserName() + " added a Customer at " + LocalDateTime.now().toString());
                            countryComboBox.getItems().clear();
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

        catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Form is Incomplete.");
            alert.showAndWait();
        } 
        
        catch (Exception ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    @FXML   //Switches back to main screen after the user is notified.
    void onActionMain(ActionEvent event) throws IOException {
        
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Data will not be saved, continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            
            countryComboBox.getItems().clear();
            List.clearCustomer();
            List.clearAppointment();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        countryComboBox.setItems(getAllCountry());
        
    }    


}
