package com.sportygroup.liveevents.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ExternalAdapter {
  @AliasFor(annotation = Component.class, attribute = "value")
  String value() default "";
}