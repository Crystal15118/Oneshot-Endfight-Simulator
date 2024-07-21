package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.pathfinding.Path;
import Dragon.Sim.net.minecraft.pathfinding.PathPoint;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nullable;

public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vector3d targetLocation;

   public TakeoffPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos blockpos = new BlockPos(0,this.dragon.getFightManager().getHeight(0,0),0);
         if (!blockpos.withinDistance(this.dragon.getPositionVec(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void initPhase() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int i = this.dragon.initPathPoints();
      Vector3d vector3d = this.dragon.getHeadLookVec(1.0F);
      int j = this.dragon.getNearestPpIdx(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);
      if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() > 0) {
         j = j % 12;
         if (j < 0) {
            j += 12;
         }
      } else {
         j = j - 12;
         j = j & 7;
         j = j + 12;
      }

      this.currentPath = this.dragon.findPath(i, j, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.incrementPathIndex();
         if (!this.currentPath.isFinished()) {
            Vector3i vector3i = this.currentPath.getCurrentPos();
            this.currentPath.incrementPathIndex();

            double d0;
            while(true) {
               d0 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0F);
               if (!(d0 < (double)vector3i.getY())) {
                  break;
               }
            }

            this.targetLocation = new Vector3d((double)vector3i.getX(), d0, (double)vector3i.getZ());
         }
      }

   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getType() {
      return PhaseType.TAKEOFF;
   }
}
