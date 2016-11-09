package com.solverpeng.ssh.converter;

import org.apache.struts2.util.StrutsTypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author solverpeng
 * @create 2016-11-08-17:55
 */
public class StringDateConverter extends StrutsTypeConverter {
    private SimpleDateFormat dateFormat;

    {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public Object convertFromString(Map map, String[] strings, Class aClass) {
        if (Date.class == aClass) {
            try {
                return dateFormat.parse(strings[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String convertToString(Map map, Object o) {
        if (o instanceof Date) {
            return dateFormat.format(o);
        }
        return null;
    }
}
