package com.ta.elivgo;

import static org.junit.Assert.*;

import com.ta.elivgo.globalfunction.EpochConverter;

import org.junit.Test;

public class EpochConvertertest {

    @Test
    public void addition_isCorrect() {
        EpochConverter ec = new EpochConverter();
        String result = ec.fromEpoch(1696512027, "dd/MM/yyyy");
        String expected = "05/10/2023";
        assertEquals(expected, result);

        result = ec.fromEpoch(1696512027, "dd/MM/yyyy HH:mm:ss");
        expected = "05/10/2023 20:20:27";
        assertEquals(expected, result);

        result = ec.toEpoch("2023-10-05T20:20:27Z");
        String expected2 = "1696512027";
        assertEquals(expected2, result);

    }

}
