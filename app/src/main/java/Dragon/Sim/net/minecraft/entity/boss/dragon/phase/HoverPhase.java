package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;


public class HoverPhase extends Phase {
   private Vector3d targetLocation;

   public HoverPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = this.dragon.getPositionVec();
      }

   }

   public boolean getIsStationary() {
      return true;
   }

   public void initPhase() {
      this.targetLocation = null;
   }

   public float getMaxRiseOrFall() {
      return 1.0F;
   }

   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<HoverPhase> getType() {
      return PhaseType.HOVER;
   }
}
