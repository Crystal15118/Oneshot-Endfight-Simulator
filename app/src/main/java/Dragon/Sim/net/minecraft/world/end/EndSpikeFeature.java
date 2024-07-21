package Dragon.Sim.net.minecraft.world.end;

import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.util.math.MathHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.block.Blocks;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EndSpikeFeature {
    private static final LoadingCache<Long, List<EndSpikeFeature.EndSpike>> LOADING_CACHE = CacheBuilder.newBuilder().build(new EndSpikeFeature.EndSpikeCacheLoader());

    public EndSpikeFeature() {
    }

    public static List<EndSpikeFeature.EndSpike> func_236356_a_(long seed) {
        Random random = new Random(seed);
        long i = random.nextLong() & 65535L;
        return LOADING_CACHE.getUnchecked(i);
    }

    public void genPillars(long seed) {
        List<EndSpikeFeature.EndSpike> list = func_236356_a_(seed);

        for(EndSpikeFeature.EndSpike endspikefeature$endspike : list) {
            this.placeSpike(endspikefeature$endspike);
        }
    }

    private void placeSpike(EndSpikeFeature.EndSpike spike) {
        int i = spike.getRadius();
        for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i))) {
            if (blockpos.distanceSq((double)spike.getCenterX(), (double)blockpos.getY(), (double)spike.getCenterZ(), false) <= (double)(i * i + 1) && blockpos.getY() < spike.getHeight()) {
                DragonFightManager.world.put(DragonFightManager.preHash(blockpos), Blocks.OBSIDIAN);
            } else if (blockpos.getY() > 65) {
                DragonFightManager.world.put(DragonFightManager.preHash(blockpos), Blocks.AIR);
            }
        }

        if (spike.isGuarded()) {

            for(int k = -2; k <= 2; ++k) {
                for(int l = -2; l <= 2; ++l) {
                    for(int i1 = 0; i1 <= 3; ++i1) {
                        boolean flag = MathHelper.abs(k) == 2;
                        boolean flag1 = MathHelper.abs(l) == 2;
                        boolean flag2 = i1 == 3;
                        if (flag || flag1 || flag2) {
                            Block block = Blocks.IRON_BARS;
                            DragonFightManager.world.put(DragonFightManager.preHash(new BlockPos(spike.getCenterX() + k, spike.getHeight() + i1, spike.getCenterZ() + l)), block);
                        }
                    }
                }
            }
        }

        DragonFightManager.world.put(DragonFightManager.preHash(new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ())), Blocks.BEDROCK);
    }

    public static class EndSpike {
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;

        public EndSpike(int centerXIn, int centerZIn, int radiusIn, int heightIn, boolean guardedIn) {
            this.centerX = centerXIn;
            this.centerZ = centerZIn;
            this.radius = radiusIn;
            this.height = heightIn;
            this.guarded = guardedIn;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }
    }

    static class EndSpikeCacheLoader extends CacheLoader<Long, List<EndSpikeFeature.EndSpike>> {
        private EndSpikeCacheLoader() {
        }

        public List<EndSpikeFeature.EndSpike> load(Long p_load_1_) {
            List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(p_load_1_));
            List<EndSpikeFeature.EndSpike> list1 = Lists.newArrayList();

            for(int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
                int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
                int l = list.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;
                list1.add(new EndSpikeFeature.EndSpike(j, k, i1, j1, flag));
            }

            return list1;
        }
    }
}
