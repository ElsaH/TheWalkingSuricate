package walking.suricate;

//import processing.core.PApplet;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
/*import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;*/
import javafx.stage.Stage;
import javafx.util.Duration;
//import javafx.builders.RectangleBuilder;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.geometry.Point3D;

//package walking.suricate;

import processing.core.PApplet;

import java.io.File;
import java.util.Scanner;

import com.interactivemesh.jfx.importer.col.ColModelImporter;
/*
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;*/

public class TheWalkingSuricate extends Application {

	final Group root = new Group();
	static Group epee = new Group();
	static ParallelTransition transition;
    final Xform world = new Xform();
    final Group testGroup = new Group();
    boolean isRunning = false;
 
    private static final double AXIS_LENGTH = 250.0;
	final Xform axisGroup = new Xform();
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		PApplet.main("TheWalkingSuricate");
//	}
//	
	public void settings() {
		//size(300,300);
	}
	
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -150;
    private static final double CAMERA_INITIAL_X_ANGLE = 10.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double CAMERA_INITIAL_Y = 70;

    private static final int LARGEUR_SCENE = 150;
    private static final int SCALE_EPEE = 30;
    
    @Override
    public void start(Stage primaryStage) {

    	buildScene();
    	buildCamera();
        //buildAxes();
        
        test();
    	
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.GREY);
        scene.setCamera(camera);

        primaryStage.setTitle("The Walking Suricate");
        primaryStage.setScene(scene);
        
        
        
        primaryStage.show();
        
        
    }
    private Group importFromFile(String fileName) {
    	File file = new File(fileName);
        ColModelImporter importer = new ColModelImporter();
        importer.read(file);
        Node[] nodes = importer.getImport();
    	
        System.out.println("There is " + nodes.length + " nodes imported from file " + fileName);
        System.out.println(nodes[0].toString());
        System.out.println(nodes[0].getBoundsInParent().getHeight());
        System.out.println(nodes[0].getBoundsInParent().getWidth());
        System.out.println(nodes[0].getBoundsInParent().getDepth());
        
        Group myG = new Group(nodes);
        return myG;
    }
    
    private void test() {
    	//Box b = new Box(50,50,50); 
    	Box sol = new Box(LARGEUR_SCENE,1,LARGEUR_SCENE*5000);
    	Box murGauche = new Box(1,LARGEUR_SCENE,LARGEUR_SCENE*5000);
    	Box murDroit = new Box(1,LARGEUR_SCENE,LARGEUR_SCENE*5000);
    	
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
    	
        epee = importFromFile("src/epeesimple.dae");
        
        epee.setScaleX(SCALE_EPEE);
        epee.setScaleY(-SCALE_EPEE);
        epee.setScaleZ(SCALE_EPEE);
     
        
        Translate transX = new Translate(-2,0,0);
        Translate transY = new Translate(0,-4,0);
        Translate transZ = new Translate(0,0,-3.5);
        Rotate rotateY = new Rotate(-90, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(45,Rotate.Z_AXIS);
        epee.getTransforms().addAll(rotateY,transZ,transX,transY,rotateZ);         
        
        Point3D pts = new Point3D(0,1,0);
        RotateTransition rotate = new RotateTransition(Duration.millis(200)); 
        epee.setRotationAxis(pts);
        rotate.setFromAngle(-20);
        rotate.setToAngle(20);
        rotate.setCycleCount(Timeline.INDEFINITE);
        rotate.setAutoReverse(true);
        
        transition = new ParallelTransition(epee,rotate);
        
        
        
        System.out.println(epee.getTranslateX() + "," + epee.getTranslateY() + "," + epee.getTranslateZ());        
        
        testGroup.getChildren().addAll(sol,murDroit,murGauche);
        testGroup.getChildren().addAll(epee);
        testGroup.setVisible(true);
        world.getChildren().addAll(testGroup);
    	
    }
    private void buildScene() {
        System.out.println("buildScene");
        root.getChildren().add(world);
    }
    
    public void turnSword() {
    	if(!isRunning) {
    		isRunning = true;
    		transition.play();
    	}
    	else {
    		isRunning = false;
    		transition.stop();
    	}
        
    }
    
    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
 
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
 
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
 
        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);
        
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
 
        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    
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
        System.out.println("Bonjour entrer si vous voulez faire tourner l'épée ... ");
        Scanner in = new Scanner(System.in);
        int k = in.nextInt();
        if(k == 0)
        	transition.play();
        
    }
	


}
