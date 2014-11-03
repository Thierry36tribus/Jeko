package models;

public class MovingAverageResult {

	public double movingAverageConsumption;
	public double movingAverageRef;
	public double saving;

	public MovingAverageResult(final double movingAverageConsumption, final double movingAverageRef, final double saving) {
		super();
		this.movingAverageConsumption = movingAverageConsumption;
		this.movingAverageRef = movingAverageRef;
		this.saving = saving;
	}

	@Override
	public String toString() {
		return "MovingAverageResult [movingAverageConsumption=" + movingAverageConsumption + ", movingAverageRef="
				+ movingAverageRef + ", saving=" + saving + "]";
	}

}
