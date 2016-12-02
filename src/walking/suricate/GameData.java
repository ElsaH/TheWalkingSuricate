package walking.suricate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameData {
	
	int level = 1;
	String name = "unknown";
	int score = 0;
	private final int nbSuriMini = 1;
	private int nbSuricateMort = 0;
	double facteurLVL = 0.1;
	int pointSuri = 100;
	private int defaultTimeSuricateAnimation = 5000;
	private int defaultTimeRespawnSuricate = 5000;
	public List<String> messages = new ArrayList<String>();
	private Timer exec = new Timer();

	
	private static String scoreFileName = "score.txt";
	
	public GameData() {
		
	}
	public int getSuricateSpeed() {
		return defaultTimeSuricateAnimation;
	}
	public void start() {
		
		
		TimerTask execTask = new TimerTask() {
		  @Override
		  public void run() {
		    messages.add("SURICATE");
		  }
		};
		exec.scheduleAtFixedRate(execTask, 0,defaultTimeSuricateAnimation);
		
		
	}
	public void setName(String userName) {
		name = userName;
		name.replace(' ', '_');
	}
	public void tue() {
		double pts = pointSuri * facteurLVL * Math.exp(level);
		score = score + (int)pts;
		nbSuricateMort++;
		if(nbSuricateMort >= level * nbSuriMini) {
			nbSuricateMort = nbSuricateMort - level * nbSuriMini;
			level++;
			defaultTimeSuricateAnimation-=(100*level);
			
			if(defaultTimeSuricateAnimation < 1000) 
				defaultTimeSuricateAnimation = 1000;
			
			defaultTimeRespawnSuricate /= 2;
			
			
			exec.cancel();
			
			exec = new Timer();
			
			TimerTask execTask = new TimerTask() {
			  @Override
			  public void run() {
			    messages.add("SURICATE");
			  }
			};
			exec.scheduleAtFixedRate(execTask, 0,defaultTimeSuricateAnimation);
			
			
			
			messages.add("LEVEL_UP");
		}
	}
	public void saveScoreToFile() {
		List<String> scores = readScoreFromFile();
		int position = 0;
		int ascore = 0;
		for(int i = 0; i < scores.size(); i++) {
			ascore = Integer.parseInt(scores.get(i).substring(scores.get(i).indexOf(' ')+1,scores.size())); 
			if( ascore > score )
				position++;
		}
		String line = name + " " + score;
		scores.add(position,line);
		try {
			PrintWriter writer = new PrintWriter(scoreFileName,"UTF-8");
			for(int i = 0 ; i < scores.size() ; i++) {
	    		writer.println(line);
		    }
			writer.println(line);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    
			
		}
		
	}
	public List<String> readScoreFromFile() {
		BufferedReader br;
		List<String> scores = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(scoreFileName));
			String line;
			while((line = br.readLine()) != null) {
		    	scores.add(line);
		    }
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Fichier de score inexistant ou vide.");
		}
		for(int i = 0 ; i < scores.size() ; i++) {
			System.out.println(scores.get(i));
		}
		return scores;
	}
	
	public static void main(String[] args) {
		
	}

}
