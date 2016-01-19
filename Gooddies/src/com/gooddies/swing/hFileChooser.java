package com.gooddies.swing;

import com.gooddies.persistence.Properties;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author sad
 */
public class hFileChooser extends JFileChooser {
    private static String directory;

    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();
        if (selectedFile != null) {
            directory = selectedFile.getPath();
            Properties.get().putString("workFolder", directory);
        }

        return selectedFile;

    }

    public hFileChooser() {
        if (directory == null) {
            directory = Properties.get().getString("workFolder");
        }
        if (directory != null) {
            setCurrentDirectory(new File(directory));
        }
    }

    public void setFileFilter(final hFileFilter[] filters, boolean allowDirectory) {
        for (hFileFilter filter : filters) {
            addFilter(filter, allowDirectory);
        }
    }

    private void addFilter(final hFileFilter filter, final boolean allowDirectory) {
        addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (allowDirectory && f.isDirectory()) {
                    return true;
                }
                return filter.checkFile(f);
            }

            @Override
            public String getDescription() {
                return filter.description;
            }
        });
    }

    /**
     * Filter that allows directories and filters files with
     * <i>fileFilterString</i>
     *
     * @param fileFilterString Should be like:<br/> <b>JPG Images(JPG,
     * JPEG)|jpg,jpeg;BMP Images|bmp;All Files|*</b><br/>
     */
    public void setFileFilter(String fileFilterString) {
        for (FileFilter ff : getChoosableFileFilters()) {
            removeChoosableFileFilter(ff);
        }
        String[] filters = fileFilterString.split(";");
        for (String filter : filters) {
            filter = filter.trim();
            if (filter.isEmpty()) {
                continue;
            }

            String[] filterParts = filter.split("\\|");
            if (filterParts.length != 2) {
                throw new RuntimeException("Filter part " + filter + " of the fileFilterString [" + fileFilterString + "] does not contain description and filter separated by ';' symbol");
            }

            String description = filterParts[0].trim();
            String extensionsList = filterParts[1].trim();
            String regexp = formatFileValidateRegexp(extensionsList);
            addFilter(new hFileFilter(description, regexp), true);
        }
    }

    private static String formatFileValidateRegexp(String extensionsList) {
        if (extensionsList.equals("*")) {
            return ".*";
        }
        String[] extensions = extensionsList.split("\\,");
        StringBuilder sb = new StringBuilder();
        sb.append(".*?\\.(?:");
        boolean first = true;
        for (String ext : extensions) {
            if (!first) {
                sb.append("|");
            }
            sb.append("(?:");
            sb.append(ext.toLowerCase());
            sb.append(")");
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public void setFileFilter(hFileFilter[] filters) {
        setFileFilter(filters, true);
    }

    public static class hFileFilter {
        private String description;
        private final Pattern pattern;

        public hFileFilter(String description, String regExp) {
            this.description = description;
            pattern = Pattern.compile(regExp);
        }

        public String getDescription() {
            return description;
        }

        public boolean checkFile(File file) {
            return pattern.matcher(file.getName().toLowerCase()).matches();
        }
    };
}
