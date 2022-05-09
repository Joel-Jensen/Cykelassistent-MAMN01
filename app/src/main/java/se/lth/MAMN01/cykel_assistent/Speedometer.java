package se.lth.MAMN01.cykel_assistent;

import java.util.LinkedList;
import java.util.List;

public class Speedometer {

    public static final int ABOVE_THRESHOLD = 1;
    public static final int BELOW_THRESHOLD = -1;
    public static final int WITHIN_THRESHOLD = 0;
    private List<Double> samples;
    private int SAMPLES_PER_SECONDS = 1;
    private int NUMBER_OF_SAMPLES = 10;
    private double lowestLimit;
    private double highestLimit;

    public Speedometer(double lowestLimit, double highestLimit) {
        samples = new LinkedList<>();
    }

    public int onSpeedUpdate(double currentSpeed){
        samples.add(currentSpeed);
        removeOldSample();
        return currentSpeedStatus();
    }

    public void setLowestLimit(double lowestLimit) {
        this.lowestLimit = lowestLimit;
    }

    public void setHighestLimit(double highestLimit) {
        this.highestLimit = highestLimit;
    }

    private int currentSpeedStatus() {
        double averageSpeed = samples.stream().mapToDouble(x -> x).sum();
        if(samples.size() == NUMBER_OF_SAMPLES && averageSpeed / NUMBER_OF_SAMPLES > 2) {
            if (averageSpeed / NUMBER_OF_SAMPLES > highestLimit) {
                samples = new LinkedList<>();
                return ABOVE_THRESHOLD;
            } else if (averageSpeed / NUMBER_OF_SAMPLES > lowestLimit) {
                samples = new LinkedList<>();
                return BELOW_THRESHOLD;
            }
        }
        return WITHIN_THRESHOLD;
    }

    private void removeOldSample() {
        if(samples.size() > NUMBER_OF_SAMPLES) {
            samples.remove(0);
        }
    }
}
