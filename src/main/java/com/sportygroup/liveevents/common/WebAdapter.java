package com.sportygroup.liveevents.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
public @interface WebAdapter {
  @AliasFor(annotation = RestController.class, attribute = "value")
  String value() default "";
}