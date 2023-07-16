import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.LoggerFactory;

import controller.AthleteController;
import enums.NavigationRouteEnum;
import model.AthleteModel;
import shared.DesktopApplicationContext;
import view.RegisterPanel;
import view.WorkoutInfoPanel;

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

        RegisterPanel registerPanel = new RegisterPanel();
        registerPanel.setNavigationListener(page -> cardLayout.show(mainPanel, page));

        mainPanel.add(NavigationRouteEnum.REGISTER.toString(), registerPanel);
        mainPanel.add(NavigationRouteEnum.WORKOUTINFO.toString(), new WorkoutInfoPanel());
        // mainPanel.add(NavigationRouteEnum.USERDETAILS.toString(), new
        // UserDetailsPanel());
        // mainPanel.add(NavigationRouteEnum.ACTIVITYLIST.toString(), new
        // ActivityListPanel());
        // mainPanel.add(NavigationRouteEnum.NEWACTIVITY.toString(), new
        // NewActivityPanel());

        cardLayout.show(mainPanel, NavigationRouteEnum.REGISTER.toString());

        frame.add(mainPanel);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }

    public void navigateTo(String page) {
        cardLayout.show(mainPanel, page);
    }
    
    public static void main(String[] args) {
        App app = new App();
        DesktopApplicationContext context = DesktopApplicationContext.getInstance();
        AthleteController athleteController = context.getAthleteController();
        CompletableFuture<List<AthleteModel>> futureAthletes = CompletableFuture.supplyAsync(athleteController::getAllAthletes);
        
        futureAthletes.thenAccept(athletes -> {
            if (athletes.size() == 1) {
                context.connectNewUser(athletes.get(0).getId());
                app.navigateTo(NavigationRouteEnum.WORKOUTINFO.toString());
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
                
                CompletableFuture<Void> allDeletesFuture = CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
                allDeletesFuture.thenRun(() -> {
                    app.navigateTo(NavigationRouteEnum.REGISTER.toString());
                }).exceptionally(ex -> {
                    logger.error("An error occures", ex);
                    return null;
                });
            }
        });
    }
}
