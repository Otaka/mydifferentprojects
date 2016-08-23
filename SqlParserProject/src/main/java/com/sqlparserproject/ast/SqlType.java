package com.sqlparserproject.ast;

/**
 * @author sad
 */
public class SqlType {

    public static class IntType extends SqlType {

        @Override
        public String toString() {
            return "int";
        }

    }

    public static class VarbinaryType extends SqlType {

        @Override
        public String toString() {
            return "varbinary";
        }

    }

    public static class VarcharType extends SqlType {

        private Integer size;

        public VarcharType(Integer size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "varchar(" + size + ")";
        }

    }

    public static class NumericType extends SqlType {

        private Integer size1;
        private Integer size2;

        public NumericType(Integer size1, Integer size2) {
            this.size1 = size1;
            this.size2 = size2;
        }

        public Integer getSize1() {
            return size1;
        }

        public Integer getSize2() {
            return size2;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("numeric(");
            if (size2 != null) {
                sb.append(size1).append(",").append(size2);
            } else {
                sb.append(size1);
            }
            sb.append(")");
            return sb.toString();

        }

    }
}
