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
    
    @PropertyColor(category = "Colors", name = "Color of car")
    private Color someColor;
    
    @PropertyFile(category = "Colors", name = "File of car",width = "180px",selectDir = false)
    private File file;

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

    public void setSomeColor(Color someColor) {
        this.someColor = someColor;
    }

    
}
