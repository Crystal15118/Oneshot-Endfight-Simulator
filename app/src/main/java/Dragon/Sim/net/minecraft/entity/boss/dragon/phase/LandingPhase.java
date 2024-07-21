package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.entity.Entity;
import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class LandingPhase extends Phase {
   private Vector3d targetLocation;

   public LandingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = Vector3d.func_237492_c_(new BlockPos(0,this.dragon.getFightManager().getHeight(0,0),0));
      }

      if (this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ()) < 1.0D) {
         this.dragon.getPhaseManager().getPhase(PhaseType.SITTING_FLAMING).resetFlameCount();
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
      }

   }

   public float getMaxRiseOrFall() {
      return 1.5F;
   }

   public float getYawFactor() {
      float f = MathHelper.sqrt(Entity.horizontalMag(this.dragon.getMotion())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return f1 / f;
   }

   public void initPhase() {
      this.targetLocation = null;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<LandingPhase> getType() {
      return PhaseType.LANDING;
   }
}
