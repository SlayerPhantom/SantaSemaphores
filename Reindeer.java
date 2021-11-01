import java.util.Random;


public class Reindeer implements Runnable {

	public enum ReindeerState {AT_BEACH, AT_WARMING_SHED, AT_THE_SLEIGH};
	private ReindeerState state;
	private SantaScenario scenario;
	private Random rand = new Random();
	private boolean overworked;

	/**
	 * The number associated with the reindeer
	 */
	private int number;
	
	public Reindeer(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ReindeerState.AT_BEACH;
		this.overworked = false;
	}

	public void stop() {
		overworked = true;
	}

	@Override
	public void run() {
		while(!overworked) {
		// wait a day
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// see what we need to do:
		switch(state) {
		case AT_BEACH: { // if it is December, the reindeer might think about returning from the beach
			if (scenario.isDecember) {
				try {
					if (rand.nextDouble() < 0.1) {
						scenario.reindeerMutex.acquire();
						scenario.reindeerHomeCount++;
						scenario.reindeerMutex.release();
						state = ReindeerState.AT_WARMING_SHED;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;			
		}
		case AT_WARMING_SHED:
			try{
				if (scenario.reindeerHomeCount == 9) {
					scenario.santa.setState(Santa.SantaState.WOKEN_UP_BY_REINDEER);
				}
				scenario.waitingforallreindeers.acquire();
				if (overworked)
					break;
				state = ReindeerState.AT_THE_SLEIGH;
			} catch(Exception e) {
				e.printStackTrace();
			}
			// if all the reindeer are home, wake up santa
			break;
		case AT_THE_SLEIGH: 
			// keep pulling
			break;
		}
		}
	};
	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Reindeer " + number + " : " + state);
	}
	
}
