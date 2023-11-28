package gov.noaa.pmel.excel2oap;

import java.nio.charset.Charset;

public class EncodingConversionTest {

  private static String udata = "\u201e \u20ac \u201a \u0192 \u2026 \u2020 \u2021 \u02c6 \u2030 \u0160  "
            + "\u2039 \u0152 \u017d"
            + "\u2018 \u2019 \u201c \u201d \u2022 \u2013 \u2014 \u02dc "
            + "\u2122 \u0161 \u203a \u0153 \u017e \u0178";
  private static String germanStr = "Entwickeln Sie mit Vergnügen";
  private static String winStr = "±0.1%";
  private static String csvStr = "76,DIC: Uncertainty,±2 umol/kg,22.14";
  private static String oadsStr = ">�2 umol/kg</";


    private void run(String data) throws Exception {
        System.out.println(" -------- " + data);
        
        String unicodeToWindows = new String(data.getBytes(), // "windows-1252"
                "windows-1252");
        System.out.println("unicode as windows-1252 " + unicodeToWindows);
        String windowsToUtf8 = new String(data.getBytes(), // "windows-1252"
                "utf-8");
        System.out.println("windows to utf-8 " + windowsToUtf8);

        String unicodeToUtf8 = new String(data.getBytes("utf-8"), "utf-8");
        System.out.println("unicode as utf-8 " + unicodeToUtf8);
        String utf8ToWindows = new String(data.getBytes("utf-8"),
                "windows-1252");
        System.out.println("utf-8 to windows " + utf8ToWindows);

        String charset = Charset.defaultCharset().displayName();
        System.out.println("default : " + charset);
        String unicodeToDefault = new String(data.getBytes(charset), charset);
        System.out.println("Default Charset " + unicodeToDefault);

        String utf8ToDefault = new String(data.getBytes("utf-8"), charset);
        System.out.println("Default Charset from UTF-8 " + utf8ToDefault);

        String windowsToDefault = new String(data.getBytes("windows-1252"),
                charset);
        System.out.println("Default Charset from Windows " + windowsToDefault);

    }

    public static void main(String[] args) throws Exception {
        EncodingConversionTest module = new EncodingConversionTest();
//        module.run(udata);
        module.run(germanStr);
        module.run(winStr);
        module.run(csvStr);
        module.run(oadsStr);
    }

}
