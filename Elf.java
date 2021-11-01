import java.util.Random;

public class Elf implements Runnable {

	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR, DONE
	};

	private ElfState state;
	/**
	 * The number associated with the Elf
	 */
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;
	private boolean overworked;
	public boolean hasPermit;

	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
		this.overworked = false;
	}


	public ElfState getState() {
		return state;
	}

	/**
	 * Santa might call this function to fix the trouble
	 * @param state
	 */
	public void setState(ElfState state) {
		this.state = state;
	}

	public void stop() {
		overworked = true;
	}


	@Override
	public void run() {
		while (!overworked) {
      // wait a day
  		try {
  			Thread.sleep(100);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
			switch (state) {
			case WORKING: {
				try {
					if (rand.nextDouble() < 0.01) {
						state = ElfState.TROUBLE;
						scenario.countMutex.acquire();
						scenario.count++;
						scenario.countMutex.release();
						scenario.help.acquire();
						if (overworked) {
							break;
						}
						scenario.inTroubleMutex.acquire();
						scenario.inTrouble.add(this);
						scenario.inTroubleMutex.release();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case TROUBLE:
				state = ElfState.AT_SANTAS_DOOR;
				scenario.readytohelp.release();
				break;
			case AT_SANTAS_DOOR:
				if (scenario.santa.getState() == Santa.SantaState.SLEEPING)
					scenario.santa.setState(Santa.SantaState.WOKEN_UP_BY_ELVES);
				break;
			}
		}
	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}

}
