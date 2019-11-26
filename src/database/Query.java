package database;

import static database.connection.conn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import model.Appointment;
import model.Customer;
import static model.List.addAppointment;
import static model.List.addCustomer;
import static model.List.addReport1;
import static model.List.addReport2;
import static model.List.addReport3;
import model.User;
import static time.Time.stringToLDT;
import static time.Time.utcToLDT;

/**
 *
 * @author krish
 */
public class Query {
    
    private static String query;
    private static Statement statement;
    private static ResultSet result;
    
    public static void makeQuery(String q){
        
        query = q;

        try{
            
            //Create statement object
            statement = conn.createStatement();
            
            //Determine query execution
            if(query.toLowerCase().startsWith("select")){
                result = statement.executeQuery(query);
            }
            
            if(query.toLowerCase().startsWith("delete") || query.toLowerCase().startsWith("insert") || query.toLowerCase().startsWith("update"))
                statement.executeUpdate(query);
            
        }
        
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        
    }
    
    public static ResultSet getResult(){
        return result;
    }
    
    /**
     *
     * @throws SQLException
     * @throws Exception
     */
    //Retrieves all Customer information from the Database.
    public static void retrieveCustomers() throws SQLException, Exception{
        
        connection.makeConnection();
        boolean act = false;
        String sqlStatement="select customer.customerId, customer.customerName, address.address, address.address2, country.countryId, city.city, address.postalCode, address.phone, customer.active\n" +
                            "from customer, address, city, country\n" +
                            "where customer.addressId = address.addressId \n" +
                            "and address.cityId = city.cityId\n" +
                            "and country.countryId = city.countryId;";    
        
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        while(result.next()){
            
            int customerId = result.getInt("customerid");
            String customerName = result.getString("customerName");
            String address = result.getString("address");
            String address2 = result.getString("address2");
            int countryId = result.getInt("countryId");
            String city = result.getString("city");
            int postalCode = result.getInt("postalCode");
            String phone = result.getString("phone");
            int active=result.getInt("active");
            if(active == 1) act = true;
            Customer cus = new Customer(customerId, customerName, address, address2, countryId, city, postalCode, phone, act);
            addCustomer(cus);

        }
         
        connection.closeConnection();
        
    } 
    
    //Retrieves all Appointments from the Database.
    public static void retrieveAppointments() throws SQLException, Exception{
        
        connection.makeConnection();
        String sqlStatement="select appointment.appointmentId, appointment.customerId, appointment.userId, appointment.type, appointment.start, appointment.end, customer.customerName\n" +
                            "from appointment, customer\n" +
                            "where appointment.customerId = customer.customerId\n" +
                            "and appointment.userId = " + User.getUserId();          
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        while(result.next()){
             
            int appointmentId = result.getInt("appointmentid");
            int customerId = result.getInt("customerId");
            int userId = result.getInt("userId");
            String type = result.getString("type");
            String startS = result.getString("start");
            String endS = result.getString("end");
            String customerName = result.getString("customerName");
            LocalDateTime start = utcToLDT(stringToLDT(startS));
            LocalDateTime end = utcToLDT(stringToLDT(endS));
            Appointment app = new Appointment(appointmentId, customerId, userId, type, start, end, customerName);
            addAppointment(app);

        }
        
        connection.closeConnection();
        
    }
    
    //Retrieves the Appointments report from the Database.
    public static void queryReport1() throws SQLException, Exception{
        
        connection.makeConnection();
        String sqlStatement="select count(distinctrow type) , monthname(start) from appointment group by monthname(start);";    
        
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        while(result.next()){
            
            String type = result.getString("count(distinctrow type)");
            String month = result.getString("monthname(start)");
            String row = "There is/are " + type + " type(s) of Appointment(s) in " + month + ". ";
            addReport1(row);

        }
         
        connection.closeConnection();
        
    } 
    
    //Retrieves the Schedule report from the Database.
    public static void queryReport2() throws SQLException, Exception{
        
        connection.makeConnection();
        String sqlStatement="select user.userName, appointment.type, appointment.start\n" +
                            "from user, appointment\n" +
                            "where user.userId = appointment.userId\n" +
                            "order by 1, 3";    
        
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        while(result.next()){
            
            String user = result.getString("userName");
            String type = result.getString("type");
            String time = result.getString("start");
            String row = "User " + user + " has " + type + " at " + time;
            addReport2(row);

        }
         
        connection.closeConnection();
        
    } 
    
    //Retrieves the Address report from the Database.
    public static void queryReport3() throws SQLException, Exception{
        
        connection.makeConnection();
        String sqlStatement="select customer.customerName, address.address, city.city, country.country\n" +
                            "from customer,address, city, country\n" +
                            "where customer.addressId = address.addressId and address.cityId = city.cityId and city.countryId = country.countryId\n" +
                            "order by 1";    
        
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        while(result.next()){
            
            String customer = result.getString("customerName");
            String address = result.getString("address");
            String city = result.getString("city");
            String country = result.getString("country");
            String row = customer + " lives at " + address + ", " + city + ", " + country;
            addReport3(row);

        }
         
        connection.closeConnection();
        
    }  
    
}
