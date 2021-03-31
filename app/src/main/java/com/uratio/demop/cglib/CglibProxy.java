package com.uratio.demop.cglib;

import java.lang.reflect.Method;

/**
 * 使用cglib动态代理
 * 
 * @author yanbin
 * 
 */
public class CglibProxy {

    private Object target;

    /**
     * 创建代理对象
     * 
     * @param target
     * @return
     */
   /* public Object getInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        // 回调方法
        enhancer.setCallback(this);
        // 创建代理对象
        return enhancer.create();
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        System.out.println("事物开始");
        result = methodProxy.invokeSuper(proxy, args);
        System.out.println("事物结束");
        return result;
    }*/

}