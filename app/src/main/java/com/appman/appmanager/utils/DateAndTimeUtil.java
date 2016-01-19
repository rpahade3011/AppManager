package com.appman.appmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rudhraksh.pahade on 15-01-2016.
 */
public class DateAndTimeUtil {

    /**
     * METHOD TO CONVERT DATE IN HUMAN READABLE FORM OF DD/MM/YYYY
     * @param date long
     * @return new_date String
     */

    public static String convertSMSDate(long date){
        String new_date = "";
        try{
            Date date1 = new Date(date);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            new_date = sdf.format(date1);

        }catch (Exception e){
            e.getMessage().toString();
        }
        return new_date;
    }
}
