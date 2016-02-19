package polito.environmental.business;

public class Phase {
	
	private String phaseName;
	
	private boolean[] valveStatus;
	
	private long duration;

	public Phase(String phaseName, long duration, boolean ... valveStatus) {
		super();
		this.phaseName = phaseName;
		this.valveStatus = valveStatus;
		this.duration = duration;
	}
	
	public long getDuration() {
		return duration;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public boolean[] getValveStatus() {
		return valveStatus;
	}

	public void setValveStatus(boolean[] valveStatus) {
		this.valveStatus = valveStatus;
	}

}
