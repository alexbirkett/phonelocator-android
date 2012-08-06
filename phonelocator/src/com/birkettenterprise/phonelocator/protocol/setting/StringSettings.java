/**
 * 
 *  Copyright 2011-2012 Birkett Enterprise Ltd
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

package com.birkettenterprise.phonelocator.protocol.setting;

public class StringSettings {
	public static final int AUTHENTICATION_TOKEN = 0; /* 0 authentication token*/
	public static final int FIRMWARE_VERSON = 1; /* 1 phone firmware version (read only) */
	public static final int MODEL_NAME = 2; /* 2 phone model name read only */
	public static final int BUDDY_TELEPHONE_NUMBER = 3; /* 3 buddy telephone number */
	public static final int BUDDY_MESSAGE = 4; /* 4 buddy message */
	public static final int URL1 = 5; /* 5 Url 1 see EShowUrl1 */
	public static final int URL2 = 6; /* 6 Url 1 see EShowUrl2 */
	public static final int URL1_NAME = 7; /* 7 Url 1 name (text displayed on menu) see EShowUrl1 */
	public static final int URL2_NAME = 8; /* 8 Url 2 name (text displayed on menu) see EShowUrl2 */
	public static final int CLIENT_DISABLED_MESSAGE = 9; /* 9 message displayed when client is disabled */
	public static final int CLIENT_DISABLED_URL = 10; /* 10 url to offer to user when client is disabled */
	public static final int HOSTNAME = 11;  /* 11 server host name */
	public static final int PINCODE = 12; /* 12 pin code (should be numeric) */
	public static final int MESSAGE = 13; /* 13 message to display EAlertActive */
};
