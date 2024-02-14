package com.ta.elivgo;

import org.junit.Test;

import static org.junit.Assert.*;

import com.ta.elivgo.globalfunction.DistanceConverter;


public class DistanceConverterTest {
    @Test
    public void addition_isCorrect() {

        DistanceConverter dc = new DistanceConverter();
        String result = dc.ConvertDistance("125400");
        String expected = "125.40 km";
        assertEquals(expected, result);

        result = dc.ConvertDistance("190");
        expected = "190.00 m";
        assertEquals(expected, result);
    }

}
