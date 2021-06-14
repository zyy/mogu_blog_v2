package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Link;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.xo.vo.LinkVO;
import com.moxi.mogublog.xo.vo.UserVO;
import com.moxi.mogublog.xo.vo.UserWatchVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 用户关注表 服务类
 *
 * @author 陌溪
 * @date 2021年6月13日17:19:42
 */
public interface UserWatchService extends SuperService<UserWatch> {

    /**
     * 获取用户关注列表
     *
     * @param userWatchVO
     * @return
     */
    IPage<UserWatch> getPageList(UserWatchVO userWatchVO);

    /**
     * 关注某人
     * @return
     */
    String addUserWatch(UserWatchVO userWatchVO);

    /**
     * 取消关注
     * @return
     */
    String deleteUserWatch(UserWatchVO userWatchVO);


    /**
     * 判断是否关注了该用户
     * @param userWatchVO
     * @return
     */
    Boolean checkUserWatch(UserWatchVO userWatchVO);

}
