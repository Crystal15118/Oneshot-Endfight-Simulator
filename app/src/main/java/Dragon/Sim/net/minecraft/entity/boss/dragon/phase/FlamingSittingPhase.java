package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class FlamingSittingPhase extends SittingPhase {
   private int flameTicks;
   private int flameCount;

   public FlamingSittingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
         }
      }
   }

   public void initPhase() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public PhaseType<FlamingSittingPhase> getType() {
      return PhaseType.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
