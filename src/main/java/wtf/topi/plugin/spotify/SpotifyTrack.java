package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifyTrack extends DelegatedAudioTrack {

    private AudioPlayerManager manager;

    public static SpotifyTrack of(TrackSimplified track, AudioPlayerManager manager) {
        return new SpotifyTrack(track.getName(), track.getArtists(), track.getDurationMs(), track.getUri(), manager);
    }

    public static SpotifyTrack of(Track track, AudioPlayerManager manager) {
        return new SpotifyTrack(track.getName(), track.getArtists(), track.getDurationMs(), track.getUri(), manager);
    }

    public static SpotifyTrack of(PlaylistTrack track, AudioPlayerManager manager) {
        return new SpotifyTrack(track.getName(), track.getArtists(), track.getDurationMs(), track.getUri(), manager);
    }

    public SpotifyTrack(String name, ArtistSimplified[] artists, Integer trackDuration, String trackURI, AudioPlayerManager manager) {
        super(new AudioTrackInfo(name, artists[0].getName(), trackDuration.longValue(), "ytsearch:" + name + " " + artists[0].getName(), false, trackURI));
        this.manager = manager;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        var track = this.manager.source(YoutubeAudioSourceManager.class).loadItem(this.manager, new AudioReference(this.trackInfo.identifier, null));
        processDelegate((InternalAudioTrack) track, executor);
    }
}
