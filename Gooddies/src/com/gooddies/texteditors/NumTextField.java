package com.gooddies.texteditors;

import com.gooddies.events.ValueChangedEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.gooddies.texteditors.validation.AbstractNumericValidator;
import com.gooddies.texteditors.validation.AbstractValidator;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dmitry Savchenko
 */
public abstract class NumTextField extends ExtTextField {

    private DecimalFormat formatter = null;
    private DecimalFormat formatterWithoutGrouping = null;
    private boolean allowDot = false;
    private char dotSymbol = '.';
    private char DigitGroupSeparator = ',';
    private int DigitGroupLength = 3;
    private boolean useGroupSymbol = false;
    private int drobLength = 2;
    private boolean allowNegative = false;
    protected static final double MININTEGER = -2147483647.0;
    protected static final double MAXPOSITIVEINTEGER = 2147483647.0;
    protected static final double MINDOUBLE = -9999999999999999999999999.0;
    protected static final double MAXDOUBLE = 9999999999999999999999999.0;
    protected double minValue = -9999999999999999999999999.0;
    protected double maxValue = 99999999999999999999999999.0;
    private int dragLastX = 0;
    private int dragThresthold = 7;
    private boolean isDrag = false;
    private boolean allowNaN = false;
    private boolean isNaN = false;
    private Timer validationTimer = null;
    private double dragStep = 0;
    private ValueChangedEvent<Float> valueChanged;

    public enum TextFieldType {

        INTEGER, FLOAT
    };

    public NumTextField() {
        initNumField();
        NumTextField.this.setValue(0);
    }

    public NumTextField(TextFieldType type) {
        if (type == TextFieldType.INTEGER) {
            initNumField();
        } else {
            initFloatField();
        }
    }

    private void initNumField() {
        setShowPrefixSymbol(false);
        setHorizontalAlignment(JTextField.RIGHT);
        setAllowDot(false);
        setShowGroupSymbol(true);
        setDigitGroupLength(3);
        setDigitGroupSeparator(',');
        setKeyListener();
        // setFocusListener();
        setMouseListener();
        setDocumentListener();
        setForeground(Color.BLACK);
    }

    private int getScreenWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    private void setMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    dragLastX = e.getXOnScreen();
                    isDrag = true;
                    dragStep = getvalue() / 100.0;
                    super.mousePressed(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDrag = false;
                super.mouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDrag == false) {
                    return;
                }

                if (e.getLocationOnScreen().x == (getScreenWidth() - 1)) {
                    try {
                        int x = e.getLocationOnScreen().x;
                        int dX = x - dragLastX;
                        double step = dX * dragStep;
                        setValue(getvalue() + step);

                        Robot r = new Robot();
                        r.mouseMove(1, e.getLocationOnScreen().y);
                        dragLastX = 0;
                        return;
                    } catch (AWTException ex) {
                        Logger.getLogger(NumTextField.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (e.getLocationOnScreen().x == 0) {
                    try {
                        int x = e.getLocationOnScreen().x;
                        int dX = x - dragLastX;
                        double step = dX * dragStep;
                        setValue(getvalue() + step);

                        Robot r = new Robot();
                        r.mouseMove(getScreenWidth() - 2, e.getLocationOnScreen().y);
                        dragLastX = getScreenWidth() - 2;
                        return;
                    } catch (AWTException ex) {
                        Logger.getLogger(NumTextField.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                int x = e.getLocationOnScreen().x;
                if (x != dragLastX) {
                    int dX = x - dragLastX;
                    double step = dX * dragStep;
                    setValue(getvalue() + step);
                    dragLastX = x;
                }
                numTextFieldFireChange();
                super.mouseDragged(e);
            }
        });

    }

    private void initFloatField() {
        setShowPrefixSymbol(false);
        setHorizontalAlignment(JTextField.RIGHT);
        setAllowDot(true);
        setFloatLength(2);
        setShowGroupSymbol(true);
        setDigitGroupLength(3);
        setDigitGroupSeparator(',');
        setKeyListener();
        //  setFocusListener();
        setMouseListener();
        setForeground(Color.BLACK);
        setDocumentListener();
    }

    private void setDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent event) {
                clearValidationTimer();

                validationTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clearValidationTimer();
                        final double value = parseValue();
                        if (value > maxValue) {
                            int caret = getCaretPosition();
                            try {
                                undoInsertedText(event);
                                setCaretPosition(caret - 1);
                            } catch (Exception ex) {
                            }
                            String digit = formatNumber(maxValue);
                            setErrorMessage("Maximum value for the field = " + digit);
                            setErrorMode(true, TOOLTIPTYPE.WARNING);
                            fireChange();
                        }

                        if (value < minValue) {
                            try {
                                undoInsertedText(event);
                            } catch (Exception ex) {
                            }
                            String digit = formatNumber(minValue);
                            setErrorMessage("Minimum value for the field = " + digit);
                            setErrorMode(true, TOOLTIPTYPE.WARNING);
                            fireChange();
                        }
                    }
                });
                validationTimer.setRepeats(false);
                validationTimer.start();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearValidationTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearValidationTimer();
            }
        });
    }

    private void clearValidationTimer() {
        if (validationTimer != null) {
            validationTimer.stop();
            validationTimer = null;
        }
    }

    private void undoInsertedText(DocumentEvent e) {
        int offset = e.getOffset();
        int length = e.getLength();
        if (length != 0) {
            String text = getText();
            if (!text.isEmpty()) {
                String before = text.substring(0, offset);
                String after = text.substring(offset + length);
                text = before + after;
                setText(text);
            }
        }
    }

    private String formatNumber(double number) {
        String result = "";
        if (isAllowDot()) {
            String format = "%." + getFloatLength() + "f";
            result = String.format(format, number);
        } else {
            result = String.format("%d", (int) number);
        }
        return result;
    }

    private void setFocusListener() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isNaN()) {
                    setText("");
                } else {
                    int caret = getCaretPosition();
                    updateValue(true);
                    if (caret < NumTextField.super.getText().length()) {
                        setCaretPosition(caret);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                int selStart = getSelectionStart();
                int selEnd = getSelectionEnd();
                int caret = getCaretPosition();
                if (isAllowNaN() && (getText().length() == 0)) {
                    setIsNaN(true);

                } else {
                    updateValue(false);
                    if (caret < NumTextField.super.getText().length()) {
                        setCaretPosition(caret);
                    }
                }
                setSelectionStart(selStart);
                setSelectionEnd(selEnd);
            }
        });
    }

    @Override
    protected void fireChange() {
        //disable fire change
    }

    protected void numTextFieldFireChange() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (valueChanged != null) {
                    valueChanged.fire((float) getvalue(), NumTextField.this);
                }
            }
        });
    }

    @Override
    public void setValueChangedEvent(ValueChangedEvent<String> action) {
        super.setValueChangedEvent(action);
    }

    public void setValueChangedEventNumber(ValueChangedEvent<Float> event) {
        this.valueChanged = event;
    }

    private void setKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                isNaN = false;
                dotCommaHack(e);
                if (!processKeyPress(e.getKeyChar())) {
                    e.consume();
                } else {
                    numTextFieldFireChange();
                    super.keyTyped(e);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    numTextFieldFireChange();
                }
                super.keyPressed(e);
            }
        });
    }

    @Override
    public void paste() {
        super.paste();
        numTextFieldFireChange();
    }
    
    

    //Method allows to use , and . as separator of the float. Automaticaly converts to current separator
    private void dotCommaHack(KeyEvent e) {
        char separator = getDotSymbol();
        char key = e.getKeyChar();
        if (separator == '.' || separator == ',') {
            if (key == '.' || key == ',') {
                e.setKeyChar(separator);
            }
        }
    }

    private boolean processKeyPress(char key) {
        if (isAllowNegative()) {
            if (key == '-') {
                if (processNegative()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (getCaretPosition() == 0) {
                    if (super.getText().indexOf('-') != -1) {
                        return false;
                    }
                }
            }
        }
        if (!isAllowDot()) {
            if (key < '0' || key > '9') {
                return false;
            }
        } else {
            char separatorSymbol = getDotSymbol();
            if (key == separatorSymbol) {
                if (super.getText().indexOf(separatorSymbol) != -1) {
                    return false;
                }
            } else if (key < '0' || key > '9') {
                return false;
            }
        }

        return true;
    }

    private void updateValue(boolean withoutGroup) {
        try {
            double val = getvalue();
            setValue(val, withoutGroup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean processNegative() {
        if (super.getText().indexOf('-') != -1) {
            super.setText(super.getText().replaceAll("-", ""));
            return false;
        }

        setCaretPosition(0);
        return true;
    }

    protected double getvalue() {
        if (isAllowNaN()) {
            if (isNaN()) {
                return -1;
            }
        }
        double value = parseValue();
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        return value;
    }

    public void setValue(double value, boolean withoutGroup) {
        double max = getMaxValue();
        double min = getMinValue();
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        if (value < 0 && !isAllowNegative()) {
            value = 0;
        }

        String result;
        if (!isAllowDot() || getFloatLength() == 0) {
            result = String.valueOf((int) value);
        } else {
            result = doubleToString(value, withoutGroup);

        }

        //setVisibleText(result);
        super.setText(result);
    }

    public void setValue(double value) {
        setValue(value, false);
        setIsNaN(false);
    }

    private void setValue2(double value) {
        setValue(value, false);
    }

    public void setDotSymbol(char dotSymbol) {
        this.dotSymbol = dotSymbol;
        createFormatter();
    }

    public char getDotSymbol() {
        return dotSymbol;
    }

    private void createFormatter() {
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols(Locale.getDefault());
        unusualSymbols.setDecimalSeparator(getDotSymbol());
        unusualSymbols.setGroupingSeparator(getDigitGroupSeparator());
        StringBuilder b = new StringBuilder();
        b.append('#');
        if (isShowGroupSymbol()) {
            b.append(',');
        }
        b.append("##0.");
        if (getFloatLength() == -1) {
            b.append("#");
        } else {

            int len = getFloatLength();
            for (int i = 0; i < len; i++) {
                b.append('#');
            }

        }

        String pattern = b.toString();
        formatter = new DecimalFormat(pattern, unusualSymbols);
        formatter.setGroupingSize(getDigitGroupLength());
        pattern = pattern.replace(',', '#');
        formatterWithoutGrouping = new DecimalFormat(pattern, unusualSymbols);
    }

    private String doubleToString(double value, boolean withoutGroup) {
        String text = null;
        if (withoutGroup) {
            text = formatterWithoutGrouping.format(value);
        } else {
            text = formatter.format(value);
        }

        if (isAllowDot()) {
            int f = getFloatLength();
            if (f != -1) {
                int textLen = text.length();
                int dotPos = text.indexOf('.');
                if (((textLen - dotPos - 1) != f) || (dotPos == -1)) {
                    StringBuilder sb = new StringBuilder(100);
                    if (dotPos == 0) {
                        //It seems we should do nothing
                    } else if (dotPos == -1) {
                        sb.append(text);
                        sb.append('.');
                        for (int i = 0; i < f; i++) {
                            sb.append('0');
                        }
                    } else {
                        int nullCount = (textLen - dotPos) - 1;
                        sb.append(text);
                        for (int i = 0; i < nullCount; i++) {
                            sb.append('0');
                        }
                    }
                    text = sb.toString();
                }
            }
        }
        return text;
    }

    private double StringToDouble(String text) {
        try {
            if (text.equals("NA")) {
                return 0.0;
            }
            return formatter.parse(text).doubleValue();
        } catch (Exception ex) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setValue(0);
                }
            });
            return 0.0;
        }
    }

    private double parseValue() {
        String text = super.getText();
        if (text == null || text.isEmpty()) {
            text = "0";
        }
        double value = StringToDouble(text);
        if (!isAllowDot()) {
            if (isAllowNegative()) {
                if (value < MININTEGER) {
                    value = MININTEGER;
                }
                if (value > MAXPOSITIVEINTEGER) {
                    value = MAXPOSITIVEINTEGER;
                }
            } else {
                if (value < 0) {
                    value = 0;
                }

                if (value > MAXPOSITIVEINTEGER) {
                    value = MAXPOSITIVEINTEGER;
                }
            }
        }
        return value;
    }

    public void setMinMax(double minValue, double maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("Cannot set minimum value greate then maximum [" + minValue + "," + maxValue + "]");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        double tVal = getvalue();
        setValue2(tVal);
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        double tVal = getvalue();
        setValue2(tVal);
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        double tVal = getvalue();
        setValue2(tVal);
    }

    public boolean isAllowNegative() {
        return allowNegative;
    }

    public void setAllowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;
        if (allowNegative == false) {
            if (super.getText().indexOf('-') != -1) {
                super.setText(super.getText().replace("-", ""));
            }
        }
    }

    /**
     * is the number float or integer
     */
    public boolean isAllowDot() {
        return allowDot;
    }

    /**
     * is the number float or integer
     */
    public void setAllowDot(boolean allowDot) {
        if (allowDot == false) {
            setMaxTextLength(11);
        } else {
            setMaxTextLength(15);
        }
        this.allowDot = allowDot;
        createFormatter();
    }

    public int getFloatLength() {
        return drobLength;
    }

    /**
     * @param drobLength digits after dot in float numbers
     */
    public void setFloatLength(int drobLength) {
        this.drobLength = drobLength;
        createFormatter();
    }

    public boolean isShowGroupSymbol() {
        return useGroupSymbol;
    }

    public int getDigitGroupLength() {
        return DigitGroupLength;
    }

    public void setDigitGroupLength(int DigitGroupLength) {
        this.DigitGroupLength = DigitGroupLength;
        createFormatter();
    }

    public char getDigitGroupSeparator() {
        return DigitGroupSeparator;
    }

    public void setDigitGroupSeparator(char DigitGroupSeparator) {
        this.DigitGroupSeparator = DigitGroupSeparator;
        createFormatter();
    }

    /**
     * @param useGroupSymbol if true the number will be formatted as
     * 45,455,455.00 if false 45455455.00
     */
    public void setShowGroupSymbol(boolean useGroupSymbol) {
        this.useGroupSymbol = useGroupSymbol;
        createFormatter();
    }

    /**
     * use addValidator(AbstractNumericValidator)
     *
     */
    @Override
    public void addValidator(AbstractValidator validator) {
        throw new UnsupportedOperationException("use addValidator(AbstractNumericValidator)");
    }

    /**
     *
     * use addValidator(AbstractNumericValidator[])
     *
     */
    @Override
    public void addValidator(AbstractValidator[] validator) {
        throw new UnsupportedOperationException("use addValidator(AbstractNumericValidator[])");
    }

    public void addValidator(AbstractNumericValidator validator) {
        super.addValidator((AbstractValidator) validator);
    }

    public void addValidator(AbstractNumericValidator[] validators) {
        super.addValidator((AbstractValidator[]) validators);
    }

    @Override
    public boolean startValidation(boolean indication) {
        if (!getEnableValidation()) {
            return true;
        }
        double value = getvalue();
        int count = validators.size();
        for (int i = 0; i < count; i++) {
            AbstractNumericValidator validator = (AbstractNumericValidator) validators.get(i);
            try {
                if ((validator != null) && (!validator.validate(value))) {
                    setErrorMessage(validator.getText());
                    if (indication) {
                        setErrorMode(true, TOOLTIPTYPE.ERROR);
                    }

                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setErrorMode(false, TOOLTIPTYPE.ERROR);
        return true;
    }

    /**
     * Do not use this function. Use double getValue() instead
     */
    @Override
    public String getText() {
        return super.getText();
    }

    /**
     * Do not use this function. Use void setValue(double) instead
     */
    @Override
    public void setText(String t) {
        if (!isAllowDot()) {
            if (t != null) {
                if (t.indexOf(getDotSymbol()) != -1) {
                    t = t.substring(0, t.indexOf(getDotSymbol()));
                }
            }
        }

        super.setText(t);
    }

    public boolean isAllowNaN() {
        return allowNaN;
    }

    public void setAllowNaN(boolean allowNaN) {
        this.allowNaN = allowNaN;
    }

    public boolean isNaN() {
        return isNaN;
    }

    public void setIsNaN(boolean isNaN) {
        if (isAllowNaN() == false) {
            return;
        }
        this.isNaN = isNaN;
        if (isNaN == true) {
            super.setText("NA");
        } else {
            NumTextField.this.setValue2(getvalue());
        }
    }
}
