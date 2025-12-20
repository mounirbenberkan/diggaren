package app.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Utility {
    private HttpClient client;
    private Gson gson;

    public Utility() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
    public JsonObject get(String url) throws IOException, InterruptedException {
        HttpRequest request=  HttpRequest.newBuilder().uri(URI.create(url))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        JsonReader reader = new JsonReader(new StringReader(body));
        reader.setLenient(true);
        if (response.statusCode() == 404 || body == null || body.isEmpty()) {
            throw new IOException("Failed to retrieve " + url);
        }
        return gson.fromJson(reader, JsonObject.class);

    }
    public static void main(String[] args){
        Utility utility = new Utility();
        JsonObject json= null;
        try {
            json = utility.get("https://api.sr.se/api/v2/playlists/rightnow?channelid=2576&format=json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json.toString());
    }
}
