package model;

import database.Query;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.Duration;
import java.time.temporal.IsoFields;

/**
 *
 * @author krish
 */
public class List {
    
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<String> countryList = FXCollections.observableArrayList();
    private static ObservableList<String> USA = FXCollections.observableArrayList();
    private static ObservableList<String> Canada = FXCollections.observableArrayList();
    private static ObservableList<String> Japan = FXCollections.observableArrayList();
    private static ObservableList<String> Hours = FXCollections.observableArrayList();
    private static ObservableList<String> Type = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
    private static ObservableList<String> report1 = FXCollections.observableArrayList();
    private static ObservableList<String> report2 = FXCollections.observableArrayList();
    private static ObservableList<String> report3 = FXCollections.observableArrayList();
    
   
    public static void addCustomer(Customer newCustomer){
        
        allCustomers.add(newCustomer);
        
    }
    
    public static void addAppointment(Appointment newAppointment){
        
        allAppointments.add(newAppointment);
        
    }
    
    public static ObservableList<Customer> getAllCustomers() {
        
        return allCustomers;
        
    }
    
    public static ObservableList<Appointment> getAllAppointments() {
            
        return allAppointments;
        
    }
    
    public static ObservableList<Appointment> getWeeklyAppointments() throws Exception{
        
        clearAppointment();
        clearWeeklyAppointments();
        Query.retrieveAppointments();
        for(Appointment app : List.getAllAppointments()){
            int week = app.getStart().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int weekNow = LocalDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            if (week == weekNow){
                weeklyAppointments.add(app);
                System.out.println("found 1");
            }
        }
        return weeklyAppointments;
        
    }
    
    public static void clearWeeklyAppointments(){
        
        weeklyAppointments.clear();
        
    }
    
    public static ObservableList<Appointment> getMonthlyAppointments() throws Exception{
        
        clearAppointment();
        clearMonthlyAppointments();
        Query.retrieveAppointments();
        for(Appointment app : List.getAllAppointments()){
            int month = app.getStart().getMonthValue();
            int monthNow = LocalDateTime.now().getMonthValue();
            int year = app.getStart().getYear();
            int yearNow = LocalDateTime.now().getYear();
            if ((month == monthNow) && (year == yearNow)){
                monthlyAppointments.add(app);
                System.out.println("found 1");
            }
        }
        return monthlyAppointments;
        
    }
    
    public static void clearMonthlyAppointments(){
        
        monthlyAppointments.clear();
        
    }
    
    public static void clearCustomer(){
        
        allCustomers.clear();
        
    }
    
    public static void clearAppointment(){
        
        allAppointments.clear();
        
    }
     
    
    public static Customer lookupCustomer(int customerId){  
        for(Customer customer : List.getAllCustomers()){
            if(customer.getCustomerId() == customerId)
                return customer;        
        }
        return null;
    }
    
    public static int getCityId(String city){
        
        int cityId = 0;
        switch(city){
            case "New York": cityId = 1;
                break;
            case "Los Angeles": cityId = 2;
                break;
            case "Toronto": cityId = 3;
                break;
            case "Vancouver": cityId = 4;
                break;
            case "Tokyo": cityId = 5;
                break;
            case "Osaka": cityId = 6;
                break;
            default: cityId = 0;
                break; 
        }
        
        return cityId;

    }
    
    public static ObservableList<String> getAllCountry() {
        
        countryList.add("USA");
        countryList.add("Canada");
        countryList.add("Japan");
        return countryList;
        
    }
    
    public static ObservableList<String> getUSA() {
        
        USA.add("New York");
        USA.add("Los Angeles");
        return USA;
        
    }
    
    public static ObservableList<String> getCanada() {
        
        Canada.add("Toronto");
        Canada.add("Vancouver");
        return Canada;
        
    }
    
    public static ObservableList<String> getJapan() {
        
        Japan.add("Tokyo");
        Japan.add("Osaka");
        return Japan;
        
    }
    
    public static ObservableList<String> getHours() {
        
        Hours.addAll("08", "09", "10", "11", "12", "13", "14", "15", "16", "17");
        return Hours;
        
    }
    
    public static ObservableList<String> getType() {
        
        Type.addAll("Planning", "Presentation", "Scrum", "Status Update");
        return Type;
        
    }
    
    public static boolean checkTime(LocalDateTime start){   
        
        for(Appointment app : List.getAllAppointments()){
            if(app.getStart().equals(start)){
                return true;
            }
        }
        return false;
        
    }
    
    public static Appointment checkAppointment(){
        
        for(Appointment app : List.getAllAppointments()){
            Duration duration = Duration.between(LocalDateTime.now(), app.getStart());
            long minutes = duration.toMinutes();
            if (minutes >=0 && minutes <= 15 ){
                System.out.println("found at " + minutes);
                return app;
            }
        }
        return null;
        
    }
    
    public static void addReport1(String row){
        
        report1.add(row);
        
    }
    
    public static void addReport2(String row){
        
        report2.add(row);
        
    }
    
    public static void addReport3(String row){
        
        report3.add(row);
        
    }
    
    public static void clearReport1(){
        
        report1.clear();
        
    }
    
    public static void clearReport2(){
        
        report2.clear();
        
    }
    
    public static void clearReport3(){
        
        report3.clear();
        
    }
    
    public static ObservableList<String> getReport1() {
        
        return report1;
        
    }
    
    public static ObservableList<String> getReport2() {
        
        return report2;
        
    }
    
    public static ObservableList<String> getReport3() {
        
        return report3;
        
    }
    
    public static String retrieveReport1(){
        
        String report = "";
        for(String row : List.getReport1()){
            report = report + "\n" + row;
        }
        return report;
        
    }
    
    public static String retrieveReport2(){
        
        String report = "";
        for(String row : List.getReport2()){
            report = report + "\n" + row;
        }
        return report;
        
    }
    
    public static String retrieveReport3(){
        
        String report = "";
        for(String row : List.getReport3()){
            report = report + "\n" + row;
        }
        return report;
        
    }
    
}
