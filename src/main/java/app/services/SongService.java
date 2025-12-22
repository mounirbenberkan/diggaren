package app.services;

import app.models.TrackInfo;

import java.io.IOException;

public class SongService {
    private RadioSverigeService sr;
    private SpotifyService spo;

    public SongService(RadioSverigeService sr, SpotifyService spo) {
        this.sr = sr;
        this.spo = spo;
    }

    public TrackInfo getTrackWithSpotifyLink(String channel, boolean current) {
        TrackInfo song = null;
        if(current) {
            song = sr.getCurrentTrack(channel);
        }else{
            //song = sr.getPreviousSong(channel);
        }

        if(song != null){
            String query = song.getArtist();
            String spotifyURL = null;
            try {
                spotifyURL = spo.searchArtist(query);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            song.setSpotifyLink(spotifyURL);
        }
        return song;
    }

}
