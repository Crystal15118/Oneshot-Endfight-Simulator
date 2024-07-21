package Dragon.Sim.net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import Dragon.Sim.net.minecraft.util.math.vector.Vector3i;
import com.google.errorprone.annotations.Immutable;

@Immutable
public class BlockPos extends Vector3i {
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public static Iterable<BlockPos> getAllInBoxMutable(BlockPos firstPos, BlockPos secondPos) {
      return getAllInBoxMutable(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.max(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));
   }

   public static Iterable<BlockPos> getAllInBoxMutable(int x1, int y1, int z1, int x2, int y2, int z2) {
      int i = x2 - x1 + 1;
      int j = y2 - y1 + 1;
      int k = z2 - z1 + 1;
      int l = i * j * k;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final BlockPos.Mutable field_239616_g_ = new BlockPos.Mutable();
            private int field_239617_h_;

            protected BlockPos computeNext() {
               if (this.field_239617_h_ == l) {
                  return this.endOfData();
               } else {
                  int i1 = this.field_239617_h_ % i;
                  int j1 = this.field_239617_h_ / i;
                  int k1 = j1 % j;
                  int l1 = j1 / j;
                  ++this.field_239617_h_;
                  return this.field_239616_g_.setPos(x1 + i1, y1 + k1, z1 + l1);
               }
            }
         };
      };
   }

   public static class Mutable extends BlockPos {
      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(int x_, int y_, int z_) {
         super(x_, y_, z_);
      }

      public BlockPos.Mutable setPos(int xIn, int yIn, int zIn) {
         this.setX(xIn);
         this.setY(yIn);
         this.setZ(zIn);
         return this;
      }

      public void setX(int xIn) {
         super.setX(xIn);
      }

      public void setY(int yIn) {
         super.setY(yIn);
      }

      public void setZ(int zIn) {
         super.setZ(zIn);
      }
   }
}
