package com.github.topisenpai.spotify.plugin;

import com.github.topisenpai.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration{

	private static final Logger log = LoggerFactory.getLogger(SpotifyPlugin.class);

	private final SpotifyConfig config;

	public SpotifyPlugin(SpotifyConfig config){
		log.info("Loading Spotify Plugin...");
		this.config = config;
	}

	@Override
	public AudioPlayerManager configure(AudioPlayerManager manager){
		if(config.getClientId() == null || config.getClientId().isEmpty()){
			log.error("No spotify client id found in configuration. Not registering spotify source manager. Config key is 'plugins.spotify.clientId");
			return manager;
		}
		if(config.getClientSecret() == null || config.getClientSecret().isEmpty()){
			log.error("No spotify client secret found in configuration. Not registering spotify source manager. Config key is 'plugins.spotify.clientSecret");
			return manager;
		}
		if(config.getCountryCode() == null){
			log.error("No spotify country code found in configuration. Defaulting to US. Config key is 'plugins.spotify.countryCode");
		}
		manager.registerSourceManager(new SpotifySourceManager(this.config.getClientId(), this.config.getClientSecret(), this.config.getCountryCode(), manager.source(YoutubeAudioSourceManager.class)));
		return manager;
	}

}
