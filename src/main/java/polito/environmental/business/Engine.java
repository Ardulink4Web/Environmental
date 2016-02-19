package polito.environmental.business;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import polito.environmental.GUI;

public class Engine implements Runnable {
	
	private List<Phase> phases;
	
	private ActionListener listener;

	private GUI logger;
    
	private Connection connection;
	
	private boolean invertPin;
	
	public Engine(GUI logger, ActionListener listener, Connection connection, Phase ... phases) {
		this.connection = connection;
		this.phases = Arrays.asList(phases);
		this.logger = logger;
        this.listener = listener;
	}

	public void run() {

		Phase currentPhase = null;
		
		try {

			for ( Phase phase : phases ) {
				
				currentPhase = phase;
				
				this.logger.appendStatus("Go to phase: " + phase.getPhaseName());

				connection.setPhase(invertPin, phase);
				
				this.logger.appendStatus("Waiting " + phase.getDuration() + " seconds");
				
				Thread.sleep(1000 * phase.getDuration());

			}

		} catch (InterruptedException e) {
			if ( currentPhase != null ) {
				this.logger.appendStatus("Process was interrupted during phase: " + currentPhase);
			}
			this.logger.appendStatus("Process was interrupted before start");
		}
		this.listener.actionPerformed(null);
		
	}
	
	public boolean isInvertPin() {
        return this.invertPin;
    }

    public void setInvertPin(boolean invertPin) {
        this.invertPin = invertPin;
    }

}
