package Dragon.Sim.net.minecraft.util.math.vector;

import Dragon.Sim.net.minecraft.dispenser.IPosition;
import Dragon.Sim.net.minecraft.util.math.MathHelper;

public class Vector3d implements IPosition {
   public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);
   public final double x;
   public final double y;
   public final double z;

   public static Vector3d func_237492_c_(Vector3i p_237492_0_) {
      return new Vector3d((double)p_237492_0_.getX() + 0.5D, (double)p_237492_0_.getY(), (double)p_237492_0_.getZ() + 0.5D);
   }

   public Vector3d(double xIn, double yIn, double zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   public Vector3d(Vector3f p_i225900_1_) {
      this((double)p_i225900_1_.getX(), (double)p_i225900_1_.getY(), (double)p_i225900_1_.getZ());
   }

   public Vector3d normalize() {
      double d0 = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return d0 < 1.0E-4D ? ZERO : new Vector3d(this.x / d0, this.y / d0, this.z / d0);
   }

   public double dotProduct(Vector3d vec) {
      return this.x * vec.x + this.y * vec.y + this.z * vec.z;
   }

   public Vector3d subtract(double x, double y, double z) {
      return this.add(-x, -y, -z);
   }

   public Vector3d add(Vector3d vec) {
      return this.add(vec.x, vec.y, vec.z);
   }

   public Vector3d add(double x, double y, double z) {
      return new Vector3d(this.x + x, this.y + y, this.z + z);
   }

   public double squareDistanceTo(double xIn, double yIn, double zIn) {
      double d0 = xIn - this.x;
      double d1 = yIn - this.y;
      double d2 = zIn - this.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public Vector3d scale(double factor) {
      return this.mul(factor, factor, factor);
   }

   public Vector3d mul(Vector3d p_216369_1_) {
      return this.mul(p_216369_1_.x, p_216369_1_.y, p_216369_1_.z);
   }

   public Vector3d mul(double factorX, double factorY, double factorZ) {
      return new Vector3d(this.x * factorX, this.y * factorY, this.z * factorZ);
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vector3d)) {
         return false;
      } else {
         Vector3d vector3d = (Vector3d)p_equals_1_;
         if (Double.compare(vector3d.x, this.x) != 0) {
            return false;
         } else if (Double.compare(vector3d.y, this.y) != 0) {
            return false;
         } else {
            return Double.compare(vector3d.z, this.z) == 0;
         }
      }
   }

   public int hashCode() {
      long j = Double.doubleToLongBits(this.x);
      int i = (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.y);
      i = 31 * i + (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.z);
      return 31 * i + (int)(j ^ j >>> 32);
   }

   public final double getX() {
      return this.x;
   }

   public final double getY() {
      return this.y;
   }

   public final double getZ() {
      return this.z;
   }
}
