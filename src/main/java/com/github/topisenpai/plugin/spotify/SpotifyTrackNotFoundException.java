package com.github.topisenpai.plugin.spotify;

public class SpotifyTrackNotFoundException extends RuntimeException{

	public SpotifyTrackNotFoundException(){
		super("No matching track found");
	}

}
