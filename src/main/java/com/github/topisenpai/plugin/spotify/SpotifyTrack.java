package com.github.topisenpai.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifyTrack extends DelegatedAudioTrack {

	private final SpotifyPlugin spotifyPlugin;

	public SpotifyTrack(String title, String identifier, ArtistSimplified[] artists, Integer trackDuration, SpotifyPlugin spotifyPlugin) {
		this(new AudioTrackInfo(title, artists[0].getName(), trackDuration.longValue(), identifier, false, "https://open.spotify.com/track/" + identifier), spotifyPlugin);
	}

	public SpotifyTrack(AudioTrackInfo trackInfo, SpotifyPlugin spotifyPlugin) {
		super(trackInfo);
		this.spotifyPlugin = spotifyPlugin;
	}

	public static SpotifyTrack of(TrackSimplified track, SpotifyPlugin spotifyPlugin) {
		return new SpotifyTrack(track.getName(), track.getId(), track.getArtists(), track.getDurationMs(), spotifyPlugin);
	}

	public static SpotifyTrack of(Track track, SpotifyPlugin spotifyPlugin) {
		return new SpotifyTrack(track.getName(), track.getId(), track.getArtists(), track.getDurationMs(), spotifyPlugin);
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception {
		var track = this.spotifyPlugin.manager.source(YoutubeAudioSourceManager.class).loadItem(this.spotifyPlugin.manager, new AudioReference("ytsearch:" + trackInfo.title + " " + trackInfo.author, null));
		if (track == null) {
			throw new YouTubeTrackNotFoundException("No matching youtube track found");
		}
		if (track instanceof AudioPlaylist) {
			track = ((AudioPlaylist) track).getTracks().get(0);
		}
		if (track instanceof InternalAudioTrack) {
			processDelegate((InternalAudioTrack) track, executor);
			return;
		}
		throw new YouTubeTrackNotFoundException("No matching youtube track found");
	}

	@Override
	public AudioSourceManager getSourceManager() {
		return this.spotifyPlugin;
	}

}
