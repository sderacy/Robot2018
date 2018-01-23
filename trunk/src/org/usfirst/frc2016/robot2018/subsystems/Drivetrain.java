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

import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.commands.GamePadDrive;
import org.usfirst.frc2016.robot2018.commands.TankDrive;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
/**
import.edu.wpi.first.wpilibj.SpeedController;


 */
public class Drivetrain extends Subsystem {

	private final double SPEED_P = .15;
	private final double SPEED_I = .05;
	private final double speedFeedForward = .6;
	/*
     * The following block of variables are used to hold values loaded from
     * NV RAM by RobotPrefs.
    */
	public double drivetrainVoltageLimit;
	public double rampIncrement = .2;
	private double lastDesiredSpeed = 0;
	
	private double leftCurrentSpeed = 0;
	private double rightCurrentSpeed = 0;
	
    /*
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * End of values set by RobotPrefs
     */
    private double lastRightCount, lastLeftCount;
    private double accumSpeed =0;

    
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final SpeedController leftdrive = RobotMap.drivetrainLeftdrive;
    private final SpeedController rightdrive = RobotMap.drivetrainRightdrive;
    private final RobotDrive robotDrive = RobotMap.drivetrainRobotDrive;
    private final Encoder leftEncoder = RobotMap.drivetrainLeftEncoder;
    private final Encoder rightEncoder = RobotMap.drivetrainRightEncoder;
    private final Solenoid shiftSolenoid = RobotMap.drivetrainShiftSolenoid;
    private final Compressor compressor = RobotMap.drivetrainCompressor;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public Drivetrain() {
    	compressor.start();
    	leftEncoder.reset();
    	rightEncoder.reset();
    }

    public void periodic() {
    	Robot.currentMonitor.driveTrainCurrentReport(leftdrive.get(), rightdrive.get());
    }
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void arcadeDrive(Joystick joy) {
    	robotDrive.arcadeDrive(joy, true);
    }
    
    public void arcadeDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	double rightY;
    	double leftY;
    	//if (Robot.frontCameraActive) {
//    		rightY= adjustDriveValue(rightJoy.getY());
    		rightY= adjustDriveValue(leftJoy.getRawAxis(4));
    		leftY = adjustDriveValue(leftJoy.getY());
    	//}
    	//else {
    	//	leftY= -adjustDriveValue(rightJoy.getY());
    	//	rightY = -adjustDriveValue(leftJoy.getY());
    	//}
        // The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, leftY);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, rightY);
        //SmartDashboard.putNumber("LJoyY", letfY);
        //SmartDashboard.putNumber("LCurrentSpeed", leftCurrentSpeed);
    	robotDrive.arcadeDrive(leftCurrentSpeed, rightCurrentSpeed, true);
    }
    public void arcadeDrive(double speed, double direction) {
//    	robotDrive.arcadeDrive(speed, direction);
		velocityDrive(speed, direction);
    }
    
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
        	robotDrive.arcadeDrive(Robot.oi.driveRight.getY(), steer);
    	}
    	else {
//    		Robot.drivetrain.arcadeDrive(speed, steer);
    		velocityDrive(speed, steer);
    	}
    }
    
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
    	robotDrive.arcadeDrive(finalSpeed, direction);
    	//Robot.drivetrain.arcadeDrive(finalSpeed, direction);
    }
    
    public double getAverageRate() {
    	double rightRate, leftRate, averageRate;

    	rightRate = rightEncoder.getRate();
    	leftRate = leftEncoder.getRate();
    	averageRate=(leftRate+rightRate)/2;
    	return averageRate;
    }
    
    public void tankDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	double rightY;
    	double leftY;
    	//if (Robot.frontCameraActive) {
//    		rightY= adjustDriveValue(rightJoy.getY());
    		rightY= adjustDriveValue(leftJoy.getRawAxis(5));
    		leftY = adjustDriveValue(leftJoy.getY());
    	//}
    	//else {
    	//	leftY= -adjustDriveValue(rightJoy.getY());
    	//	rightY = -adjustDriveValue(leftJoy.getY());
    	//}
        // The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, leftY);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, rightY);
        //SmartDashboard.putNumber("LJoyY", letfY);
        //SmartDashboard.putNumber("LCurrentSpeed", leftCurrentSpeed);
    	robotDrive.tankDrive(leftCurrentSpeed, rightCurrentSpeed);
    }	
    
    public void driveStop() {
    
    	robotDrive.tankDrive(0,0);
    	accumSpeed = 0;
    }
    
    public void setMax() {
        robotDrive.setMaxOutput(drivetrainVoltageLimit);
    }
    
    private double adjustDriveValue(double joyY) {
        // Scale the value by the drive limit to limit speed over full travel of the joystick
        joyY = joyY * drivetrainVoltageLimit;
        
        // Square the values for better control at the low end
        // Check the sign now to fix it later
        boolean ltz = joyY < 0;
        joyY = joyY * joyY;

        /* should no longer be needed with scale above
        // Limit logic
        if (Math.abs(leftY) > RobotMain.drivetrainVoltageLimit) {
            leftY = RobotMain.drivetrainVoltageLimit;
        }
        */
        
        // Fix the sign
        if (ltz) {
            joyY *= -1;
        }
        
    	return joyY ;
    }

    // This method performs the ramp calculation for the drive train
    double returnRamp(double currentSpeed, double desiredSpeed) {
    	double delta = desiredSpeed - currentSpeed;
/*    	if ( delta < 0 && desiredSpeed > 0) { //slowing from forward, but still forward
    		return desiredSpeed;
    	}
    	if ( delta > 0 && desiredSpeed < 0) { // slowing from reverse but still reverse)
    		return desiredSpeed;
    	}
*/    	if (delta > rampIncrement) {
    		delta = rampIncrement;
    	}
    	else if ( delta < - rampIncrement) {
    		delta = - rampIncrement;
    	}
    	return (currentSpeed + delta);
    }
    public void shiftLow() {
    	shiftSolenoid.set(false);
    }

    public void shiftHigh() {
    	shiftSolenoid.set(true);
    	
    }
    
    public double getRightEncoder() {
    	return rightEncoder.getDistance();
    }

    public double getLeftEncoder() {
    	return leftEncoder.getDistance();
    }
    
    public void resetEncoders() {
    	rightEncoder.reset();
    	leftEncoder.reset();
    }
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new GamePadDrive());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
}

