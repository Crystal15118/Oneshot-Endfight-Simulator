package Dragon.Sim.net.minecraft.entity.boss.dragon;

import Dragon.Sim.net.minecraft.entity.Entity;
import Dragon.Sim.net.minecraft.entity.EntitySize;

public class EnderDragonPartEntity extends Entity {
   public final EnderDragonEntity dragon;
   public final String field_213853_c;
   private final EntitySize field_213854_d;

   public EnderDragonPartEntity(EnderDragonEntity dragon, String p_i50232_2_, float width, float height) {
      super();
      this.field_213854_d = EntitySize.flexible(width, height);
      super.setSize(this.field_213854_d);
      this.dragon = dragon;
      this.field_213853_c = p_i50232_2_;
   }

   public boolean attackEntityFrom(float amount) {
      return this.dragon.func_213403_a(this, amount);
   }

   public EntitySize getSize() {
      if (this.field_213854_d == null){
         return super.getSize();
      }
      return this.field_213854_d;
   }
}
