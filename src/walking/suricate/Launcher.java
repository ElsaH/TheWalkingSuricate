package walking.suricate;

import java.util.Scanner;

public class Launcher {
	
	TheWalkingSuricate scene ;
	
	public void Launcher() {
		scene = new TheWalkingSuricate();
	}
	
	public void startScene() {
		new Thread() {
            @Override
            public void run() {
            	javafx.application.Application.launch(TheWalkingSuricate.class);
            }
        }.start();
	}
	public void moveSword() {
		scene.turnSword();
	}
	
	
	public static void main(String[] args) {
		Launcher game = new Launcher();
		game.startScene();
		System.out.println("Bonjour entrer si vous voulez faire tourner l'épée ... ");
        Scanner in = new Scanner(System.in);
        int k = in.nextInt();
        if(k == 0)
        	game.moveSword();;
        
	}

}
