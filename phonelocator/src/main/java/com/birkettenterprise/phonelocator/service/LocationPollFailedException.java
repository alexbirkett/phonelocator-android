package com.birkettenterprise.phonelocator.service;

public class LocationPollFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LocationPollFailedException(String error) {
		super(error);
	}
}
