package controller;

import database.Query;
import database.connection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.List;
import static model.List.checkAppointment;
import static model.Log.log;
import model.User;

/**
 *
 * @author krish
 */
public class LoginScreenController implements Initializable {
    
    Stage stage;
    Parent scene;
    
    @FXML
    private Label titleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTxt;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTxt;

    @FXML
    private Button loginBtn;

    @FXML
    private Button exitBtn;

    @FXML
    void onActionExit(ActionEvent event) {
        
        System.exit(0);

    }
    
    //Main Login Screen, checks username and password and switches to Main Screen if Login is Successful,
    @FXML
    void onActionLogin(ActionEvent event) throws IOException, Exception {
        
        connection.makeConnection();
        String sqlStatement="select * from user where userName = '" + usernameTxt.getText() + "' and password = '" + passwordTxt.getText() + "'";          
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();

        String check = "";

        while(result.next()){

            int userId = result.getInt("userid");
            String userName = result.getString("userName");
            String password = result.getString("password");
            User.setUserId(userId);
            User.setUserName(userName);
            User.setPassword(password);
            System.out.println(User.getUserName() + ", " + User.getPassword() + ", " + User.getUserId());      
            check = User.getUserName();
            
        }
        
        connection.closeConnection();
            
        if (check.isEmpty()) {
            
                Alert alert = new Alert(Alert.AlertType.ERROR);
                if(Locale.getDefault().getDisplayCountry() == "Japan"){
                    
                    alert.setTitle("エラー");
                    alert.setHeaderText("ユーザーネームまたはパスワードが違います");
                    alert.setContentText("正しいユーザー名またはパスワードを入力してください. 入力では大文字と小文字が区別されます.");
                            
                }
                
                else{
                    
                    alert.setTitle("Error");
                    alert.setHeaderText("Incorrect Username or Password!");
                    alert.setContentText("Please enter correct Username and Password. Input is case sensitive.");
                    
                }
                
                alert.setGraphic(null);
                alert.showAndWait();
            
        }

        else {
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                
                if(Locale.getDefault().getDisplayCountry() == "Japan"){ 
                    alert.setTitle("警戒!");
                    alert.setHeaderText("ログイン成功!");
                    alert.setContentText("こんにちは " + User.getUserName());
                    Query.retrieveAppointments();
                    Appointment checkApp = checkAppointment();
                    if (checkApp != null){
                        Alert alertApp = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("アラート!");
                        alert.setHeaderText("今後の予定");
                        alert.setContentText("あなたとの約束があります " + checkApp.getCustomerName() + " で " + checkApp.getStart().format(DateTimeFormatter.ISO_TIME));
                        alert.showAndWait(); 
                    }
                    else
                    alert.showAndWait();
                }
                
                else {
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Login Successful");
                    alert.setContentText("Hello " + User.getUserName()); 
                    Query.retrieveAppointments();
                    Appointment checkApp = checkAppointment();
                    if (checkApp != null){
                        Alert alertApp = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Alert!");
                        alert.setHeaderText("Upcoming Appointment");
                        alert.setContentText("You have an appointment with " + checkApp.getCustomerName() + " at " + checkApp.getStart().format(DateTimeFormatter.ISO_TIME)); 
                        alert.showAndWait(); 
                    }
                    else
                    alert.showAndWait();
                    
                }
                
              
            List.clearCustomer();
            List.clearAppointment();
            log(User.getUserName() + " logged in at " + LocalDateTime.now().toString());
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }

    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
       //Uncomment the Line below to Change language.
       Locale.setDefault(new Locale("jp", "JP"));
       System.out.println(Locale.getDefault().getDisplayCountry());
        
       if(Locale.getDefault().getDisplayCountry() == "Japan") {
       
            titleLabel.setText("予定のスケジューリング");
            usernameLabel.setText("ユーザー名:");
            passwordLabel.setText("パスワード:");
            loginBtn.setText("入る");
            exitBtn.setText("出口");
       }
        
    }    
    
}
