package walking.suricate;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.interactivemesh.jfx.importer.col.ColModelImporter;

public class TheWalkingSuricate extends Application implements Runnable {

	// Interface part
	final Group global = new Group();
	private Text score = new Text();
	static Text score2;
	private HBox hb;
	
	List<String> messages = new ArrayList<String>();
	final static int nbMaxSuri = 100;
	int compteurSuri = 0;
	static Node[] Suricates = new Node[nbMaxSuri];
	static int nbSuricates = 0;
	int timeForSuricate = 5000;
	int suricateSize = 100;
	int suricateL = 10;
	
	final Group root = new Group();
	
	static Group epee = new Group();
	static ParallelTransition swordTransition1;
	
    final Xform world = new Xform();
    final Group testGroup = new Group();
    private Scene scene ;
    private BorderPane border;
 	
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -150;
    private static final double CAMERA_INITIAL_X_ANGLE = 10.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 100000.0;
    private static final double CAMERA_INITIAL_Y = 70;

    private static final int LARGEUR_SCENE = 150;
    private static final int RATIO_PROFONDEUR = 50;
    private static final int SCALE_EPEE = 30;
    
    
    @Override
    public void start(Stage primaryStage) {

    	buildScene();
    	buildCamera();
        //buildAxes();
        
        baseGame();
        addSword();
        initializeSuricates();
        //addSuricate();
        /*System.out.println("SCENE : Adding message READY");
        messages.add(messages.size(),"READY");*/
        

        
        border = new BorderPane();
    	hb = addHBox();
    	border.setTop(hb);
        
        SubScene sub = new SubScene(root, 800, 600, true,SceneAntialiasing.BALANCED);
        sub.setCamera(camera);
        
        global.getChildren().add(sub);
        border.setCenter(global);
        
        
        scene = new Scene(border);
        
        primaryStage.setTitle("The Walking Suricate");
        primaryStage.setScene(scene);
                
        primaryStage.show();
        
        
    }
    private Group importFromFile(String fileName) {
    	File file = new File(fileName);
        ColModelImporter importer = new ColModelImporter();
        importer.read(file);
        Node[] nodes = importer.getImport();
    	
        Group myG = new Group(nodes);
        return myG;
    }
    public void initializeSuricates() {
    	
    	int transX;
    	final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
    	Cylinder Suricate ;
        
        
    	for(int i = 0 ; i < nbMaxSuri ; i++) {
    		transX = (int) ((-LARGEUR_SCENE/2) + Math.random() * (LARGEUR_SCENE-suricateL));
    		Suricate = new Cylinder(suricateL,suricateSize);
            Suricate.setMaterial(greenMaterial);
            Suricate.setTranslateY(suricateSize/2); //pour le remettre à la bonne hauteur 
            Suricate.setTranslateX(transX);
            Suricate.setTranslateZ(LARGEUR_SCENE * RATIO_PROFONDEUR / 2 - suricateL); //On le recule
            
    		Suricates[i] = Suricate;
    		Suricates[i].setVisible(false);
    		world.getChildren().add(Suricates[i]);
    	}
    }
    
    public void addSuricate() {
    	addSuricate(timeForSuricate);
    }
    
    public void addSuricate(int animationTime) {
    	Suricates[nbSuricates%nbMaxSuri].setVisible(true);;
    	Suricates[nbSuricates%nbMaxSuri].setTranslateZ(LARGEUR_SCENE * RATIO_PROFONDEUR / 2 - suricateL); //On le recule
    	
        TranslateTransition t1 = new TranslateTransition(Duration.millis(animationTime));
        t1.setByZ(-LARGEUR_SCENE * RATIO_PROFONDEUR / 2 - suricateL);
        t1.setCycleCount(1);
        
        ParallelTransition transi = new ParallelTransition(Suricates[nbSuricates%nbMaxSuri],t1);
        transi.play();
        
        nbSuricates++;
        
    }
    public void addSword() {
    	epee = importFromFile("src/epeesimple.dae");
        epee.setScaleX(SCALE_EPEE/1.5);
        epee.setScaleY(-SCALE_EPEE);
        epee.setScaleZ(SCALE_EPEE);

        Translate transX = new Translate(-2,0,0);
        Translate transY = new Translate(0,-4,0);
        Translate transZ = new Translate(0,0,-3.5);
        Rotate rotateY = new Rotate(-90, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(45,Rotate.Z_AXIS);
        epee.getTransforms().addAll(rotateY,transZ,transX,transY,rotateZ);         
        
        int duration = 100;
        Point3D pts = new Point3D(0,1,0);
        RotateTransition rotate = new RotateTransition(Duration.millis(duration)); 
        epee.setRotationAxis(pts);
        rotate.setFromAngle(-40);
        rotate.setToAngle(70);
        rotate.setCycleCount(2);
        rotate.setAutoReverse(true);
        
        TranslateTransition translate1 = new TranslateTransition(Duration.millis(duration));
        translate1.setByY(-35);
        translate1.setFromY(12);
        translate1.setAutoReverse(true);
        translate1.setCycleCount(2);
        
        swordTransition1 = new ParallelTransition(epee,rotate,translate1);
        
        
        
        System.out.println(epee.getTranslateX() + "," + epee.getTranslateY() + "," + epee.getTranslateZ());        
        testGroup.getChildren().addAll(epee);
    }
    
    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        
        score = new Text("Score : 0 .......................................");
        score.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        score2 = score;
        hbox.getChildren().add(score);
        setScore(0);
        
        return hbox;
    }
    
    
    public void setScore(int _score) {
    	//System.out.println("Setting score : " + _score);
    	score2.setText("Score : " + _score);
    }
    
    private void baseGame() {
    	//Box b = new Box(50,50,50); 
    	Box sol = new Box(LARGEUR_SCENE,1,LARGEUR_SCENE * RATIO_PROFONDEUR);
    	Box murGauche = new Box(1,LARGEUR_SCENE,LARGEUR_SCENE * RATIO_PROFONDEUR);
    	Box murDroit = new Box(1,LARGEUR_SCENE,LARGEUR_SCENE * RATIO_PROFONDEUR);
    	
    	final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.RED);
    	
        //b.setMaterial(blueMaterial);  
    	murDroit.setMaterial(redMaterial);
    	murGauche.setMaterial(redMaterial);
        sol.setMaterial(redMaterial);

    	murDroit.setTranslateY(LARGEUR_SCENE/2);
    	murDroit.setTranslateX(-LARGEUR_SCENE/2);
    	murGauche.setTranslateY(LARGEUR_SCENE/2);
    	murGauche.setTranslateX(LARGEUR_SCENE/2);
    	
        
        testGroup.getChildren().addAll(sol,murDroit,murGauche);
        
        testGroup.setVisible(true);
        world.getChildren().addAll(testGroup);
    	
    }
    private void buildScene() {
        root.getChildren().add(world);
    }
    
    public void turnSword() {
    	swordTransition1.play();
    	//System.out.println(score.getText());
    	
    	if(nbSuricates != 0){
    		//System.out.println("Position du suricate le plus proche : " + Suricates[0].getTranslateZ());
    		if(compteurSuri < nbSuricates) {
	    		while(Suricates[compteurSuri%nbMaxSuri].getTranslateZ() < epee.getBoundsInParent().getHeight() ) {
	    			System.out.println("Vous découpez un suricate");
	    			
	    			Suricates[compteurSuri%nbMaxSuri].setVisible(false);
	    			
	    			compteurSuri++;
	    			messages.add("TUE");
	    			//addSuricate();
	        	}
    		}
    	}
    	
    }
    
    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }
    
    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
 
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.t.setY(CAMERA_INITIAL_Y);
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		launch();
	}
	public void exit() {
		// TODO Auto-generated method stub
		Platform.exit();
		System.exit(0);
		
	}
	


}
