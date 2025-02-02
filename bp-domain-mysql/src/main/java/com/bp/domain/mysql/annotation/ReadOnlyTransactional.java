package com.bp.domain.mysql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Target({ElementType.METHOD, ElementType.TYPE})  // 메서드 및 클래스에 적용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 유지되어야 리플렉션을 사용할 수 있음
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)  // 트랜잭션 속성 설정
public @interface ReadOnlyTransactional {

}
