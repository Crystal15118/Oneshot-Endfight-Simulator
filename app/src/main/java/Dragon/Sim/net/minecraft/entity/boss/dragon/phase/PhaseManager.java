package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseManager {
   private final EnderDragonEntity dragon;
   private final IPhase[] phases = new IPhase[PhaseType.getTotalPhases()];
   private IPhase phase;

   public PhaseManager(EnderDragonEntity dragonIn) {
      this.dragon = dragonIn;
      this.setPhase(PhaseType.HOVER);
   }

   public void setPhase(PhaseType<?> phaseIn) {
      if (this.phase == null || phaseIn != this.phase.getType()) {
         if (this.phase != null) {
            this.phase.removeAreaEffect();
         }

         this.phase = this.getPhase(phaseIn);

         this.phase.initPhase();
      }
   }

   public IPhase getCurrentPhase() {
      return this.phase;
   }

   public <T extends IPhase> T getPhase(PhaseType<T> phaseIn) {
      int i = phaseIn.getId();
      if (this.phases[i] == null) {
         this.phases[i] = phaseIn.createPhase(this.dragon);
      }

      return (T)this.phases[i];
   }
}
