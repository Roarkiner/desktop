package Model;

public class AthleteWorkoutInformationsModel {
    private double totalLoad;
    private double monotony;
    private double constraint;
    private double fitness;
    private Double awcr;

    public AthleteWorkoutInformationsModel(
            double totalLoad,
            double monotony,
            double constraint,
            double fitness,
            Double awcr) {
        this.totalLoad = totalLoad;
        this.monotony = monotony;
        this.constraint = constraint;
        this.fitness = fitness;
        this.awcr = awcr;
    }

    public double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public double getMonotony() {
        return monotony;
    }

    public void setMonotony(double monotony) {
        this.monotony = monotony;
    }

    public double getConstraint() {
        return constraint;
    }

    public void setConstraint(double constraint) {
        this.constraint = constraint;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Double getAwcr() {
        return awcr;
    }

    public void setAwcr(Double awcr) {
        this.awcr = awcr;
    }
}
