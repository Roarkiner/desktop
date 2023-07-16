package view;

import javax.swing.*;

import org.bson.types.ObjectId;
import shared.NavigationListener;

import com.toedter.calendar.JDateChooser;

import controller.AthleteController;
import enums.NavigationRouteEnum;
import enums.SexEnum;
import exceptions.AthleteValidationException;
import jakarta.validation.ConstraintViolation;
import model.AthleteModel;
import shared.DesktopApplicationContext;

import java.awt.*;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class RegisterPanel extends JPanel {
    private JLabel firstnameLabel;
    private JTextField firstnameField;

    private JLabel lastnameLabel;
    private JTextField lastnameField;

    private JLabel birthdateLabel;
    private JDateChooser birthdateChooser;

    private JLabel sexLabel;
    private ImageIcon maleIcon;
    private ImageIcon femaleIcon;
    private ImageIcon undefinedIcon;
    private JButton maleButton;
    private JButton femaleButton;
    private JButton undefinedButton;

    private JButton registerButton;

    private SexEnum sex;

    private transient NavigationListener navigationListener;

    public void setNavigationListener(NavigationListener listener) {
        this.navigationListener = listener;
    }

    public RegisterPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Firstname
        firstnameLabel = new JLabel("Firstname:");
        firstnameField = new JTextField(20);
        add(firstnameLabel, gbc);
        gbc.gridy++;
        add(firstnameField, gbc);
        gbc.gridy++;

        // Lastname
        lastnameLabel = new JLabel("Lastname:");
        lastnameField = new JTextField(20);
        add(lastnameLabel, gbc);
        gbc.gridy++;
        add(lastnameField, gbc);
        gbc.gridy++;

        // Birthdate
        birthdateLabel = new JLabel("Birthdate:");
        birthdateChooser = new JDateChooser(); // Create a new JDateChooser instance
        add(birthdateLabel, gbc);
        gbc.gridy++;
        add(birthdateChooser, gbc);
        gbc.gridy++;

        // Sex
        sexLabel = new JLabel("Sex:");
        maleIcon = new ImageIcon(getClass().getResource("/img/gender-male.png"));
        femaleIcon = new ImageIcon(getClass().getResource("/img/gender-female.png"));
        undefinedIcon = new ImageIcon(getClass().getResource("/img/gender-ambiguous.png"));
        maleButton = new JButton(maleIcon);
        femaleButton = new JButton(femaleIcon);
        undefinedButton = new JButton(undefinedIcon);
        ButtonGroup sexButtonGroup = new ButtonGroup();
        sexButtonGroup.add(maleButton);
        sexButtonGroup.add(femaleButton);
        sexButtonGroup.add(undefinedButton);
        JPanel sexPanel = new JPanel(new FlowLayout());
        sexPanel.add(maleButton);
        sexPanel.add(femaleButton);
        sexPanel.add(undefinedButton);
        add(sexLabel, gbc);
        gbc.gridy++;
        add(sexPanel, gbc);
        gbc.gridy++;

        // Register button
        registerButton = new JButton("Register");
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        maleButton.setFocusPainted(false);
        femaleButton.setFocusPainted(false);
        undefinedButton.setFocusPainted(false);
        registerButton.setFocusPainted(false);

        maleButton.addActionListener(event -> {
            sex = SexEnum.MALE;
            updateSexButtonAppearance();
        });

        femaleButton.addActionListener(event -> {
            sex = SexEnum.FEMALE;
            updateSexButtonAppearance();
        });

        undefinedButton.addActionListener(event -> {
            sex = SexEnum.UNDEFINED;
            updateSexButtonAppearance();
        });

        registerButton.addActionListener(event -> {
            String firstname = firstnameField.getText();
            String lastname = lastnameField.getText();
            Date birthdate = birthdateChooser.getDate();

            DesktopApplicationContext context = DesktopApplicationContext.getInstance();
            AthleteModel athlete = new AthleteModel(lastname, firstname, birthdate, sex);
            AthleteController athleteController = context.getAthleteController();
            CompletableFuture<ObjectId> futureUserId = CompletableFuture.supplyAsync(() -> {
                try {
                    return athleteController.saveAthlete(athlete);
                } catch (AthleteValidationException exception) {
                    throw new CompletionException(exception);
                }
            });
            futureUserId.thenAccept(userId -> {
                context.connectNewUser(userId);
                navigationListener.navigateTo(NavigationRouteEnum.WORKOUTINFO.toString());
            }).exceptionally(exception -> {
                Throwable cause = exception.getCause();
                if (cause instanceof AthleteValidationException) {
                    Set<ConstraintViolation<AthleteModel>> validationErrors = ((AthleteValidationException) cause)
                            .getValidationErrors();
                    for (ConstraintViolation<AthleteModel> constraintViolation : validationErrors) {
                        showErrorDialog(constraintViolation.getMessage());
                    }
                } else {
                    showErrorDialog("An error occurred while saving the athlete.");
                }
                return null;
            });
        });

    }

    private void updateSexButtonAppearance() {
        maleButton.setBorderPainted(sex.equals(SexEnum.MALE));
        femaleButton.setBorderPainted(sex.equals(SexEnum.FEMALE));
        undefinedButton.setBorderPainted(sex.equals(SexEnum.UNDEFINED));
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}