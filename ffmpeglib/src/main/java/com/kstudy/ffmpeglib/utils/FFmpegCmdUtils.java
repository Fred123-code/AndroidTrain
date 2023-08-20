package com.kstudy.ffmpeglib.utils;

public class FFmpegCmdUtils {
   private static String[] insert(String[] cmd, int position, String inputPath) {
      return insert(cmd, position, inputPath, null);
   }

   /**
    * insert inputPath and outputPath into target array
    */
   private static String[] insert(String[] cmd, int position, String inputPath, String outputPath) {
      if (cmd == null || inputPath == null || position < 2) {
         return cmd;
      }
      int len = (outputPath != null ? (cmd.length + 2) : (cmd.length + 1));
      String[] result = new String[len];
      System.arraycopy(cmd, 0, result, 0, position);
      result[position] = inputPath;
      System.arraycopy(cmd, position, result, position + 1, cmd.length - position);
      if (outputPath != null) {
         result[result.length - 1] = outputPath;
      }
      return result;
   }

   /***
    * probeFormat
    * @param inputPath
    * @return
    */
   public static String[] probeFormat(String inputPath) {
      String ffprobeCmd = "ffprobe -i -show_streams -show_format -print_format json";
      return insert(ffprobeCmd.split(" "), 2, inputPath);
   }

   public static String[] transCode(String s, String s1) {
      return new String[]{};
   }
}
