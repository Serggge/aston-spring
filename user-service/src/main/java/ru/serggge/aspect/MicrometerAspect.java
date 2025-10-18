package ru.serggge.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.time.Duration;

@Aspect
public class MicrometerAspect {

    private final Counter requestsTotal;
    private final Counter requestsFail;
    private final Timer processingTimer;

    public MicrometerAspect(MeterRegistry meterRegistry) {
        requestsTotal = Counter.builder("event_total")
                               .description("Total number of events")
                               .tags("request", "total")
                               .register(meterRegistry);
        requestsFail = Counter.builder("event_request_failed")
                              .description("Total number of failed events")
                              .tags("request", "fail")
                              .register(meterRegistry);
        processingTimer = Timer.builder("job.timer")
                               .description("Time taken for processing tasks")
                               .tags("task", "duration")
                               .serviceLevelObjectives(
                                       Duration.ofMillis(100),
                                       Duration.ofMillis(200),
                                       Duration.ofMillis(300)
                               )
                               .publishPercentiles(0.5, 0.95)
                               .publishPercentileHistogram()
                               .register(meterRegistry);
    }

    @Before(("execution(* ru.serggge.controllers.*.*(..))"))
    public void increaseTotalCounter() {
        requestsTotal.increment();
    }

    @AfterThrowing(("execution(* ru.serggge.controllers.*.*(..))"))
    public void increaseFailCounter() {
        requestsFail.increment();
    }

    @Around((("execution(* ru.serggge.controllers.*.*(..))")))
    public Object measureDuration(ProceedingJoinPoint joinPoint) {
        return processingTimer.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
