package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.Entity;
import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public abstract class Phase implements IPhase {
   protected final EnderDragonEntity dragon;

   public Phase(EnderDragonEntity dragonIn) {
      this.dragon = dragonIn;
   }

   public boolean getIsStationary() {
      return false;
   }

   public void serverTick() {
   }

   /*public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
   }*/

   public void initPhase() {
   }

   public void removeAreaEffect() {
   }

   public float getMaxRiseOrFall() {
      return 0.6F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return null;
   }

   public float func_221113_a(float p_221113_2_) {
      return p_221113_2_;
   }

   public float getYawFactor() {
      float f = MathHelper.sqrt(Entity.horizontalMag(this.dragon.getMotion())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return 0.7F / f1 / f;
   }
}
