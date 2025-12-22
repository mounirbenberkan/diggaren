package app.services;

import app.models.TrackInfo;
import app.util.Utility;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Instant;

public class RadioSverigeService {
    private Utility util ;
    private JsonObject json;

    public RadioSverigeService() {
        this.util = new Utility();
            json= new JsonObject();

    }
    public String getChanelName(String channelId) {
        String chanelsURL="https://api.sr.se/api/v2/channels?format=json";
        try {
            JsonObject json= util.get(chanelsURL);
            JsonArray jsonArray= json.getAsJsonArray("channels");
            for (JsonElement jsonElement:jsonArray) {
                JsonObject channel=jsonElement.getAsJsonObject();
                if (channel.get(("id")).getAsString().equals(channelId)) {
                    return channel.get("name").getAsString();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public TrackInfo getCurrentTrack(String channelId) {
        String songURL="https://api.sr.se/api/v2/playlists/rightnow?channelid="+channelId+"&format=json";
        try {
            json= util.get(songURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonObject playlist= json.getAsJsonObject("playlist");
        JsonObject song = playlist.getAsJsonObject("song");
        String title=song.get("title").getAsString();
        String artist=song.get("artist").getAsString();
        String rawStartTime=song.get("starttimeutc").getAsString();
        String millisStr = rawStartTime.replaceAll("/Date\\((\\d+)\\)/", "$1");
        Instant startTime = Instant.ofEpochMilli(Long.parseLong(millisStr));
        TrackInfo currentTrack=new TrackInfo();
        currentTrack.setArtist(artist);
        currentTrack.setTitle(title);
        currentTrack.setPalyedAt(startTime);
        currentTrack.setChanel(getChanelName(channelId));
        return currentTrack;
    }
    public TrackInfo getPreviousSong(String channelId) {
        String songURL="https://api.sr.se/api/v2/playlists/rightnow?channelid="+channelId+"&format=json";
        try {
            json= util.get(songURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonObject previousSong = json.getAsJsonObject("playlist").getAsJsonObject("previoussong");
        String title=previousSong.get("title").getAsString();
        String artist=previousSong.get("artist").getAsString();
        String rawStartTime=previousSong.get("starttimeutc").getAsString();
        String millisStr = rawStartTime.replaceAll("/Date\\((\\d+)\\)/", "$1");
        Instant startTime = Instant.ofEpochMilli(Long.parseLong(millisStr));
        TrackInfo prevTrack=new TrackInfo();
        prevTrack.setArtist(artist);
        prevTrack.setTitle(title);
        prevTrack.setPalyedAt(startTime);
        prevTrack.setChanel(getChanelName(channelId));
        return prevTrack;
    }

    public static void main(String[] args){
      RadioSverigeService rs = new RadioSverigeService();
      TrackInfo currentTrack = rs.getCurrentTrack("163") ;
        System.out.println(currentTrack.toString());
      TrackInfo prevTrack = rs.getPreviousSong("163");
      System.out.println(prevTrack.toString());
    }
}
