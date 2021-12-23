# Spotify-Plugin

This plugin lazy loads playlists/albums/songs from [Spotify](https://www.spotify.com)

## Installing

To install this plugin either download the latest release and place it into your `plugins` folder or add the following into your `application.yml` 

```yaml
lavalink:
    plugins:
      - dependency: "com.github.topisenpai:spotify-plugin:latest-release"
        repository: "https://jitpack.io"
```

## Usage

Just tell Lavalink to load a Spotify url like `https://open.spotify.com/album/7qemUq4n71awwVPOaX7jw4` and the plugin does the rest

In some cases a the requested Spotify songs can't be found on YouTube then the 

### Configuration

To get your Spotify clientId & clientSecret go to https://developer.spotify.com/dashboard/applications create a new application & copy them into your `application.yml` liek the following

```yaml
lavalink:
    plugins:
        spotify:
            clientId: "your client id"
            clientSecret: "your client secret"
```
