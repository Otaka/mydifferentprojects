package com.gooddies.texteditors;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

/**
 * @author Dmitry Savchenko
 */
public class TextFieldContextMenu extends JPopupMenu {

    private static TextFieldContextMenu instance = null;

    private TextFieldContextMenu() {
        init();
    }

    /**
     * Creates new instance of the context menu. It will be not became a singletone instance.
     * Requires if you want to add some new elements without touch already exist menus
     * @return 
     */
    public static TextFieldContextMenu getNewInstance() {
        return new TextFieldContextMenu();
    }

    public static TextFieldContextMenu getInstance() {
        if (instance == null) {
            instance = new TextFieldContextMenu();
        }
        return instance;
    }

    public void registerMenu(JTextComponent component) {
        component.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                tryPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tryPopupMenu(e);
            }
        });
    }

    private void init() {
        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupMenu menu = (JPopupMenu) e.getSource();
                JTextComponent field = (JTextComponent) menu.getInvoker();
                MenuElement[] elements = menu.getSubElements();
                if (field.getSelectedText() == null || field.getSelectedText().length() == 0) {
                    elements[0].getComponent().setEnabled(false);
                    elements[1].getComponent().setEnabled(false);
                } else {
                    elements[0].getComponent().setEnabled(true);
                    elements[1].getComponent().setEnabled(true);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        JMenuItem cut = new JMenuItem("Вырезать  [Ctrl+X]");
        cut.setBackground(Color.WHITE);
        cut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextComponent f = (JTextComponent) getInvoker();
                f.cut();
                f.requestFocusInWindow();
            }
        });

        JMenuItem copy = new JMenuItem("Скопировать");
        copy.setBackground(Color.WHITE);
        copy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextComponent f = (JTextComponent) getInvoker();
                f.copy();
                f.requestFocusInWindow();
            }
        });

        JMenuItem paste = new JMenuItem("Вставить");
        paste.setBackground(Color.WHITE);
        paste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextComponent f = (JTextComponent) getInvoker();
                f.paste();
                f.requestFocusInWindow();
            }
        });

        JMenuItem selectAll = new JMenuItem("Выделить все");
        selectAll.setBackground(Color.WHITE);
        selectAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTextComponent f = (JTextComponent) getInvoker();
                f.selectAll();
                f.requestFocusInWindow();
            }
        });
        add(cut);
        add(copy);
        add(paste);
        add(selectAll);
    }

    private void tryPopupMenu(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (e.getComponent().isEnabled()) {
                show(e.getComponent(), e.getX(), e.getY());
                e.consume();
            }
        }
    }
}
