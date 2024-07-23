package Dragon.Sim.net.minecraft.world.end;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.PhaseType;
import Dragon.Sim.net.minecraft.pathfinding.PathPoint;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.block.Blocks;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.terrain.EndTerrainGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

public class DragonFightManager {

   public float angle;
   private int aliveCrystals;
   private long seed;
   private EndBiomeSource biomeSource;
   private EndTerrainGenerator terrainGen;
   private EndSpikeFeature spikeFeature;
   private int frontTower;
   private int backTower;
   public int fountainHeight;
   public final PathPoint[] pathPoints = new PathPoint[24];
   public final int[] neighbors = new int[24];
   public static HashMap<Integer, Block> world;

   public static int preHash(BlockPos pos) {
      return (pos.getX() + 512) << 20 | (pos.getY() + 512) << 10 | (pos.getZ() + 512);
   }

   public DragonFightManager(long seedIn) {
      world = new HashMap<>();
      seed = seedIn;
      biomeSource = new EndBiomeSource(MCVersion.v1_16_1, seed);
      terrainGen = new EndTerrainGenerator(biomeSource);
      fountainHeight = terrainGen.getHeightOnGround(0, 0) + 3;
      spikeFeature = new EndSpikeFeature();
      spikeFeature.genPillars(seed);

      // Determine front and back tower heights dynamically
      frontTower = getTowerHeight(40, 0);
      backTower = getTowerHeight(-40, 0);

      this.initPathPoints();
   }

   private int getTowerHeight(int x, int z) {
      int maxY = -1;
      for (int y = 255; y >= 0; --y) {
         BlockPos pos = new BlockPos(x, y, z);
         if (world.containsKey(preHash(pos))) {
            Block block = world.get(preHash(pos));
            if (block != Blocks.AIR) {
               maxY = y;
               break;
            }
         }
      }
      return maxY;
   }

   public void updateCrystals() {
      aliveCrystals = 10;
   }

   public EnderDragonEntity createNewDragon() {
      return this.createNewDragonWithAngle((new Random()).nextFloat() * 360.0F);
   }

   public EnderDragonEntity createNewDragonWithAngle(float angle) {
      this.angle = angle;
      EnderDragonEntity enderdragonentity = new EnderDragonEntity();
      enderdragonentity.setFightManager(this);
      enderdragonentity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      enderdragonentity.setLocationAndAngles(0.0D, 128.0D, 0.0D, angle, 0.0F);
      return enderdragonentity;
   }

   public int getHeight(int x, int z) {
      if (z == 0) {
         if (x == 0) {
            return fountainHeight;
         }
         if (x == 40) {
            return frontTower;
         }
         if (x == -40) {
            return backTower;
         }
      }
      return terrainGen == null ? 58 : terrainGen.getHeightOnGround(x, z);
   }

   public int getNumAliveCrystals() {
      return this.aliveCrystals;
   }

   private void initPathPoints() {
      if (this.pathPoints[0] == null) {
         for (int i = 0; i < 24; ++i) {
            int j = 5;
            int l;
            int i1;
            if (i < 12) {
               l = MathHelper.floor(60.0F * MathHelper.cos(2.0F * (-(float) Math.PI + 0.2617994F * (float) i)));
               i1 = MathHelper.floor(60.0F * MathHelper.sin(2.0F * (-(float) Math.PI + 0.2617994F * (float) i)));
            } else if (i < 20) {
               int lvt_3_1_ = i - 12;
               l = MathHelper.floor(40.0F * MathHelper.cos(2.0F * (-(float) Math.PI + ((float) Math.PI / 8F) * (float) lvt_3_1_)));
               i1 = MathHelper.floor(40.0F * MathHelper.sin(2.0F * (-(float) Math.PI + ((float) Math.PI / 8F) * (float) lvt_3_1_)));
               j += 10;
            } else {
               int k1 = i - 20;
               l = MathHelper.floor(20.0F * MathHelper.cos(2.0F * (-(float) Math.PI + ((float) Math.PI / 4F) * (float) k1)));
               i1 = MathHelper.floor(20.0F * MathHelper.sin(2.0F * (-(float) Math.PI + ((float) Math.PI / 4F) * (float) k1)));
            }

            int j1 = Math.max(73, this.getHeight(l, i1) + j);
            this.pathPoints[i] = new PathPoint(l, j1, i1);
         }

         this.neighbors[0] = 6146;
         this.neighbors[1] = 8197;
         this.neighbors[2] = 8202;
         this.neighbors[3] = 16404;
         this.neighbors[4] = 32808;
         this.neighbors[5] = 32848;
         this.neighbors[6] = 65696;
         this.neighbors[7] = 131392;
         this.neighbors[8] = 131712;
         this.neighbors[9] = 263424;
         this.neighbors[10] = 526848;
         this.neighbors[11] = 525313;
         this.neighbors[12] = 1581057;
         this.neighbors[13] = 3166214;
         this.neighbors[14] = 2138120;
         this.neighbors[15] = 6373424;
         this.neighbors[16] = 4358208;
         this.neighbors[17] = 12910976;
         this.neighbors[18] = 9044480;
         this.neighbors[19] = 9706496;
         this.neighbors[20] = 15216640;
         this.neighbors[21] = 13688832;
         this.neighbors[22] = 11763712;
         this.neighbors[23] = 8257536;
      }
   }
}
