import app.services.RadioSverigeService;
import app.services.SongService;
import app.services.SpotifyService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinPebble;

public class MainApp {

    //This class start the javalin app
    public static void main(String[] args) {
        RadioSverigeService radioSverigeService = new RadioSverigeService();
        SpotifyService spotifyService = new SpotifyService();
        SongService songService= new SongService(radioSverigeService, spotifyService);
        Javalin app = Javalin.create(config->{
            config.staticFiles.add("/static", Location.CLASSPATH);

            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });

            JavalinPebble.init();
        }).start(5500);
        app.get("/track/current/{chanelId}", ctx -> {
            String chanelId = ctx.pathParam("chanelId");
            ctx.json(songService.getTrackWithSpotifyLink(chanelId,true));
        });
        app.post("/track/previous/{chanelId}", ctx -> {
            String chanelId = ctx.pathParam("chanelId");
            ctx.json(songService.getTrackWithSpotifyLink(chanelId,false));
        });
    }
}
