package com.github.topisenpai.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration {

	private static final Logger log = LoggerFactory.getLogger(SpotifyPlugin.class);

	private SpotifyApi spotify;
	private final SpotifyConfig config;
	private ClientCredentialsRequest clientCredentialsRequest;

	public SpotifyPlugin(SpotifyConfig config) {
		this.config = config;
		log.info("Loading Spotify Plugin...");
		if (config.clientId == null || config.clientId.isEmpty()) {
			log.error("No spotify client id found in configuration. Aborting start. config key is 'plugins.spotify.clientId");
			return;
		}
		if (config.clientSecret == null || config.clientSecret.isEmpty()) {
			log.error("No spotify client secret found in configuration. Aborting start. config key is 'plugins.spotify.clientSecret");
			return;
		}
		this.spotify = new SpotifyApi.Builder().setClientId(config.clientId).setClientSecret(config.clientSecret).build();
		this.clientCredentialsRequest = this.spotify.clientCredentials().build();

		var thread = new Thread(() -> {
			try {
				while (true) {
					try {
						var clientCredentials = this.clientCredentialsRequest.execute();
						this.spotify.setAccessToken(clientCredentials.getAccessToken());
						Thread.sleep(clientCredentials.getExpiresIn() * 1000);
					} catch (IOException | SpotifyWebApiException | ParseException e) {
						log.error("Failed to update the spotify access token. Retrying in 1 minute ", e);
						Thread.sleep(60 * 1000);
					}
				}
			} catch (Exception e) {
				log.error("Failed to update the spotify access token", e);
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public SpotifyConfig getConfig() {
		return this.config;
	}

	public SpotifyApi getSpotifyAPI() {
		return this.spotify;
	}

	@Override
	public AudioPlayerManager configure(AudioPlayerManager manager) {
		manager.registerSourceManager(new SpotifySourceManager(this, manager.source(YoutubeAudioSourceManager.class)));
		return manager;
	}

}
