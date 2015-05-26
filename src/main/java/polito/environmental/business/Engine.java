package polito.environmental.business;

import java.util.Arrays;
import java.util.List;

public class Engine implements Runnable {
	
	private List<Phase> phases;
	
	private long pauseBetweenPhases;
	
	private Connection connection;
	
	public Engine(Connection connection, long pauseBetweenPhases, Phase ... phases) {
		this.connection = connection;
		this.phases = Arrays.asList(phases);
		this.pauseBetweenPhases = pauseBetweenPhases;
	}

	public void run() {

		Phase currentPhase = null;
		
		try {

			for ( Phase phase : phases ) {
				
				currentPhase = phase;
				
				System.out.println("Go to phase: " + phase.getPhaseName());

				connection.setPhase(phase);

				System.out.println("Waiting " + pauseBetweenPhases + " seconds");

				Thread.sleep(1000 * pauseBetweenPhases);

			}

		} catch (InterruptedException e) {
			if ( currentPhase != null ) {
				System.out.println("Process was interrupted during phase: " + currentPhase);
			}
			else {
				System.out.println("Process was interrupted before start");
			}
		}
		
	}

}
