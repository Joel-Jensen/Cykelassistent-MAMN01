package se.lth.MAMN01.cykel_assistent;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Speedometer {

    public static final int ABOVE_THRESHOLD = 1;
    public static final int BELOW_THRESHOLD = -1;
    public static final int WITHIN_THRESHOLD = 0;
    public static final int STANDING_STILL_THRESHOLD = 3;
    private List<Double> samples;
    private int NUMBER_OF_SAMPLES = 10;
    private int lowestLimit = 0;
    private int highestLimit = 0;

    public Speedometer(int lowestLimit, int highestLimit) {
        this.lowestLimit = lowestLimit;
        this.highestLimit = highestLimit;
        samples = new LinkedList<>();
    }

    public int onSpeedUpdate(double currentSpeed){
        if(currentSpeed > STANDING_STILL_THRESHOLD){
            samples.add(currentSpeed);
            removeOldSample();
        }
        return currentSpeedStatus();
    }

    public void setLowestLimit(int lowestLimit) {
        this.lowestLimit = lowestLimit;
    }

    public void setHighestLimit(int highestLimit) {
        this.highestLimit = highestLimit;
    }

    private int currentSpeedStatus() {
        double averageSpeed = samples.stream().mapToDouble(x -> x).sum() / NUMBER_OF_SAMPLES;

        if(samples.size() < NUMBER_OF_SAMPLES) {
            return WITHIN_THRESHOLD;
        }
        samples = new LinkedList<>();
        if (averageSpeed > highestLimit) {
            return ABOVE_THRESHOLD;
        }
        if (averageSpeed > lowestLimit) {
            return BELOW_THRESHOLD;
        }
        return WITHIN_THRESHOLD;
    }

    private void removeOldSample() {
        if(samples.size() > NUMBER_OF_SAMPLES) {
            samples.remove(0);
        }
    }
}
