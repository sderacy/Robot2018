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
import edu.wpi.first.wpilibj.Timer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.usfirst.frc2016.robot2018.Robot;

/**
 *
 */
public class DriveUsingFile extends Command {
	private String sp_FileName= "centermove.csv";    /** Default: centermove.csv */
	private String sp_FilePath= "/c";				/** Default: /c folder. */
	private final String csvSplitBy = ",";
	
	public Double SecondsPerExecute = .02;
	
	public Double MaxVel = 20.0;
	public Double MaxAccel = 10.0;
	public Double MaxDistance = 150.0;
	public Timer elapsedTime;
	
	private ArrayList<MoveSegment> moveList;
	private Integer moveListIndex;
	private Integer executeCount;
	private DrivePosition posLeft;
	private DrivePosition posRight;

	private Boolean failed = false;
	private Boolean finished = false;
	
	private enum AutoCmd { NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh, Stop }
	
	private class DrivePosition {
		private DriveUsingFile parent;  // DriveUsingFile object using this one
		private Double offsetDistance;  // all moves are relative to the starting offset
		private String name;			// display name for tracing
		private Double distance; 		// absolute distance from startDistance
		private Double velocity;		// current move velocity
		private Double accel;			// current move acceleration
		private Double targetAccel;     // acceleration to use for the current move
		private Double targetVelocity;  // max velocity to use for the current move
		private Double targetDistance;  // end point of the move
		private Double startDistance;   // current distance at the start of the move (setOffsetDistance)
		private Double moveLength;		// absolute length of current move
		private Boolean isMoving;		// true when actively moving
		private Boolean isForward;		// true when moving in a positive direction, flag simplifies the profile logic
		
		public DrivePosition(DriveUsingFile parent, String name) {
			this.parent = parent;
			this.name = name;
		}
				
		public void setOffsetDistance(double dist) {
			offsetDistance = dist;
			endMove(0.0);
		}
		
		public void endMove(double dist) {
			startDistance = dist;
			targetDistance = dist;
			distance = 0.0;
			velocity = 0.0;
			accel = 0.0;
			moveLength = 0.0;
			isMoving = false;
			isForward = false;
			targetVelocity = parent.MaxVel;
			targetAccel = parent.MaxAccel;			
		}
		
		
		public void setTarget(double newAccel, double newMaxVel, double newDist)
		{
			// default or limit accel
			newAccel = Math.abs(newAccel);
			if (newAccel == 0 || newAccel > parent.MaxAccel) 
				newAccel = parent.MaxAccel;
			targetAccel = newAccel;
		
			// default or limit vel
			newMaxVel = Math.abs(newMaxVel);
			if (newMaxVel==0 || newMaxVel > parent.MaxVel )
				newMaxVel = parent.MaxVel;
			
			targetVelocity = newMaxVel;

			// start distance the current location without the base offset
			startDistance = getOffsetDistance() - offsetDistance;
			targetDistance = newDist;
			
			
			boolean newIsFoward = targetDistance > startDistance;
			if (isForward != newIsFoward)
			{
				// new direction, change sign of current velocity
				velocity = -velocity;
			}
			
			// length and direction of the move
			isForward = newIsFoward;
			moveLength = Math.abs(targetDistance - startDistance);
			
			double dt = parent.SecondsPerExecute;
			if (Math.abs(velocity) < targetAccel * dt && moveLength < targetAccel * dt * dt) {
				// small move, just go there
				endMove(newDist);
			}
			else
			{
				isMoving = true;
				if (velocity > targetVelocity) {
					// need to slow down to the new max
					accel = -targetAccel;
				}
				if (velocity < targetVelocity) {
					// need to speed up to new max
					accel = targetAccel;
				}
			}
		}
		
		// get the distance including the starting offset
		public double getOffsetDistance() {
			if (!isMoving) {
				// not moving, return target
				return offsetDistance + targetDistance;
			}
			
			if (isForward) {
				// moving forward, return start + distance moved
				return offsetDistance + startDistance + distance;
			}
			
			// moving backward, return start - distance moved
			return offsetDistance + startDistance - distance;
		}
		
		public double updatePosition() {
			if (!isMoving)
			{
				return getOffsetDistance();
			}
			double dt = parent.SecondsPerExecute;
			
			// calculate next position based on current velocity and acceleration
			
			Double newAccel = accel;
			Double newVelocity = velocity + newAccel * dt;
			
			if (newAccel > 0 && newVelocity >= targetVelocity) {
				// accelerated to max positive velocity, limit
				newVelocity = targetVelocity;
				newAccel = 0.0;
			} 
			
			if (newAccel < 0 && newVelocity <= targetVelocity) {
				// de-accelerated to max positive velocity, limit
				newVelocity = targetVelocity;
				newAccel = 0.0;
			}
			
			Double newDistance = distance + newVelocity * dt + 0.5 * newAccel * dt * dt;
			if (newDistance > parent.MaxDistance) {
				// safety check out of bounds
				System.out.println(name + ": distance (" + newDistance + ") exceeded MaxDistance (" + parent.MaxDistance + ")");
				endMove(distance);
				parent.shutdown();
				return getOffsetDistance();
			}
			
			if (newDistance >= targetDistance) {
				// stop when the target point is reached
				endMove(targetDistance);
				System.out.println(name + ": at targetDistance (" + targetDistance + ")");
				return getOffsetDistance();
			}
			
			// calculate where to start slowing down
			Double stopDt = dt * Math.round(Math.abs(newVelocity)/(targetAccel*dt));
			Double brakeAt = targetDistance - 0.5 * targetAccel * stopDt * stopDt;
			if (newDistance >= brakeAt)
			{
				// begin to slow down to stop at the target
				newAccel = -targetAccel;
				System.out.println(name + ".Plus: braking (" + brakeAt + ") for " + stopDt + " secs");
			}
			
			System.out.println(name + ": a=" + accel + " v=" + newVelocity + " d=" + newDistance);
			
			accel = newAccel;
			distance = newDistance;
			velocity = newVelocity;
			return getOffsetDistance();
		}
	}
	

	public class MoveSegment {
		public int lineNumber;
		public double time;
		public AutoCmd command;
		public double targetAccel;
		public double targetVelocity;
		public double targetDistance;
		
		public MoveSegment(int line, double t, AutoCmd cmd, double a, double v, double d)
		{
			lineNumber = line;
			time = t; 
			command = cmd;
			targetAccel = a;
			targetVelocity = v;
			targetDistance = d;
		}
		
		public MoveSegment(int line, double t, AutoCmd cmd)
		{
			lineNumber = line;
			time = t; 
			command = cmd;
		}
	}

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public DriveUsingFile() {

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES


		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		posLeft = new DrivePosition(this, "Left");
		posRight = new DrivePosition(this, "Right");
		moveList = new ArrayList<MoveSegment>();
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("MoveUsingFile(): INFO: Loading points from file [" + this.sp_FileName + "]");

		try{
			moveList = new ArrayList<MoveSegment>();
			String str;
			Integer line = 0;
			BufferedReader in = new BufferedReader(new FileReader(this.sp_FilePath+"/"+this.sp_FileName));
			
			while ((str = in.readLine()) != null) {
				line ++;
				str = str.trim();
				if(str.length() == 0 || str.charAt(0) == '#')
					continue;
				
				String[] fields = str.split(csvSplitBy);

				if (fields.length < 2){
					System.out.println("DriveUsingFile: ****ERROR Line " + line.toString() + " is not a comment (#) but does not have a pipe ("+csvSplitBy+") delimiter!");
					failed=true;
				}
				
				double time = Double.parseDouble(fields[0].trim());
				
				String cmdName = fields[1].trim(); 
				AutoCmd cmd = AutoCmd.NotFound;
				for(AutoCmd cmdFind : AutoCmd.values())	{
					if (cmdFind.name().equals(cmdName)) {
						cmd = cmdFind;
						break;
					}
				}
				
				if(cmd==AutoCmd.NotFound)
				{
					System.out.println("DriveUsingFile: ****ERROR Line " + line.toString() + " command '" + fields[1] + "' not found");
					failed=true;					
				}
				
				// { NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh, Stop }
				switch(cmd)
				{
				case Left:
				case Right:
					if (fields.length < 5) {
						System.out.println("DriveUsingFile: ****ERROR Line " + line.toString() + " Left/Right is missing accel,targetVel,targetPos fields!");
						failed=true;
					}
					double a = Double.parseDouble(fields[2].trim());
					double v = Double.parseDouble(fields[3].trim());
					double d = Double.parseDouble(fields[4].trim());
					moveList.add(new MoveSegment(line, time, cmd, a, v, d));
					break;
					
				default:
					moveList.add(new MoveSegment(line, time, cmd));
					break;
				}

			}
			in.close();
			
		} catch (IOException e) {
			System.out.println("DriveUsingFile(): ****ERROR: Failed to load the file " + this.sp_FileName + 
					"   Exception:" + e + "  Reason:" + e.getMessage() );
			failed = true;
		}


		setStartDistance();
		executeCount = 0;
		moveListIndex = 0;
		elapsedTime = new Timer();
		elapsedTime.start();
		
		processCommands();
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		executeCount++;
		processCommands();
		updateDriveTrain();
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return (failed || finished);
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		shutdown();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		shutdown();
	}
	
	private void shutdown()
	{
		finished = true;
		failed = true;
		Robot.cubePickup.autoEnd();
		Robot.driveTrainSRX.driveStop();
	}
	
	private void setStartDistance()
	{
		posLeft.setOffsetDistance(Robot.driveTrainSRX.getLeftDistance());
		posRight.setOffsetDistance(Robot.driveTrainSRX.getRightDistance());
	}
	
	private void updateDriveTrain()
	{
		Robot.driveTrainSRX.pingDifferentialDrive();
		
		double leftDistance = posLeft.updatePosition();
		double rightDistance = posRight.updatePosition();
		if (!failed)
		{
			if (Robot.gameData == 'R') {
				Robot.driveTrainSRX.goToDistance(rightDistance, leftDistance);
			}
			else {
				Robot.driveTrainSRX.goToDistance(leftDistance, rightDistance);
			}
		}
	}

	private void processCommands() {
		Double tickNow = executeCount * SecondsPerExecute;
		Double timerNow = elapsedTime.get();
		System.out.println("processCommands: tick time=" + tickNow.toString() + " timer= " + timerNow.toString());
		
		while(moveListIndex < moveList.size()) {
			MoveSegment moveSeg = moveList.get(moveListIndex);
			if (moveSeg.time > tickNow) {
				break;
			}
			moveListIndex++;
			
			System.out.println("processCommands: Executing Line " + moveSeg.lineNumber + ": " + moveSeg.command.name());
			// NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh
			switch(moveSeg.command)
			{
			default:
				System.out.println("processCommands: " + moveSeg.command.name() + " not handled");
				break;
				
			case Left:
				posLeft.setTarget(moveSeg.targetAccel, moveSeg.targetVelocity, moveSeg.targetDistance);
				break;
				
			case Right:
				posRight.setTarget(moveSeg.targetAccel, moveSeg.targetVelocity, moveSeg.targetDistance);
				break;	
				
			case ResetPos:
				setStartDistance();
				break;
				
			case Close:
				Robot.cubePickup.closeArms();
				break;
				
			case Open:
				Robot.cubePickup.openArms();
				break;
				
			case PullIn:
				Robot.cubePickup.acquireCube();
				break;
				
			case Eject:
				Robot.cubePickup.autoEjectCube();
				break;
				
			case StopIntake:
				Robot.cubePickup.autoEnd();
				break;
				
			case ArmFloor:
				Robot.arm.goToPreset(Robot.arm.FLOOR);
				break;
				
			case ArmHigh:
				Robot.arm.goToPreset(Robot.arm.HIGH);
				break;
				
			case Stop:
				moveListIndex = moveList.size();
				break;
			}
			
		}
		finished = moveListIndex >= moveList.size();
	}
}
