package Dragon.Sim.net.minecraft.entity.boss.dragon;

import Dragon.Sim.net.minecraft.entity.*;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.IPhase;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.PhaseManager;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.PhaseType;
import Dragon.Sim.net.minecraft.entity.monster.IMob;
import Dragon.Sim.net.minecraft.pathfinding.Path;
import Dragon.Sim.net.minecraft.pathfinding.PathHeap;
import Dragon.Sim.net.minecraft.pathfinding.PathPoint;
import Dragon.Sim.net.minecraft.util.math.AxisAlignedBB;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3d;
import Dragon.Sim.net.minecraft.world.end.DragonFightManager;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class EnderDragonEntity extends MobEntity implements IMob {
   public float[] targetHeights = new float[10];
   public int nodes = 0;
   public final double[][] ringBuffer = new double[64][3];
   public int ringBufferIndex = -1;
   private final EnderDragonPartEntity[] dragonParts;
   public final EnderDragonPartEntity dragonPartHead;
   private final EnderDragonPartEntity dragonPartNeck;
   private final EnderDragonPartEntity dragonPartBody;
   //private final EnderDragonPartEntity dragonPartTail1;
   //private final EnderDragonPartEntity dragonPartTail2;
   //private final EnderDragonPartEntity dragonPartTail3;
   //private final EnderDragonPartEntity dragonPartRightWing;
   //private final EnderDragonPartEntity dragonPartLeftWing;
   public float rotationFactor;
   private DragonFightManager fightManager;
   private final PhaseManager phaseManager;
   private int sittingDamageReceived;
   private final PathHeap pathFindQueue = new PathHeap();
   public float health;
   private boolean slowed;
   public int ticksExisted;

   public EnderDragonEntity() {
      super();
      this.dragonPartHead = new EnderDragonPartEntity(this, "head", 1.0F, 1.0F);
      this.dragonPartNeck = new EnderDragonPartEntity(this, "neck", 3.0F, 3.0F);
      this.dragonPartBody = new EnderDragonPartEntity(this, "body", 5.0F, 3.0F);
      //this.dragonPartTail1 = new EnderDragonPartEntity(this, "tail");
      //this.dragonPartTail2 = new EnderDragonPartEntity(this, "tail");
      //this.dragonPartTail3 = new EnderDragonPartEntity(this, "tail");
      //this.dragonPartRightWing = new EnderDragonPartEntity(this, "wing");
      //this.dragonPartLeftWing = new EnderDragonPartEntity(this, "wing");
      this.dragonParts = new EnderDragonPartEntity[]{this.dragonPartHead, this.dragonPartNeck, this.dragonPartBody};
      health = 200.0F;
      this.ignoreFrustumCheck = true;

      this.phaseManager = new PhaseManager(this);
   }

   public void setFightManager(DragonFightManager fightManagerIn){
      this.fightManager = fightManagerIn;
   }

   public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {

      p_70974_2_ = 1.0F - p_70974_2_;
      int i = this.ringBufferIndex - p_70974_1_ & 63;
      int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
      double[] adouble = new double[3];
      double d0 = this.ringBuffer[i][0];
      double d1 = MathHelper.wrapDegrees(this.ringBuffer[j][0] - d0);
      adouble[0] = d0 + d1 * (double)p_70974_2_;
      d0 = this.ringBuffer[i][1];
      d1 = this.ringBuffer[j][1] - d0;
      adouble[1] = d0 + d1 * (double)p_70974_2_;
      adouble[2] = MathHelper.lerp((double)p_70974_2_, this.ringBuffer[i][2], this.ringBuffer[j][2]);
      return adouble;
   }

   public void livingTick() {
      ++ticksExisted;
      //System.out.println(this.getPosX() + " " + this.getPosY() + " " + this.getPosZ());

      this.updateDragonEnderCrystal();
      this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
      if (this.ringBufferIndex < 0) {
         for(int i = 0; i < this.ringBuffer.length; ++i) {
            this.ringBuffer[i][0] = (double)this.rotationYaw;
            this.ringBuffer[i][1] = this.getPosY();
         }
      }

      if (++this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
      this.ringBuffer[this.ringBufferIndex][1] = this.getPosY();
      IPhase iphase = this.phaseManager.getCurrentPhase();
      iphase.serverTick();
      if (this.phaseManager.getCurrentPhase() != iphase) {
         iphase = this.phaseManager.getCurrentPhase();
         iphase.serverTick();
      }

      Vector3d vector3d = iphase.getTargetLocation();
      if (vector3d != null) {
         double d8 = vector3d.x - this.getPosX();
         double d9 = vector3d.y - this.getPosY();
         double d10 = vector3d.z - this.getPosZ();
         double d3 = d8 * d8 + d9 * d9 + d10 * d10;
         float f6 = iphase.getMaxRiseOrFall();
         double d4 = (double)MathHelper.sqrt(d8 * d8 + d10 * d10);
         if (d4 > 0.0D) {
            d9 = MathHelper.clamp(d9 / d4, (double)(-f6), (double)f6);
         }

         this.setMotion(this.getMotion().add(0.0D, d9 * 0.01D, 0.0D));
         this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
         double d5 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d8, d10) * (double)(180F / (float)Math.PI) - (double)this.rotationYaw), -50.0D, 50.0D);
         //System.out.println(rotationYaw + " " + rotationFactor + " " + d5 + " " + d8 + " " + d10 + " " + iphase.getYawFactor() + " " + this.getPosX() + " " + this.getPosY() + " " + this.getPosZ());
         Vector3d vector3d1 = vector3d.subtract(this.getPosX(), this.getPosY(), this.getPosZ()).normalize();
         Vector3d vector3d2 = (new Vector3d((double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), this.getMotion().y, (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))))).normalize();
         float f8 = Math.max(((float)vector3d2.dotProduct(vector3d1) + 0.5F) / 1.5F, 0.0F);
         this.rotationFactor *= 0.8F;
         this.rotationFactor = (float)((double)this.rotationFactor + d5 * (double)iphase.getYawFactor());
         this.rotationYaw += this.rotationFactor * 0.1F;
         float f9 = (float)(2.0D / (d3 + 1.0D));
         this.moveRelative(0.06F * (f8 * f9 + (1.0F - f9)), new Vector3d(0.0D, 0.0D, -1.0D));
         if (this.slowed) {
            this.move(this.getMotion().scale((double)0.8F));
         } else {
            this.move(this.getMotion());
         }

         Vector3d vector3d3 = this.getMotion().normalize();
         double d6 = 0.8D + 0.15D * (vector3d3.dotProduct(vector3d2) + 1.0D) / 2.0D;
         this.setMotion(this.getMotion().mul(d6, (double)0.91F, d6));
      }
      Vector3d[] avector3d = new Vector3d[this.dragonParts.length];

      //for(int j = 0; j < this.dragonParts.length; ++j) {
      //   avector3d[j] = new Vector3d(this.dragonParts[j].getPosX(), this.dragonParts[j].getPosY(), this.dragonParts[j].getPosZ());
      //}

      float f15 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float)Math.PI / 180F);
      float f16 = MathHelper.cos(f15);
      float f2 = MathHelper.sin(f15);
      float f17 = this.rotationYaw * ((float)Math.PI / 180F);
      float f3 = MathHelper.sin(f17);
      float f18 = MathHelper.cos(f17);
      this.func_226526_a_(this.dragonPartBody, (double)(f3 * 0.5F), 0.0D, (double)(-f18 * 0.5F));
      //this.func_226526_a_(this.dragonPartRightWing, (double)(f18 * 4.5F), 2.0D, (double)(f3 * 4.5F));
      //this.func_226526_a_(this.dragonPartLeftWing, (double)(f18 * -4.5F), 2.0D, (double)(f3 * -4.5F));

      float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F) - this.rotationFactor * 0.01F);
      float f19 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F) - this.rotationFactor * 0.01F);
      float f5 = this.func_226527_er_();
      this.func_226526_a_(this.dragonPartHead, (double)(f4 * 6.5F * f16), (double)(f5 + f2 * 6.5F), (double)(-f19 * 6.5F * f16));
      this.func_226526_a_(this.dragonPartNeck, (double)(f4 * 5.5F * f16), (double)(f5 + f2 * 5.5F), (double)(-f19 * 5.5F * f16));
      //double[] adouble = this.getMovementOffsets(5, 1.0F);

      //for(int k = 0; k < 3; ++k) {
         //EnderDragonPartEntity enderdragonpartentity = null;
         //if (k == 0) {
         //   enderdragonpartentity = this.dragonPartTail1;
         //}

         //if (k == 1) {
         //   enderdragonpartentity = this.dragonPartTail2;
         //}

         //if (k == 2) {
         //   enderdragonpartentity = this.dragonPartTail3;
         //}

         //double[] adouble1 = this.getMovementOffsets(12 + k * 2, 1.0F);
         //float f7 = this.rotationYaw * ((float)Math.PI / 180F) + this.simplifyAngle(adouble1[0] - adouble[0]) * ((float)Math.PI / 180F);
         //float f20 = MathHelper.sin(f7);
         //float f21 = MathHelper.cos(f7);
         //float f23 = (float)(k + 1) * 2.0F;
         //this.func_226526_a_(enderdragonpartentity, (double)(-(f3 * 1.5F + f20 * f23) * f16), adouble1[1] - adouble[1] - (double)((f23 + 1.5F) * f2) + 1.5D, (double)((f18 * 1.5F + f21 * f23) * f16));
      //}

      if (this.getPosY() > 104 || (Math.pow(this.getPosX(),2)+Math.pow(this.getPosZ(),2) < 625 && this.getPosY() > 70)){
         this.slowed = false;
      }
      else {
         this.slowed = this.destroyBlocksInAABB(this.dragonPartNeck.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getBoundingBox());
      }

      //for(int l = 0; l < this.dragonParts.length; ++l) {
      //   this.dragonParts[l].prevPosX = avector3d[l].x;
      //   this.dragonParts[l].prevPosY = avector3d[l].y;
      //   this.dragonParts[l].prevPosZ = avector3d[l].z;
      //   this.dragonParts[l].lastTickPosX = avector3d[l].x;
      //   this.dragonParts[l].lastTickPosY = avector3d[l].y;
      //   this.dragonParts[l].lastTickPosZ = avector3d[l].z;
      //}
   }

   private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
      int i = MathHelper.floor(p_70972_1_.minX);
      int j = MathHelper.floor(p_70972_1_.minY);
      int k = MathHelper.floor(p_70972_1_.minZ);
      int l = MathHelper.floor(p_70972_1_.maxX);
      int i1 = MathHelper.floor(p_70972_1_.maxY);
      int j1 = MathHelper.floor(p_70972_1_.maxZ);
      boolean flag = false;

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = j; l1 <= i1; ++l1) {
            for(int i2 = k; i2 <= j1; ++i2) {
               BlockPos blockpos = new BlockPos(k1, l1, i2);
               Block block = DragonFightManager.world.get(DragonFightManager.preHash(blockpos));
               if (block!=null && !block.equals(Blocks.AIR)) {
                  if (!block.equals(Blocks.OBSIDIAN) && !block.equals(Blocks.BEDROCK)) {
                     DragonFightManager.world.remove(DragonFightManager.preHash(blockpos));
                  } else {
                     //System.out.println("Obsidian at: " + blockpos.getX() + " " + blockpos.getY() + " " + blockpos.getZ());
                     //System.out.println("Slow: " + (p_70972_1_.maxX-p_70972_1_.minX));
                     flag = true;
                  }
               }
               else {
                  //System.out.println("Air at: " + blockpos.getX() + " " + blockpos.getY() + " " + blockpos.getZ());
               }
            }
         }
      }

      return flag;
   }

   private void func_226526_a_(EnderDragonPartEntity p_226526_1_, double p_226526_2_, double p_226526_4_, double p_226526_6_) {
      p_226526_1_.setPosition(this.getPosX() + p_226526_2_, this.getPosY() + p_226526_4_, this.getPosZ() + p_226526_6_);
   }

   private float func_226527_er_() {
      if (this.phaseManager.getCurrentPhase().getIsStationary()) {
         return -1.0F;
      } else {
         double[] adouble = this.getMovementOffsets(5, 1.0F);
         double[] adouble1 = this.getMovementOffsets(0, 1.0F);
         return (float)(adouble[1] - adouble1[1]);
      }
   }

   private float simplifyAngle(double p_70973_1_) {
      return (float)MathHelper.wrapDegrees(p_70973_1_);
   }

   public boolean func_213403_a(EnderDragonPartEntity p_213403_1_, float p_213403_3_) {
      if (this.phaseManager.getCurrentPhase().getType() == PhaseType.DYING) {
         return false;
      } else {
         p_213403_3_ = this.phaseManager.getCurrentPhase().func_221113_a(p_213403_3_);
         //if (p_213403_1_ != this.dragonPartHead) {
         //   p_213403_3_ = p_213403_3_ / 4.0F + Math.min(p_213403_3_, 1.0F);
         //}

         if (p_213403_3_ < 0.01F) {
            return false;
         } else {
            float f = health;
            this.attackDragonFrom(p_213403_3_);
            if (health<0 && !this.phaseManager.getCurrentPhase().getIsStationary()) {
               health = 1;
               this.phaseManager.setPhase(PhaseType.DYING);
            }

            if (this.phaseManager.getCurrentPhase().getIsStationary()) {
               this.sittingDamageReceived = (int)((float)this.sittingDamageReceived + (f - health));
               if ((float)this.sittingDamageReceived > 0.25F * 200.0F) {
                  this.sittingDamageReceived = 0;
                  this.phaseManager.setPhase(PhaseType.TAKEOFF);
               }
            }

            return true;
         }
      }
   }

   private void updateDragonEnderCrystal() {
         if (this.ticksExisted % 10 == 0 && health < 200.0D) {
            health += 1.0F;
         }
   }

   public boolean attackEntityFrom(float amount) {
      return false;
   }

   protected boolean attackDragonFrom(float amount) {
      return super.attackEntityFrom(amount);
   }

   public int initPathPoints() {
      return this.getNearestPpIdx(this.getPosX(), this.getPosY(), this.getPosZ());
   }

   public int getNearestPpIdx(double x, double y, double z) {
      float f = 10000.0F;
      int i = 0;
      PathPoint pathpoint = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
      int j = 0;
      if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
         j = 12;
      }

      for(int k = j; k < 24; ++k) {
         if (this.getFightManager().pathPoints[k] != null) {
            float f1 = this.getFightManager().pathPoints[k].distanceToSquared(pathpoint);
            if (f1 < f) {
               f = f1;
               i = k;
            }
         }
      }

      return i;
   }

   public Path findPath(int startIdx, int finishIdx, PathPoint andThen) {
      for(int i = 0; i < 24; ++i) {
         PathPoint pathpoint = this.getFightManager().pathPoints[i];
         pathpoint.visited = false;
         pathpoint.distanceToTarget = 0.0F;
         pathpoint.totalPathDistance = 0.0F;
         pathpoint.distanceToNext = 0.0F;
         pathpoint.previous = null;
         pathpoint.index = -1;
      }

      PathPoint startNode = this.getFightManager().pathPoints[startIdx];
      PathPoint targetNode = this.getFightManager().pathPoints[finishIdx];
      startNode.totalPathDistance = 0.0F;
      startNode.distanceToNext = startNode.distanceTo(targetNode);
      startNode.distanceToTarget = startNode.distanceToNext;
      this.pathFindQueue.clearPath();
      this.pathFindQueue.addPoint(startNode);
      PathPoint furthestNodeChosen = startNode;
      int firstEnabledIdx = 0;
      if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
         firstEnabledIdx = 12;
      }

      while(!this.pathFindQueue.isPathEmpty()) {
         PathPoint nodeBeingTried = this.pathFindQueue.dequeue();
         if (nodeBeingTried.equals(targetNode)) {
            if (andThen != null) {
               andThen.previous = targetNode;
               targetNode = andThen;
            }
            return this.makePath(startNode, targetNode);
         }

         if (nodeBeingTried.distanceTo(targetNode) < furthestNodeChosen.distanceTo(targetNode)) {
            furthestNodeChosen = nodeBeingTried;
         }

         nodeBeingTried.visited = true;
         int nodeBeingTriedIdx = 0;

         for(int idxToTry = 0; idxToTry < 24; ++idxToTry) {
            if (this.getFightManager().pathPoints[idxToTry] == nodeBeingTried) {
               nodeBeingTriedIdx = idxToTry;
               break;
            }
         }

         for(int idxToTry = firstEnabledIdx; idxToTry < 24; ++idxToTry) {
            if ((this.getFightManager().neighbors[nodeBeingTriedIdx] & 1 << idxToTry) > 0) {
               PathPoint nodeToTry = this.getFightManager().pathPoints[idxToTry];
               if (!nodeToTry.visited) {
                  float f = nodeBeingTried.totalPathDistance + nodeBeingTried.distanceTo(nodeToTry);
                  if (!nodeToTry.isAssigned() || f < nodeToTry.totalPathDistance) {
                     nodeToTry.previous = nodeBeingTried;
                     nodeToTry.totalPathDistance = f;
                     nodeToTry.distanceToNext = nodeToTry.distanceTo(targetNode);
                     if (nodeToTry.isAssigned()) {
                        this.pathFindQueue.changeDistance(nodeToTry, nodeToTry.totalPathDistance + nodeToTry.distanceToNext);
                     } else {
                        nodeToTry.distanceToTarget = nodeToTry.totalPathDistance + nodeToTry.distanceToNext;
                        this.pathFindQueue.addPoint(nodeToTry);
                     }
                  }
               }
            }
         }
      }

      if (furthestNodeChosen == startNode) {
         return null;
      } else {
         if (andThen != null) {
            andThen.previous = furthestNodeChosen;
            furthestNodeChosen = andThen;
         }
         return this.makePath(startNode, furthestNodeChosen);
      }
   }

   private Path makePath(PathPoint start, PathPoint finish) {
      List<PathPoint> list = new ArrayList<>();
      PathPoint pathpoint = finish;
      list.add(0, finish);

      while(pathpoint.previous != null) {
         pathpoint = pathpoint.previous;
         list.add(0, pathpoint);
      }

      return new Path(list, new BlockPos(finish.x, finish.y, finish.z));
   }

   public Vector3d getHeadLookVec(float p_184665_1_) {
      IPhase iphase = this.phaseManager.getCurrentPhase();
      PhaseType<? extends IPhase> phasetype = iphase.getType();
      Vector3d vector3d;
      if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
         if (iphase.getIsStationary()) {
            float f4 = this.rotationPitch;
            float f5 = 1.5F;
            this.rotationPitch = -45.0F;
            vector3d = this.getLook(p_184665_1_);
            this.rotationPitch = f4;
         } else {
            vector3d = this.getLook(p_184665_1_);
         }
      } else {
         BlockPos blockpos = new BlockPos(0,this.getFightManager().getHeight(0,0),0);
         float f = Math.max(MathHelper.sqrt(blockpos.distanceSq(this.getPositionVec(), true)) / 4.0F, 1.0F);
         float f1 = 6.0F / f;
         float f2 = this.rotationPitch;
         float f3 = 1.5F;
         this.rotationPitch = -f1 * 1.5F * 5.0F;
         vector3d = this.getLook(p_184665_1_);
         this.rotationPitch = f2;
      }

      return vector3d;
   }

   /*public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc) {
      if (dmgSrc.getTrueSource() instanceof PlayerEntity) {
         playerentity = (PlayerEntity)dmgSrc.getTrueSource();
      } else {
         playerentity = this.world.getClosestPlayer(field_213405_bO, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
      }

      if (crystal == this.closestEnderCrystal) {
         this.func_213403_a(this.dragonPartHead, DamageSource.causeExplosionDamage(playerentity), 10.0F);
      }

      this.phaseManager.getCurrentPhase().onCrystalDestroyed(crystal, pos, dmgSrc, playerentity);
   }*/

   public PhaseManager getPhaseManager() {
      return this.phaseManager;
   }

   public DragonFightManager getFightManager() {
      return this.fightManager;
   }
}
