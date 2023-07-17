package view;

import javax.swing.*;

import controller.AthleteWorkoutInformationsController;
import enums.NavigationRouteEnum;
import enums.PhysicalConditionEnum;
import exceptions.NotEnoughActivitiesException;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import model.AthleteWorkoutInformationsModel;
import shared.DesktopApplicationContext;
import shared.NavigationListener;

public class WorkoutInfosPanel extends JPanel {

    private JButton activityListButton;
    private JLabel titleLabel;
    private JLabel totalLoadLabel;
    private JLabel monotonyLabel;
    private JLabel constraintLabel;
    private JLabel fitnessLabel;
    private JLabel awcrLabel;
    private JPanel errorPanel;
    private JLabel errorLabel;
    private JPanel physicalConditionPanel;
    private PhysicalConditionEnum currentPhysicalCondition = PhysicalConditionEnum.RAS;

    private String arialFont = "Arial";
    private transient AthleteWorkoutInformationsController athleteWorkoutInformationsController;
    private transient NavigationListener navigationListener;

    public WorkoutInfosPanel() {
        DesktopApplicationContext context = DesktopApplicationContext.getInstance();
        AthleteWorkoutInformationsController contextAthleteWorkoutInformationsController = context
                .getAthleteWorkoutInformationsController();
        this.athleteWorkoutInformationsController = contextAthleteWorkoutInformationsController;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        activityListButton = new JButton("Retour à la liste des activités");
        activityListButton.setFont(new Font(arialFont, Font.PLAIN, 14));
        activityListButton.setFocusPainted(false);
        activityListButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(NavigationRouteEnum.ACTIVITYLIST, null);
            }
        });

        titleLabel = new JLabel("Mon entraînement");
        titleLabel.setFont(new Font(arialFont, Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        totalLoadLabel = new JLabel();
        totalLoadLabel.setFont(new Font(arialFont, Font.PLAIN, 16));
        totalLoadLabel.setHorizontalAlignment(SwingConstants.CENTER);

        monotonyLabel = new JLabel();
        monotonyLabel.setFont(new Font(arialFont, Font.PLAIN, 16));
        monotonyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        constraintLabel = new JLabel();
        constraintLabel.setFont(new Font(arialFont, Font.PLAIN, 16));
        constraintLabel.setHorizontalAlignment(SwingConstants.CENTER);

        fitnessLabel = new JLabel();
        fitnessLabel.setFont(new Font(arialFont, Font.PLAIN, 16));
        fitnessLabel.setHorizontalAlignment(SwingConstants.CENTER);

        awcrLabel = new JLabel();
        awcrLabel.setFont(new Font(arialFont, Font.PLAIN, 16));
        awcrLabel.setHorizontalAlignment(SwingConstants.CENTER);

        physicalConditionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int circleSize = Math.min(50, 50);
                int circleX = (getWidth() - circleSize) / 2;
                int circleY = (getHeight() - circleSize) / 2;
                Ellipse2D circle = new Ellipse2D.Double(circleX, circleY, circleSize, circleSize);

                Color circleColor = getColorForPhysicalCondition(currentPhysicalCondition);
                g2d.setColor(circleColor);
                g2d.fill(circle);
            }
        };
        physicalConditionPanel.setPreferredSize(new Dimension(150, 150));

        errorLabel = new JLabel();
        errorLabel.setFont(new Font(arialFont, Font.BOLD, 16));
        errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        errorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(activityListButton);
        add(titleLabel);
        add(totalLoadLabel);
        add(monotonyLabel);
        add(constraintLabel);
        add(fitnessLabel);
        add(awcrLabel);
        add(physicalConditionPanel);
        add(errorPanel);

        if (contextAthleteWorkoutInformationsController != null) {
            CompletableFuture<AthleteWorkoutInformationsModel> futureAthleteWorkoutInformations = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return athleteWorkoutInformationsController.getAthleteWorkoutInformations();
                        } catch (Exception exception) {
                            throw new CompletionException(exception);
                        }
                    });

            futureAthleteWorkoutInformations.thenAccept(this::updateWorkoutInformation)
                    .exceptionally(exception -> {
                        Throwable cause = exception.getCause();
                        if (cause instanceof NotEnoughActivitiesException) {
                            displayErrorMessage(
                                    "Vous n'avez pas fait assez d'activité pour pouvoir avoir des informations d'entraînement");
                        } else {
                            displayErrorMessage(
                                    "Une erreur s'est produite lors de la récupération des informations d'entraînement");
                        }
                        return null;
                    });
        }
    }

    public void displayErrorMessage(String message) {
        errorLabel.setText(message);

        removeAll();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(activityListButton);
        add(buttonPanel, BorderLayout.NORTH);
        add(errorPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public void updateWorkoutInformation(AthleteWorkoutInformationsModel workoutInformations) {
        totalLoadLabel.setText("Charge totale : " + workoutInformations.getTotalLoad());
        monotonyLabel.setText("Monotonie : " + workoutInformations.getMonotony());
        constraintLabel.setText("Contrainte : " + workoutInformations.getConstraint());
        fitnessLabel.setText("Forme : " + workoutInformations.getFitness());
        if (workoutInformations.getAwcr() == null) {
            awcrLabel.setText("Il faut au minimum un mois d'entrainement pour pouvoir calculer le RCA");
        } else {
            awcrLabel.setText("RCA: " + workoutInformations.getAwcr());
            PhysicalConditionEnum physicalCondition = getPhysicalCondition(workoutInformations);
            currentPhysicalCondition = physicalCondition;
            physicalConditionPanel.repaint();
        }
    }

    private Color getColorForPhysicalCondition(PhysicalConditionEnum physicalCondtion) {
        switch (physicalCondtion) {
            case OPTIMAL:
                return Color.GREEN;
            case EXHAUSTING:
                return Color.ORANGE;
            case DANGEROUS:
                return Color.RED;
            case RAS:
                return Color.GRAY;
            default:
                return Color.WHITE;
        }
    }

    private PhysicalConditionEnum getPhysicalCondition(AthleteWorkoutInformationsModel workoutInformations) {
        return athleteWorkoutInformationsController.getPhysicalCondition(workoutInformations);
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }
}