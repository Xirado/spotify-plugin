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

## Configuration

To get your Spotify clientId & clientSecret go to https://developer.spotify.com/dashboard/applications create a new application & copy the `Client ID` & `Client Secret` into your `application.yml` like the following

```yaml
plugins:
    spotify:
        clientId: "your client id"
        clientSecret: "your client secret"
```

## Usage

Just tell Lavalink to load a Spotify url like `https://open.spotify.com/album/7qemUq4n71awwVPOaX7jw4` and the plugin does the rest

In some cases a requested Spotify songs can't be found on YouTube then you will receive a normal `TrackStartEvent` followed by a `TrackExceptionEvent` and later `TrackEndEvent`

<details>
<summary>TrackStartEvent Example</summary>

```json
{
    "op": "event",
    "type": "TrackStartEvent",
    "guildId": "730879265956167740",
    "track": "QAAAdwIADTMyNTM0NmI0NTZiNTYAEDc0NXY5NjQ4OTY3dmI0ODkAAAAAAAO9CAALamRXaEpjcnJqUXMAAQAraHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1qZFdoSmNycmpRcwAHc3BvdGlmeQAAAAAAA7ok"
}
```
</details>

<details>
<summary>TrackExceptionEvent Example</summary>

```json
{
    "op": "event",
    "type": "TrackExceptionEvent",
    "guildId": "730879265956167740",
    "track": "QAAAdwIADTMyNTM0NmI0NTZiNTYAEDc0NXY5NjQ4OTY3dmI0ODkAAAAAAAO9CAALamRXaEpjcnJqUXMAAQAraHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1qZFdoSmNycmpRcwAHc3BvdGlmeQAAAAAAA7ok",
    "error": "Something broke when playing the track.",
    "exception": {
        "severity": "FAULT",
        "cause": "com.github.topisenpai.plugin.spotify.YouTubeTrackNotFoundException: No matching youtube track found",
        "message": "Something broke when playing the track."
    }
}
```
</details>
        
<details>
<summary>TrackEndEvent Example</summary>

```json
{
    "op": "event",
    "reason": "CLEANUP",
    "type": "TrackEndEvent",
    "guildId": "730879265956167740",
    "track": "QAAAdwIADTMyNTM0NmI0NTZiNTYAEDc0NXY5NjQ4OTY3dmI0ODkAAAAAAAO9CAALamRXaEpjcnJqUXMAAQAraHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1qZFdoSmNycmpRcwAHc3BvdGlmeQAAAAAAA7ok"
}
```
</details>
