package com.github.topisenpai.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration, AudioSourceManager {

	public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist)/(?<identifier>[a-zA-Z0-9-_]+)");
	private static final Logger log = LoggerFactory.getLogger(SpotifyPlugin.class);

	private final SpotifyApi spotify;
	private final ClientCredentialsRequest clientCredentialsRequest;
	public AudioPlayerManager manager;
	private final Thread thread;

	public SpotifyPlugin(SpotifyConfig config) {
		log.info("Loading Spotify Plugin...");
		this.spotify = new SpotifyApi.Builder().setClientId(config.clientId).setClientSecret(config.clientSecret).build();
		this.clientCredentialsRequest = this.spotify.clientCredentials().build();

		this.thread = new Thread(() -> {
			while (true) {
				try {
					this.spotify.setAccessToken(this.clientCredentialsRequest.execute().getAccessToken());
					Thread.sleep(29 * 60 * 1000);
				} catch (Exception e) {
					log.error("Failed to update the spotify access token: " + e);
				}
			}
		});
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@Override
	public AudioPlayerManager configure(AudioPlayerManager manager) {
		this.manager = manager;
		manager.registerSourceManager(this);
		return manager;
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
		try {
			switch (matcher.group("type")) {
				case "album":
					return this.getAlbum(id);

				case "track":
					return this.getTrack(id);

				case "playlist":
					return this.getPlaylist(id);
			}
		} catch (IOException | ParseException | SpotifyWebApiException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public SpotifyPlaylist getAlbum(String id) throws IOException, ParseException, SpotifyWebApiException {
		var album = this.spotify.getAlbum(id).build().execute();

		var tracks = new ArrayList<AudioTrack>();
		for (var item : album.getTracks().getItems()) {
			tracks.add(SpotifyTrack.of(item, this));
		}

		return new SpotifyPlaylist(album.getName(), tracks, 0);
	}

	public SpotifyTrack getTrack(String id) throws IOException, ParseException, SpotifyWebApiException {
		var track = this.spotify.getTrack(id).build().execute();
		return SpotifyTrack.of(track, this);
	}

	public SpotifyPlaylist getPlaylist(String id) throws IOException, ParseException, SpotifyWebApiException {
		var playlist = this.spotify.getPlaylist(id).build().execute();

		var tracks = new ArrayList<AudioTrack>();
		for (var item : playlist.getTracks().getItems()) {
			tracks.add(SpotifyTrack.of((Track) item.getTrack(), this));
		}

		return new SpotifyPlaylist(playlist.getName(), tracks, 0);
	}

	@Override
	public boolean isTrackEncodable(AudioTrack track) {
		return true;
	}

	@Override
	public void encodeTrack(AudioTrack track, DataOutput output) {
		// no need to encode custom fields
	}

	@Override
	public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
		return new SpotifyTrack(trackInfo, this);
	}

	@Override
	public void shutdown() {
		log.info("Shutting down Spotify Plugin...");
		this.thread.interrupt();
	}

}
