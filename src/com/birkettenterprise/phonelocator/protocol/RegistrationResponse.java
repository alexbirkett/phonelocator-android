package com.birkettenterprise.phonelocator.protocol;

public class RegistrationResponse {

	private String mAuthenticationToken;
	private String mRegistrationUrl;
	
	public String getAuthenticationToken() {
		return mAuthenticationToken;
	}
	public void setAuthenticationToken(String authenticationToken) {
		this.mAuthenticationToken = authenticationToken;
	}
	public String getRegistrationUrl() {
		return mRegistrationUrl;
	}
	public void setRegistrationUrl(String registrationUrl) {
		this.mRegistrationUrl = registrationUrl;
	}
	
}
