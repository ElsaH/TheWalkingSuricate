package walking.suricate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.bluetooth.*;
import javax.microedition.io.*;
  
/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SimpleSPPServer {
	
	private final UUID uuid = new UUID("1101", true);
	private StreamConnectionNotifier streamConnNotifier;
	public BufferedReader bReader;
	public StreamConnection connection;
	
	boolean isConnected = true;
	
	
	
	SimpleSPPServer(){
		try {
			String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
			streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);
			connection = streamConnNotifier.acceptAndOpen();
		} catch (IOException e) {
			System.out.println("Error : impossible de cr�er le serveur SPP.");
		}
	}
	
	public RemoteDevice getRemoteDevice() {
		// dev.getBluetoothAddress()
		// dev.getFriendlyName(true)
		try {
			RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
			InputStream inStream = connection.openInputStream();
			bReader = new BufferedReader(new InputStreamReader(inStream));
			return dev;
		} catch (IOException e) {
			return null;
		}
	}
	
	public String getMessage(RemoteDevice dev) {
        try {
        	return bReader.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
    public void stopServer(){
        try {
			streamConnNotifier.close();
			connection = null;
		} catch (IOException e) {
			connection = null;
		}
    }
    
    public boolean testConnection(){
    	return isConnected;
    }
    
    public void disconnect() {
    	isConnected = false;
    }
    public void connect() {
    	isConnected = true;
    }
  
  
    public static void main(String[] args) throws IOException {
        SimpleSPPServer server = new SimpleSPPServer();
        RemoteDevice dev = null;
        
        // En attente de connexion d'un appareil
        while(dev == null) {
        	dev = server.getRemoteDevice();
        }
        
        System.out.println(dev.getFriendlyName(true));
        
        // lecture des message
        while(server.testConnection()) {
        	String message = server.getMessage(dev);
        	if(message != null)
        		System.out.println(message);
        }
        
    }
}