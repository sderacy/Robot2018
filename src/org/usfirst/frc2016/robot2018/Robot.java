// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

	/*
	 * 	Motors
	 * 		Drive train left 3 Talons SRX Drive train
	 * 		Drive train rigt 3 TalonSRX
	 * 		Climber   1 spark		winch
	 * 		Arm  1 Talon SRX
	 * 		Intake Wheels left Talon SRX
	 * 		Intake Wheels right Talon SRX
	 * 		Intake opener  1 solenoid
	 * 		Compressor
	 */

package org.usfirst.frc2016.robot2018;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.CameraServer;

import org.usfirst.frc2016.robot2018.OI;
import org.usfirst.frc2016.robot2018.commands.*;
import org.usfirst.frc2016.robot2018.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
	private final String GEARALIGNMENT = "GearAlignment";
	private final String FLASHLEDS = "FlashLEDs";
	private final String RECORDVIDEO = "RecordVideo";
	private final String IDLE = "Idle";
	private  String PIMode = "comeback2later";
	private final double DELAYPERCOUNT = .02;
	private final double CAL_TIME_LIMIT = 4;
	private final String CONFIG_FILE_NAME = "/c/robot.cfg";
	private double calAttemptTimer;
	private boolean calTimerExpired;
	private CameraServer camServer;
	public static UsbCamera frontCamera;
	public static UsbCamera rearCamera;
	public static VideoSink server;
	public static boolean frontCameraActive=true;
	SendableChooser<Command> chooser = new SendableChooser<>();
	public static LCTelemetry telem;
	public static Config config;
	public static char gameData = 'N'; 
	
	/*
	 * Labels for auto routines 
	 */
	final String robotCenter = "Robot Center";
	final String driveUsingFile = "Drive Using File";
	final String driveForward = "Drive Forward";
	final String robotRightSide = "Robot Right";
	final String robotLeftSide = "Robot Left";
	final String robotLeftLongSide = "Robot Left Long";
	final String robotRightLongSide = "Robot Right Long";
	final String robotLeft45 = "Robot Left45";
	final String robotRight45 = "Robot Right45";
	
	/*
	 * What autonomous command to run
	 * and options on the smart dashboard for auto
	 */
	//    Command autonomousCommand;

	private static SendableChooser<CommandGroup> autoChooser;

	/*
	 * Flag to indicate that all systems are calibrated and ready
	 */
	public static boolean robotIsCalibrated = false;

	public static OI oi;
	//--public PowerDistributionPanel pdPanel;

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static Gyro gyro;
    public static Winch winch;
    public static Cameras cameras;
    public static CurrentMonitor currentMonitor;
    public static DriveTrainSRX driveTrainSRX;
    public static CubePickup cubePickup;
    public static Arm arm;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	public static CubeTrigger cubeTrigger;
    public static RobotPrefs robotPrefs; 
	public int startingPosition;
	Command autonomousCommand; // SHOULD HAVE BEEN AUTOGENERATED
	//    private CameraServer server;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		RobotMap.init();
		robotIsCalibrated = false;
		robotPrefs = new RobotPrefs();
		camServer = CameraServer.getInstance();
		//--rearCamera= camServer.startAutomaticCapture("Switcher", 0);
		config = new Config(CONFIG_FILE_NAME);
		//loadConfig();
		calAttemptTimer = 0;
		calTimerExpired = false;

		//SmartDashboard.putData(Scheduler.getInstance());
		telem = new LCTelemetry();      // create telem handle.
		telem.loadConfig(config);

		//		camServer = CameraServer.getInstance();
		//		frontCamera=camServer.startAutomaticCapture("Switcher", 0);
		// 0 being the port of the camera
		/*		frontCamera = new UsbCamera("Front",0);
		//        rearCamera = new UsbCamera("Rear", 1);
		camServer.addCamera(frontCamera);
		//        camServer.addCamera(rearCamera);
		server = camServer.addServer("serve_Switcher");
		server.setSource(frontCamera);
		frontCameraActive=true;
		 */



		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        gyro = new Gyro();
        
        winch = new Winch();
        //--SmartDashboard.putData(winch);
        
        cameras = new Cameras();
        currentMonitor = new CurrentMonitor();
        
        driveTrainSRX = new DriveTrainSRX();
        //--SmartDashboard.putData(driveTrainSRX);
        
        cubePickup = new CubePickup();
        //--SmartDashboard.putData(cubePickup);
        // Cube must be created before arm since arm will read values from 
        // the left cube talon.
        
        arm = new Arm();
        //--SmartDashboard.putData(arm);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
		// OI must be constructed after subsystems. If the OI creates Commands
		//(which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		
        cubeTrigger = new CubeTrigger();
        robotPrefs.setupPrefs();
//		robotPrefs.doLoadPrefs();
		//arm.goToPreset(arm.HIGH);
		oi = new OI();
		//--pdPanel = new PowerDistributionPanel();
		//		Scheduler.getInstance().add(new CalibrateElevator());

		// Set up the position choices
		autoChooser = new SendableChooser<CommandGroup>();
		autoChooser.addDefault(robotCenter, new AutoStartCenter());
		autoChooser.addObject(driveUsingFile, new DriveUsingFile());
		autoChooser.addObject(driveForward, new AutoCrossBaseline());
		autoChooser.addObject(robotLeftSide, new AutoStartLeft());
		autoChooser.addObject(robotRightSide, new AutoStartRight());
		autoChooser.addObject(robotLeftLongSide, new AutoStartLeftLongSide());
		autoChooser.addObject(robotRightLongSide, new AutoStartRightLongSide());
		autoChooser.addObject(robotLeft45, new AutoLeft45());
		autoChooser.addObject(robotRight45, new AutoRight45());
		SmartDashboard.putData("Autonomous choices", autoChooser);

		// instantiate the command used for the autonomous period
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

        chooser.addDefault("AutonomousCommand", new AutonomousCommand());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS
		/*
		 * Register all headers
		 */ 
		driveTrainSRX.addTelemetryHeaders();
		cubePickup.addTelemetryHeaders();
		gyro.addTelemetryHeaders();
		arm.addTelemetryHeaders();
		winch.addTelemetryHeaders();
	}

	/**
	 * This function is called when the disabled button is hit.
	 * You can use it to reset subsystems before shutting down.
	 */
	public void disabledInit(){
		telem.saveSpreadSheet();
	}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		updateDashboard();
	}

	public void autonomousInit() {
		telem.restartTimer();
		autonomousCommand = (Command) autoChooser.getSelected();
		// schedule the autonomous command (example)
		if (autonomousCommand != null) autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		//--if (!robotIsCalibrated) {
		//--	calibrateRobot();
		//--}
		writeTelem();
		updateDashboard();
	}

	public void teleopInit() {
		// This makes sure that the autonomous stops running whens
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		telem.restartTimer();
		if (autonomousCommand != null) autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
	//	robotPrefs.periodic();
		if (!robotIsCalibrated) {
			calibrateRobot();
		}
		Scheduler.getInstance().run();
		writeTelem();
		updateDashboard();
	}
	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		//--LiveWindow.run();
	}

	public void calibrateRobot() {
		if ((calAttemptTimer) <= CAL_TIME_LIMIT/DELAYPERCOUNT) {
			calAttemptTimer ++;
		}
		else {
			calTimerExpired = true;
		}
	}

	public boolean isCalibrated() {
		return robotIsCalibrated;
	}

	private void writeTelem() {
		cubePickup.writeTelemetyValues();
		gyro.writeTelemetry(); // remember to do Gyro
		driveTrainSRX.writeTelemetry();
		arm.writeTelemetyValues();
		telem.writeRow();
	}

	private void loadConfig() {
		arm.loadConfig(config);
		cubePickup.loadConfig(config);
		driveTrainSRX.loadConfig(config);
	}
	
	private void updateDashboard() {
		//SmartDashboard.putBoolean("Robot Calibrated",robotIsCalibrated);
		//SmartDashboard.putNumber("Left Encoder", Robot.driveTrainSRX.getLeftEncoder());
		SmartDashboard.putNumber("Left Distance ", Robot.driveTrainSRX.getLeftDistance());
		//SmartDashboard.putNumber("Left Encoder Vel", Robot.driveTrainSRX.getLeftEncoderVelocity());
		
		//SmartDashboard.putNumber("Right Encoder", Robot.driveTrainSRX.getRightEncoder());
		SmartDashboard.putNumber("Right Distance ", Robot.driveTrainSRX.getRightDistance());
		//SmartDashboard.putNumber("Right Encoder Vel", Robot.driveTrainSRX.getRightEncoderVelocity());
		//--SmartDashboard.putNumber("Encoder Rate", driveTrainSRX.getAverageRate());

		//--SmartDashboard.putString("PIMode", PIMode);
		//--SmartDashboard.putBoolean("Cube Detector", !RobotMap.cubeDetector.get());

	}
}

