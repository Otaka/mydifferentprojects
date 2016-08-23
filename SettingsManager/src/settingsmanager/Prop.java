package settingsmanager;

import com.settings.editor.components.annotations.PropertyColor;
import com.settings.editor.components.annotations.PropertyFile;
import com.settings.editor.components.annotations.PropertyInt;
import com.settings.editor.components.annotations.PropertyText;
import java.awt.Color;
import java.io.File;

public class Prop {

    @PropertyInt(category = "someCategory", name = "X")
    private int x;
    @PropertyText(category = "someCategory", name = "Text")
    private String text;
    @PropertyText(category = "someCategory", name = "TextMultiline", multiline = true,width = "90%",height="200px")
    private String multiline;

    @PropertyColor(category = "Colors", name = "Color of car")
    private Color someColor;

    @PropertyFile(category = "Colors", name = "File of car", width = "180px", selectDir = false)
    private File file;

    public String getMultiline() {
        return multiline;
    }

    public void setMultiline(String multiline) {
        this.multiline = multiline;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getSomeColor() {
        return someColor;
    }

    public void setSomeColor(Color someColor) {
        this.someColor = someColor;
    }
}
