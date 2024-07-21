package Dragon.Sim.net.minecraft.util.math;

import Dragon.Sim.net.minecraft.util.Util;

public class MathHelper {
   private static final float[] SIN_TABLE = Util.make(new float[65536], (p_203445_0_) -> {
      for(int i = 0; i < p_203445_0_.length; ++i) {
         p_203445_0_[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
      }

   });
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASINE_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public static float sin(float value) {
      return SIN_TABLE[(int)(value * 10430.378F) & '\uffff'];
   }

   public static float cos(float value) {
      return SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float value) {
      return (float)Math.sqrt((double)value);
   }

   public static float sqrt(double value) {
      return (float)Math.sqrt(value);
   }

   public static int floor(float value) {
      int i = (int)value;
      return value < (float)i ? i - 1 : i;
   }

   public static int floor(double value) {
      int i = (int)value;
      return value < (double)i ? i - 1 : i;
   }

   public static float abs(float value) {
      return Math.abs(value);
   }

   public static int clamp(int num, int min, int max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   public static double clamp(double num, double min, double max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   public static boolean epsilonEquals(double x, double y) {
      return Math.abs(y - x) < (double)1.0E-5F;
   }

   public static float wrapDegrees(float value) {
      float f = value % 360.0F;
      if (f >= 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public static double wrapDegrees(double value) {
      double d0 = value % 360.0D;
      if (d0 >= 180.0D) {
         d0 -= 360.0D;
      }

      if (d0 < -180.0D) {
         d0 += 360.0D;
      }

      return d0;
   }

   public static double atan2(double p_181159_0_, double p_181159_2_) {
      double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
      if (Double.isNaN(d0)) {
         return Double.NaN;
      } else {
         boolean flag = p_181159_0_ < 0.0D;
         if (flag) {
            p_181159_0_ = -p_181159_0_;
         }

         boolean flag1 = p_181159_2_ < 0.0D;
         if (flag1) {
            p_181159_2_ = -p_181159_2_;
         }

         boolean flag2 = p_181159_0_ > p_181159_2_;
         if (flag2) {
            double d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
         }

         double d9 = fastInvSqrt(d0);
         p_181159_2_ = p_181159_2_ * d9;
         p_181159_0_ = p_181159_0_ * d9;
         double d2 = FRAC_BIAS + p_181159_0_;
         int i = (int)Double.doubleToRawLongBits(d2);
         double d3 = ASINE_TAB[i];
         double d4 = COS_TAB[i];
         double d5 = d2 - FRAC_BIAS;
         double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
         double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
         double d8 = d3 + d7;
         if (flag2) {
            d8 = (Math.PI / 2D) - d8;
         }

         if (flag1) {
            d8 = Math.PI - d8;
         }

         if (flag) {
            d8 = -d8;
         }

         return d8;
      }
   }

   public static double fastInvSqrt(double number) {
      double d0 = 0.5D * number;
      long i = Double.doubleToRawLongBits(number);
      i = 6910469410427058090L - (i >> 1);
      number = Double.longBitsToDouble(i);
      return number * (1.5D - d0 * number * number);
   }

   public static float lerp(float pct, float start, float end) {
      return start + pct * (end - start);
   }

   public static double lerp(double pct, double start, double end) {
      return start + pct * (end - start);
   }

   @Deprecated
   public static float rotLerp(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
      float f;
      for(f = p_226167_1_ - p_226167_0_; f < -180.0F; f += 360.0F) {
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_226167_0_ + p_226167_2_ * f;
   }

   static {
      for(int i = 0; i < 257; ++i) {
         double d0 = (double)i / 256.0D;
         double d1 = Math.asin(d0);
         COS_TAB[i] = Math.cos(d1);
         ASINE_TAB[i] = d1;
      }

   }
}
