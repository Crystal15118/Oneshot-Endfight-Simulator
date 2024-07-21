package Dragon.Sim.net.minecraft.entity.boss.dragon.phase;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class ScanningSittingPhase extends SittingPhase {
   private int scanningTime;

   public ScanningSittingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public void serverTick() {
      ++this.scanningTime;
      if (this.scanningTime >= 100)
         this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
      /*if (this.scanningTime > 25) {
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
      } else {
         Vector3d vector3d = (new Vector3d(livingentity.getPosX() - this.dragon.getPosX(), 0.0D, livingentity.getPosZ() - this.dragon.getPosZ())).normalize();
         Vector3d vector3d1 = (new Vector3d((double) MathHelper.sin(this.dragon.rotationYaw * ((float) Math.PI / 180F)), 0.0D, (double) (-MathHelper.cos(this.dragon.rotationYaw * ((float) Math.PI / 180F))))).normalize();
         float f = (float) vector3d1.dotProduct(vector3d);
         float f1 = (float) (Math.acos((double) f) * (double) (180F / (float) Math.PI)) + 0.5F;
         if (f1 < 0.0F || f1 > 10.0F) {
            double d0 = livingentity.getPosX() - this.dragon.dragonPartHead.getPosX();
            double d1 = livingentity.getPosZ() - this.dragon.dragonPartHead.getPosZ();
            double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d0, d1) * (double) (180F / (float) Math.PI) - (double) this.dragon.rotationYaw), -100.0D, 100.0D);
            this.dragon.rotationFactor *= 0.8F;
            float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0F;
            float f3 = f2;
            if (f2 > 40.0F) {
               f2 = 40.0F;
            }

            this.dragon.rotationFactor = (float) ((double) this.dragon.rotationFactor + d2 * (double) (0.7F / f2 / f3));
            this.dragon.rotationYaw += this.dragon.rotationFactor;
         }
      }*/
   }

   public void initPhase() {
      this.scanningTime = 0;
   }

   public PhaseType<ScanningSittingPhase> getType() {
      return PhaseType.SITTING_SCANNING;
   }
}
