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
import org.usfirst.frc2016.robot2018.MMW_DifferentialDrive;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.*;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 *
 */
@SuppressWarnings("deprecation")
public class DriveTrainSRX extends Subsystem {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX talonDriveLeft2 = RobotMap.driveTrainSRXTalonDriveLeft2;
    private final WPI_TalonSRX talonDriveLeft3 = RobotMap.driveTrainSRXTalonDriveLeft3;
    private final WPI_TalonSRX talonDriveRight2 = RobotMap.driveTrainSRXTalonDriveRight2;
    private final WPI_TalonSRX talonDriveRight3 = RobotMap.driveTrainSRXTalonDriveRight3;
    private final WPI_TalonSRX talonDriveLeft1 = RobotMap.driveTrainSRXTalonDriveLeft1;
    private final WPI_TalonSRX talonDriveRight1 = RobotMap.driveTrainSRXTalonDriveRight1;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    private final MMW_DifferentialDrive differentialDrive = RobotMap.driveTrainSRXDifferentialDrive;
    
    //    private final RobotDrive robotDrive = RobotMap.drivetrainRobotDrive;
    private final double SPEED_P = .15;
	private final double SPEED_I = .05;
	private final double speedFeedForward = .6;
	private final double COUNTS_PER_INCH = 4096/12.57;  //Encoder counts / inch of travel
	private final int MAX_POSITION_ERROR = 15;
	
	/*
     * The following block of variables are used to hold values loaded from
     * NV RAM by RobotPrefs.
    */
	public double drivetrainVoltageLimit;
	public double rampIncrement;
	public double driveP;
	public double driveI;
	public double driveD;
	public double driveF;
	/*
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * End of values set by RobotPrefs
     */

	private double lastDesiredSpeed = 0;
	
	private double leftCurrentSpeed = 0;
	private double rightCurrentSpeed = 0;
	
	private double lastJoyLeft, lastJoyRight;
	private String lastDriveMode;
    private double lastRightCount, lastLeftCount;
    private double accumSpeed =0;
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;

	/*
	 * Magic Motion vales
	 * 
	 * Cim max no load speed 5330
	 * Cim load speed 5330 * .85 = 4530
	 * Gear box 64:1 ??
	 * 4964/64 = 77.5 RPMs
	 * 77.5/60 = 1.29 RPS
	 * 4096 counts/rev * 1.29 RPS = 5295 Counts / second
	 * 5295/1000*100 = 529.5 Revs Per 100ms
	 * SRX intrnal speed is -1023 to +1023
	 * Feed forward = 1023/529.5 = 1.93
	 * 
	 * Arm travel approx 1000 counts
	 * Desired travel time 1 second
	 * 1000 Counts/second
	 * 1000/1000 * 100 = 100 counts / 100ms
	 * Cruise Velocity = 100
	 * 
	 * 6 inches /sec = 2048 counts/second
	 * 2048/1000 * 100 = 205 counts/100ms
	 * Cruise Velocity = 205
	 * 
	 * Plan on .25 seconds to accelerate and .25 to decelerate
	 * Acceleration = 200
	 */

	
	
	
   public DriveTrainSRX() {
	loadConfig(Robot.config);
	talonDriveLeft1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative , 0, 0);
   	talonDriveLeft1.setSensorPhase(true); //!!!! Check this !!!!!
   	talonDriveLeft1.setInverted(false);
   	talonDriveLeft1.configAllowableClosedloopError(0, 0, 0);
   	talonDriveLeft1.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveLeft1.configForwardSoftLimitEnable(false, 0);
   	talonDriveLeft1.configReverseSoftLimitEnable(false, 0);
   	talonDriveLeft1.clearStickyFaults(0);
   	talonDriveLeft1.setIntegralAccumulator(0, 0, 0);
   	talonDriveLeft1.setNeutralMode(NeutralMode.Brake);
   	talonDriveLeft1.set(ControlMode.PercentOutput, 0);
   // talonDriveLeft1.setSafetyEnabled(false);
    
   	talonDriveLeft2.setInverted(false);
   	talonDriveLeft2.configAllowableClosedloopError(0, 0, 0);
   	talonDriveLeft2.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveLeft2.configForwardSoftLimitEnable(false, 0);
   	talonDriveLeft2.configReverseSoftLimitEnable(false, 0);
   	talonDriveLeft2.clearStickyFaults(0);
   	talonDriveLeft2.setIntegralAccumulator(0, 0, 0);
   	talonDriveLeft2.setNeutralMode(NeutralMode.Brake);
   	talonDriveLeft2.set(ControlMode.Follower, 0);
   	
   	talonDriveLeft3.setInverted(false);
   	talonDriveLeft3.configAllowableClosedloopError(0, 0, 0);
   	talonDriveLeft3.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveLeft3.configForwardSoftLimitEnable(false, 0);
   	talonDriveLeft3.configReverseSoftLimitEnable(false, 0);
   	talonDriveLeft3.clearStickyFaults(0);
   	talonDriveLeft3.setIntegralAccumulator(0, 0, 0);
   	talonDriveLeft3.setNeutralMode(NeutralMode.Brake);
   	talonDriveLeft3.set(ControlMode.Follower, 0);

   	talonDriveRight1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative , 0, 0);
   	talonDriveRight1.setSensorPhase(true); //!!!! Check this !!!!!
   	talonDriveRight1.setInverted(true);
   	talonDriveRight1.configAllowableClosedloopError(0, 0, 0);
   	talonDriveRight1.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveRight1.configForwardSoftLimitEnable(false, 0);
   	talonDriveRight1.configReverseSoftLimitEnable(false, 0);
   	talonDriveRight1.clearStickyFaults(0);
   	talonDriveRight1.setIntegralAccumulator(0, 0, 0);
   	talonDriveRight1.setNeutralMode(NeutralMode.Brake);
   	talonDriveRight1.set(ControlMode.PercentOutput, 0);
  // 	talonDriveRight1.setSafetyEnabled(false);
   	talonDriveRight2.setInverted(true);
   	talonDriveRight2.configAllowableClosedloopError(0, 0, 0);
   	talonDriveRight2.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveRight2.configForwardSoftLimitEnable(false, 0);
   	talonDriveRight2.configReverseSoftLimitEnable(false, 0);
   	talonDriveRight2.clearStickyFaults(0);
   	talonDriveRight2.setIntegralAccumulator(0, 0, 0);
   	talonDriveRight2.setNeutralMode(NeutralMode.Brake);
   	talonDriveRight2.set(ControlMode.Follower, 3);

   	talonDriveRight3.setInverted(true);
   	talonDriveRight3.configAllowableClosedloopError(0, 0, 0);
   	talonDriveRight3.configForwardLimitSwitchSource(
   			LimitSwitchSource.FeedbackConnector,
   			LimitSwitchNormal.NormallyOpen, 0);
   	talonDriveRight3.configForwardSoftLimitEnable(false, 0);
   	talonDriveRight3.configReverseSoftLimitEnable(false, 0);
   	talonDriveRight3.clearStickyFaults(0);
   	talonDriveRight3.setIntegralAccumulator(0, 0, 0);
   	talonDriveRight3.setNeutralMode(NeutralMode.Brake);
   	talonDriveRight3.set(ControlMode.Follower, 3);

   	/*
   	 * Additional settings for motion magic Left
   	 */
	talonDriveLeft1.config_kF(0, driveF, 0);
	/* Set relevant frame periods to be at least as fast as periodic rate*/
	talonDriveLeft1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
	talonDriveLeft1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 0);
	
	/* set the peak and nominal outputs */
	talonDriveLeft1.configNominalOutputForward(0, 0);
	talonDriveLeft1.configNominalOutputReverse(0, 0);
	talonDriveLeft1.configPeakOutputForward(1, 0);
	talonDriveLeft1.configPeakOutputReverse(-1, 0);
	
	/* set acceleration and vcruise velocity - see documentation */
	talonDriveLeft1.configMotionCruiseVelocity(205, 0);
	talonDriveLeft1.configMotionAcceleration(100, 0);
	
   	/*
   	 * Additional settings for motion magic Right
   	 */
	talonDriveRight1.config_kF(0, driveF, 0);
	/* Set relevant frame periods to be at least as fast as periodic rate*/
	talonDriveRight1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
	talonDriveRight1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 0);
	
	/* set the peak and nominal outputs */
	talonDriveRight1.configNominalOutputForward(0, 0);
	talonDriveRight1.configNominalOutputReverse(0, 0);
	talonDriveRight1.configPeakOutputForward(1, 0);
	talonDriveRight1.configPeakOutputReverse(-1, 0);
	
	/* set acceleration and vcruise velocity - see documentation */
	talonDriveRight1.configMotionCruiseVelocity(204, 0);
	talonDriveRight1.configMotionAcceleration(100, 0);
   }
   
   
   public void periodic() {
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
   
   public void gameVelocityDrive(Joystick joy) {
	   final double MAX_SPEED = 2000;
	   double leftOut, rightOut;
		double rightYstick = -joy.getY();
		double motorOutput = talonDriveRight1.getMotorOutputPercent();
		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
		_sb.append("\tspd:");
		_sb.append(talonDriveRight1.getSelectedSensorVelocity(0));
		
		if (joy.getRawButton(1)) {
			/* Speed mode */
			/* Convert 500 RPM to units / 100ms.
			 * 4096 Units/Rev * 500 RPM / 600 100ms/min in either direction:
			 * velocity setpoint is in units/100ms
			 */
			double targetVelocity_UnitsPer100ms = rightYstick * 500.0 * 4096 / 600;
			/* 500 RPM in either direction */
			talonDriveRight1.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms);

			/* append more signals to print when in speed mode. */
			_sb.append("\terr:");
			_sb.append(talonDriveRight1.getClosedLoopError(0));
			_sb.append("\ttrg:");
			_sb.append(targetVelocity_UnitsPer100ms);
		} else {
			/* Percent voltage mode */
			SmartDashboard.putNumber("Vel Pad Y", rightYstick);
			talonDriveRight1.set(ControlMode.PercentOutput, rightYstick);
			
		}

		if (++_loops >= 10) {
			_loops = 0;
			System.out.println(_sb.toString());
		}
		_sb.setLength(0);
		//differentialDrive.tankDrive(0,0);
		
		/*
		leftOut = joy.getY()*MAX_SPEED;
		rightOut = joy.getRawAxis(5) * MAX_SPEED;
		talonDriveLeft1.set(ControlMode.Velocity, leftOut);
		talonDriveRight1.set(ControlMode.Velocity, rightOut);
		 */   
		pingDifferentialDrive();
   }
   
   /*
    * Used for single joystick device using X and Y
    */
   public void arcadeDrive(Joystick joy) {
	   accumSpeed = 0;
	   lastJoyLeft = joy.getY();
	   lastJoyRight = joy.getX();
	   differentialDrive.arcadeDrive(joy.getY(), joy.getX(), true);
	   lastDriveMode = "ArcadeJoy";
    }
    
   /*
    * Arcade drive using game pad axis 5 as direction
    */
   public void arcadeDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	lastJoyRight= leftJoy.getRawAxis(5);
    	lastJoyLeft = leftJoy.getY();
    	// The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, lastJoyLeft);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, lastJoyRight);
        //SmartDashboard.putNumber("LJoyY", lastJoyLeft);
        //SmartDashboard.putNumber("LCurrentSpeed", leftCurrentSpeed);
        differentialDrive.arcadeDrive(leftCurrentSpeed, rightCurrentSpeed, true);
        lastDriveMode = "ArcadeJoy";
    }

   /*
    * Arcade drive using values for speed and direction
    */
   public void arcadeDrive(double speed, double direction) {
		velocityDrive(speed, direction);
    	lastJoyLeft = speed;
    	lastJoyRight = direction;
    	lastDriveMode = "Arcade";
    }
    
   /*
    * Arcade style drive using the gyro for feed back
    * 
    * Special case:
    * 	If the speed is zero, take the speed from the left joy stick.
    */
   public void gyroDrive(double speed, double angle) {
    	double steer =  (Robot.gyro.getAngle() - angle);
    	if (steer > 180) {
    		steer = steer - 360;
    	}
    	else if (steer < -180) {
    		steer = steer + 360;
    	}
    	steer *= -Robot.gyro.gyroP;
    	
    	if (steer > Robot.gyro.TURN_MAX) {
    		steer = Robot.gyro.TURN_MAX;
    	}
    	else if (steer < -Robot.gyro.TURN_MAX) {
    		steer = -Robot.gyro.TURN_MAX;
    	}
    	if (speed== 0) {
    		//Use the joystick or stop if centered
    		//Robot.drivetrain.arcadeDrive(Robot.oi.driveRight.getY(), steer);
    		//velocityDrive(Robot.oi.driveRight.getY(), steer);
//        	robotDrive.arcadeDrive(Robot.oi.driveRight.getY(), steer);
        	differentialDrive.arcadeDrive(Robot.oi.driveLeft.getX(), steer);
    	}
    	else {
//    		Robot.drivetrain.arcadeDrive(speed, steer);
    		velocityDrive(speed, steer);
        	lastJoyLeft = speed;
        	lastJoyRight = steer;
        	lastDriveMode = "Gyro";
    	}
    }
    
   /*
    * Used by other methods for sort of PID speed control 
    */
   public void velocityDrive(double speed, double direction) {
    	double rateError;
    	double finalSpeed;
    	double averageRate;
    	finalSpeed = 0;
    	speed /= 10;
    	if (Math.abs(speed) < .01) speed = 0;
    	if (speed==0) {
    		accumSpeed = 0;
    	}
    	else
    	{
        	if (accumSpeed == 0)
        	{
        		accumSpeed = (speed > 0) ? speedFeedForward : -speedFeedForward;
        	}

    		averageRate=getAverageRate();
    		rateError = speed + averageRate/300;
    		accumSpeed += rateError * SPEED_I;
    		if (accumSpeed > .7) {
    			accumSpeed = .7;
    		}
    		else if (accumSpeed <-.7) {
    			accumSpeed = -.7;
    		}
    		finalSpeed = accumSpeed + rateError * SPEED_P;
    		SmartDashboard.putNumber("rateError", rateError);
    		SmartDashboard.putNumber("accumSpeed", accumSpeed);
    	}

    	differentialDrive.arcadeDrive(finalSpeed, direction);
    	lastJoyLeft = finalSpeed;
    	lastJoyRight = direction;
    	lastDriveMode = "Velocity";
    }
   
   /*
    * Method for driving a fixed distance straight
    * This may get expanded to steer as well.
    * For now it's a test to used motion magic to drive
    */
   public void goToUsingMM(double speed, int distance, double direction) {
	   int finalLeft, finalRight;
	   int distanceAsCounts;
	   distance = 100;
	   // convert absolute distance into absolute encoder counts
	   distanceAsCounts = (int)Math.round(distance * COUNTS_PER_INCH);
	   
	   // for now reset encoders to make debugging easier
	   // May not want to do this in the final code.
	   resetEncoders();
	   
	   /*
	    * Get the current encoder counts and 
	    * convert distance counts to relative counts
	    * It may be better to reset the counters.
	    */
	   finalLeft = distanceAsCounts + getLeftEncoder();
	   finalRight = distanceAsCounts + getRightEncoder();
	   
		talonDriveLeft1.set(ControlMode.MotionMagic, finalLeft);
		talonDriveRight1.set(ControlMode.MotionMagic, finalRight);
		/*
		 * Will need to disable motorSafetyHepler when not using differentialDrive Calls
		 */
   }
   
   public void pingDifferentialDrive() {
		differentialDrive.pingMotorSafety();
   }

   /*
    * Method to go with the above move used to determine
    * when the move has completed
    */
   public boolean moveComplete() {
	   boolean leftGood = Math.abs(talonDriveLeft1.getClosedLoopError(1)) < MAX_POSITION_ERROR;
	   boolean rightGood = Math.abs(talonDriveRight1.getClosedLoopError(1)) < MAX_POSITION_ERROR;
	   return (leftGood && rightGood);
   }
   /*
    * Returns the average encoder rate of left and right 
    */
   public double getAverageRate() {
    	double rightRate, leftRate, averageRate;

    	rightRate = talonDriveRight1.getSelectedSensorVelocity(0);
    	leftRate = talonDriveLeft1.getSelectedSensorVelocity(0);
    	averageRate=(leftRate+rightRate)/2;
    	return averageRate;
    }
    
    /*
     * Tank drive using game pad where right comes from axis 5
     */
    public void tankDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	lastJoyRight= rightJoy.getRawAxis(5);
    	lastJoyLeft = leftJoy.getY();

        // The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, lastJoyLeft);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, lastJoyRight);
        SmartDashboard.putNumber("LSpeed", leftCurrentSpeed);
        SmartDashboard.putNumber("RSpeed", rightCurrentSpeed);
        SmartDashboard.putNumber("lastJoyLeft", lastJoyLeft);
    	differentialDrive.tankDrive(-leftCurrentSpeed, rightCurrentSpeed, true);
    	lastDriveMode = "Tank";    	
    }	
    
    /*
     * Stop the robot
     */
    public void driveStop() {
    	differentialDrive.tankDrive(0,0);
    	accumSpeed = 0;
    }
    
    /*
     * Set the max voltage to the drive train
     * The joystick values will be scaled by this value
     */
    public void setMax() {
        differentialDrive.setMaxOutput(drivetrainVoltageLimit);
    }
    
    // This method performs the ramp calculation for the drive train
    double returnRamp(double currentSpeed, double desiredSpeed) {
    	double delta = desiredSpeed - currentSpeed;

    	if (delta > rampIncrement) {
    		delta = rampIncrement;
    	}
    	else if ( delta < - rampIncrement) {
    		delta = - rampIncrement;
    	}
    	return (currentSpeed + delta);
    }
    
    public int getRightEncoder() {
    	
    	return talonDriveRight1.getSelectedSensorPosition(0);
    }

    public int getLeftEncoder() {
    	return talonDriveLeft1.getSelectedSensorPosition(0);
    }
    
    public void resetEncoders() {
    	talonDriveLeft1.setSelectedSensorPosition(0, 0, 0);
    	talonDriveRight1.setSelectedSensorPosition(0, 0, 0);
    }

    public void addTelemetryHeaders() {
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveLeft1, "Drive Left 1");
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveLeft2, "Drive Left 2");
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveLeft3, "Drive Left 3");
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveRight1, "Drive Right 1");
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveRight2, "Drive Right 2");
	   	Robot.currentMonitor.registerMonitorDevive(talonDriveRight3, "Drive Right 3");
	   	Robot.telem.addColumn("Drive Left");
	   	Robot.telem.addColumn("Drive Right");
	   	Robot.telem.addColumn("Drive Mode");
   }
   
   public void writeTelemetry() {
	   Robot.telem.saveDouble("Drive Left", lastJoyLeft);
	   Robot.telem.saveDouble("Drive Right", lastJoyRight);
	   Robot.telem.saveString("Drive Mode", lastDriveMode);
   }
   
    public void loadConfig(Config config) {
    	drivetrainVoltageLimit = config.getDouble("DriveVLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
    	setMax();
    	rampIncrement = config.getDouble("DriveRampIncrement", Defaults.DRIVETRAIN_RAMP_INCREMENT);
    	driveP = config.getDouble("DriveP", Defaults.DRIVETRAIN_P);
    	driveI = config.getDouble("DriveI", Defaults.DRIVETRAIN_I);
    	driveD = config.getDouble("DriveD", Defaults.DRIVETRAIN_D);
    	driveF = config.getDouble("DriveF", Defaults.DRIVETRAIN_F);
    }
    @Override
    	public void initDefaultCommand() {
      // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new GamePadDrive());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

      // Set the default command for a subsystem here.
      // setDefaultCommand(new MySpecialCommand());
  }

}

