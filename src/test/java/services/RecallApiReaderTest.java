package services;

import dto.RecallResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecallApiReaderTest {

    @Test
    void apiReaderShouldReturnData() {

        RecallApiReader apiReader = new RecallApiReader();

        String result = apiReader.apiReader();

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    void apiReaderShouldReturnNhtsaResponse() {

        RecallApiReader apiReader = new RecallApiReader();

        String result = apiReader.apiReader();

        assertNotNull(result);
        assertTrue(result.contains("results"));
    }

    @Test
    void convertFromJsonToDTO(){
        RecallApiReader apiReader = new RecallApiReader();
        String json = apiReader.apiReader();
        RecallResponseDto result = apiReader.convertFromJson(json);
        assertNotNull(result);
        assertEquals(2, result.getCount());
        assertEquals("Results returned successfully", result.getMessage());
        assertNotNull(result.getResults()); assertEquals(2
                , result.getResults().size());
    }

    @Test
    void convertFromJsonToRecallDTO(){
        RecallApiReader apiReader = new RecallApiReader();
        String json = apiReader.apiReader();
        RecallResponseDto result = apiReader.convertFromJson(json);
        assertNotNull(result); assertFalse(result.getResults().isEmpty());
        assertEquals( "Honda (American Honda Motor Co.)", result.getResults().get(0).getManufacturer() );
        assertEquals( "19V182000", result.getResults().get(0).getNhtsaCampaignNumber() );
        assertEquals( "2012", result.getResults().get(0).getModelYear() );
        assertEquals( "ACURA", result.getResults().get(0).getMake() );
        assertEquals( "RDX", result.getResults().get(0).getModel() );
    }
}