package com.gooddies.swing.swypepanel;

import com.gooddies.events.BooleanEvent;
import com.gooddies.graphics.InteractiveIcon;
import com.gooddies.graphics.LinkIconLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {

    private LinkIconLabel backButton;
    private List<ActionListener> onBackEvents = new ArrayList<>();
    private JLabel titleLabel;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        createBackButton();
        setBackground(new Color(29, 172, 231));
        createTitleLabel();
        setOpaque(true);

    }

    public void addOnBackEvent(ActionListener event) {
        onBackEvents.add(event);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    private void createTitleLabel() {
        titleLabel = new JLabel();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20.0f));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.CENTER);
    }

    private void createBackButton() {
        InputStream stream = this.getClass().getResourceAsStream("back.png");
        BufferedImage image;
        try {
            image = ImageIO.read(stream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            stream.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        backButton = new LinkIconLabel(null, new InteractiveIcon(image));
        backButton.addClickEvent(new BooleanEvent() {
            @Override
            public boolean fireEvent(Object aThis) {
                for (ActionListener actionListener : onBackEvents) {
                    actionListener.actionPerformed(new ActionEvent(backButton, 0, "back"));
                }

                return false;
            }
        });

        add(backButton, BorderLayout.WEST);
    }
}
