package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class ChargingPlayerPhase extends Phase {
   private Vector3d targetLocation;
   private int timeSinceCharge;

   public ChargingPlayerPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else {
         double d0 = this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
         if (d0 < 100.0D || d0 > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
            ++this.timeSinceCharge;
         }

      }
   }

   public void initPhase() {
      this.targetLocation = null;
      this.timeSinceCharge = 0;
   }

   public void setTarget(Vector3d p_188668_1_) {
      this.targetLocation = p_188668_1_;
   }

   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<ChargingPlayerPhase> getType() {
      return PhaseType.CHARGING_PLAYER;
   }
}
