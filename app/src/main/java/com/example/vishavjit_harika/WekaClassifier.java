package com.example.vishavjit_harika;

class WekaClassifier {
    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N259f42000(i);
        return p;
    }
    static double N259f42000(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 13.390311) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 13.390311) {
            p = WekaClassifier.N6cfb97a81(i);
        }
        return p;
    }
    static double N6cfb97a81(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 14.534508) {
            p = WekaClassifier.N1f770cf2(i);
        } else if (((Double) i[64]).doubleValue() > 14.534508) {
            p = 2;
        }
        return p;
    }
    static double N1f770cf2(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 14.034383) {
            p = WekaClassifier.N32ff42d93(i);
        } else if (((Double) i[4]).doubleValue() > 14.034383) {
            p = 1;
        }
        return p;
    }
    static double N32ff42d93(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 4.804712) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 4.804712) {
            p = 2;
        }
        return p;
    }
}

