package app.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.HttpResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class SpotifyService {
    private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
    private String accessToken;
    private Instant accessTokenExpires;
    private final String baseUrl = "https://api.spotify.com/v1/search";

    public String searchArtist(String query) throws InterruptedException, IOException, InterruptedException {
        JsonArray items = search(query, "artist");
        String spotifyURL = null;
        if(!items.isEmpty()){
            for(JsonElement item : items){
                JsonObject artist = item.getAsJsonObject();
                String artistName =  artist.get("name").getAsString();
                spotifyURL = artist.getAsJsonObject("external_urls").get("spotify").getAsString();
                break;

            }
        }else{
            System.out.println("No artist found");
        }
        return spotifyURL;
    }

    public void searchTrack(String query) throws InterruptedException, IOException, InterruptedException {
        JsonArray items = search(query, "track");
        if(!items.isEmpty()){
            for(JsonElement item : items){
                JsonObject track = item.getAsJsonObject();
                String trackName =  track.get("name").getAsString();
                String SpotifyUrl = track.getAsJsonObject("external_urls").get("spotify").getAsString();
                System.out.println(trackName);
                System.out.println("________________");

            }
        }else{
            System.out.println("No tracks found");
        }
    }

    public JsonArray search(String query, String type) throws InterruptedException, IOException, InterruptedException {
        String token = getAccessToken();
        String enCodedQuery = java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);

        String url = baseUrl + "?q=" + enCodedQuery + "&type=" + type + "&limit=10";

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).
                header("Authorization", "Bearer " + token).GET().build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject results = json.getAsJsonObject(type + "s");
            return results.getAsJsonArray("items");
        }else{
            System.out.println("Error: " + response.statusCode() + " " + response.body());
            return new JsonArray();
        }
    }

    public String getAccessToken(){
        if (accessToken == null || Instant.now().isAfter(accessTokenExpires)){
            try {
                refreshAccessToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return accessToken;
    }

    private void refreshAccessToken() throws IOException, InterruptedException {
        //Create a Base64-encoded string of clientId and clientSecret
        String authorization = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        //HTTP POST request to Spotify token endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + authorization)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        //Send the request with HttpClient
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            int expiresIn = json.get("expires_in").getAsInt();
            accessTokenExpires = Instant.now().plusSeconds(expiresIn - 60);
        }else{
            throw new HttpResponseException(response.statusCode(), response.body());
        }

    }

    public static void main(String[] args){
        SpotifyService service = new SpotifyService();

        try{
            service.searchArtist("");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
