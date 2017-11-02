package com.gooddies.texteditors;

import com.gooddies.swing.IModifyableComponent;
import com.gooddies.ballontips.ErrorBallonToolTip;
import com.gooddies.ballontips.WarningBallonToolTip;
import com.gooddies.events.ValueChangedEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import net.java.balloontip.BalloonTip;
import com.gooddies.texteditors.validation.AbstractValidator;

/**
 * @author Dmitry Savchenko
 */
public abstract class ExtTextField extends JTextField implements IModifyableComponent {

    private boolean modified = false;
    private boolean allowValidation = true;
    private Color borderColor = Color.BLUE;
    private String prefixSymbol = "$";
    private boolean showPrefixSymbol = false;
    private JLabel helpLabel = null;
    private boolean showHelp = false;
    private int maxTextLength = -1;
    protected ArrayList<AbstractValidator> validators = null;
    private boolean errorMode = false;
    private String errorMessage = "";
    private BalloonTip toolTip;
    private static BalloonTip helpToolTip = null;
    private String helpMessage = "";
    private JLabel prefixLabel = null;
    private BalloonTip.Orientation helpTipOrientation = BalloonTip.Orientation.LEFT_ABOVE;
    private InputMode inputType = InputMode.ALL;
    private long tooltipShowTime = -1;//measure the time when the tooltip was showed.
    private long tooltipIgnoreFocusInterval = 50;//in some cases tooltip closes just after showing. We should wait some time and do not react on the focuslost
    private Pattern regexpMatcher = null;
    protected ValueChangedEvent<String> onChangeEvent;

    protected enum TOOLTIPTYPE {

        ERROR, WARNING
    };

    public ExtTextField() {
        super();
        ExtTextField.this.init();
    }

    public void init() {
        setPreferredSize(new Dimension(100, 18));
        showPrefixSymbol = false;
        setHorizontalAlignment(JTextField.LEFT);
        setKeyListener();
        setFocusListener();
        initValidators();
        createErrorBallonTooltip(TOOLTIPTYPE.ERROR);
        CompoundBorder newBorder = new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new BasicBorders.MarginBorder());
        super.setBorder(newBorder);
        setBorderColor(new Color(192, 192, 192));
        initDefaultMenu();
    }

    private void initDefaultMenu() {
        //TextFieldContextMenu.getInstance().registerMenu(this);
    }

    public void setBorderColor(Color color) {
        if (errorMode == false) {
            borderColor = color;
        }
        CompoundBorder b = (CompoundBorder) getBorder();
        b.getBorderInsets(this).toString();
        b.getOutsideBorder().toString();
        CompoundBorder newBorder = new CompoundBorder(BorderFactory.createLineBorder(color), b.getInsideBorder());
        super.setBorder(newBorder);
    }

    protected void fireChange() {
        if (onChangeEvent != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    onChangeEvent.fire(getText(), ExtTextField.this);
                }
            });

        }
    }

    public void setValueChangedEvent(ValueChangedEvent<String> action) {
        onChangeEvent = action;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void createErrorBallonTooltip(TOOLTIPTYPE tooltipType) {
        if (tooltipType == TOOLTIPTYPE.ERROR) {
            toolTip = new ErrorBallonToolTip(this) {
                @Override
                public void setVisible(boolean bln) {
                    boolean last = isVisible();
                    super.setVisible(bln);
                    if (last == true && bln == false) {
                    }
                }
            };
        } else {
            toolTip = new WarningBallonToolTip(this) {
                @Override
                public void setVisible(boolean bln) {
                    super.setVisible(bln);
                }
            };
        }

        toolTip.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                setErrorMode(false, TOOLTIPTYPE.ERROR);
            }
        });
        toolTip.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setErrorMode(false, TOOLTIPTYPE.ERROR);
            }
        });
    }

    private void removeMouseListeners(JComponent component) {
        MouseListener[] list = component.getMouseListeners();
        for (MouseListener l : list) {
            component.removeMouseListener(l);
        }
    }

    @Override
    public void setBorder(Border border) {
    }

    private void removeHelpLabel() {
        if (helpLabel == null) {
            return;
        }
        remove(helpLabel);
        removeMouseListeners(helpLabel);
        Insets insets = (Insets) getMargin().clone();
        insets.right = 1;
        setMargin(insets);
        helpLabel = null;
        validate();
        repaint();
    }

    protected void removePrefix() {
        if (prefixLabel == null) {
            return;
        }
        remove(prefixLabel);
        removeMouseListeners(prefixLabel);
        Insets insets = (Insets) getMargin().clone();
        insets.left = 1;
        setMargin(insets);
        prefixLabel = null;
        validate();
        repaint();
    }

    protected int textWidth(JComponent component, String text) {
        return component.getFontMetrics(component.getFont()).stringWidth(text);
    }

    protected void createPrefix() {
        removePrefix();
        prefixLabel = new JLabel(getPrefixSymbol());
        prefixLabel.setForeground(getForeground());
        int width = textWidth(prefixLabel, getPrefixSymbol());
        prefixLabel.setBounds(1, 1, width, 17);
        Insets insets = getMargin();
        Insets insets2 = (Insets) insets.clone();
        insets2.left = width;
        setMargin(insets2);
        add(prefixLabel);
        validate();
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        if (prefixLabel != null) {
            prefixLabel.setForeground(fg);
        }
        super.setForeground(fg);
    }

    private void repositionHelpLabelLabel() {
        Rectangle rect = getBounds();
        int helpWidth = helpLabel.getPreferredSize().width;
        int helpHeight = helpLabel.getPreferredSize().height;
        helpLabel.setBounds(rect.width - helpWidth - 1, 1, helpWidth, helpHeight);
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public void setHelpMessage(String helpMessage, BalloonTip.Orientation orientation) {
        setHelpMessage(helpMessage);
        helpTipOrientation = orientation;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (isShowHelp()) {
            repositionHelpLabelLabel();
        }
    }

    private void initValidators() {
        validators = new ArrayList<AbstractValidator>();
    }

    private void setFocusListener() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (shouldIgnoreFocusLost()) {
                    return;
                }
                setErrorMode(false, TOOLTIPTYPE.ERROR);
                super.focusGained(e);
            }
        });
    }

    private void setKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (isErrorMode()) {
                    setErrorMode(false, TOOLTIPTYPE.ERROR);
                }

                char key = e.getKeyChar();
                key = processChar(key);
                if (key == 0) {
                    e.consume();
                    return;
                }

                if (!e.isActionKey()) {
                    if (maxTextLength != -1) {
                        if (getText().length() >= maxTextLength) {
                            if (getSelectionEnd() == getSelectionStart()) {
                                e.consume();
                            }
                        }
                    }
                }
                setModified(true);
                fireChange();
            }
        });
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public void setShowPrefixSymbol(boolean showPrefixSymbol) {
        this.showPrefixSymbol = showPrefixSymbol;
        if (showPrefixSymbol == true) {
            createPrefix();
        } else {
            removePrefix();
        }
    }

    public boolean getShowPrefixSymbol() {
        return showPrefixSymbol;
    }

    public void setPrefixSymbol(String prefixSymbol) {
        this.prefixSymbol = prefixSymbol;
        createPrefix();
    }

    public String getPrefixSymbol() {
        return prefixSymbol;
    }

    /**
     *
     * @param maxLength -1 any string length allowed
     */
    public void setMaxTextLength(int maxLength) {
        if (getText().length() > maxLength) {
            setText(getText().substring(0, maxLength));
        }
        this.maxTextLength = maxLength;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    protected boolean isErrorMode() {
        return errorMode;
    }

    protected void setErrorMode(boolean error, TOOLTIPTYPE tooltipType) {
        errorMode = error;
        if (error == false) {
            if (toolTip.isVisible()) {
                toolTip.setVisible(false);
            }

            setBorderColor(borderColor);
        } else {
            Color color = Color.RED;
            if (tooltipType == TOOLTIPTYPE.WARNING) {
                color = new Color(255, 128, 0);
            }
            setBorderColor(color);
            showTooltip(getErrorMessage(), tooltipType);
            startFocusLostIgnoring();
        }
    }

    private boolean shouldIgnoreFocusLost() {
        long current = (new Date()).getTime();
        if ((current - tooltipShowTime) >= tooltipIgnoreFocusInterval) {
            return false;
        }
        return true;
    }

    private void startFocusLostIgnoring() {
        tooltipShowTime = (new Date()).getTime();
    }

    public void addValidator(AbstractValidator validator) {
        validators.add(validator);
    }

    public void addValidator(AbstractValidator[] validators) {
        if (validators != null) {
            this.validators.addAll(Arrays.asList(validators));
        }
    }

    public void removeValidator(AbstractValidator validator) {
        validators.remove(validator);
    }

    public void removeValidator(int index) {
        validators.remove(index);
    }

    public void removeValidators() {
        validators.clear();
    }

    public int getValidatorsCount() {
        return validators.size();
    }

    public void setEnableValidation(boolean enable) {
        this.allowValidation = enable;
    }

    public boolean getEnableValidation() {
        return allowValidation;
    }

    public boolean startValidation() {
        return startValidation(true);
    }

    @Override
    public String getToolTipText() {
        if (errorMode) {
            return getErrorMessage();
        }
        return super.getToolTipText();
    }

    public void showErrorToolTip() {
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setErrorMode(true, TOOLTIPTYPE.ERROR);
            }
        });

        timer.setRepeats(false);
        timer.start();

    }

    public void showError(final String text) {
        showTooltip(text, TOOLTIPTYPE.ERROR);
    }

    public void showWarning(final String text) {
        showTooltip(text, TOOLTIPTYPE.WARNING);
    }

    private void showTooltip(final String text, final TOOLTIPTYPE tooltipType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (tooltipType == TOOLTIPTYPE.ERROR) {
                    createErrorBallonTooltip(tooltipType);
                    toolTip.setText(text);
                    toolTip.setVisible(true);
                    toolTip.requestFocusInWindow();
                    toolTip.refreshLocation();
                } else {
                    createErrorBallonTooltip(tooltipType);
                    toolTip.setFocusable(false);
                    toolTip.setText(text);
                    toolTip.setVisible(true);
                    toolTip.requestFocusInWindow();
                    toolTip.refreshLocation();
                }
            }
        });
    }

    public boolean startValidation(boolean indication) {
        if (!allowValidation) {
            return true;
        }
        String text = getText();
        for (AbstractValidator validator : validators) {
            if ((validator != null) && (!validator.validate(text))) {
                setErrorMessage(validator.getText());
                if (indication) {
                    setErrorMode(true, TOOLTIPTYPE.ERROR);
                }

                return false;
            }
        }

        setErrorMode(false, TOOLTIPTYPE.ERROR);
        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public InputMode getInputMode() {
        return inputType;
    }

    public void setInputMode(InputMode inputType) {
        this.inputType = inputType;
        regexpMatcher = null;
    }

    public void setInputMode(InputMode inputType, Object value) {
        this.inputType = inputType;
        if (inputType == InputMode.REGEXP_CASE_SENCE) {
            String str = (String) value;
            regexpMatcher = Pattern.compile(str);
        } else if (inputType == InputMode.REGEXP_CASE_N_SENCE) {
            String str = (String) value;
            regexpMatcher = Pattern.compile(str, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public void paste() {
        super.paste();
        setModified(true);
        boolean changed = false;
        String text = getText();
        if (getMaxTextLength() != -1) {
            int maxLength = getMaxTextLength();
            if (text.length() > maxLength) {
                text = text.substring(0, maxTextLength);
                changed = true;
            }
        }

        if (inputType != InputMode.ALL) {
            char[] array = text.toCharArray();
            StringBuilder dest = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                char c = array[i];
                c = processChar(c);
                if (c != 0) {
                    dest.append(c);
                } else {
                    changed = true;
                }
            }
            if (changed == true) {
                text = dest.toString();
            }
        }

        if (changed == true) {
            setText(text);
            fireChange();
        }
    }

    private char processChar(char c) {
        switch (inputType) {
            case REGEXP_CASE_N_SENCE:
            case REGEXP_CASE_SENCE:
                String s = "" + c;
                Matcher match = regexpMatcher.matcher(s);
                return match.matches() == true ? c : 0;
            case CHARACTER:
                if (!((c >= 'a' && c <= 'z') || (c == ' ') || (c == '_') || (c >= 'A' && c <= 'Z'))) {
                    return 0;
                }
                break;
            case CHARACTER_SIMPLE:
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                    return 0;
                }
                break;
            case NUMBER:
                if (c < '0' || c > '9') {
                    return 0;
                }
                break;
            case CHARACTER_NUMBER:
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '-') || (c == '\'') || (c == ' ') || (c == ',') || (c == '.') || (c >= '0' && c <= '9'))) {
                    return 0;
                }
                break;
            case CHARACTER_NUMBER_SIMPLE:
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                    return 0;
                }
                break;
            case PHONE:
                if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || Arrays.asList('+', '-', '*', '#', '(', ')', '.').contains(c))) {
                    return 0;
                }
                break;
        }
        return c;
    }

    @Override
    public void clearModified() {
        modified = false;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public void setTextAndScrollToStart(String text) {
        setText(text);
        if (text != null && !text.isEmpty()) {
            setCaretPosition(0);
        }
    }

    public void addFilter(DocumentFilter documentFilter) {
        ((AbstractDocument) getDocument()).setDocumentFilter(documentFilter);
    }

    public void setInputInUpperCase() {
        addFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                fb.insertString(offset, string.toUpperCase(), attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
                    AttributeSet attrs) throws BadLocationException {
                fb.replace(offset, length, text.toUpperCase(), attrs);
            }
        });
    }

    public void setInputInLowerCase() {
        addFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                fb.insertString(offset, string.toLowerCase(), attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
                    AttributeSet attrs) throws BadLocationException {
                fb.replace(offset, length, text.toLowerCase(), attrs);
            }
        });
    }
}
