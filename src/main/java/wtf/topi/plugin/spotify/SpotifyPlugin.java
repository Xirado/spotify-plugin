package wtf.topi.plugin.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lavalink.api.AudioPlayerManagerConfiguration;
import lavalink.api.PluginInfo;
import org.springframework.stereotype.Service;

@Service
public class SpotifyPlugin implements AudioPlayerManagerConfiguration, PluginInfo {

    @Override
    public int getMajor() {
        return 0;
    }

    @Override
    public int getMinor() {
        return 0;
    }

    @Override
    public int getPatch() {
        return 1;
    }

    @Override
    public String getName() {
        return "spotify";
    }

    @Override
    public AudioPlayerManager configure(AudioPlayerManager manager) {
        manager.registerSourceManager(new SpotifyAudioSourceManager(manager));
        return manager;
    }

}
