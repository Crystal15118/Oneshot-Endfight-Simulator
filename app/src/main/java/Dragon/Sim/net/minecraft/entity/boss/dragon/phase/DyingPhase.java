package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class DyingPhase extends Phase {
   private Vector3d targetLocation;

   public DyingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         BlockPos blockpos = new BlockPos(0,this.dragon.getFightManager().getHeight(0,0),0);
         this.targetLocation = Vector3d.func_237492_c_(blockpos);
      }

      double d0 = this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
      if (!(d0 < 100.0D) && !(d0 > 22500.0D) && !this.dragon.collidedHorizontally && !this.dragon.collidedVertically) {
         dragon.health = 1.0F;
      } else {
         dragon.health = 0.0F;
      }

   }

   public void initPhase() {
      this.targetLocation = null;
   }

   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<DyingPhase> getType() {
      return PhaseType.DYING;
   }
}
