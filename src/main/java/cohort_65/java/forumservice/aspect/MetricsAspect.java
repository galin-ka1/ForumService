package cohort_65.java.forumservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class MetricsAspect {

    private final ConcurrentHashMap<String, Long> metrics = new ConcurrentHashMap<>();

    @Around("execution(* cohort_65.java.forumservice.accounting.service.*.*(..))")
    public Object trackAccountingMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackExecution(joinPoint);
    }

    @Around("execution(* cohort_65.java.forumservice.security.service*.*(..))")
    public Object trackSecurityMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackExecution(joinPoint);
    }

    private Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        metrics.put(methodName, endTime - startTime);
        System.out.println("Time: " + LocalDateTime.now() + " [METRICS] Method " + methodName +
                " execution time: " + (endTime - startTime) + " ms");
        return result;
    }

    @Pointcut("execution(* cohort_65.java.forumservice.post.service.*.*(..))")
    public void serviceMethod() {
    }

    @Before("serviceMethod()")
    public void logBefore() {
        System.out.println("[LOG] Before method");
    }

    @AfterReturning(pointcut = "serviceMethod()", returning = "result")
    public void logAfter(Object result) {
        System.out.println("[LOG] After method: " + result);
    }

}
