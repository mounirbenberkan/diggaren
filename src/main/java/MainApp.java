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
        app.get("/track/current/{chanelId}", ctx -> {
            String chanelId = ctx.pathParam("chanelId");
            ctx.json(songService.getTrackWithSpotifyLink(chanelId,true));
        });
        app.get("/track/previous/{chanelId}", ctx -> {
            String chanelId = ctx.pathParam("chanelId");
            ctx.json(songService.getTrackWithSpotifyLink(chanelId,false));
        });
        System.out.println("API running on http://localhost:7070");
    }
}
