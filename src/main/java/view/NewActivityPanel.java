package view;

import javax.swing.*;

import org.bson.types.ObjectId;

import java.awt.*;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.toedter.calendar.JDateChooser;

import controller.ActivityController;
import enums.NavigationRouteEnum;
import exceptions.ActivityValidationException;
import jakarta.validation.ConstraintViolation;
import model.ActivityModel;
import shared.DesktopApplicationContext;
import shared.NavigationListener;

public class NewActivityPanel extends JPanel {
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel durationLabel;
    private JFormattedTextField durationField;
    private JLabel dateLabel;
    private JDateChooser dateChooser;
    private JLabel sliderLabel;
    private JSlider slider;
    private JButton createButton;

    private transient NavigationListener navigationListener;
    private String arialFont = "Arial";

    public NewActivityPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Back button
        backButton = new JButton("<html>&larr; Retour à la liste des activités</html>");
        backButton.setFont(new Font(arialFont, Font.PLAIN, 22));
        backButton.setFocusPainted(false);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(backButton, gbc);
        gbc.gridy++;
        backButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(NavigationRouteEnum.ACTIVITYLIST);
            }
        });

        // Title
        titleLabel = new JLabel("Ajouter une activité");
        titleLabel.setFont(new Font(arialFont, Font.BOLD, 40));
        add(titleLabel, gbc);
        gbc.gridy++;

        // Name
        nameLabel = new JLabel("Nom:");
        nameField = new JTextField(20);
        nameLabel.setFont(new Font(arialFont, Font.BOLD, 22));
        nameField.setFont(new Font(arialFont, Font.PLAIN, 22));
        add(nameLabel, gbc);
        gbc.gridy++;
        add(nameField, gbc);
        gbc.gridy++;

        // Duration
        durationLabel = new JLabel("Durée (en minutes):");
        durationField = new JFormattedTextField();
        durationField.setColumns(10);
        durationField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        durationLabel.setFont(new Font(arialFont, Font.BOLD, 22));
        durationField.setFont(new Font(arialFont, Font.PLAIN, 22));
        add(durationLabel, gbc);
        gbc.gridy++;
        add(durationField, gbc);
        gbc.gridy++;

        // Date
        dateLabel = new JLabel("Date:");
        dateChooser = new JDateChooser();
        dateLabel.setFont(new Font(arialFont, Font.BOLD, 22));
        dateChooser.setFont(new Font(arialFont, Font.PLAIN, 22));
        add(dateLabel, gbc);
        gbc.gridy++;
        add(dateChooser, gbc);
        gbc.gridy++;

        // Slider
        sliderLabel = new JLabel("RPE:");
        slider = new JSlider(0, 20, 0);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setLabelTable(createSliderLabels());
        sliderLabel.setFont(new Font(arialFont, Font.BOLD, 22));
        slider.setFont(new Font(arialFont, Font.PLAIN, 22));
        add(sliderLabel, gbc);
        gbc.gridy++;
        add(slider, gbc);
        gbc.gridy++;

        // Create button
        createButton = new JButton("Créer");
        createButton.setFocusPainted(false);
        createButton.setFont(new Font(arialFont, Font.BOLD, 22));
        gbc.anchor = GridBagConstraints.CENTER;
        add(createButton, gbc);

        createButton.addActionListener(e -> {
            String name = nameField.getText();
            Double duration = "".equals(durationField.getText()) ? null : Double.parseDouble(durationField.getText());
            java.util.Date date = dateChooser.getDate();
            Double rpe = slider.getValue() / 2.0;

            DesktopApplicationContext context = DesktopApplicationContext.getInstance();
            ActivityModel activity = new ActivityModel(name, duration, date, rpe);
            ActivityController activityController = context.getActivityController();
            CompletableFuture<ObjectId> futureActivityId = CompletableFuture.supplyAsync(() -> {
                try {
                    return activityController.saveActivity(activity);
                } catch (ActivityValidationException exception) {
                    throw new CompletionException(exception);
                }
            });
            futureActivityId.thenAccept(activityId -> {
                navigationListener.navigateTo(NavigationRouteEnum.ACTIVITYLIST);
            }).exceptionally(exception -> {
                Throwable cause = exception.getCause();
                if (cause instanceof ActivityValidationException) {
                    Set<ConstraintViolation<ActivityModel>> validationErrors = ((ActivityValidationException) cause)
                            .getValidationErrors();
                    for (ConstraintViolation<ActivityModel> constraintViolation : validationErrors) {
                        showErrorDialog(constraintViolation.getMessage());
                    }
                } else {
                    showErrorDialog("Une erreur s'est produite lors de l'ajout de l'activité.");
                }
                return null;
            });
        });
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private Hashtable<Integer, JLabel> createSliderLabels() {
        Hashtable<Integer, JLabel> labels = new Hashtable<>();

        labels.put(0, new JLabel("0"));
        labels.put(4, new JLabel("2"));
        labels.put(8, new JLabel("4"));
        labels.put(12, new JLabel("6"));
        labels.put(16, new JLabel("8"));
        labels.put(20, new JLabel("10"));

        return labels;
    }
}