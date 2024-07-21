package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.pathfinding.Path;
import Dragon.Sim.net.minecraft.pathfinding.PathPoint;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nullable;

public class LandingApproachPhase extends Phase {
   private Path currentPath;
   private Vector3d targetLocation;

   public LandingApproachPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public PhaseType<LandingApproachPhase> getType() {
      return PhaseType.LANDING_APPROACH;
   }

   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   public void serverTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
         this.findNewTarget();
      }

   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isFinished()) {
         int i = this.dragon.initPathPoints();
         BlockPos blockpos = new BlockPos(0,this.dragon.getFightManager().getHeight(0,0),0);
         int j;
         Vector3d vector3d;
         if (this.dragon.getPosZ() > 0)
            vector3d = (new Vector3d(0, 0.0D, -10)).normalize();
         else
            vector3d = (new Vector3d(-10, 0.0D, 10)).normalize();
         j = this.dragon.getNearestPpIdx(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);

         PathPoint pathpoint = new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
         this.currentPath = this.dragon.findPath(i, j, pathpoint);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }
      this.navigateToNextPathNode();
      if (this.currentPath != null && this.currentPath.isFinished()) {
         this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
      }

   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vector3i vector3i = this.currentPath.getCurrentPos();
         this.currentPath.incrementPathIndex();
         double d0 = (double)vector3i.getX();
         double d1 = (double)vector3i.getZ();

         double d2;
         while(true) {
            d2 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0F);
            if (!(d2 < (double)vector3i.getY())) {
               break;
            }
         }

         this.targetLocation = new Vector3d(d0, d2, d1);
      }
   }
}
