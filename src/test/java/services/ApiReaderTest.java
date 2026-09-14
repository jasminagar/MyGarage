package services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiReaderTest {

    @Test
    void apiReaderShouldReturnData() {

        ApiReader apiReader = new ApiReader();

        String result = apiReader.apiReader();

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }
}