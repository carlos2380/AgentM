package com.example.agentm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.agentm.Patterns.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/*
TOKEN

eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdGFibGVfc2lkIjoic2lkOmRkZjExZDQzN2M5NzA1Y2Y3MjA1N2I0YjU2NDBjYzhlIiwic3ViIjoic2lkOjdlNDFiMzVjODVjYjUzMDg2NjUwMTUzMjU2MTdjODE3IiwiaWRwIjoiYWFkIiwidmVyIjoiMyIsImlzcyI6Imh0dHBzOi8vYWdlbnRtLmF6dXJld2Vic2l0ZXMubmV0LyIsImF1ZCI6Imh0dHBzOi8vYWdlbnRtLmF6dXJld2Vic2l0ZXMubmV0LyIsImV4cCI6MTQ5NzM3NjE0OSwibmJmIjoxNDk0Nzg0MTQ5fQ.SHfPPfBUgSyobdbpuAX0eAtLmOlAPZYYPNc26dqs1ns


sid:7e41b35c85cb5308665015325617c817
 */
@RunWith(AndroidJUnit4.class)
public class UtilsTest {
    @Test
    public void testSimpleDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        String sDate = "21/12/2012";
        assertEquals(date, Utils.stringToDate(sDate));
        assertEquals(sDate, Utils.dateToString(date));
    }

    @Test
    public void testHour() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = sdf.parse("14:30");
        String sHour = "14:30";
        assertEquals(date, Utils.hourToDate(sHour));
        assertEquals(sHour, Utils.hourToString(date));
    }

    @Test
    public void testCompleteDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = sdf.parse("21/12/2012 14:30");
        String sDate = "21/12/2012 14:30";
        assertEquals(date, Utils.completeDateToDate(sDate));
        assertEquals(sDate, Utils.completeDateToString(date));
    }
}
