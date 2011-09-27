package com.birkettenterprise.phonelocator.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class Session {

	private Socket mSocket;
	
	private static final String HOST = "server1.phonelocator.mobi";
	private static final int PORT = 4952;
			
	public void connect() throws UnknownHostException, IOException {
		mSocket = new Socket(HOST, PORT);
	}
	
	public void close() throws IOException {
		mSocket.close();
	}
	
	public RegistrationResponse register() throws IOException {
		
		DataOutputStream dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		
		dataOutputStream.writeByte(Methods.REGISTER);
		dataOutputStream.writeShort(0); // Message Length
		
		DataInputStream dataInputStream = new DataInputStream(mSocket.getInputStream());

		dataInputStream.readShort(); // length
		
		RegistrationResponse response = new RegistrationResponse();
		response.setAuthenticationToken(dataInputStream.readUTF());
		response.setRegistrationUrl(dataInputStream.readUTF());
		
		return response;	
	}
	
	Map<String, Object> synchonizeSettings(Map<String, Object> settingsToSend) {
		
		 Iterator it = settingsToSend.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Object> pairs = (Map.Entry<String, Object>)it.next();
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		    }
		return settingsToSend;
	}
	
}
