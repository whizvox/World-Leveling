package me.whizvox.worldleveling.common.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtil {

  public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
      .setPrettyPrinting()
      .create();

  public static final Type
      TYPE_STRING_LIST = new TypeToken<List<String>>() {}.getType(),
      TYPE_STRING_INT_MAP = new TypeToken<Map<String, Integer>>() {}.getType();

}
