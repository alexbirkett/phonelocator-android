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

package com.birkettenterprise.phonelocator.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import android.util.Log;

import com.birkettenterprise.phonelocator.domain.BeaconList;
import com.birkettenterprise.phonelocator.protocol.setting.SettingsProtocol;
import com.birkettenterprise.phonelocator.settings.Setting;

public class Session {

	private Socket mSocket;
	
	//private static final String HOST = "stage.phonelocator.mobi";
	private static final String HOST = "server1.phonelocator.mobi";
	//private static final String HOST = "test1.phonelocator.mobi";

	private static final int PORT = 4952;

	private static final byte AUTHENTICATION_SUCCESS = 1;
	private static final byte PROTOCOL_VERSION_2 = 2;
	private static final short AUTHENTICATION_RESPONSE_LENGTH = 1;
	private static final short POSITION_UPDATE_RESPONSE_LENGTH = 0;

	private static final String LOG_TAG = "SESSION";
	
	public void connect() throws UnknownHostException, IOException {
		mSocket = new Socket(HOST, PORT);
	}

	public void close() {
		try {
			mSocket.close();
		} catch (Throwable e) {
			// ignore
		}
	}

	public RegistrationResponse register() throws IOException {

		DataOutputStream dataOutputStream = new DataOutputStream(
				mSocket.getOutputStream());

		dataOutputStream.writeByte(Methods.REGISTER);
		dataOutputStream.writeShort(0); // Message Length

		DataInputStream dataInputStream = new DataInputStream(
				mSocket.getInputStream());

		dataInputStream.readShort(); // length

		RegistrationResponse response = new RegistrationResponse();
		response.setAuthenticationToken(dataInputStream.readUTF());
		response.setRegistrationUrl(dataInputStream.readUTF());

		return response;
	}

	public void authenticate(String authenticationToken) throws IOException,
			CorruptStreamException, AuthenticationFailedException {
		Log.d(LOG_TAG, "authentication token " + authenticationToken);
		DataOutputStream dataOutputStream = new DataOutputStream(
				mSocket.getOutputStream());
		dataOutputStream.writeByte(Methods.AUTHENTICATE);

		ByteArrayOutputStream authenticationStream = new ByteArrayOutputStream();
		DataOutputStream authetnicationDataStream = new DataOutputStream(
				authenticationStream);
		authetnicationDataStream.writeByte(PROTOCOL_VERSION_2);
		authetnicationDataStream.writeUTF(authenticationToken);

		dataOutputStream.writeShort(authenticationStream.size());
		dataOutputStream.write(authenticationStream.toByteArray());

		DataInputStream dataInputStream = new DataInputStream(
				mSocket.getInputStream());
		if (dataInputStream.readShort() != AUTHENTICATION_RESPONSE_LENGTH) {
			throw new CorruptStreamException();
		}
		if (dataInputStream.readByte() != AUTHENTICATION_SUCCESS) {
			throw new AuthenticationFailedException();
		}
	}

	public void sendPositionUpdate(BeaconList beaconList) throws IOException,
			CorruptStreamException {

		DataOutputStream dataOutputStream = new DataOutputStream(
				mSocket.getOutputStream());
		dataOutputStream.writeByte(Methods.POSITIONUPDATE);

		ByteArrayOutputStream beaconListStream = new ByteArrayOutputStream();
		DataOutputStream beaconListDataStream = new DataOutputStream(
				beaconListStream);
		beaconList.externalize(beaconListDataStream);

		dataOutputStream.writeShort(beaconListStream.size());
		dataOutputStream.write(beaconListStream.toByteArray());

		DataInputStream dataInputStream = new DataInputStream(
				mSocket.getInputStream());
		if (dataInputStream.readShort() != POSITION_UPDATE_RESPONSE_LENGTH) {
			throw new CorruptStreamException();
		}
	}

	/**
	 * Synchronize settings
	 * @param settingsToSend Setting to send (passing null will create default settings on the server)
	 * @return settings changed on the server since last synchronization
	 * @throws IOException
	 */
	public Vector<Setting> synchronizeSettings(Vector<Setting> settingsToSend) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(
				mSocket.getOutputStream());
		
		dataOutputStream.writeByte(Methods.SYNCHRONIZESETTINGS);

		ByteArrayOutputStream settingsStream = new ByteArrayOutputStream();
		
		SettingsProtocol.writeSettings(settingsToSend, new DataOutputStream(settingsStream));

		dataOutputStream.writeShort(settingsStream.size());
		dataOutputStream.write(settingsStream.toByteArray());
		dataOutputStream.flush();
		return SettingsProtocol.readingSettings(new DataInputStream(mSocket.getInputStream()));
	}
	
	public Vector<Setting> createDefaultSettings() throws IOException {
		return synchronizeSettings(null);
	}

}
