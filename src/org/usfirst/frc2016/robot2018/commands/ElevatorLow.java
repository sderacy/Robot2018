// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2016.robot2018.commands;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2016.robot2018.Robot;

/**
 *
 */
public class ElevatorLow extends Command {
	private int stuckCount = 0;
	private final int STUCKLIMIT = 75; // 1. 5 seconds
	private int lastElevatorPreset;
	
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public ElevatorLow() {
    	
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.gearElevator);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    protected void initialize() {
   	stuckCount = 0;
   	lastElevatorPreset = Robot.gearElevator.currentPreset();
    Robot.gearElevator.goToPreset(Robot.gearElevator.LOW);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	/*
    	 * if in auto, and coming from pre-pickup, 
    	 * check that we didn't get stuck
    	 * If so, change target to prepick up.
    	 * engage the grabber first to prevent the trigger from thinking
    	 * it's ok go grab again.
    	 */
    	if (Robot.oi.cCI.getRawButton(1) &&   // In auto?
    		lastElevatorPreset == Robot.gearElevator.PREPICKUP) { // coming from pre-pickup?
    		//Yes
    		if (stuckCount++ > STUCKLIMIT) {
        		Robot.gearGrabber.gearGrab();
        		Robot.gearElevator.goToPreset(Robot.gearElevator.PREPICKUP);
    		}
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return Robot.gearElevator.isPositioned();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
