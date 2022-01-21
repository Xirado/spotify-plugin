package com.github.topisenpai.spotify.plugin;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "plugins.spotify")
@Component
public class SpotifyConfig{

	private String clientId;
	private String clientSecret;
	private CountryCode countryCode = CountryCode.US;

	public String getClientId(){
		return this.clientId;
	}

	public void setClientId(String clientId){
		this.clientId = clientId;
	}

	public String getClientSecret(){
		return this.clientSecret;
	}

	public void setClientSecret(String clientSecret){
		this.clientSecret = clientSecret;
	}

	public CountryCode getCountryCode(){
		return this.countryCode;
	}

	public void setCountryCode(String countryCode){
		this.countryCode = CountryCode.getByCode(countryCode);
	}

}
