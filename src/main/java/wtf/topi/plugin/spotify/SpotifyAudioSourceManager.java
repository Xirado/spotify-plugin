package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import se.michaelthelin.spotify.SpotifyApi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.regex.Pattern;

public class SpotifyAudioSourceManager implements AudioSourceManager {

    public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist)/(?<identifier>[a-zA-Z0-9-_]+)");


    private AudioPlayerManager manager;
    private  SpotifyApi spotify;

    public SpotifyAudioSourceManager(AudioPlayerManager manager) {
        this.manager = manager;
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

        var identifier = matcher.group("identifier");
        switch (matcher.group("type")) {
            case "album":

                break;
            case "track":

                break;
            case "playlist":

                break;
            default:
                return null;
        }
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
