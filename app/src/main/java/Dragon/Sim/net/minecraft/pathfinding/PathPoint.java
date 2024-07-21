package Dragon.Sim.net.minecraft.pathfinding;

import Dragon.Sim.net.minecraft.util.math.MathHelper;

public class PathPoint {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int index = -1;
   public float totalPathDistance;
   public float distanceToNext;
   public float distanceToTarget;
   public PathPoint previous;
   public boolean visited;

   public PathPoint(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.hash = makeHash(x, y, z);
   }

   public static int makeHash(int x, int y, int z) {
      return y & 255 | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? '\u8000' : 0);
   }

   public float distanceTo(PathPoint pathpointIn) {
      float f = (float)(pathpointIn.x - this.x);
      float f1 = (float)(pathpointIn.y - this.y);
      float f2 = (float)(pathpointIn.z - this.z);
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public float distanceToSquared(PathPoint pathpointIn) {
      float f = (float)(pathpointIn.x - this.x);
      float f1 = (float)(pathpointIn.y - this.y);
      float f2 = (float)(pathpointIn.z - this.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof PathPoint)) {
         return false;
      } else {
         PathPoint pathpoint = (PathPoint)p_equals_1_;
         return this.hash == pathpoint.hash && this.x == pathpoint.x && this.y == pathpoint.y && this.z == pathpoint.z;
      }
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }
}
