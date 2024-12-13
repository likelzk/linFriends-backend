package com.lin.linfriends.conmmon;

/**
 * 返回工具类
 */
public class ResultUtils {
    //todo 要总结这个流程
    //todo 为什么这里要用静态方法
    //todo 为什么这里这么定义没错？前面的<T>什么含义
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"success");
    }

    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMessage(),description);
    }

    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }
}
