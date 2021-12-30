package com.github.topisenpai.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.sedmelluq.discord.lavaplayer.tools.DataFormatTools.readNullableText;
import static com.sedmelluq.discord.lavaplayer.tools.DataFormatTools.writeNullableText;

public class SpotifySourceManager implements AudioSourceManager {

	public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");

	public final SpotifyPlugin spotifyPlugin;
	public final YoutubeAudioSourceManager youtubeAudioSourceManager;

	public SpotifySourceManager(SpotifyPlugin spotifyPlugin, YoutubeAudioSourceManager youtubeAudioSourceManager) {
		this.spotifyPlugin = spotifyPlugin;
		this.youtubeAudioSourceManager = youtubeAudioSourceManager;
	}

	@Override
	public String getSourceName() {
		return "spotify";
	}

	@Override
	public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
		if (this.spotifyPlugin.getSpotifyAPI() == null) {
			return null;
		}
		var matcher = SPOTIFY_URL_PATTERN.matcher(reference.identifier);
		if (!matcher.find()) {
			return null;
		}

		var id = matcher.group("identifier");
		try {
			switch (matcher.group("type")) {
				case "album":
					return this.getAlbum(id);

				case "track":
					return this.getTrack(id);

				case "playlist":
					return this.getPlaylist(id);

				case "artist":
					return this.getArtist(id);
			}
		} catch (IOException | ParseException | SpotifyWebApiException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public boolean isTrackEncodable(AudioTrack track) {
		return true;
	}

	@Override
	public void encodeTrack(AudioTrack track, DataOutput output) {
		var spotifyTrack = (SpotifyTrack) track;
		if (spotifyTrack.getISRC() != null) {
			try {
				writeNullableText(output, spotifyTrack.getISRC());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
		String isrc = null;
		try {
			isrc = readNullableText(input);
		} catch (IOException ignored) {
		}
		return new SpotifyTrack(trackInfo, isrc, this, this.youtubeAudioSourceManager);
	}

	@Override
	public void shutdown() {

	}

	public SpotifyTrack getTrack(String id) throws IOException, ParseException, SpotifyWebApiException {
		var track = this.spotifyPlugin.getSpotifyAPI().getTrack(id).build().execute();
		return SpotifyTrack.of(track, this, this.youtubeAudioSourceManager);
	}

	public SpotifyPlaylist getAlbum(String id) throws IOException, ParseException, SpotifyWebApiException {
		var album = this.spotifyPlugin.getSpotifyAPI().getAlbum(id).build().execute();
		var tracks = new ArrayList<AudioTrack>();

		Paging<TrackSimplified> paging = null;
		do {
			paging = this.spotifyPlugin.getSpotifyAPI().getAlbumsTracks(id).limit(50).offset(paging == null ? 0 : paging.getOffset() + 50).build().execute();
			for (var item : paging.getItems()) {
				if (item.getType() != ModelObjectType.TRACK) {
					continue;
				}
				tracks.add(SpotifyTrack.of(item, this, this.youtubeAudioSourceManager));
			}
		}
		while (paging.getNext() != null);

		return new SpotifyPlaylist(album.getName(), tracks, 0);
	}

	public SpotifyPlaylist getPlaylist(String id) throws IOException, ParseException, SpotifyWebApiException {
		var playlist = this.spotifyPlugin.getSpotifyAPI().getPlaylist(id).build().execute();
		var tracks = new ArrayList<AudioTrack>();

		Paging<PlaylistTrack> paging = null;
		do {
			paging = this.spotifyPlugin.getSpotifyAPI().getPlaylistsItems(id).limit(50).offset(paging == null ? 0 : paging.getOffset() + 50).build().execute();
			for (var item : paging.getItems()) {
				if (item.getIsLocal() || item.getTrack().getType() != ModelObjectType.TRACK) {
					continue;
				}
				tracks.add(SpotifyTrack.of((Track) item.getTrack(), this, this.youtubeAudioSourceManager));
			}
		}
		while (paging.getNext() != null);

		return new SpotifyPlaylist(playlist.getName(), tracks, 0);
	}

	public SpotifyPlaylist getArtist(String id) throws IOException, ParseException, SpotifyWebApiException {
		var artist = this.spotifyPlugin.getSpotifyAPI().getArtist(id).build().execute();
		var artistTracks = this.spotifyPlugin.getSpotifyAPI().getArtistsTopTracks(id, this.spotifyPlugin.getConfig().countryCode).build().execute();

		var tracks = new ArrayList<AudioTrack>();
		for (var item : artistTracks) {
			if (item.getType() != ModelObjectType.TRACK) {
				continue;
			}
			tracks.add(SpotifyTrack.of(item, this, this.youtubeAudioSourceManager));
		}

		return new SpotifyPlaylist(artist.getName() + "'s Top Tracks", tracks, 0);
	}

}
