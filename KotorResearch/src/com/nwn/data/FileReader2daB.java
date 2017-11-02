package com.nwn.data;

import com.nwn.data.twoda.Array2da;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FileReader2daB extends BaseReader {

    public Array2da loadFile(FileInputStream stream, String fileName) throws IOException {
        Array2da array = new Array2da();
        byte[] magicBuffer = new byte[8];
        stream.read(magicBuffer);
        String magic = new String(magicBuffer, "UTF8");
        if (!magic.equals("2DA V2.b")) {
            throw new IllegalArgumentException("File " + fileName + " is not proper 2daB file. Magic string is '" + magic + "'");
        }

        stream.read();//should be '\n'
        List<String> columns = readColumns(stream);
        array.setColumnNames(columns);
        int rowsCount = readInt(stream);
        int columnCount = columns.size();
        List<String> rowNames = readRowsNames(stream, rowsCount);

        int cellsCount = rowsCount * columns.size();
        short[] cellOffsets = new short[cellsCount + 1];
        for (int i = 0; i < cellsCount + 1; i++) {
            cellOffsets[i] = readShort(stream);
        }

        List<String[]> rows = new ArrayList<>();
        int index = 0;
        for (int j = 0; j < rowsCount; j++) {
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++, index++) {
                int length = cellOffsets[index + 1] - cellOffsets[index];
                String value = readString(stream, length);
                row[i] = value;
            }
            rows.add(row);
        }

        array.setRowData(rows);
        return array;
    }

    private List<String> readRowsNames(InputStream stream, int count) throws IOException {
        List<String> rowNames = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            rowNames.add(readTabEndedString(stream));
        }

        return rowNames;
    }

    private List<String> readColumns(InputStream stream) throws IOException {
        List<String> columns = new ArrayList<>();
        while (true) {
            String column = readTabEndedString(stream);
            if (column != null) {
                columns.add(column);
            } else {
                break;
            }
        }
        return columns;
    }
}
