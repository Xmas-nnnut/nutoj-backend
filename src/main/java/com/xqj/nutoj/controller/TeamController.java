package com.xqj.nutoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqj.nutoj.common.BaseResponse;
import com.xqj.nutoj.common.DeleteRequest;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.common.ResultUtils;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.model.dto.team.*;
import com.xqj.nutoj.model.entity.Team;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.entity.UserTeam;
import com.xqj.nutoj.model.vo.TeamUserVO;
import com.xqj.nutoj.model.vo.UserVO;
import com.xqj.nutoj.service.UserService;
import com.xqj.nutoj.service.TeamService;
import com.xqj.nutoj.service.UserTeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍接口
 *
 */
@RestController
@RequestMapping("/team")
@Api(tags = "TeamController")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @PostMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(@RequestBody TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(user);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryRequest, isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
        }
        // 3、查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    // todo 查询分页
    @PostMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryRequest, team);
        Page<Team> page = new Page<>(teamQueryRequest.getCurrent(), teamQueryRequest.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(@RequestBody TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQueryRequest.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryRequest, true);
        return ResultUtils.success(teamList);
    }


    /**
     * 获取我加入的队伍
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(@RequestBody TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQueryRequest.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryRequest, true);
        return ResultUtils.success(teamList);
    }

    /**
     * 得到队伍包括用户
     *
     * @param teamId  队伍id
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link UserVO}>>
     */
    @GetMapping("/include/user")
    public BaseResponse<List<UserVO>> getTeamIncludeUser(long teamId,HttpServletRequest request){
        if(teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = teamService.getIncludeUser(teamId);
        if(userList==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取失败");
        }
        // 脱敏
        List<UserVO> userVoList = userList.stream().map(e -> userService.getUserVO(e)).collect(Collectors.toList());
        return ResultUtils.success(userVoList);
    }
}



























