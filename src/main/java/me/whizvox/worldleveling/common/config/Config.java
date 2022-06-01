package me.whizvox.worldleveling.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.whizvox.worldleveling.common.util.JsonUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class Config {

  private final LinkedHashMap<String, ConfigValue<?>> entries;
  private JsonElement root;

  Config(LinkedHashMap<String, ConfigValue<?>> entries) {
    this.entries = entries;
    root = null;
  }

  public void load(InputStream in) {
    root = JsonParser.parseReader(new InputStreamReader(in));
  }

  public void save(OutputStream out) {
    JsonObject savedRoot = new JsonObject();
    entries.forEach((key, value) -> {
      savedRoot.add(key, value.encode());
    });
    try (Writer writer = new OutputStreamWriter(out)) {
      JsonUtil.GSON.toJson(savedRoot, writer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static class Builder {

    private final LinkedHashMap<String, ConfigValue<?>> entries;

    public Builder() {
      entries = new LinkedHashMap<>();
    }

    public <T> ConfigValue<T> define(String key, T defaultValue, ConfigValue.Validator<T> validator, Class<T> clazz) {
      ConfigValue<T> entry = new ConfigValue<>(key, defaultValue, validator, new ConfigValue.Codec<>() {
        @Override
        public T decode(JsonElement element) throws ConfigurationDecodeException {
          try {
            return JsonUtil.GSON.fromJson(element, clazz);
          } catch (JsonSyntaxException e) {
            throw new ConfigurationDecodeException("(" + clazz.getSimpleName() + ") " + element.toString());
          }
        }

        @Override
        public JsonElement encode(T value) {
          return JsonUtil.GSON.toJsonTree(value);
        }
      });
      entries.put(key, entry);
      return entry;
    }

    public <T> ConfigValue<T> define(String key, T defaultValue, ConfigValue.Validator<T> validator, Type type) {
      ConfigValue<T> entry = new ConfigValue<>(key, defaultValue, validator, new ConfigValue.Codec<>() {
        @Override
        public T decode(JsonElement element) throws ConfigurationDecodeException {
          try {
            return JsonUtil.GSON.fromJson(element, type);
          } catch (JsonSyntaxException e) {
            throw new ConfigurationDecodeException("(" + type.getTypeName() + ") " + element.toString());
          }
        }

        @Override
        public JsonElement encode(T value) {
          return JsonUtil.GSON.toJsonTree(value);
        }
      });
      entries.put(key, entry);
      return entry;
    }

    private static <T> ConfigValue.Validator<T> allValid() {
      return value -> {};
    }

    public ConfigValue<Boolean> defineBoolean(String key, boolean defaultValue) {
      return define(key, defaultValue, allValid(), Boolean.class);
    }

    public ConfigValue<String> defineString(String key, String defaultValue, ConfigValue.Validator<String> validator) {
      return define(key, defaultValue, validator, String.class);
    }

    public ConfigValue<String> defineString(String key, String defaultValue) {
      return defineString(key, defaultValue, allValid());
    }

    public ConfigValue<Boolean> defineBool(String key, boolean defaultValue) {
      return define(key, defaultValue, value -> {}, Boolean.class);
    }

    public ConfigValue<Integer> defineInt(String key, int defaultValue, int min, int max) {
      return define(key, defaultValue, value -> {
        if (value < min || value > max) {
          throw new ConfigurationValueInvalidException("Value %d out of bounds: [%d, %d]".formatted(value, min, max));
        }
      }, Integer.class);
    }

    public ConfigValue<Integer> defineInt(String key, int defaultValue, int min) {
      return defineInt(key, defaultValue, min, Integer.MAX_VALUE);
    }

    public ConfigValue<Integer> defineInt(String key, int defaultValue) {
      return define(key, defaultValue, value -> {}, Integer.class);
    }

    public ConfigValue<Double> defineDouble(String key, double defaultValue, double min, double max) {
      return define(key, defaultValue, value -> {
        if (value < min || value > max) {
          throw new ConfigurationValueInvalidException("Value " + value + " out of bounds: [" + min + ", " + max + "]");
        }
      }, Double.class);
    }

    public ConfigValue<Double> defineDouble(String key, double defaultValue, double min) {
      return defineDouble(key, defaultValue, min, Float.MAX_VALUE);
    }

    public ConfigValue<Double> defineDouble(String key, double defaultValue) {
      return define(key, defaultValue, value -> {}, Double.class);
    }

    public Config build() {
      Config config = new Config(entries);
      config.entries.forEach((key, value) -> value.setRoot(config.root));
      return config;
    }

  }

}
