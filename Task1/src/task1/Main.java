package task1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	private List<Outcome> outcomes;
	private Random random;

	public Main(List<Outcome> outcomes) {
		this.outcomes = outcomes;
		random = new Random();
	}

	public String simulateEvent() {
		int totalWeight = 0;

		for (Outcome outcome : outcomes) {
			totalWeight += outcome.weight;
		}

		int randomNumber = random.nextInt(totalWeight);
		int cumulativeWeight = 0;

		for (Outcome outcome : outcomes) {
			cumulativeWeight += outcome.weight;
			if (randomNumber < cumulativeWeight) {
				return outcome.name;
			}
		}

		return null;
	}

	public static void main(String[] args) {
		List<Outcome> outcomes = new ArrayList<>();
		outcomes.add(new Outcome("Head", 35));
		outcomes.add(new Outcome("Tail", 65));

		Main eventSimulator = new Main(outcomes);

		int numSimulations = 1000;
		int headCount = 0;
		int tailCount = 0;

		for (int i = 0; i < numSimulations; i++) {
			String result = eventSimulator.simulateEvent();
			if ("Head".equals(result)) {
				headCount++;
			} else if ("Tail".equals(result)) {
				tailCount++;
			}
		}

		System.out.println("On triggering the event " + numSimulations + " times,");
		System.out.println("Head appeared " + headCount + " times");
		System.out.println("Tail appeared " + tailCount + " times");
	}
}
