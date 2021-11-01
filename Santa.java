//import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;


public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER};
	private SantaState state;
	private SantaScenario scenario;
	public boolean overworked;
	public Santa(SantaScenario scenario) {
		this.state = SantaState.SLEEPING;
		this.scenario = scenario;
		this.overworked = false;
	}

	public void setState(SantaState state) {
		this.state = state;
	}

	public void stop() {
		overworked = true;
	}

	public SantaState getState() {
		return this.state;
	}
	
	
	@Override
	public void run() {
		while(!overworked) {
			// wait a day...
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(state) {
			case SLEEPING: // if sleeping, continue to sleep
				break;
			case WOKEN_UP_BY_ELVES:
				try {
					scenario.readytohelp.acquire(3);
					if (overworked)
						break;
					for (int i = 0; i < 3; i++) {
						scenario.inTrouble.get(0).setState(Elf.ElfState.WORKING);
						scenario.inTroubleMutex.acquire();
						scenario.inTrouble.remove(0);
						scenario.inTroubleMutex.release();
					}
					state = SantaState.SLEEPING;
					scenario.hasReleasedPermits = false;
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case WOKEN_UP_BY_REINDEER:
				try {
					scenario.waitingforallreindeers.release(9);
					state = SantaState.READY_FOR_CHRISTMAS;
				} catch (Exception e) {
					e.printStackTrace();
				}
				// FIXME: assemble the reindeer to the sleigh then change state to ready 
				break;
			case READY_FOR_CHRISTMAS: // nothing more to be done
				break;
			}
		}
	}

	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Santa : " + state);
	}
	
	
}
