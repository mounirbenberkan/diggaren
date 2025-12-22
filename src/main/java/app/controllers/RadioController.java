package app.controllers;

import app.models.TrackInfo;
import app.services.RadioSverigeService;
import app.services.SpotifyService;
import io.javalin.Javalin;

import io.javalin.http.Context;

public class RadioController {
    private RadioSverigeService radioSverigeService;
    private SpotifyService spotifyService;
    private Javalin app;
    
    public RadioController(Javalin app,RadioSverigeService radioSverigeService, SpotifyService spotifyService) {
        this.app = app;
        this.radioSverigeService = radioSverigeService;
        this.spotifyService = spotifyService;
    }
    public void getDefaultTrack(Context ctx, String chanelId) {
        TrackInfo track = radioSverigeService.getCurrentTrack("");
        String trackName= track.getArtist();
        if (track != null) {
            ctx.json(track);
        }else {
            ctx.status(404).result("Track not found");
        }
    }
}
