package me.whizvox.worldleveling.common.lib.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class WLLog {

  public static final Logger LOGGER = LogManager.getLogger("WorldLeveling");

  public static final Marker
      COMMON = MarkerManager.getMarker("COMMON"),
      CLIENT = MarkerManager.getMarker("CLIENT"),
      SERVER = MarkerManager.getMarker("SERVER"),
      DATA = MarkerManager.getMarker("DATA");

}
