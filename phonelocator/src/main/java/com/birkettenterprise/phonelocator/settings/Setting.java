/**
 * 
 *  Copyright 2011 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */


package com.birkettenterprise.phonelocator.settings;

public class Setting {
	
	public class StringSettings {
		/*
		 * Stored as an int on the server
		 */
		public static final String UPDATE_FREQUENCY = "update_frequency";	
		public static final String GPS_UPDATE_TIMEOUT = "gps_update_timeout";

		/*
		 * Synchronized with server
		 */
		public static final String AUTHENTICATION_TOKEN = "authentication_token";
	    public static final String PINCODE = "pincode";
	    public static final String BUDDY_TELEPHONE_NUMBER = "buddy_telephone_number";
	    public static final String BUDDY_MESSAGE = "buddy_message";
	
	    /*
		 * Not synchronized with the server
		 */
		public static final String REGISTRATION_URL = "registration_url";
        public static final String TRACKER_NAME = "tracker_name";
	}
    
	public class BooleanSettings {
		public static final String PERIODIC_UPDATES_ENABLED = "periodic_updates_enabled"; 
	    public static final String REGISTERED = "registered";
	    public static final String PINCODE_REQUIRED_ON_STARTUP = "pincode_required_on_startup";
	    public static final String SEND_BUDDY_MESSAGE = "send_buddy_message";
	    public static final String BUDDY_MESSAGE_ENABLED = "buddy_message_enabled";
		public static final String GPS_ENABLED = "gps_enabled";
	    /*
	     * Not synchronized with the server
	     */
	    public static final String HIDE_SMS_TRIGGER = "hide_sms_trigger";
		  
	}
	
	public class Integer64Settings {
		/*
		 * Sent to server, can't be set by server
		 */
		public static final String IMEI = "imei"; 
		public static final String IMSI = "imsi";
	}
	
	public class IntegerSettings {
		/*
		 * Sent to server, can't be set by server
		 */
		public static final String VERSION_MINOR = "version_minor"; 
		public static final String VERSION_MAJOR = "version_major"; 
		public static final String VERSION_REVISION = "version_revision";
	}
	
	public class LongSettings {
		/*
		 * Not synchronized
		 */
		public static final String LAST_UPDATE_TIME_STAMP = "last_update_time_stamp";
	}
	
	private long mTimestamp;
	private String mName;
	private Object mValue;
	
	public Setting() {
		
	}
	
	public Setting(String name, Object value, long timestamp) {
		setName(name);
		setValue(value);
		setTimestamp(timestamp);
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public Object getValue() {
		return mValue;
	}
	public void setValue(Object value) {
		this.mValue = value;
	}
}
