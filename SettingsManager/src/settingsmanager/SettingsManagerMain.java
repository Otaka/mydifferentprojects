package settingsmanager;

import com.settings.editor.builder.CategoryProperties;
import com.settings.editor.builder.SettingsDialogBuilder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SettingsManagerMain {

    public static void main(String[] args) throws InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Prop p = new Prop();
        p.setText("Hi");
        p.setX(43);
        p.setSomeColor(Color.red);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SettingsDialogBuilder()
                        .setProperty(p)
                        .setCategoryProperties("someCategory", new CategoryProperties().setScroll(true))
                        .setPreferredSize(new Dimension(200, 200))
                        .show();
            }
        });
    }

}
