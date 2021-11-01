import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SantaScenario {

	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	public boolean isDecember;

	public List<Elf> inTrouble;
	public List<Elf> atDoor;
	public Semaphore help;
	public Semaphore readytohelp;
	public Semaphore countMutex;
	public Semaphore inTroubleMutex;
	public Semaphore waitingforallreindeers;
	public Semaphore reindeerMutex;
	public int count;
	public int reindeerHomeCount;
	public boolean readyforhelp;
	public boolean hasReleasedPermits;
	
	public static void headingToSanta(SantaScenario scenario) {
		for (int i = 0; i < 3; i++) {
			scenario.inTrouble.get(i).setState(Elf.ElfState.AT_SANTAS_DOOR);
		}
		scenario.atDoor.addAll(scenario.inTrouble);
	}

	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		scenario.count = 0;
		scenario.reindeerHomeCount = 0;
		scenario.isDecember = false;
		// create the participants
		// Santa
		scenario.santa = new Santa(scenario);
		Thread th = new Thread(scenario.santa);
		th.start();
		// The elves: in this case: 10
		scenario.elves = new ArrayList<>();
		scenario.inTrouble = new ArrayList<>();
		scenario.atDoor = new ArrayList<>();
		scenario.help = new Semaphore(0, true);
		scenario.readytohelp = new Semaphore(0, true);
		scenario.countMutex = new Semaphore(1, true);
		scenario.inTroubleMutex = new Semaphore(1, true);
		scenario.waitingforallreindeers = new Semaphore(0, true);
		scenario.reindeerMutex = new Semaphore(1, true);
		scenario.hasReleasedPermits = false;
		for(int i = 0; i != 10; i++) {
			Elf elf = new Elf(i+1, scenario);
			scenario.elves.add(elf);
			th = new Thread(elf);
			th.start();
		}
		// The reindeer: in this case: 9
		scenario.reindeers = new ArrayList<>();
		for(int i=0; i != 9; i++) {
			Reindeer reindeer = new Reindeer(i+1, scenario);
			scenario.reindeers.add(reindeer);
			th = new Thread(reindeer);
			th.start();
		}

		// now, start the passing of time
		for(int day = 1; day < 500; day++) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}

			if (day == 370) {
				for (Elf elf : scenario.elves)
					elf.stop();
				for(Reindeer reindeer : scenario.reindeers)
					reindeer.stop();
				scenario.santa.stop();
			}
			
			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			scenario.santa.report();
			for(Elf elf: scenario.elves) {
				elf.report();
			}
			for(Reindeer reindeer: scenario.reindeers) {
				reindeer.report();
			}

			if (scenario.count >= 3 && !scenario.hasReleasedPermits) {
				try {
					scenario.help.release(3);
					scenario.hasReleasedPermits = true;
					scenario.countMutex.acquire();
					scenario.count -=3;
					scenario.countMutex.release();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			if (day >= 370 && scenario.count > 0) {
				try {
					scenario.help.release(10);
					scenario.readytohelp.release(3);
					scenario.count = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (day >= 370 && scenario.reindeerHomeCount < 9) {
				try {
					scenario.waitingforallreindeers.release(9);
					scenario.reindeerHomeCount = 10;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}
