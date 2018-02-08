// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2016.robot2018.subsystems;

import org.usfirst.frc2016.robot2018.Defaults;
import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.Config;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.command.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


public class CubePickup extends Subsystem {
	/*
	 * Values set by config
	 */
    public double wheelSpeedShoot;
    public double wheelSpeedIn;
    public double wheelSpeedOut;
    public double wheelSpeedRotate;
    /* end config values */
    
    
	private boolean shootCubeActive = false;
	// Value from point of view control on joy stick.
	public int pov;

	// State Machine Variable
	public int armState;

	// The button that was pressed to get us out of idle
	// and that will need to be released to get back to idle
	int currentButton;

	// Internal request to operate the arms
	public boolean armOpen;

	// The button value when no button is pressed
	private final int	NO_BUTTON = -1;
	private final String CUBEWHEELSLEFT = "Cube Wheels Left";
	private final String CUBEWHEELSRIGHT = "Cube Wheels Right";
	private final String ARMSOPEN = "Arms Open";
	private final String WHEELSPOV = "Wheels POV";
	private final String SHOOTCUBEACTIVE = "Shoot Cube";
	private final String LEFTARMPOSITION = "Left Arm Position";
	private final String RIGHTARMPOSITION = "Right Arm Position";
	
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX talonCubeWheelLeft = RobotMap.cubePickupTalonCubeWheelLeft;
    private final WPI_TalonSRX talonCubeWheelRight = RobotMap.cubePickupTalonCubeWheelRight;
    private final Compressor compressor = RobotMap.cubePickupCompressor;
    private final Solenoid solenoidArm = RobotMap.cubePickupSolenoidArm;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	private final SensorCollection leftTalonSensors = new SensorCollection(talonCubeWheelLeft);
	private final SensorCollection rightTalonSensors = new SensorCollection(talonCubeWheelRight);
	
	public CubePickup() {
		loadConfig(Robot.config);
		compressor.start();
		armOpen = false;
		currentButton = NO_BUTTON;	// No buttons pressed

		talonCubeWheelLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute , 0, 0);
		talonCubeWheelLeft.setSensorPhase(true); //!!!! Check this !!!!!
		talonCubeWheelLeft.setInverted(true);
		talonCubeWheelLeft.configAllowableClosedloopError(0, 0, 0);
		talonCubeWheelLeft.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonCubeWheelLeft.configForwardSoftLimitEnable(false, 0);
		talonCubeWheelLeft.configReverseSoftLimitEnable(false, 0);
		talonCubeWheelLeft.clearStickyFaults(0);
		talonCubeWheelLeft.setIntegralAccumulator(0, 0, 0);
		talonCubeWheelLeft.setNeutralMode(NeutralMode.Brake);
		talonCubeWheelLeft.set(ControlMode.PercentOutput, 0);

		talonCubeWheelRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute , 0, 0);
		talonCubeWheelRight.setSensorPhase(true); //!!!! Check this !!!!!
		talonCubeWheelRight.setInverted(false);
		talonCubeWheelRight.configAllowableClosedloopError(0, 0, 0);
		talonCubeWheelRight.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonCubeWheelRight.configForwardSoftLimitEnable(false, 0);
		talonCubeWheelRight.configReverseSoftLimitEnable(false, 0);
		talonCubeWheelRight.clearStickyFaults(0);
		talonCubeWheelRight.setIntegralAccumulator(0, 0, 0);
		talonCubeWheelRight.setNeutralMode(NeutralMode.Brake);
		talonCubeWheelRight.set(ControlMode.PercentOutput, 0);
	}

	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new CloseArm());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop
		double leftWheel, rightWheel;
		// Wheel rotation logic
		// What position is the pov stick in
		if (shootCubeActive) {
			leftWheel = wheelSpeedShoot;
			rightWheel = wheelSpeedShoot;
		}
		else {
			pov = Robot.oi.operatorJoy.getPOV();


			switch (pov) {

			/*
			 * This is the case where the POV is centered, stop the wheels here
			 */
			case -1:
				leftWheel = 0;
				rightWheel = 0;
				break;

				/*
				 * POV is pressed forward, cause the cube to move away from the bot.
				 */
			case 0:
				leftWheel =  wheelSpeedOut;
				rightWheel = wheelSpeedOut;
				break;

				/*
				 * POV is to the right, rotate the cube clockwise.
				 */
			case 90:
				leftWheel =   wheelSpeedRotate;
				rightWheel = -wheelSpeedRotate;
				break;

				/*
				 * POV is back, cause the cube to move toward the bot.
				 */
			case 180:
				leftWheel =  -wheelSpeedIn;
				rightWheel = -wheelSpeedIn;
				break;

				/*
				 * POV is to the left, rotate the cube counter clockwise.
				 */
			case 270:
				leftWheel =  -wheelSpeedRotate;
				rightWheel =  wheelSpeedRotate;
				break;

				/*
				 *  While this isn't obvious, there are positions between the above cases.
				 * If the operator drifts into one of these, the defalut code below
				 * prevents the wheels from stopping. If the pov continues to move and
				 * lands on one of the above cases, the wheels will change as needed.
				 */
			default:
				leftWheel = talonCubeWheelLeft.get();
				rightWheel = talonCubeWheelRight.get();
				break;
			}
		}
		/*
		 * Now update the wheel motors with the values determined above.
		 */


		talonCubeWheelRight.set(ControlMode.PercentOutput, rightWheel);
		talonCubeWheelLeft.set(ControlMode.PercentOutput, leftWheel);

	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	public void openArms() {
		armOpen = true;
		solenoidArm.set(true);
	}

	public void closeArms() {
		armOpen = false;
		solenoidArm.set(false);
	}

	public void addTelemetryHeaders() {
		Robot.currentMonitor.registerMonitorDevive(talonCubeWheelLeft, CUBEWHEELSLEFT);
		Robot.currentMonitor.registerMonitorDevive(talonCubeWheelRight, CUBEWHEELSRIGHT);
		Robot.telem.addColumn(ARMSOPEN);
		Robot.telem.addColumn(WHEELSPOV);
		Robot.telem.addColumn(SHOOTCUBEACTIVE);
		Robot.telem.addColumn(LEFTARMPOSITION);
		Robot.telem.addColumn(RIGHTARMPOSITION);
	}

	public void writeTelemetyValues() {
		Robot.telem.saveBoolean(ARMSOPEN,armOpen);
		Robot.telem.saveInteger(WHEELSPOV,pov);
		Robot.telem.saveBoolean(SHOOTCUBEACTIVE, shootCubeActive);
		Robot.telem.saveInteger(LEFTARMPOSITION, leftTalonSensors.getAnalogIn());
		Robot.telem.saveInteger(RIGHTARMPOSITION, rightTalonSensors.getAnalogIn());
		
	}
	
	public void loadConfig(Config config) {
	    wheelSpeedShoot = config.getDouble("CubeWheelSpeedShoot", Defaults.WHEELSPEED_SHOOT);
	    wheelSpeedIn = config.getDouble("CubeWheelSpeedIn", Defaults.WHEELSPEED_IN);
	    wheelSpeedOut = config.getDouble("CubeWheelSpeedOut", Defaults.WHEELSPEED_OUT);
	    wheelSpeedRotate = config.getDouble("CubeWheelSpeedRotate", Defaults.WHEELSPEED_ROTATE);
	}
	public void shootCube(){
		shootCubeActive = true;
		//  talonCubeWheelRight.set(ControlMode.PercentOutput, 1);
		//  talonCubeWheelLeft.set(ControlMode.PercentOutput, 1);	
	}
	public void endShoot(){
		shootCubeActive = false;
	}
}

