package time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author krish
 */
public class Time {
    
    //Converts String DateTime to LocalDateTime.
    public static LocalDateTime stringToLDT (String time) throws ParseException{
        
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss.S"); 
        String ldtString = time;
        LocalDateTime ldtime = LocalDateTime.parse(ldtString, df);
        return ldtime;
                
    }
    
    //Converts LocalDateTime to UTC Timestamp to insert into the Database.
    public static Timestamp ldtToUTC (LocalDateTime time){
        
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = time.atZone(zid);
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        time = utc.toLocalDateTime();
        Timestamp timeUTC = Timestamp.valueOf(time);
        return timeUTC;
        //insert to database
        
    }
    
    //Converts LocalDateTime retrieved from the Database to LocalDateTime for Display.
    public static LocalDateTime utcToLDT (LocalDateTime time){

        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = time.atZone(ZoneId.of("UTC"));
        ZonedDateTime newZDT = zdt.withZoneSameInstant(zid);
        return newZDT.toLocalDateTime();
    
    }
    
}