package com.nwn.data;

import com.nwn.data.gff.GffStructure;
import com.nwn.data.gff.GffFieldType;
import com.nwn.data.gff.Gff;
import com.nwn.data.gff.GffLoadContext;
import com.nwn.data.gff.fields.GffField;
import com.nwn.data.gff.fields.GffFieldValue;
import java.io.*;

public class FileReaderGff extends BaseReader {

    public Gff loadFile(FileInputStream stream, String fileName) throws IOException {
        init(stream);

        String type = readString(stream, 4);
        String version = readString(stream, 4);

        int structOffset = readInt(stream);
        int structCount = readInt(stream);
        int fieldOffset = readInt(stream);
        int fieldCount = readInt(stream);
        int labelOffset = readInt(stream);
        int labelCount = readInt(stream);
        int fieldDataOffset = readInt(stream);
        int fieldDataCount = readInt(stream);
        int fieldIndiciesOffset = readInt(stream);
        int fieldIndiciesCount = readInt(stream) / 4;
        int listIndiciesOffset = readInt(stream);
        int listIndiciesCount = readInt(stream) / 4;

        setAbsolutePosition(stream, labelOffset);
        String[] labels = loadLabels(stream, labelCount);

        byte[] rawData = new byte[fieldDataCount];
        setAbsolutePosition(stream, fieldDataOffset);
        stream.read(rawData);
        NwnByteArrayInputStream rawDataStream = new NwnByteArrayInputStream(rawData);

        setAbsolutePosition(stream, fieldIndiciesOffset);
        int[] fieldIndicies = loadFieldIndicies(stream, fieldIndiciesCount);

        setAbsolutePosition(stream, listIndiciesOffset);
        int[] listIndicies = loadListIndicies(stream, listIndiciesCount);
        GffStructure[] structures = new GffStructure[structCount];
        for (int i = 0; i < structCount; i++) {
            structures[i] = new GffStructure();
        }

        GffLoadContext loadContext = new GffLoadContext(structures, fieldIndicies, listIndicies, labels, rawDataStream);
        setAbsolutePosition(stream, fieldOffset);
        loadFields(stream, fieldCount, loadContext);

        setAbsolutePosition(stream, structOffset);
        loadStructures(stream, structCount, loadContext);
        Gff gff = new Gff(type, version, loadContext.getStructs()[0]);
        return gff;
    }

    private void loadStructures(InputStream stream, int structCount, GffLoadContext loadContext) throws IOException {
        for (int i = 0; i < structCount; i++) {
            GffStructure structure = loadContext.getStructs()[i];
            int type = readInt(stream);
            int dataOrOffset = readInt(stream);
            int fieldsCount = readInt(stream);
            structure.setType(type);
            GffField[] fields;
            if (fieldsCount == 1) {
                fields = new GffField[]{loadContext.getFields()[dataOrOffset]};
            } else {
                int fieldIndiciesOffset = dataOrOffset / 4;
                fields = new GffField[fieldsCount];
                for (int j = 0; j < fieldsCount; j++) {
                    int index = loadContext.getFieldIndicies()[fieldIndiciesOffset + j];
                    GffField field = loadContext.getFields()[index];
                    field.toString();
                    fields[j] = field;
                }
            }

            structure.setFields(fields);
        }
    }

    private int[] loadFieldIndicies(InputStream stream, int indiciesCount) throws IOException {
        int[] result = new int[indiciesCount];
        for (int i = 0; i < indiciesCount; i++) {
            result[i] = readInt(stream);
        }
        return result;
    }

    private int[] loadListIndicies(InputStream stream, int indiciesCount) throws IOException {
        int[] rawList = new int[indiciesCount];
        for (int i = 0; i < indiciesCount; i++) {
            rawList[i] = readInt(stream);
        }

        int listCount = 0;
        for (int i = 0; i < indiciesCount; i++) {
            int n = rawList[i];
            i += n;
            listCount++;
        }

        int[] list = new int[listCount];

        return rawList;
    }

    private void loadFields(FileInputStream stream, int fieldsCount, GffLoadContext gffLoadContext) throws IOException {
        GffField[] fields = new GffField[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = new GffField();
        }

        gffLoadContext.setFields(fields);
        for (int i = 0; i < fieldsCount; i++) {
            int dataType = readInt(stream);
            int labelIndex = readInt(stream);
            String label = gffLoadContext.getLabels()[labelIndex];
            int dataOrOffset = readInt(stream);

            GffField field = fields[i];
            field.setLabel(label);
            GffFieldValue value = GffFieldType.getByType(dataType).loadGffFieldValue(gffLoadContext, dataOrOffset);
            field.setValue(value);
        }
    }

    private String[] loadLabels(FileInputStream stream, int labelsCount) throws IOException {
        String[] labels = new String[labelsCount];
        for (int i = 0; i < labelsCount; i++) {
            String label = readString(stream, 16);
            labels[i] = label;
        }
        return labels;
    }
}
