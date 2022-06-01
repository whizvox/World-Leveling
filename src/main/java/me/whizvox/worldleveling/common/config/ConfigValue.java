package me.whizvox.worldleveling.common.config;

import com.google.gson.JsonElement;
import me.whizvox.worldleveling.common.lib.internal.WLLog;
import net.minecraftforge.common.util.Lazy;

public class ConfigValue<T> {

  private JsonElement root;
  private final Codec<T> codec;
  private final Lazy<T> resolver;

  public ConfigValue(String key, T defaultValue, Validator<T> validator, Codec<T> codec) {
    root = null;
    this.codec = codec;
    resolver = Lazy.of(() -> {
      if (root != null && root.getAsJsonObject().has(key)) {
        try {
          T value = codec.decode(root.getAsJsonObject().get(key));
          validator.validate(value);
          return value;
        } catch (ConfigurationDecodeException e) {
          WLLog.LOGGER.warn("Could not decode value: {}", e.getMessage());
        } catch (ConfigurationValueInvalidException e) {
          WLLog.LOGGER.warn("Invalid configuration value: {}", e.getMessage());
        }
      }
      return defaultValue;
    });
  }

  void setRoot(JsonElement root) {
    this.root = root;
  }

  public T get() {
    return resolver.get();
  }

  public JsonElement encode() {
    return codec.encode(get());
  }

  public interface Codec<T> {
    T decode(JsonElement element) throws ConfigurationDecodeException;

    JsonElement encode(T value);
  }

  public interface Validator<T> {
    void validate(T value) throws ConfigurationValueInvalidException;
  }

}
