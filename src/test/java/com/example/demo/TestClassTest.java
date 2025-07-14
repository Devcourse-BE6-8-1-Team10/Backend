package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestClassTest {

    @Test
    @DisplayName("더하기 테스트")
    public void addTest() {
        // given
        TestClass testClass = new TestClass();

        // when
        int result = testClass.add(2, 3);

        // then
        assertEquals(5, result);
    }

}