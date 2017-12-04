package resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Dmitry
 */
public class Icons {

    public static Icon ERROR_ICON = loadIcon("Error.png");
    public static Icon HELP_ICON = loadIcon("help.png");
    public static Icon ADD_ICON = loadIcon("iconAdd.png");

    private static Icon loadIcon(String iconName) {
        return new ImageIcon(Icon.class.getResource("/resources/" + iconName));
    }
}