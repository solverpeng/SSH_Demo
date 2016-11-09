package com.solverpeng.ssh.test;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author solverpeng
 * @create 2016-11-08-19:40
 */
public class SSHTest {



    @Test
    public void testSimpleDateFormat() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = "1991-12-12";
        Date date = simpleDateFormat.parse(dateStr);
        System.out.println(date);

    }

}
