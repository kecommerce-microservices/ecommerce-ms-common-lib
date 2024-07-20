package com.kaua.ecommerce.lib.domain.utils;

import com.kaua.ecommerce.lib.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class IdentifierUtilsTest extends UnitTest {

    @Test
    void givenUUID_whenGetUUIDAsBytes_thenReturnByteArray() {
        UUID uuid = UUID.randomUUID();

        byte[] bytes = IdentifierUtils.getUUIDAsBytes(uuid);

        Assertions.assertNotNull(bytes);
        Assertions.assertEquals(16, bytes.length);
    }

    @Test
    void givenByteArray_whenBytesToUUID_thenReturnUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = IdentifierUtils.getUUIDAsBytes(uuid);

        UUID result = IdentifierUtils.bytesToUUID(bytes);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(uuid, result);
    }

    @Test
    void givenUUID_whenGenerateNewId_thenReturnString() {
        String result = IdentifierUtils.generateNewId();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(36, result.length());
    }

    @Test
    void givenUUID_whenGenerateNewIdWithoutHyphen_thenReturnString() {
        String result = IdentifierUtils.generateNewIdWithoutHyphen();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(32, result.length());
    }

    @Test
    void givenUUID_whenGenerateNewUUID_thenReturnUUID() {
        UUID result = IdentifierUtils.generateNewUUID();

        Assertions.assertNotNull(result);
    }
}
