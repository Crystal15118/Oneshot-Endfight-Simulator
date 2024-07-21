package Dragon.Sim.net.minecraft.util.math.vector;

public final class Vector3f {
   private float x;
   private float y;
   private float z;

   public Vector3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector3f vector3f = (Vector3f)p_equals_1_;
         if (Float.compare(vector3f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector3f.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(vector3f.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      return 31 * i + Float.floatToIntBits(this.z);
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }
}
