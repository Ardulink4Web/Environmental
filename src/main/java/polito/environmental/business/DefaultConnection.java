package polito.environmental.business;

import org.zu.ardulink.Link;

public class DefaultConnection implements Connection {
	
	private int[] pins;

	public DefaultConnection(int[] pins) {
		this.pins = pins;
	}

	public void setPhase(boolean invertPin, Phase phase) {
		for (int i = 0; i < phase.getValveStatus().length; ++i) {
			if (invertPin) {
				if (phase.getValveStatus()[i]) {
					Link.getDefaultInstance().sendPowerPinSwitch(this.pins[i], 1);
					continue;
				}
				Link.getDefaultInstance().sendPowerPinSwitch(this.pins[i], 0);
				continue;
			}
			if (phase.getValveStatus()[i]) {
				Link.getDefaultInstance().sendPowerPinSwitch(this.pins[i], 0);
				continue;
			}
			Link.getDefaultInstance().sendPowerPinSwitch(this.pins[i], 1);
		}
	}
}