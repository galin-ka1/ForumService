package cohort_65.java.forumservice.aspect;

import cohort_65.java.forumservice.accounting.dto.UserRegisterDto;
import org.aspectj.lang.JoinPoint;
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

    @Around("execution(* cohort_65.java.forumservice.*.service*.*.*(..))")
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

   // @Before("execution(* cohort_65.java.forumservice.accounting.service.UserAccountServiceImpl.register(..))")
    public void validateArgsForRegister(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UserRegisterDto userRegisterDto = (UserRegisterDto) args[0];
        if (userRegisterDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!userRegisterDto.getPassword().contains("!")) {
            throw new IllegalArgumentException("Password must not contain '!' character");
        }
        System.out.println("[VALIDATION] Passed validation for register");
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
