package com.edifecs.epp.flexfields.jpa.helper;

import com.edifecs.epp.flexfields.model.FieldType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sandeep.kath on 5/12/2014.
 */
public class Validator {
    public static Boolean regExMatched(String regularExpression, String input) {
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static Boolean validateValue(FieldType fieldType, String value) {
        Object result;
        try {
            switch (fieldType) {
                case DOUBLE:
                    Float.parseFloat(value);
                    break;
                case LONG:
                    Long.parseLong(value);
                    break;
                case DATE:
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
                    sdf.parse(value);
                    break;
                case BOOLEAN:
                    Boolean.parseBoolean(value);
                    break;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        } catch (ParseException parseException) {
            return false;
        }
    }

}
