package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.pathfinding.Path;
import Dragon.Sim.net.minecraft.pathfinding.PathPoint;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nullable;

public class HoldingPatternPhase extends Phase {
   private Path currentPath;
   private Vector3d targetLocation;
   private boolean clockwise;
   private float height = 20.0F;

   public HoldingPatternPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public PhaseType<HoldingPatternPhase> getType() {
      return PhaseType.HOLDING_PATTERN;
   }

   public void serverTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
         this.findNewTarget();
      }

   }

   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath != null && this.currentPath.isFinished()) {
         this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
         return;

         /*int i = this.dragon.getFightManager() == null ? 0 : this.dragon.getFightManager().getNumAliveCrystals();

         if (this.dragon.getRNG().nextInt(i + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            return;
         }*/

         /*double d0 = 64.0D;
         BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
         d0 = blockpos.distanceSq(playerentity.getPositionVec(), true) / 512.0D;

         if ((this.dragon.getRNG().nextInt(MathHelper.abs((int)d0) + 2) == 0 || this.dragon.getRNG().nextInt(i + 2) == 0)) {
            this.strafePlayer(playerentity);
            return;
         }*/ //Strafe unsupported
      }

      if (this.currentPath == null || this.currentPath.isFinished()) {
         int j = this.dragon.initPathPoints();
         int k = j;
         /*if (this.dragon.getRNG().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            k = j + 6;
         }*/
         if (this.clockwise) {
            ++k;
         } else {
            --k;
         }

         if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() >= 0) {
            k = k % 12;
            if (k < 0) {
               k += 12;
            }
         } else {
            k = k - 12;
            k = k & 7;
            k = k + 12;
         }
         this.currentPath = this.dragon.findPath(j, k, (PathPoint)null);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }
      this.navigateToNextPathNode();
   }

   /*private void strafePlayer(PlayerEntity player) {
      this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
      this.dragon.getPhaseManager().getPhase(PhaseType.STRAFE_PLAYER).setTarget(player);
   }*/

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vector3i vector3i = this.currentPath.getCurrentPos();
         this.currentPath.incrementPathIndex();
         double d0 = (double)vector3i.getX();
         double d1 = (double)vector3i.getZ();
         //double d2 = (double)((float)vector3i.getY() + this.dragon.targetHeights[this.dragon.nodes]);
         double d2 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0F);//((float)vector3i.getY() + height);
         if (this.dragon.nodes < this.dragon.targetHeights.length) {
            this.dragon.targetHeights[this.dragon.nodes] = (float) (d2-(float)vector3i.getY());
         }
         ++this.dragon.nodes;
         this.targetLocation = new Vector3d(d0, d2, d1);
      }

   }

   /*public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
      if (plyr != null && !plyr.abilities.disableDamage) {
         this.strafePlayer(plyr);
      }

   }*/
}
