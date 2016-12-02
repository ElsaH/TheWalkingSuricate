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
	boolean isstarted = false;
	
	public Launcher() {
		scene = new TheWalkingSuricate();
		server = new SimpleSPPServer();
		messages = new ArrayList<String>();
	}
	
	public void startScene() {
		isstarted = true;
		scene = new TheWalkingSuricate();
		Tscene = new Thread(scene);
		Tscene.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.start();
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
		        server.connect();
		        
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
		        	try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
	
	
	//@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Launcher game = new Launcher();
		game.startServer();
		String line;
		while(game.server.testConnection()) {
			if(game.messages.size() != 0) {
				line = game.messages.get(0);
				game.messages.remove(0);
				//System.out.println(line);
				if(line != null) {
					System.out.println("SERVER_BLUETOOTH : " + line);
					if(line.equals("COUPE") && game.isstarted) 
					{
						game.moveSword();
					}	
					else if( line.equals("STOP") || line.equals("PAUSE") ) {
						System.out.println("Quitting...");
						try {
							game.scene.stop();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						game.scene.exit();
						game.server.disconnect();
						game.isstarted = false;
					}
					else if(line.equals("START"))
						game.startScene();
					else if(line.equals("ARME1"));
					else if(line.equals("ARME2"));
					else if(line.equals("TREX"));
					else if(line.equals("BOMBE"));
				}
				
			}
			//Les messages qu'envoie la scene
			//System.out.println("Reading Message from SCENE");
			if(game.scene.messages.size() != 0) {
				line = game.scene.messages.get(0);
				game.scene.messages.remove(0);
				System.out.println("SCENE : " + line);
				if(line.equals("TUE")) {
					game.data.tue();
					game.scene.setScore(game.data.score);
					//game.scene.addSuricate();
				}
				else if(line.equals("READY")) {
					System.out.println("Starting game data server..." );
					
				}
			}
			if(game.data.messages.size() != 0) {
				line = game.data.messages.get(0);
				game.data.messages.remove(0);
				if(line != null) {
					if(line.equals("SURICATE") ) {
						System.out.println("SERVER DATA : " + line);
						game.scene.timeForSuricate = game.data.getSuricateSpeed();
						game.scene.addSuricate();
					}
					else if(line.equals("LEVEL_UP")) {
						System.out.println("SERVER DATA : " + line);
					}
				}
			}			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
	}

}
