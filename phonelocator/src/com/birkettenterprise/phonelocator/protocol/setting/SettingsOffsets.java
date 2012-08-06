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

public class SettingsOffsets {
	public static final short BOOLEAN_OFFSET = 0;
	public static final short INTEGER_OFFSET = 64;
	public static final short INT64_OFFSET = 128;
	public static final short STRING_OFFSET = 160;
	public static final short END_OF_SETTINGS_MARKER = 255;
}
