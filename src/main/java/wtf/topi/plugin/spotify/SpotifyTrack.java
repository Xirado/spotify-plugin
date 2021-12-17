package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.springframework.lang.NonNull;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifyTrack extends DelegatedAudioTrack{

	private final AudioPlayerManager manager;
	private final AudioSourceManager sourceManager;

	public SpotifyTrack(String name, ArtistSimplified[] artists, Integer trackDuration, String trackURI, AudioPlayerManager manager, @NonNull AudioSourceManager sourceManager){
		this(new AudioTrackInfo(name, artists[0].getName(), trackDuration.longValue(), "ytsearch:" + name + " " + artists[0].getName(), false, trackURI), manager, sourceManager);
	}

	public SpotifyTrack(AudioTrackInfo trackInfo, AudioPlayerManager manager, AudioSourceManager sourceManager){
		super(trackInfo);
		this.manager = manager;
		this.sourceManager = sourceManager;
	}

	public static SpotifyTrack of(TrackSimplified track, AudioPlayerManager manager, AudioSourceManager sourceManager){
		return new SpotifyTrack(track.getName(), track.getArtists(), track.getDurationMs(), track.getUri(), manager, sourceManager);
	}

	public static SpotifyTrack of(Track track, AudioPlayerManager manager, AudioSourceManager sourceManager){
		return new SpotifyTrack(track.getName(), track.getArtists(), track.getDurationMs(), track.getUri(), manager, sourceManager);
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception{
		var track = this.manager.source(YoutubeAudioSourceManager.class).loadItem(this.manager, new AudioReference(this.trackInfo.identifier, null));
		if (track instanceof AudioPlaylist){
			track = ((AudioPlaylist) track).getTracks().get(0);
		}
		processDelegate((InternalAudioTrack) track, executor);
	}

	@Override
	public AudioSourceManager getSourceManager(){
		return this.sourceManager;
	}

}
