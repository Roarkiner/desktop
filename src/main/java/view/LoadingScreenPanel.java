package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class LoadingScreenPanel extends JPanel {
    private JProgressBar progressBar;

    public LoadingScreenPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JLabel loadingLabel = new JLabel("Chargement...");
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.BOLD, 16f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(loadingLabel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        add(progressBar, gbc);

        setPreferredSize(new Dimension(200, 100));
    }
}