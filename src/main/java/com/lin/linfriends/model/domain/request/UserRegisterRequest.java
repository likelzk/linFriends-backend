package com.lin.linfriends.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author lin
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -3481745154534091197L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;


}
