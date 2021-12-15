package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SpotifyAudioSourceManager implements AudioSourceManager {

    public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist)/(?<identifier>[a-zA-Z0-9-_]+)");


    private final AudioPlayerManager manager;
    private final SpotifyApi spotify;

    public SpotifyAudioSourceManager(AudioPlayerManager manager, SpotifyApi spotify) {
        this.manager = manager;
        this.spotify = spotify;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        var matcher = SPOTIFY_URL_PATTERN.matcher(reference.identifier);
        if (!matcher.find()) {
            return null;
        }

        var id = matcher.group("identifier");
        switch (matcher.group("type")) {
            case "album":
                return this.getAlbum(id);

            case "track":
                return this.getTrack(id);

            case "playlist":

                return this.getPlaylist(id);
        }
        return null;
    }

    public SpotifyPlaylist getAlbum(String id) throws IOException, ParseException, SpotifyWebApiException {
        var album =  this.spotify.getAlbum(id).build().execute();

        var tracks = new ArrayList<AudioTrack>();
        for(var item : album.getTracks().getItems()) {
            tracks.add(SpotifyTrack.of(item, this.manager));
        }

        return new SpotifyPlaylist(album.getName(), tracks, 0);
    }

    public SpotifyTrack getTrack(String id) throws IOException, ParseException, SpotifyWebApiException {
        var track = this.spotify.getTrack(id).build().execute();
        return SpotifyTrack.of(track, this.manager);
    }

    public SpotifyPlaylist getPlaylist(String id) throws IOException, ParseException, SpotifyWebApiException {
        var playlist =  this.spotify.getPlaylistsItems(id).build().execute();

        var tracks = new ArrayList<AudioTrack>();
        for(var item : playlist.getItems()) {
            tracks.add(SpotifyTrack.of((Track)item.getTrack(), this.manager));
        }

        return new SpotifyPlaylist(playlist.getName(), tracks, 0);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
