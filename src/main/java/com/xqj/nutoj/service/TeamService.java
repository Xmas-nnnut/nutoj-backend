package com.xqj.nutoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.dto.team.TeamJoinRequest;
import com.xqj.nutoj.model.dto.team.TeamQueryRequest;
import com.xqj.nutoj.model.dto.team.TeamQuitRequest;
import com.xqj.nutoj.model.dto.team.TeamUpdateRequest;
import com.xqj.nutoj.model.entity.Team;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.TeamUserVO;

import java.util.List;

/**
 * 队伍服务
 *
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQueryRequest
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

    /**
     * 获取队员
     * @param teamId
     * @return
     */
    List<User> getIncludeUser(long teamId);
}
