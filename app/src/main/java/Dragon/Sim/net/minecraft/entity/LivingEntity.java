package Dragon.Sim.net.minecraft.entity;

import java.util.*;

public abstract class LivingEntity extends Entity {

   protected LivingEntity() {
      super();
      this.recenterBoundingBox();
      this.rotationYaw = (float)(Math.random() * (double)((float)Math.PI * 2F));
   }

   public Random getRNG() {
      return this.rand;
   }
}
