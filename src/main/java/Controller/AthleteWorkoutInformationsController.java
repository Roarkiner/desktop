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
        List<List<ActivityModel>> lastWeekActivities = activityController.getActivitiesFromPastWeeksPerDay(activities,
                1);
        double lastWeekTotalLoad = activityController.calculateSumOfLoadsOfActivities(lastWeekActivities);
        int numberOfDays = 7;
        double lastWeekLoadAverage = calculateLoadAverage(lastWeekTotalLoad, numberOfDays);
        List<List<ActivityModel>> pastFourWeeksActivities;
        Double acwr;

        try {
            pastFourWeeksActivities = activityController.getActivitiesFromPastWeeksPerDay(activities, 4);
            double chronicLoad = calculateLoadAverage(
                    activityController.calculateSumOfLoadsOfActivities(pastFourWeeksActivities), 28);
            acwr = calculateACWR(lastWeekLoadAverage, chronicLoad);
        } catch (NotEnoughActivitiesException e) {
            acwr = null;
        }

        double monotony = calculateMonotony(lastWeekActivities, lastWeekLoadAverage, numberOfDays);
        double constraint = calculateConstraint(lastWeekTotalLoad, monotony);
        double fitness = calculateFitness(lastWeekTotalLoad, constraint);
        return new AthleteWorkoutInformationsModel(
                Math.round(lastWeekTotalLoad * 10.0) / 10.0,
                Math.round(monotony * 100.0) / 100.0,
                Math.round(constraint * 10.0) / 10.0,
                Math.round(fitness * 10.0) / 10.0,
                acwr == null ? null : Math.round(acwr * 100.0) / 100.0);
    }

    public double calculateMonotony(List<List<ActivityModel>> activitiesPerDays, double loadAverage, int numberOfDays) {
        double standardDeviationDividend = 0.0;
        for (List<ActivityModel> activitiesPerDay : activitiesPerDays) {
            double sumOfLoadsPerDay = 0.0;
            for (ActivityModel activity : activitiesPerDay) {
                sumOfLoadsPerDay += activity.getLoad();
            }
            standardDeviationDividend += Math.pow(sumOfLoadsPerDay - loadAverage, 2);
        }
        for (int i = 0; i < numberOfDays - activitiesPerDays.size(); i++) {
            standardDeviationDividend += Math.pow(0 - loadAverage, 2);
        }

        double standardDeviation = Math.sqrt(standardDeviationDividend / (numberOfDays - 1));

        return loadAverage / standardDeviation;
    }

    public double calculateLoadAverage(double sumOfLoads, int numberOfDays) {
        return sumOfLoads / numberOfDays;
    }

    public double calculateConstraint(double sumOfLoads, double monotony) {
        return sumOfLoads * monotony;
    }

    public double calculateFitness(double sumOfLoads, double constraint) {
        return sumOfLoads - constraint;
    }

    public double calculateACWR(double acuteLoad, double chronicLoad) {
        return acuteLoad / chronicLoad;
    }

    public PhysicalConditionEnum getPhysicalCondition(AthleteWorkoutInformationsModel athleteWorkoutInformations) {
        if (athleteWorkoutInformations.getMonotony() < 2 &&
                athleteWorkoutInformations.getConstraint() < 6000 &&
                athleteWorkoutInformations.getAwcr() > 0.8 &&
                athleteWorkoutInformations.getAwcr() < 1.3) {
            return PhysicalConditionEnum.OPTIMAL;
        } else if (athleteWorkoutInformations.getMonotony() >= 2.5 ||
                athleteWorkoutInformations.getConstraint() >= 10000 ||
                athleteWorkoutInformations.getAwcr() > 1.5) {
            return PhysicalConditionEnum.DANGEROUS;
        } else if (athleteWorkoutInformations.getMonotony() >= 2 &&
                athleteWorkoutInformations.getMonotony() < 2.5 ||
                athleteWorkoutInformations.getConstraint() >= 6000 &&
                        athleteWorkoutInformations.getConstraint() < 10000) {
            return PhysicalConditionEnum.EXHAUSTING;
        } else {
            return PhysicalConditionEnum.RAS;
        }
    }
}
