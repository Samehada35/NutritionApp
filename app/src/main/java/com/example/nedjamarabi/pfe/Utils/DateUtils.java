package com.example.nedjamarabi.pfe.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Samy on 16/02/2018.
 */

public abstract class DateUtils {
    
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    
    public static Date parse(String source) throws ParseException {
        return formatter.parse(source);
    }
    
    public static String stringify(Date date) {
        return formatter.format(date);
    }
    
}
