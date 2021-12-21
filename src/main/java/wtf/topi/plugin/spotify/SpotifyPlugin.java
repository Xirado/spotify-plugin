package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration{

	private final SpotifyApi spotify;
	private final ClientCredentialsRequest clientCredentialsRequest;

	public SpotifyPlugin(SpotifyConfig config){
		System.out.println("Loading SpotifyPlugin...");
		this.spotify = new SpotifyApi.Builder().setClientId(config.clientId).setClientSecret(config.clientSecret).build();
		this.clientCredentialsRequest = this.spotify.clientCredentials().build();
		this.refreshAccessToken();
	}

	private void refreshAccessToken(){
		try{
			this.spotify.setAccessToken(this.clientCredentialsRequest.execute().getAccessToken());
		}
		catch(Exception e){
			System.out.println("Updating the access token failed: " + e);
		}
	}

	@Override
	public AudioPlayerManager configure(AudioPlayerManager manager){
		System.out.println("Configuring SpotifyPlugin...");
		manager.registerSourceManager(new SpotifyAudioSourceManager(manager, this.spotify));
		return manager;
	}

}
