package com.gooddies.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author dsavchenko
 */
/*Usage
 * First create class inherited from LongRunningProcess
 * Do not forget to execute super.done() at the end of the done method
 *
 *
 class PatientLoader extends LongRunningProcess {

 protected Object doInBackground() throws Exception {
 return null;
 }

 @Override
 protected void done() {
 try {
 getPatientListTableModel().fireTableDataChanged();
 debtorList = getDebtorList();
 sorter.setDebtor(debtorList);
 } finally {
 super.done();
 }
 }
 }
 *
 *
 ...
 //Execute the .execute() method
 * 
 PatientLoader process = new PatientLoader();
 try {
 process.execute();
 } catch (Exception ex) {
 ex.printStackTrace();
 }
 ...
 */
abstract public class LongRunningProcess extends SwingWorker {

    private JFrame parent;
    private JProgressBar progress = null;
    private JDialog dialog = null;
    private boolean closed = false;

    public LongRunningProcess(JFrame parent) {
        this.parent = parent;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (closed == true) {
                        return;
                    }
                    getDialog().setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private Dialog getDialog() {
        if (dialog == null) {
            dialog = new JDialog(parent, "", true);
            dialog.setPreferredSize(new Dimension(250, 25));
            dialog.setResizable(false);
            dialog.setUndecorated(true);
            dialog.setSize(new Dimension(250, 25));
            dialog.setLayout(new BorderLayout());

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            Point p = new Point(dim.width / 2 - dialog.getSize().width / 2, dim.height / 2 - dialog.getSize().height / 2);
            dialog.setLocation(p);
            dialog.add(getProgressBar(), BorderLayout.CENTER);
        }

        return dialog;
    }

    public void setProgressBarText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JProgressBar bar = getProgressBar();
                bar.setStringPainted(true);
                bar.setString(text);
            }
        });
    }

    public void setProgressValue(final int progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JProgressBar bar = getProgressBar();
                bar.setValue(progress);
            }
        });
    }

    public int getProgressValue() {
        JProgressBar bar = getProgressBar();
        return bar.getValue();
    }

    public JProgressBar getProgressBar() {
        if (progress == null) {
            progress = new JProgressBar();
            progress.setPreferredSize(new Dimension(250, 25));
            progress.setSize(new Dimension(250, 25));
            progress.setString("Идет загрузка");
            progress.setStringPainted(true);
            progress.setDoubleBuffered(true);
            progress.setIndeterminate(true);
        }

        return progress;
    }

    public void setText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progress.setString(text);
            }
        });
    }

    protected void closeDialog() {
        closed = true;
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
            dialog = null;
        }
    }

    protected void hideDialog() {
        if (dialog != null) {
            closed = true;
            dialog.setVisible(false);
        }
    }

    protected void onDone() {
    }

    /*
     * Please use onDone method
     */
    @Deprecated
    @Override
    protected void done() {
        try {
            onDone();
        } finally {
            closeDialog();
        }
    }
}
