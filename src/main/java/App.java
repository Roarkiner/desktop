import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import controller.AthleteController;
import enums.NavigationRouteEnum;
import model.AthleteModel;
import shared.DesktopApplicationContext;
import view.ActivityListPanel;
import view.LoadingScreenPanel;
import view.ModifyActivityPanel;
import view.NewActivityPanel;
import view.RegisterPanel;
import view.WorkoutInfosPanel;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class App {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(App.class);

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public App() {
        frame = new JFrame("Le sport c'est super");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(NavigationRouteEnum.LOADING.toString(), new LoadingScreenPanel());

        frame.add(mainPanel);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.pack();
        frame.setVisible(true);
    }

   public void navigateTo(NavigationRouteEnum page, ObjectId objectId) {
        switch (page) {
            case REGISTER:
                RegisterPanel registerPanel = new RegisterPanel();
                registerPanel.setNavigationListener(this::navigateTo);
                mainPanel.add(NavigationRouteEnum.REGISTER.toString(), registerPanel);
                break;
            case WORKOUTINFO:
                WorkoutInfosPanel workoutInfoPanel = new WorkoutInfosPanel();
                workoutInfoPanel.setNavigationListener(this::navigateTo);
                mainPanel.add(NavigationRouteEnum.WORKOUTINFO.toString(), workoutInfoPanel);
                break;
            case ACTIVITYLIST:
                ActivityListPanel activityListPanel = new ActivityListPanel();
                activityListPanel.setNavigationListener(this::navigateTo);
                mainPanel.add(NavigationRouteEnum.ACTIVITYLIST.toString(), activityListPanel);
                break;
            case NEWACTIVITY:
                NewActivityPanel newActivityPanel = new NewActivityPanel();
                newActivityPanel.setNavigationListener(this::navigateTo);
                mainPanel.add(NavigationRouteEnum.NEWACTIVITY.toString(), newActivityPanel);
                break;
            case MODIFYACTIVITY:
                ModifyActivityPanel modifyActivityPanel = new ModifyActivityPanel(objectId);
                modifyActivityPanel.setNavigationListener(this::navigateTo);
                mainPanel.add(NavigationRouteEnum.MODIFYACTIVITY.toString(), modifyActivityPanel);
                break;
            default:
                break;
        }
        
        cardLayout.show(mainPanel, page.toString());
    }

    public static void main(String[] args) {
        App app = new App();
        DesktopApplicationContext context = DesktopApplicationContext.getInstance();
        AthleteController athleteController = context.getAthleteController();
        CompletableFuture<List<AthleteModel>> futureAthletes = CompletableFuture
                .supplyAsync(athleteController::getAllAthletes);

        futureAthletes.thenAccept(athletes -> {
            if (athletes.size() == 1) {
                context.connectNewUser(athletes.get(0).getId());
                app.navigateTo(NavigationRouteEnum.ACTIVITYLIST, null);
            } else {
                List<CompletableFuture<Void>> deleteFutures = new ArrayList<>();
                for (AthleteModel athlete : athletes) {
                    CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
                        try {
                            athleteController.deleteAthlete(athlete.getId());
                        } catch (IllegalArgumentException exception) {
                            logger.error(exception.getMessage(), exception);
                        }
                    });
                    deleteFutures.add(deleteFuture);
                }

                CompletableFuture<Void> allDeletesFuture = CompletableFuture
                        .allOf(deleteFutures.toArray(new CompletableFuture[0]));
                allDeletesFuture.thenRun(() -> app.navigateTo(NavigationRouteEnum.REGISTER, null))
                        .exceptionally(ex -> {
                            logger.error("An error occured", ex);
                            return null;
                        });
            }
        }).exceptionally(ex -> {
            logger.error("An error occured", ex);
            return null;
        });
    }
}
