package com.xqj.nutoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.dto.user.UserQueryRequest;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.LoginUserVO;
import com.xqj.nutoj.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userName      用户昵称
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userName);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<UserVO> searchUsersByTags(List<String> tagNameList);

    /**
     * 根据标签搜索用户（SQL版）
     *
     * @param tagNameList
     * @return
     */
    @Deprecated
    List<UserVO> searchUsersByTagsBySQL(List<String> tagNameList);

    /**
     * 匹配用户
     *
     * @param num
     * @param loginUser
     * @return
     */
    List<UserVO> matchUsers(long num, User loginUser);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
