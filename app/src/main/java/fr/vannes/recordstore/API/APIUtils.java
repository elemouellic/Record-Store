package fr.vannes.recordstore.API;

import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import fr.vannes.recordstore.BO.Record;
import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Artist;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import fr.vannes.recordstore.BO.Record;

/**
 * This class contains static methods used to call the MusicBrainz API.
 */
public class APIUtils {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Gson gson = new Gson();

    public interface OnRecordFetchedListener {
        void onRecordFetched(Record record);

        void onError(Exception e);
    }

    /**
     * This method returns the URL of the MusicBrainz API to search for a record based on its barcode.
     */
    public static String barcodeRecord(String barcode) {
        return "https://musicbrainz.org/ws/2/release/?query=barcode:" + barcode + "&fmt=json";
    }


    /**
     * This method runs an asynchronous task to fetch the data of a record from the MusicBrainz API based on its barcode.
     * @param barcode The barcode of the record.
     * @param userAgent The user agent of the application.
     * @param listener The listener to handle the result of the API call.
     */
    public static void runAsync(String barcode, String userAgent, OnRecordFetchedListener listener) {
        executorService.execute(() -> {
            try {
                String jsonString = fetchRecordData(barcode, userAgent);
                Record record = deserializeRecord(jsonString, listener);
                listener.onRecordFetched(record);
            } catch (Exception e) {
                listener.onError(e);
            }
        });
    }

    /**
     * This method fetches the data of a record from the MusicBrainz API based on its barcode.
     * @param barcode The barcode of the record.
     * @param userAgent The user agent of the application.
     * @return The JSON response from the MusicBrainz API.
     * @throws Exception If the request fails.
     */
    public static String fetchRecordData(String barcode, String userAgent) throws Exception {
        Request request = new Request.Builder()
                .url(barcodeRecord(barcode))
                .addHeader("User-Agent", userAgent)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                return response.body().string();
            } else {
                throw new Exception("Request failed with HTTP error code: " + response.code());
            }
        } catch (Exception e) {
            throw new Exception("Error during API call", e);
        }
    }

    /**
     * This method deserializes the JSON response from the MusicBrainz API to a Record object.
     * @param jsonString The JSON response from the MusicBrainz API.
     * @return The Record object.
     */
    private static Record deserializeRecord(String jsonString, OnRecordFetchedListener listener) {
        JsonArray releasesArray = gson.fromJson(jsonString, JsonObject.class)
                .getAsJsonArray("releases");

        // Check if releasesArray is empty
        if (releasesArray.size() == 0) {
            // No record found for the given barcode
            listener.onError(new Exception("Aucun disque trouvé pour ce code-barres"));
            return null;
        }

        for (JsonElement releaseElement : releasesArray) {
            JsonObject releaseObject = releaseElement.getAsJsonObject();
            // Retrieve the ID from the JSON response
            String id = releasesArray.size() > 0
                    ? releasesArray.get(0).getAsJsonObject().get("id").getAsString()
                    : "";
            // Retrieve the title from the JSON response
            String title = releasesArray.size() > 0
                    ? releasesArray.get(0).getAsJsonObject().get("title").getAsString()
                    : "";
            // Retrieve the picture URL from the JSON response
            String coverArtArchiveUrl = releasesArray.size() > 0
                    ? "https://coverartarchive.org/release/" + id + "/front"
                    : "https://simplyahouse.elemouellic.tech/public/img/emptycover.png";
            String directImageUrl = releasesArray.size() > 0
                    ? getDirectImageUrl(coverArtArchiveUrl)
                    : "https://simplyahouse.elemouellic.tech/public/img/emptycover.png";

            if (directImageUrl == null) {
                directImageUrl = "https://simplyahouse.elemouellic.tech/public/img/emptycover.png";
            }

            // Retrieve the type from the JSON response
            String type = "";
            // Loop through the media array to retrieve the format
            JsonArray mediaArray = releaseObject.getAsJsonArray("media");
            if (mediaArray != null && mediaArray.size() > 0) {
                JsonObject mediaObject = mediaArray.get(0).getAsJsonObject();

                type = mediaObject.has("format") && !mediaObject.get("format").isJsonNull()
                        ? mediaObject.get("format").getAsString()
                        : "";
            }

            // Retrieve the barcode from the JSON response
            String barcode = releasesArray.size() > 0
                    ? releasesArray.get(0).getAsJsonObject().has("barcode")
                    ? releasesArray.get(0).getAsJsonObject().get("barcode").getAsString()
                    : ""
                    : "";



            // Retrieve the artist from the JSON response
            Artist artist = new Artist();
            JsonArray artistArray = releaseObject.getAsJsonArray("artist-credit");

            for (JsonElement artistElement : artistArray) {
                JsonObject artistObject = artistElement.getAsJsonObject();
                artist.setId(artistArray.size() > 0
                        ? artistArray.get(0).getAsJsonObject().has("artist")
                        ? artistArray.get(0).getAsJsonObject().getAsJsonObject("artist").has("id")
                        ? artistArray.get(0).getAsJsonObject().getAsJsonObject("artist").get("id").getAsString()
                        : ""
                        : ""
                        : "");
                artist.setName(artistArray.size() > 0
                        ? artistArray.get(0).getAsJsonObject().has("artist")
                        ? artistArray.get(0).getAsJsonObject().getAsJsonObject("artist").has("name")
                        ? artistArray.get(0).getAsJsonObject().getAsJsonObject("artist").get("name").getAsString()
                        : ""
                        : ""
                        : "");
            }

            return new Record(id, title, directImageUrl, type, barcode, artist);
        }

        return null;
    }


    /**
     * This method returns the direct URL of the image from the Cover Art Archive based on the given URL.
     *
     * @param coverArtArchiveUrl The URL of the image from the Cover Art Archive.
     * @return The direct URL of the image.
     */
    public static String getDirectImageUrl(String coverArtArchiveUrl) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(coverArtArchiveUrl)
                .build();


        try (Response response = client.newCall(request).execute()) {

            // Suivre la redirection
            if (response.isSuccessful()) {
                return response.request().url().toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;


    }



}
