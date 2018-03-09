package com.pfsoft.rnotes.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeTest {

    Change forTest = new Change("AAAAAA");
    @BeforeEach void setUp() {
        forTest.setMessage("BUG#123");
        forTest.setFullMessage("BUG#123 fdfdfd323 3232+3 234 \r\n E#321");
    }

    @Test void getGerritLink() {
        assertEquals("http://192.168.0.2:9091/r/#/q/AAAAAA", forTest.getGerritLink().getHref());
    }

    @Test void getTpLink() {
        assertEquals(2, forTest.getTpLinks().size());
        assertEquals("http://tp.pfsoft.net/entity/123", forTest.getTpLinks().get(0).getHref());
        assertEquals("http://tp.pfsoft.net/entity/321", forTest.getTpLinks().get(1).getHref());
    }
}