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
import model.Customer;
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
public class UpdateCustomerController implements Initializable {
    
    Stage stage;
    Parent scene;

    @FXML
    private TextField idTxt;

    
    @FXML
    private TextField cusNameTxt;

    @FXML
    private TextField addTxt;

    @FXML
    private TextField add2Txt;

    @FXML
    private ComboBox<String> countryCombo;

    @FXML
    private ComboBox<String> cityCombo;

    @FXML
    private TextField postTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private RadioButton yesBtn;

    @FXML
    private ToggleGroup act;

    @FXML
    private RadioButton noBtn;

    @FXML   //Populates the City Combo Box after the Country is selected.
    void handleCity(ActionEvent event) {
        
        cityCombo.getItems().clear();
        if (countryCombo.getSelectionModel().getSelectedIndex() == 0)
            cityCombo.setItems(getUSA());
        else if (countryCombo.getSelectionModel().getSelectedIndex() == 1)
            cityCombo.setItems(getCanada());
        else if (countryCombo.getSelectionModel().getSelectedIndex() == 2)
            cityCombo.setItems(getJapan());

    }


    @FXML   //Updates the customer information in the database. Uses try-catch blocks for exception controls.
    void onActionUpdate(ActionEvent event) {
        
        String address = addTxt.getText();
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
            int cityId = getCityId(cityCombo.getSelectionModel().getSelectedItem());

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
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed");
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
        if (noBtn.isSelected()) active = 0;
        
      
        try {
            
            int customerId = Integer.parseInt(idTxt.getText());
            
            String sql = "update customer set customerName = ?, addressId = ?, active = ?, lastUpdate = ?, lastUpdateBy = ?\n" +
                         "where customerId = ?";

            ps = connection.getConnection().prepareStatement(sql);
            ps.setString(1, customerName);
            ps.setInt(2, generatedKey);
            ps.setInt(3, active);
            ps.setTimestamp(4, lastUpdate);
            ps.setString(5, lastUpdateBy);
            ps.setInt(6, customerId);
            int res = ps.executeUpdate();
            if (res == 1) {
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed");
            }
            connection.closeConnection();
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information!");
            alert.setHeaderText("Update!");
            alert.setContentText("Customer information Updated!");

            //Uses Lambda expression to notify the user of successful update and switches back to main screen.
            alert.showAndWait().ifPresent((resp -> {
                    if (resp == ButtonType.OK) {
                        Parent main = null;
                        try {
                            log(User.getUserName() + " updated Customer information at " + LocalDateTime.now().toString());
                            countryCombo.getItems().clear(); 
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

    
    @FXML   //Switches back to main screen after confirmation from the user. Data is not saved.
    void onActionMain(ActionEvent event) throws IOException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Data will not be saved, continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            
            countryCombo.getItems().clear();
            List.clearCustomer();
            List.clearAppointment();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }
        

    }

    //Pre-populates the fields with the information from the selected Customer.
    public void setCustomer(Customer cus){
       
       idTxt.setText(String.valueOf(cus.getCustomerId()));
       cusNameTxt.setText(cus.getCustomerName());
       addTxt.setText(cus.getAddress());
       add2Txt.setText(cus.getAddress2());
       postTxt.setText(String.valueOf(cus.getPostalCode()));
       phoneTxt.setText(cus.getPhone());
       if (cus.isActive()) yesBtn.setSelected(true);
       else noBtn.setSelected(true);
       countryCombo.getSelectionModel().select(cus.getCountryId() - 1);
       cityCombo.getSelectionModel().select(cus.getCity());
       
        
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Populates the Country Combo Box.
        countryCombo.setItems(getAllCountry());

    }    

}
