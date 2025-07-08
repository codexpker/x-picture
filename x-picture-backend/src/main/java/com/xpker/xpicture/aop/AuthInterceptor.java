package com.xpker.xpicture.aop;

import com.xpker.xpicture.annotation.AuthCheck;
import com.xpker.xpicture.exception.ErrorCode;
import com.xpker.xpicture.exception.ThrowUtils;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.enums.UserRoleEnum;
import com.xpker.xpicture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 全局权限校验
 */
@Component
@Aspect //声明这是一个切面
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 需要的权限
        String mustRole = authCheck.mustRole();
        // 拿到request对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 获取需要权限的枚举类
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，放行通过
        if (mustRoleEnum == null) {
            return joinPoint.proceed(); //继续执行原来的方法
        }
        // 以下需要权限才能放行
        // 获取当前用户权限枚举类
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 用户没有权限，拦截
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NOT_AUTH_ERROR);
        // 要求管理员权限，但当前用户不是管理员，拦截
        ThrowUtils.throwIf(mustRoleEnum.equals(UserRoleEnum.ADMIN) && !userRoleEnum.equals(UserRoleEnum.ADMIN), ErrorCode.NOT_AUTH_ERROR);
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}
