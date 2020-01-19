package com.example.apple.marvelapi;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class NullPointerTest {

    @Test(expected = NullPointerException.class)
    public void nullTest() {
        String stringr = null;
        assertTrue(stringr.isEmpty());
    }
}
