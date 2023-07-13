package Controller;

import java.util.List;

import Enum.PhysicalConditionEnum;
import Model.ActivityModel;
import Model.AthleteWorkoutInformationsModel;
import exceptions.NotEnoughActivitiesException;

public class AthleteWorkoutInformationsController {

    private ActivityController activityController;

    public AthleteWorkoutInformationsController(ActivityController activityController) {
        this.activityController = activityController;
    }

    public AthleteWorkoutInformationsModel getAthleteWorkoutInformations() throws NotEnoughActivitiesException {
        List<ActivityModel> activities = activityController.getAllActivities();
        List<ActivityModel> lastWeekActivities = activityController.getActivitiesFromPastWeeks(activities, 1);
        double lastWeekTotalLoad = activityController.calculateSumOfLoadsOfActivities(lastWeekActivities);
        double lastWeekLoadAverage = calculateLoadAverage(lastWeekTotalLoad, 7);
        List<ActivityModel> pastFourWeeksActivities;
        Double acwr;

        try {
            pastFourWeeksActivities = activityController.getActivitiesFromPastWeeks(activities, 4);
            double chronicLoad = calculateLoadAverage(
                    activityController.calculateSumOfLoadsOfActivities(pastFourWeeksActivities), 7);
            acwr = calculateACWR(lastWeekLoadAverage, chronicLoad);
        } catch (NotEnoughActivitiesException e) {
            acwr = null;
        }

        double monotony = calculateMonotony(lastWeekTotalLoad, lastWeekLoadAverage);
        double constraint = calculateConstraint(lastWeekTotalLoad, monotony);
        double fitness = calculateFitness(lastWeekTotalLoad, constraint);
        return new AthleteWorkoutInformationsModel(lastWeekTotalLoad, monotony, constraint, fitness, acwr);
    }

    public double calculateMonotony(double sumOfLoads, double loadAverage) {
        double monotony = 0d;

        if (sumOfLoads != 0) {
            monotony = sumOfLoads / Math.pow(loadAverage, 2);
        }

        return monotony;
    }

    public double calculateLoadAverage(double sumOfLoads, int numberOfDays) {
        return sumOfLoads / numberOfDays;
    }

    public double calculateConstraint(double sumOfLoads, double monotony) {
        return sumOfLoads / monotony;
    }

    public double calculateFitness(double sumOfLoads, double constraint) {
        return sumOfLoads - constraint;
    }

    public double calculateACWR(double acuteLoad, double chronicLoad) {
        return acuteLoad / chronicLoad;
    }

    public PhysicalConditionEnum getPhysicalCondition(AthleteWorkoutInformationsModel athleteWorkoutInformations) {
        if(
            athleteWorkoutInformations.getMonotony() < 2 && 
            athleteWorkoutInformations.getConstraint() < 6000 && 
            athleteWorkoutInformations.getAwcr() > 0.8 &&
            athleteWorkoutInformations.getAwcr() < 1.3 
        ) {
            return PhysicalConditionEnum.OPTIMAL;
        } else if(
            athleteWorkoutInformations.getMonotony() >= 2 &&
            athleteWorkoutInformations.getMonotony() < 2.5 ||
            athleteWorkoutInformations.getConstraint() >= 6000 &&
            athleteWorkoutInformations.getConstraint() < 10000
        ) {
            return PhysicalConditionEnum.EXHAUSTING;
        } else if(
            athleteWorkoutInformations.getMonotony() >= 2.5 ||
            athleteWorkoutInformations.getConstraint() >= 10000 ||
            athleteWorkoutInformations.getAwcr() > 1.5
        ) {
            return PhysicalConditionEnum.DANGEROUS;
        } else {
            return PhysicalConditionEnum.RAS;
        }
    }
}
