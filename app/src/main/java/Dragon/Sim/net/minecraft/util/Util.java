package Dragon.Sim.net.minecraft.util;

import java.util.function.Consumer;

public class Util {

   public static <T> T make(T object, Consumer<T> consumer) {
      consumer.accept(object);
      return object;
   }
}
