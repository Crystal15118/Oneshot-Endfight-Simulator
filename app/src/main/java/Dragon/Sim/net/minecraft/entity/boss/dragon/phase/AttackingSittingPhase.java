package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class AttackingSittingPhase extends SittingPhase {
   private int attackingTicks;

   public AttackingSittingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (this.attackingTicks++ >= 40) {
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
      }
   }

   public void initPhase() {
      this.attackingTicks = 0;
   }

   public PhaseType<AttackingSittingPhase> getType() {
      return PhaseType.SITTING_ATTACKING;
   }
}
