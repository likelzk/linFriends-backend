package com.lin.linfriends.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author lin
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -5494330901388146360L;

    private String userAccount;
    private String userPassword;
}
