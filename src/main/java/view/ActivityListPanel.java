package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.bson.types.ObjectId;

import controller.ActivityController;
import enums.NavigationRouteEnum;
import model.ActivityModel;
import shared.DesktopApplicationContext;
import shared.NavigationListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActivityListPanel extends JPanel {

    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JPanel activitiesPanel;
    private JButton addActivityButton;
    private JButton workoutInfosNavigateButton;

    private String arialFont = "Arial";
    private transient ActivityController activityController;
    private transient NavigationListener navigationListener;

    public ActivityListPanel() {
        DesktopApplicationContext context = DesktopApplicationContext.getInstance();
        ActivityController contextActivityController = context.getActivityController();
        this.activityController = contextActivityController;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        workoutInfosNavigateButton = new JButton("Voir mes informations");
        workoutInfosNavigateButton.setFont(new Font(arialFont, Font.BOLD, 16));
        workoutInfosNavigateButton.setFocusPainted(false);
        workoutInfosNavigateButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(NavigationRouteEnum.WORKOUTINFO, null);
            }
        });
        topPanel.add(workoutInfosNavigateButton, BorderLayout.WEST);
        titleLabel = new JLabel("Mes activités");
        titleLabel.setFont(new Font(arialFont, Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        activitiesPanel = new JPanel();
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));
        activitiesPanel.add(Box.createVerticalGlue());

        scrollPane = new JScrollPane(activitiesPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        addLoadingLabel();

        addActivityButton = new JButton("Ajouter une activité");
        addActivityButton.setFont(addActivityButton.getFont().deriveFont(Font.BOLD, 16f));
        addActivityButton.setFocusPainted(false);
        addActivityButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(NavigationRouteEnum.NEWACTIVITY, null);
            }
        });

        add(addActivityButton, BorderLayout.SOUTH);

        if (contextActivityController != null) {
            CompletableFuture<List<ActivityModel>> futureActivities = CompletableFuture
                    .supplyAsync(activityController::getAllActivities);

            futureActivities.thenAccept(activities -> {
                removeLoadingLabel();
                displayActivities(activities);
            });
        }
    }

    private void addLoadingLabel() {
        JPanel loadingPanel = new JPanel();
        loadingPanel.setName("loadingPanel");
        loadingPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.CENTER;

        JLabel loadingLabel = new JLabel("Chargement");
        loadingLabel.setFont(new Font(arialFont, Font.BOLD, 16));
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingPanel.add(loadingLabel, gbc);

        activitiesPanel.add(Box.createVerticalGlue());
        activitiesPanel.add(loadingPanel);
        activitiesPanel.add(Box.createVerticalGlue());

        activitiesPanel.revalidate();
    }

    private void removeLoadingLabel() {
        Component[] components = activitiesPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getName().equals("loadingPanel")) {
                activitiesPanel.remove(component);
                break;
            }
        }
        activitiesPanel.revalidate();
        activitiesPanel.repaint();
    }

    private void displayActivities(List<ActivityModel> activities) {
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));
        activitiesPanel.setAlignmentY(0f);

        for (ActivityModel activity : activities) {
            JPanel activityPanel = createActivityPanel(activity);
            activityPanel.setMaximumSize(new Dimension(getWidth() -18, 200));
            activityPanel.setPreferredSize(new Dimension(getWidth() -18, 200));
            activitiesPanel.add(activityPanel);
        }
        activitiesPanel.revalidate();
    }

    private JPanel createActivityPanel(ActivityModel activity) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel namePanel = new JPanel(new BorderLayout());

        JButton modifyButton = new JButton(new ImageIcon(getClass().getResource("/img/modify-icon.png")));
        modifyButton.setBorder(BorderFactory.createEmptyBorder());
        modifyButton.setContentAreaFilled(false);
        modifyButton.addActionListener(e -> {
            ObjectId activityId = activity.getActivityId();
            navigationListener.navigateTo(NavigationRouteEnum.MODIFYACTIVITY, activityId);
        });
        modifyButton.setPreferredSize(new Dimension(50, 50));
        namePanel.add(modifyButton, BorderLayout.WEST);

        JButton deleteButton = new JButton(new ImageIcon(getClass().getResource("/img/trash-icon.png")));
        deleteButton.setBorder(BorderFactory.createEmptyBorder());
        deleteButton.setContentAreaFilled(false);
        deleteButton.addActionListener(e -> {
            ObjectId activityId = activity.getActivityId();
            activityController.deleteActivity(activityId);
            navigationListener.navigateTo(NavigationRouteEnum.ACTIVITYLIST, null);
        });
        deleteButton.setPreferredSize(new Dimension(50, 50));
        namePanel.add(deleteButton, BorderLayout.EAST);

        JLabel nameLabel = new JLabel(activity.getName());
        nameLabel.setFont(new Font(arialFont, Font.BOLD, 16));
        nameLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        namePanel.add(nameLabel, BorderLayout.CENTER);

        panel.add(namePanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(1, 4));
        detailsPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel durationLabel = new JLabel("Durée: ");
        durationLabel.setFont(new Font(arialFont, Font.BOLD, 12));
        JLabel durationValueLabel = new JLabel(String.valueOf(activity.getDuration()));
        durationValueLabel.setFont(new Font(arialFont, Font.PLAIN, 12));

        JLabel rpeLabel = new JLabel("RPE: ");
        rpeLabel.setFont(new Font(arialFont, Font.BOLD, 12));
        JLabel rpeValueLabel = new JLabel(String.valueOf(activity.getRpe()));
        rpeValueLabel.setFont(new Font(arialFont, Font.PLAIN, 12));

        JLabel loadLabel = new JLabel("Charge: ");
        loadLabel.setFont(new Font(arialFont, Font.BOLD, 12));
        JLabel loadValueLabel = new JLabel(String.valueOf(activity.getLoad()));
        loadValueLabel.setFont(new Font(arialFont, Font.PLAIN, 12));

        JLabel dateLabel = new JLabel("Date: ");
        dateLabel.setFont(new Font(arialFont, Font.BOLD, 12));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String activityDate = simpleDateFormat.format(activity.getDate());
        JLabel dateValueLabel = new JLabel(activityDate);
        dateValueLabel.setFont(new Font(arialFont, Font.PLAIN, 12));

        detailsPanel.add(durationLabel);
        detailsPanel.add(durationValueLabel);
        detailsPanel.add(rpeLabel);
        detailsPanel.add(rpeValueLabel);
        detailsPanel.add(loadLabel);
        detailsPanel.add(loadValueLabel);
        detailsPanel.add(dateLabel);
        detailsPanel.add(dateValueLabel);

        panel.add(detailsPanel);

        return panel;
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }
}