package uk.co.ticklethepanda.fitbit;

public class Profiler {

    private static long lastTime = System.currentTimeMillis();

    public static void printTimeElapsed(String message) {
	long timeElapsed = System.currentTimeMillis() - lastTime;
	System.out
		.println("Time elapsed was " + timeElapsed + "ms while " + message);
	lastTime = System.currentTimeMillis();
    }

}
