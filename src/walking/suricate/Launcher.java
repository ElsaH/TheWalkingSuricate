package walking.suricate;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.RemoteDevice;


public class Launcher {
	
	TheWalkingSuricate scene ; //will be in thread.
	SimpleSPPServer server;
	GameData data = new GameData();
	List<String> messages;
	
	public Launcher() {
		scene = new TheWalkingSuricate();
		server = new SimpleSPPServer();
		messages = new ArrayList<String>();
	}
	
	public void startScene() {
		new Thread() {
            @Override
            public void run() {
            	javafx.application.Application.launch(TheWalkingSuricate.class);
            }
        }.start();
	}
	
	
	public void startServer() {
		new Thread() {
			@Override
			public void run() {
				RemoteDevice dev = null;
		        
		        // En attente de connexion d'un appareil
		        while(dev == null) {
		        	dev = server.getRemoteDevice();
		        }
		        
		        try {
					System.out.println(dev.getFriendlyName(true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        // lecture des message
		        while(server.testConnection()) {
		        	String message = server.getMessage(dev);
		        	if(message != null){
						messages.add(message);
		        	}
		        }
			}
		}.start();
	}
	public void moveSword() {
		scene.turnSword();
	}
	
	
	public static void main(String[] args) {
		Launcher game = new Launcher();
		game.startScene();
		game.startServer();
		String line;
		while(game.server.testConnection()) {
			if(game.messages.size() != 0) {
				line = game.messages.get(0);
				game.messages.remove(0);
				System.out.println(line);
				if(line.equals("COUPE")) 
					game.moveSword();
			}
		}
                
	}

}
