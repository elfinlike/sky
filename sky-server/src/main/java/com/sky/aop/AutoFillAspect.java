package com.sky.aop;

//自定义切面，实现公共字段自动填充

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
//指定切入点

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //前置通知，在通知中进行公共字段的赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始进行公共字段的自动赋值");
        //获取当前被拦截的方法的数据库操作类型
        MethodSignature methodSignature=(MethodSignature) joinPoint.getSignature();
        AutoFill autoFill=methodSignature.getMethod().getAnnotation(AutoFill.class);//获得方法上注解对象的操作类型
        OperationType operationType=autoFill.value();
        //获取当前被拦截的方法上的数据库操作类型
        Object[] args=joinPoint.getArgs();
        if(args==null||args.length==0){
            return;
        }
        Object entity=args[0];


        //为实体类对象的公共属性进行赋值
        LocalDateTime now=LocalDateTime.now();
        Long id= BaseContext.getCurrentId();
        //根据当前的不同类型，为对应属性通过反射来进行赋值
        if (operationType==OperationType.INSERT){
            //为4个公共字段进行赋值
            // 获得setCreateTime方法
          Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            //获得setUpdateTime方法
          Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            //获得setCreateUser方法
          Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
          //获得setUpdateUser方法
          Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
          //通过反射为对象进行赋值
            setCreateTime.invoke(entity,now);
            setUpdateTime.invoke(entity,now);
            setCreateUser.invoke(entity,id);
            setUpdateUser.invoke(entity,id);
        } else if (operationType==OperationType.UPDATE) {
            // 为两个公共字段进行赋值
            //获得setUpdateUser方法
            Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            //获得setUpdateTime方法
            Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            setUpdateUser.invoke(entity,id);
            setUpdateTime.invoke(entity,now);
        }
    }

}
