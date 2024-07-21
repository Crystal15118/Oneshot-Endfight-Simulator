package Dragon.Sim.net.minecraft.util.math.vector;

import Dragon.Sim.net.minecraft.dispenser.IPosition;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Vector3i implements Comparable<Vector3i> {
   private int x;
   private int y;
   private int z;

   public Vector3i(int xIn, int yIn, int zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   protected void setX(int xIn) {
      this.x = xIn;
   }

   protected void setY(int yIn) {
      this.y = yIn;
   }

   protected void setZ(int zIn) {
      this.z = zIn;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vector3i)) {
         return false;
      } else {
         Vector3i vector3i = (Vector3i)p_equals_1_;
         if (this.getX() != vector3i.getX()) {
            return false;
         } else if (this.getY() != vector3i.getY()) {
            return false;
         } else {
            return this.getZ() == vector3i.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vector3i p_compareTo_1_) {
      if (this.getY() == p_compareTo_1_.getY()) {
         return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
      } else {
         return this.getY() - p_compareTo_1_.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public boolean withinDistance(IPosition p_218137_1_, double distance) {
      return this.distanceSq(p_218137_1_.getX(), p_218137_1_.getY(), p_218137_1_.getZ(), true) < distance * distance;
   }

   public double distanceSq(IPosition p_218138_1_, boolean useCenter) {
      return this.distanceSq(p_218138_1_.getX(), p_218138_1_.getY(), p_218138_1_.getZ(), useCenter);
   }

   public double distanceSq(double p_218140_1_, double p_218140_3_, double p_218140_5_, boolean useCenter) {
      double d0 = useCenter ? 0.5D : 0.0D;
      double d1 = (double)this.getX() + d0 - p_218140_1_;
      double d2 = (double)this.getY() + d0 - p_218140_3_;
      double d3 = (double)this.getZ() + d0 - p_218140_5_;
      return d1 * d1 + d2 * d2 + d3 * d3;
   }
}
