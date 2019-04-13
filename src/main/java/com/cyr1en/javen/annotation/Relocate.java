package com.cyr1en.javen.annotation;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

@IndexAnnotated
@Repeatable(Relocations.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Relocate {

  String from();

  String to();
}
