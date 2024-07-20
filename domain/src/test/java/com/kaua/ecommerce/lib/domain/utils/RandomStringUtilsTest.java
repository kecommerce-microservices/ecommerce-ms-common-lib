package com.kaua.ecommerce.lib.domain.utils;

import com.kaua.ecommerce.lib.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RandomStringUtilsTest extends UnitTest {

    @Test
    void givenLength_whenGenerateValue_thenReturnString() {
        int length = 10;

        String result = RandomStringUtils.generateValue(length);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(length, result.length());
    }
}
