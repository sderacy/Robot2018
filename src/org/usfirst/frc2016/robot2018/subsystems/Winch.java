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

import org.usfirst.frc2016.robot2018.Config;
import org.usfirst.frc2016.robot2018.Defaults;
import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.commands.WinchJoy;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 */
public class Winch extends Subsystem {
	public PowerDistributionPanel pdPanel;
	private final String WINCHMOTOR = "Winch Motor";
	private boolean upperLimitDetected = false;
	private boolean lowerLimitDetected = false;
	/*
	 * Values set by config
	 */
	private double stopDelay;
	/* End config values */

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX winchTalon = RobotMap.winchWinchTalon;
    private final DigitalInput winchUpperLimit = RobotMap.winchWinchUpperLimit;
    private final DigitalInput winchLowerLimit = RobotMap.winchWinchLowerLimit;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	// public double rampIncrement = 0.2;

	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	public Winch() {
		loadConfig(Robot.config);
		winchTalon.setInverted(false);
		winchTalon.configAllowableClosedloopError(0, 0, 0);
		winchTalon.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		winchTalon.configForwardSoftLimitEnable(false, 0);
		winchTalon.configReverseSoftLimitEnable(false, 0);
		winchTalon.clearStickyFaults(0);
		winchTalon.setIntegralAccumulator(0, 0, 0);
		winchTalon.setNeutralMode(NeutralMode.Brake);
		winchTalon.set(ControlMode.PercentOutput, 0);
	}
	public void periodic() {
		//    	Robot.currentMonitor.winchCurrentReport(winchTalon.get());
		if (!winchUpperLimit.get()) {
			upperLimitDetected = true;
		}
		if (!winchLowerLimit.get()) {
			lowerLimitDetected = true;
		}
		SmartDashboard.putBoolean("Winch Upper Limit", !winchUpperLimit.get());
		SmartDashboard.putBoolean("Winch Lower Limit", !winchLowerLimit.get());
	}
	public void lift()
	{
		/*	if (pdPanel.getCurrent(11) >= 5) {
    	winchTalon.set(0);
    }
    	else {
    		winchTalon.set(-1);
    	}
		 */
		winchTalon.set(-1);
	}
	public void drop()
	{
		winchTalon.set(1);
	}

	public void stop()
	{
		winchTalon.set(0);
	}

	public void variable(double speed) {
			speed = -applyDeadband(speed, .05);
			SmartDashboard.putNumber("WinchSpeed", speed);
		if (Robot.oi.operatorJoy.getRawButton(9)){
			if (speed > 0) {
				if(!upperLimitDetected) {
					winchTalon.set(speed);
				}
				else {
					winchTalon.set(0);
				}
				if (Robot.oi.operatorJoy.getRawAxis(3) == 1) {
					winchTalon.set(speed);
				}
				lowerLimitDetected = !winchLowerLimit.get();
			}
			else {
			if (!lowerLimitDetected) {
				winchTalon.set(speed);
			}
			else {
				winchTalon.set(0);
			}
		}
	}
		else {
			winchTalon.set(0);
		}
}		
	protected double applyDeadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			if (value > 0.0) {
				return (value - deadband) / (1.0 - deadband);
			} else {
				return (value + deadband) / (1.0 - deadband);
			}
		} else {
			return 0.0;
		}
	}
	public void lowCurrent() {
		winchTalon.set(-.1);
	}
	/* double returnRamp(double currentSpeed, double desiredSpeed) {
    	double delta = desiredSpeed - currentSpeed;
    	if (delta > rampIncrement) {
    		delta = rampIncrement;
    	}
    	else if ( delta < - rampIncrement) {
    		delta = - rampIncrement;
    	}
    	return (currentSpeed + delta);
    }
	 */
	public double getStopDelay() {
		return(stopDelay);
	}
	public void addTelemetryHeaders() {
		Robot.currentMonitor.registerMonitorDevive(winchTalon, WINCHMOTOR);
	}

	public void writeTelemetyValues() {
	}
	public void loadConfig(Config config) {
		//driveP = config.getDouble("DriveP", Defaults.DRIVETRAIN_P);
		stopDelay = config.getDouble("WinchStopDelay", Defaults.WINCHDELAYSTOP);
	}

	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new WinchJoy());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
}
