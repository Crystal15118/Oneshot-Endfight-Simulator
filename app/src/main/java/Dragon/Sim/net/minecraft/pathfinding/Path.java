package Dragon.Sim.net.minecraft.pathfinding;

import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3i;

import java.util.List;

public class Path {
   private final List<PathPoint> points;
   private int currentPathIndex;
   private final BlockPos target;

   public Path(List<PathPoint> p_i51804_1_, BlockPos p_i51804_2_) {
      this.points = p_i51804_1_;
      this.target = p_i51804_2_;
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public int getCurrentPathLength() {
      return this.points.size();
   }

   public void incrementPathIndex() {
      ++this.currentPathIndex;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.points.size();
   }

   public Vector3i getCurrentPos() {
      PathPoint pathpoint = this.func_237225_h_();
      return new Vector3i(pathpoint.x, pathpoint.y, pathpoint.z);
   }

   public PathPoint func_237225_h_() {
      return this.points.get(this.currentPathIndex);
   }
}
