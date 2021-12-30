package com.github.topisenpai.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifyTrack extends DelegatedAudioTrack {

	private final String isrc;
	private final SpotifySourceManager spotifySourceManager;
	private final YoutubeAudioSourceManager youtubeAudioSourceManager;

	public SpotifyTrack(String title, String identifier, String isrc, String uri, ArtistSimplified[] artists, Integer trackDuration, SpotifySourceManager spotifySourceManager, YoutubeAudioSourceManager youtubeAudioSourceManager) {
		this(new AudioTrackInfo(title,
				artists.length == 0 ? "unknown" : artists[0].getName(),
				trackDuration.longValue(),
				identifier == null ? uri : identifier,
				false,
				identifier == null ? null : "https://open.spotify.com/track/" + identifier
		), isrc, spotifySourceManager, youtubeAudioSourceManager);
	}

	public SpotifyTrack(AudioTrackInfo trackInfo, String isrc, SpotifySourceManager spotifySourceManager, YoutubeAudioSourceManager audioSourceManager) {
		super(trackInfo);
		this.isrc = isrc;
		this.spotifySourceManager = spotifySourceManager;
		this.youtubeAudioSourceManager = audioSourceManager;
	}

	public static SpotifyTrack of(TrackSimplified track, SpotifySourceManager spotifySourceManager, YoutubeAudioSourceManager youtubeAudioSourceManager) {
		return new SpotifyTrack(track.getName(), track.getId(), null, track.getUri(), track.getArtists(), track.getDurationMs(), spotifySourceManager, youtubeAudioSourceManager);
	}

	public static SpotifyTrack of(Track track, SpotifySourceManager spotifySourceManager, YoutubeAudioSourceManager youtubeAudioSourceManager) {
		return new SpotifyTrack(track.getName(), track.getId(), track.getExternalIds().getExternalIds().getOrDefault("isrc", null), track.getUri(), track.getArtists(), track.getDurationMs(), spotifySourceManager, youtubeAudioSourceManager);
	}

	public String getISRC() {
		return this.isrc;
	}

	private String getQuery() {
		var query = trackInfo.title;
		if (!trackInfo.author.equals("unknown")) {
			query += " " + trackInfo.author;
		}
		return query;
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception {
		AudioItem track = null;
		if (this.isrc != null) {
			track = this.youtubeAudioSourceManager.loadItem(null, new AudioReference("ytsearch:\"" + this.isrc + "\"", null));
		}
		if (track == null) {
			track = this.youtubeAudioSourceManager.loadItem(null, new AudioReference(getQuery(), null));
		}
		if (track instanceof AudioPlaylist) {
			track = ((AudioPlaylist) track).getTracks().get(0);
		}
		if (track instanceof InternalAudioTrack) {
			processDelegate((InternalAudioTrack) track, executor);
			return;
		}
		throw new SpotifyTrackNotFoundException();
	}

	@Override
	public AudioSourceManager getSourceManager() {
		return this.spotifySourceManager;
	}

}
