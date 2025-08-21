package com.sportygroup.liveevents.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repository
public @interface PersistenceAdapter {
  @AliasFor(annotation = Repository.class, attribute = "value")
  String value() default "";
}