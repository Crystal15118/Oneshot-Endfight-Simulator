package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;

public abstract class SittingPhase extends Phase {
   public SittingPhase(EnderDragonEntity p_i46794_1_) {
      super(p_i46794_1_);
   }

   public boolean getIsStationary() {
      return true;
   }

   public float func_221113_a(float p_221113_2_) {
      return super.func_221113_a(p_221113_2_);
   }
}
