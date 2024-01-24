package fr.vannes.recordstore;

import org.junit.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.junit.Before.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import fr.vannes.recordstore.API.APIUtils;

/**
 * This class tests the API call to the MusicBrainz API.
 */
public class APICallTest {



    @Test
    public void testFetchRecordData() throws Exception {
        String barcode = "731458633129";
        String userAgent = "RecordStore/1.0 (elemouellic)";

        String jsonString = APIUtils.fetchRecordData(barcode, userAgent);

        assertNotNull("JSON string is null", jsonString);
        assertFalse("JSON string is empty", jsonString.isEmpty());

        System.out.println("JSON string: " + jsonString);

    }


    /**
     * This method tests the API call to the Cover Art Archive API.
     * The URLS are printed in the console.
     */
    @Test
    public void testDirectImageURL() {
        String testUrl = "https://coverartarchive.org/release/3653346e-670c-40ab-99aa-c4db5dc01967/front";

        String directUrl = APIUtils.getDirectImageUrl(testUrl);

        System.out.println("Test URL: " + testUrl);
        System.out.println("Direct URL: " + directUrl);

        assertNotNull("Direct URL is null", directUrl);
        assertFalse("Direct URL is empty", directUrl.isEmpty());
        assertTrue("Direct URL is not an image", directUrl.endsWith(".jpg") || directUrl.endsWith(".png"));
    }


        @Mock
        private APIUtils.OnRecordFetchedListener listener;


//        @Test
//        public void runAsync_fetchesRecordSuccessfully() {
//            String barcode = "1234567890123";
//            String userAgent = "Record Store/1.0 (by elemouellic)";
//
//            APIUtils.runAsync(barcode, userAgent, listener);
//
//            verify(listener).onRecordFetched(any(fr.vannes.recordstore.BO.Record.class));
//        }
//
//        @Test
//        public void runAsync_handlesExceptionDuringFetch() {
//            String barcode = "invalid_barcode";
//            String userAgent = "Record Store/1.0 (by elemouellic)";
//
//            APIUtils.runAsync(barcode, userAgent, listener);
//
//            verify(listener).onError(any(Exception.class));
//        }
    }

