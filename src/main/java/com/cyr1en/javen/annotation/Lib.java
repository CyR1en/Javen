package com.cyr1en.javen.annotation;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

@IndexAnnotated
@Repeatable(Libs.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lib {

  /**
   * Group of the artifact.
   *
   * @return Group of the artifact.
   */
  String group();

  /**
   * Name of the artifact.
   *
   * @return Name of the artifact.
   */
  String name();

  /**
   * Version of artifact.
   *
   * @return Version of artifact.
   */
  String version();
}
