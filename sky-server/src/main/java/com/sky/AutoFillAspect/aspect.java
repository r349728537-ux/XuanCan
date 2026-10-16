package com.sky.AutoFillAspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class aspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void aotuofillAspecct() {
    }
    @Before("aotuofillAspecct()")
    public void before(JoinPoint joinPoint) {
        log.info("开始进行公共字段的拦截");
        // 1、拿到被拦截方法上的 @AutoFill 注解，取出操作类型（INSERT / UPDATE）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 2、拿到方法的第一个参数（约定：mapper 方法的第一个参数就是要填充的实体）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        // 3、准备好要填的值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();   // 当前登录员工 id（拦截器存的）

        // 4、反射调用 setter 赋值
        Class<?> clazz = entity.getClass();
        try {
            if (operationType == OperationType.INSERT) {
                // 新增：4 个公共字段
                Method setCreateTime = clazz.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } else if (operationType == OperationType.UPDATE) {
                // 修改：只填 2 个
                Method setUpdateTime = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }
        } catch (Exception e) {
            log.error("公共字段自动填充失败", e);
        }
    }


}
