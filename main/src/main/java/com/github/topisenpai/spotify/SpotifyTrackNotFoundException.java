package com.github.topisenpai.spotify;

public class SpotifyTrackNotFoundException extends RuntimeException{

	public SpotifyTrackNotFoundException(){
		super("No matching track found");
	}

}
