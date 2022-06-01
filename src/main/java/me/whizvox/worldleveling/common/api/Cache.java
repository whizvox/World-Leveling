package me.whizvox.worldleveling.common.api;

public interface Cache {

  void clear();

  Cache DUMMY = () -> {};

}
