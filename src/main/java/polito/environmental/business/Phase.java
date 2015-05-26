package polito.environmental.business;

public class Phase {
	
	private String phaseName;
	
	private boolean[] valveStatus;

	public Phase(String phaseName, boolean ... valveStatus) {
		super();
		this.phaseName = phaseName;
		this.valveStatus = valveStatus;
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
