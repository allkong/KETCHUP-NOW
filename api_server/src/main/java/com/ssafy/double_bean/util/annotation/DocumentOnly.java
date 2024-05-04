package com.ssafy.double_bean.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 해당 Annotation이 있으면, 기능적으로는 어떠한 것도 수행하지 않으며 오직 문서화를 위해 사용되었음을 의미한다.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentOnly {
}
