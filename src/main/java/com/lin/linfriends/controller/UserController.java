package com.lin.linfriends.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.lin.linfriends.conmmon.BaseResponse;
import com.lin.linfriends.conmmon.ErrorCode;
import com.lin.linfriends.conmmon.ResultUtils;
import com.lin.linfriends.exception.BusinessException;
import com.lin.linfriends.model.domain.User;
import com.lin.linfriends.model.domain.request.UserLoginRequest;
import com.lin.linfriends.model.domain.request.UserRegisterRequest;
import com.lin.linfriends.model.domain.response.UserRecommendResponse;
import com.lin.linfriends.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lin.linfriends.contant.UserConstant.ADMIN_ROLE;
import static com.lin.linfriends.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author lin
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 注册
     *
     * @param userRegisterRequest
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        return ResultUtils.success(result);
    }

    /**
     * 登录
     *
     * @param userLoginRequest 请求封装类
     * @param request
     * @return 脱敏用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    /**
     * 注销
     *
     * @param request
     * @return 注销结果
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return 脱敏用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User result = userService.getSafetyUser(user);
        return ResultUtils.success(result);
    }

    /**
     * 查询用户列表
     *
     * @param username 有则查名，无则查全表
     * @param request
     * @return List<User>脱敏信息
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);

        List<User> result = userList.stream().map(user -> {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());

        return ResultUtils.success(result);
    }

    /**
     * 根据标签查询用户列表
     *
     * @param tagNameList 标签列表
     * @return List<UserRecommendResponse>脱敏信息
     */
    //todo 前端怎么实际调用到？
    //todo 用户的脱敏操作可以抽象出来
    @GetMapping("/search/tags")
    public BaseResponse<List<UserRecommendResponse>> searchUsersByTags(@RequestParam List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<User> users = userService.searchUsersByTags(tagNameList);
        //查出来为空，创建一个空集
        Optional.ofNullable(users).orElse(Collections.emptyList());

        //非空处理后返回给前端
        List<UserRecommendResponse> result = users.stream().map(user -> {
            UserRecommendResponse userRecommendResponse = new UserRecommendResponse();
            userRecommendResponse.setId(user.getId());
            userRecommendResponse.setUsername(user.getUsername());
            userRecommendResponse.setUserAccount(user.getUserAccount());
            userRecommendResponse.setAvatarUrl(user.getAvatarUrl());
            userRecommendResponse.setGender(user.getGender());
            userRecommendResponse.setPhone(user.getPhone());
            userRecommendResponse.setEmail(user.getEmail());
            userRecommendResponse.setTags(user.getTags());
            userRecommendResponse.setUserRole(user.getUserRole());
            return userRecommendResponse;
        }).collect(Collectors.toList());

        return ResultUtils.success(result);
    }

    /**
     * 分页查询用户列表
     *
     * @param pageNum  页数
     * @param pageSize 一页的大小
     * @return List<UserRecommendResponse> 必要的返回类
     */
    //todo 可能需要抽象出impl方法，精简cotroller层
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(@RequestParam int pageNum, @RequestParam int pageSize, HttpServletRequest httpServletRequest) {
        if (pageSize <= 0 || pageNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数必须大于零");
        }
        //方式一:拼接查询语句
        //String limitString = "limit " + (pageNum - 1) * pageSize + "," + pageSize;
        //QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //queryWrapper.last(limitString);
        //List<User> userList = userService.list(queryWrapper);
        //List<UserRecommendResponse> result = userList.stream().map(user -> {
        //            UserRecommendResponse userRecommendResponse = new UserRecommendResponse();
        //            userRecommendResponse.setId(user.getId());
        //            userRecommendResponse.setUsername(user.getUsername());
        //            userRecommendResponse.setUserAccount(user.getUserAccount());
        //            userRecommendResponse.setAvatarUrl(user.getAvatarUrl());
        //            userRecommendResponse.setGender(user.getGender());
        //            userRecommendResponse.setPhone(user.getPhone());
        //            userRecommendResponse.setEmail(user.getEmail());
        //            userRecommendResponse.setTags(user.getTags());
        //            userRecommendResponse.setUserRole(user.getUserRole());
        //            return userRecommendResponse;
        //        }).collect(Collectors.toList());

        //方式二:使用userService里的page，其实这么做有点麻烦
        //确保每个用户的缓存key不相同
        User logininUser = (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        String redisKey = String.format("lin:user:recommend:%s", logininUser.getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        //缓存有直接返回
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        //缓存没有，查完存缓存再返回
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userList = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        //写缓存,10s过期
        try {
            valueOperations.set(redisKey,userList,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            log.error("redis set key error",e);
        }
        return ResultUtils.success(userList);
    }

    /**
     * 删除用户
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        //1. 判断各参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (request == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //2. 管理员能更新所有人，普通人只能更新自己（管理员否，改的自己否？）
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        //3. 根据用户id和更改map更新数据库
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }
}


