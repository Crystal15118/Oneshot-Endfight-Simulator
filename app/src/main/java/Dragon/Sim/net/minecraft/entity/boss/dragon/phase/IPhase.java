package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface IPhase {
   boolean getIsStationary();

   void serverTick();

   /*void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr);*/

   void initPhase();

   void removeAreaEffect();

   float getMaxRiseOrFall();

   float getYawFactor();

   PhaseType<? extends IPhase> getType();

   @Nullable
   Vector3d getTargetLocation();

   float func_221113_a(float p_221113_2_);
}
