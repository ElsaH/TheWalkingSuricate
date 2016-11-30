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
	Thread Tscene;
	Thread Tserver;
	
	public Launcher() {
		scene = new TheWalkingSuricate();
		server = new SimpleSPPServer();
		messages = new ArrayList<String>();
	}
	
	public void startScene() {
		Tscene = new Thread() {
            @Override
            public void run() {
            	javafx.application.Application.launch(TheWalkingSuricate.class);
            }
        };
        Tscene.start();
	}
	
	
	public void startServer() {
		Tserver = new Thread() {
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
						messages.add(messages.size(),message);
		        	}
		        }
		        server.stopServer();
			}
		};
		Tserver.start();
	}
	public void moveSword() {
		scene.turnSword();
	}
	
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Launcher game = new Launcher();
		game.startScene();
		game.startServer();
		String line;
		while(game.server.testConnection()) {
			if(game.messages.size() != 0) {
				line = game.messages.get(0);
				game.messages.remove(0);
				//System.out.println(line);
				if(line != null && line.equals("COUPE")) 
					game.moveSword();
			}
			//Les messages qu'envoie la scene
			if(game.scene.messages.size() != 0) {
				line = game.scene.messages.get(0);
				game.scene.messages.remove(0);
				if(line.equals("TUE")) {
					game.data.tue();
					game.scene.setScore(game.data.score);
					game.scene.addSuricate();
				}
			}
		}
		
		
		game.Tserver.stop();
		game.Tscene.stop();
		
	}

}
