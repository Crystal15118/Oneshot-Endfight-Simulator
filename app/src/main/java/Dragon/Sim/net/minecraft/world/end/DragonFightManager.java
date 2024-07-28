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
import java.util.Random;

public class DragonFightManager {

   public float angle; // Angle for the dragon's flight
   private int AliveCrystals; // Number of alive end crystals
   private long seed; // Seed for terrain and biome generation
   private EndBiomeSource BiomeSource; // Biome source for the End dimension
   private EndTerrainGenerator TerrainGen; // Terrain generator for the End dimension
   private EndSpikeFeature SpikeFeature; // Feature for generating end spikes
   private int FrontTowerHeight; // Height of the front tower
   private int BackTowerHeight; // Height of the back tower
   public int FountainHeight; // Height of the central fountain
   public final PathPoint[] pathPoints = new PathPoint[24]; // Path points for dragon navigation
   public final int[] neighbors = new int[24]; // Neighbors array for pathfinding
   public static HashMap<Integer, Block> world; // Simulated world state

   // Utility method to hash block positions for the simulated world map
   public static int preHash(BlockPos pos) {
      return (pos.getX() + 512) << 20 | (pos.getY() + 512) << 10 | (pos.getZ() + 512);
   }

   // Initialize the DragonFightManager with a given seed
   public DragonFightManager(long seedIn) {
      world = new HashMap<>();
      seed = seedIn;
      BiomeSource = new EndBiomeSource(MCVersion.v1_16_1, seed);
      TerrainGen = new EndTerrainGenerator(BiomeSource);
      FountainHeight = TerrainGen.getHeightOnGround(0, 0) + 3;
      SpikeFeature = new EndSpikeFeature();
      SpikeFeature.genPillars(seed);

      // Determine front and back tower heights
      FrontTowerHeight = getTowerHeight(40, 0);
      BackTowerHeight = getTowerHeight(-40, 0);

      // Initialize path points for dragon navigation
      this.initPathPoints();
   }

   // Method to get the height of towers (in this case, we need it for the front and back tower)
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
      AliveCrystals = 10;
   }

   // Method to create a new Ender Dragon with a random angle
   public EnderDragonEntity createNewDragon() {
      return this.createNewDragonWithAngle((new Random()).nextFloat() * 360.0F);
   }

   // Method to create a new Ender Dragon with a specific angle
   public EnderDragonEntity createNewDragonWithAngle(float angle) {
      this.angle = angle;
      EnderDragonEntity enderdragonentity = new EnderDragonEntity();
      enderdragonentity.setFightManager(this);
      enderdragonentity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      enderdragonentity.setLocationAndAngles(0.0D, 128.0D, 0.0D, angle, 0.0F);
      return enderdragonentity;
   }

   // Method to get the height of a block at specific x and z coordinates
   public int getHeight(int x, int z) {
      if (z == 0) {
         if (x == 0) {
            return FountainHeight;
         }
         if (x == 40) {
            return FrontTowerHeight;
         }
         if (x == -40) {
            return BackTowerHeight;
         }
      }
      return TerrainGen == null ? 58 : TerrainGen.getHeightOnGround(x, z);
   }

   public int getNumAliveCrystals() {
      return this.AliveCrystals;
   }

   // Method to initialize path points for dragon navigation
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

         // Initialize neighbors for pathfinding
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