package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration{

	private static final Logger log = LoggerFactory.getLogger(SpotifyPlugin.class);

	private final SpotifyApi spotify;
	private final ClientCredentialsRequest clientCredentialsRequest;

	public SpotifyPlugin(){
		this.spotify = new SpotifyApi.Builder().setClientId("").setClientSecret("").build();
		this.clientCredentialsRequest = this.spotify.clientCredentials().build();
		//this.scheduler.scheduleAtFixedRate(this::refreshAccessToken, 0, 1, TimeUnit.HOURS);
	}

	private void refreshAccessToken(){
		try{
			this.spotify.setAccessToken(this.clientCredentialsRequest.execute().getAccessToken());
		}
		catch(Exception e){
			log.error("Updating the access token failed", e);
		}
	}

	@Override
	public AudioPlayerManager configure(AudioPlayerManager manager){
		manager.registerSourceManager(new SpotifyAudioSourceManager(manager, this.spotify));
		return manager;
	}

}
