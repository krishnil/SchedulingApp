package model;

/**
 *
 * @author krish
 */
public class  User {
    
    private static int userId;
    private static String userName;
    private static String password;

    public User() {
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        User.userId = userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        User.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    } 
    
}
