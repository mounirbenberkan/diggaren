package app.controllers;

import app.models.TrackInfo;
import app.services.RadioSverigeService;
import app.services.SongService;
import io.javalin.Javalin;

public class TrackController {
    private SongService songService;


    public TrackController(SongService songService){
        this.songService = songService;
    }

    public void registerEndpoints(Javalin app){
        app.get("/channels/{channelId}/track", ctx -> {
            String channelId = ctx.pathParam("channelId");

            String currentParam = ctx.queryParam("current");
            boolean current;

            if(currentParam == null){
                current = true;
            }else{
                current = Boolean.parseBoolean(currentParam);
            }

            TrackInfo track = songService.getTrackWithSpotifyLink(channelId, current);

            if(track == null){
                ctx.status(404).result("No track found");
            }else{
                ctx.json(track);
            }
        });
    }
}
