package walking.suricate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
  
import javax.bluetooth.*;
import javax.microedition.io.*;
  
/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SimpleSPPServer {
	
	private final UUID uuid = new UUID("1101", true);
	private StreamConnectionNotifier streamConnNotifier;
	public StreamConnection connection;
	
	SimpleSPPServer(){
		try {
			String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
			streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);
			connection=streamConnNotifier.acceptAndOpen();
		} catch (IOException e) {
			System.out.println("Error : impossible de créer le serveur SPP.");
		}
	}
	
	private RemoteDevice getRemoteDevice() {
		// dev.getBluetoothAddress()
		// dev.getFriendlyName(true)
		try {
			return  RemoteDevice.getRemoteDevice(connection);
		} catch (IOException e) {
			return null;
		}
	}
	
	private String getMessage(RemoteDevice dev) {
        try {
        	InputStream inStream = connection.openInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
			return bReader.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
    private void stopServer(){
        try {
			streamConnNotifier.close();
			connection = null;
		} catch (IOException e) {
			
		}
    }
  
  
    public static void main(String[] args) throws IOException {
        SimpleSPPServer server = new SimpleSPPServer();
        RemoteDevice dev = null;
        
        // En attente de connexion de l'appareil
        while(dev == null) {
        	dev = server.getRemoteDevice();
        }
        
        System.out.println(dev.getFriendlyName(true));
        
        // lecture des message
        while(server.connection != null) {
        	String message = server.getMessage(dev);
        	if(message != null) {
        		System.out.println(message);
        	}
        }
    }
}