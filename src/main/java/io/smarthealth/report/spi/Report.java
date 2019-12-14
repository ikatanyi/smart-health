package io.smarthealth.report.spi;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface Report {
  String category();
  String identifier();
}
