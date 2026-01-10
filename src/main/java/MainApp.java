import app.models.TrackInfo;
import app.services.RadioSverigeService;
import app.services.SongService;
import app.services.SpotifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.template.JavalinPebble;

public class MainApp {

    //This class start the javalin app
    public static void main(String[] args) {
        RadioSverigeService radioSverigeService = new RadioSverigeService();
        SpotifyService spotifyService = new SpotifyService();
        SongService songService= new SongService(radioSverigeService, spotifyService);
        Javalin app = Javalin.create(config->{
            ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());

                    config.jsonMapper(new JavalinJackson(mapper));
                }).before(ctx->{
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        }).start(7070);
        app.get("/", ctx -> {
            ctx.result("Song API is running");
        });
        app.get("/track/current/{channelId}", ctx -> {
            String channelId = ctx.pathParam("channelId");
            TrackInfo track = songService.getTrackWithSpotifyLink(channelId,true);
            if (track == null) {
                ctx.status(404);
                return;
            }
            System.out.println(track.getPlayedAt().toString());
            ctx.json(track);
        });
        app.get("/track/previous/{channelId}", ctx -> {
            String channelId = ctx.pathParam("channelId");
            TrackInfo track = songService.getTrackWithSpotifyLink(channelId,false);
            if (track == null) {
                ctx.status(404);
                return;
            }
            ctx.json(track);
        });
        System.out.println("API running on http://localhost:7070");
    }
}
