package Dragon.Sim.net.minecraft.entity;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.util.math.AxisAlignedBB;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;

import java.util.*;

public abstract class Entity  {
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   private Vector3d field_233557_ao_;
   private BlockPos field_233558_ap_;
   private Vector3d motion = Vector3d.ZERO;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   private AxisAlignedBB boundingBox = ZERO_AABB;
   public boolean collidedHorizontally;
   public boolean collidedVertically;
   public boolean velocityChanged;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   protected final Random rand = new Random();
   public boolean ignoreFrustumCheck;
   private EntitySize size;

   public Entity() {
      this.size = this.getSize();
      this.field_233557_ao_ = Vector3d.ZERO;
      this.field_233558_ap_ = BlockPos.ZERO;
      this.setPosition(0.0D, 0.0D, 0.0D);
   }

   protected void setSize(EntitySize sizeIn) {
      this.size = sizeIn;
   }

   public EntitySize getSize() {
      return EntitySize.flexible(16.0F, 8.0F);
   }

   public void setPosition(double x, double y, double z) {
      this.setRawPosition(x, y, z);
      float f = this.size.width / 2.0F;
      float f1 = this.size.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   protected void recenterBoundingBox() {
      this.setPosition(this.field_233557_ao_.x, this.field_233557_ao_.y, this.field_233557_ao_.z);
   }

   public void move(Vector3d pos) {
      this.setBoundingBox(this.getBoundingBox().offset(pos));
      this.resetPositionToBB();
   }

   public static double horizontalMag(Vector3d vec) {
      return vec.x * vec.x + vec.z * vec.z;
   }

   public void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.setRawPosition((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
   }

   public void moveRelative(float p_213309_1_, Vector3d relative) {
      Vector3d vector3d = getAbsoluteMotion(relative, p_213309_1_, this.rotationYaw);
      this.setMotion(this.getMotion().add(vector3d));
   }

   private static Vector3d getAbsoluteMotion(Vector3d relative, float p_213299_1_, float facing) {
      double d0 = relative.lengthSquared();
      if (d0 < 1.0E-7D) {
         return Vector3d.ZERO;
      } else {
         Vector3d vector3d = (d0 > 1.0D ? relative.normalize() : relative).scale((double)p_213299_1_);
         float f = MathHelper.sin(facing * ((float)Math.PI / 180F));
         float f1 = MathHelper.cos(facing * ((float)Math.PI / 180F));
         return new Vector3d(vector3d.x * (double)f1 - vector3d.z * (double)f, vector3d.y, vector3d.z * (double)f1 + vector3d.x * (double)f);
      }
   }

   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.forceSetPosition(x, y, z);
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
      this.recenterBoundingBox();
   }

   public void forceSetPosition(double x, double y, double z) {
      this.setRawPosition(x, y, z);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.lastTickPosX = x;
      this.lastTickPosY = y;
      this.lastTickPosZ = z;
   }

   public double getDistanceSq(Entity entityIn) {
      return this.getDistanceSq(entityIn.getPositionVec());
   }

   public double getDistanceSq(Vector3d p_195048_1_) {
      double d0 = this.getPosX() - p_195048_1_.x;
      double d1 = this.getPosY() - p_195048_1_.y;
      double d2 = this.getPosZ() - p_195048_1_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   protected void markVelocityChanged() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(float amount) {
      this.markVelocityChanged();
      return false;
   }

   public final Vector3d getLook(float partialTicks) {
      return this.getVectorForRotation(this.getPitch(partialTicks), this.getYaw(partialTicks));
   }

   public float getPitch(float partialTicks) {
      return partialTicks == 1.0F ? this.rotationPitch : MathHelper.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);
   }

   public float getYaw(float partialTicks) {
      return partialTicks == 1.0F ? this.rotationYaw : MathHelper.lerp(partialTicks, this.prevRotationYaw, this.rotationYaw);
   }

   protected final Vector3d getVectorForRotation(float pitch, float yaw) {
      float f = pitch * ((float)Math.PI / 180F);
      float f1 = -yaw * ((float)Math.PI / 180F);
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vector3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }

   public Vector3d getPositionVec() {
      return this.field_233557_ao_;
   }

   public Vector3d getMotion() {
      return this.motion;
   }

   public void setMotion(Vector3d motionIn) {
      this.motion = motionIn;
   }

   public final double getPosX() {
      return this.field_233557_ao_.x;
   }

   public final double getPosY() {
      return this.field_233557_ao_.y;
   }

   public final double getPosZ() {
      return this.field_233557_ao_.z;
   }

   public void setRawPosition(double x, double y, double z) {
      if (this.field_233557_ao_.x != x || this.field_233557_ao_.y != y || this.field_233557_ao_.z != z) {
         this.field_233557_ao_ = new Vector3d(x, y, z);
         int i = MathHelper.floor(x);
         int j = MathHelper.floor(y);
         int k = MathHelper.floor(z);
         if (i != this.field_233558_ap_.getX() || j != this.field_233558_ap_.getY() || k != this.field_233558_ap_.getZ()) {
            this.field_233558_ap_ = new BlockPos(i, j, k);
         }

      }

   }
}
