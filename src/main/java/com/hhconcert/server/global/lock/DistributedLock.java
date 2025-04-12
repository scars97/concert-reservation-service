package com.hhconcert.server.global.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락 이름
     * @return
     */
    String key();

    /**
     * 락 시간 단위
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 대기 시간
     * 락 획득을 위해 waitTime 만큼 대기한다.
     * @return
     */
    long waitTime() default 5L;

    /**
     * 락 임대 시간
     * 락 확득한 이후 leaseTime 이 지나면 락을 해제한다.
     * @return
     */
    long leaseTime() default 3L;
}
