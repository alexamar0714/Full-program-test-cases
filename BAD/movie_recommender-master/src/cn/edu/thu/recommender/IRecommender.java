package cn.edu.thu.recommender;

import java.util.HashMap;

import cn.edu.thu.similarity.IDistance;

/**
 * @author zhf 
 * @email <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="84feece2aaf0ecf1c4e3e9e5ede8aae7ebe9">[email protected]</a>
 * @version 创建时间：2014年6月9日 上午11:24:08
 */
public interface IRecommender {
    /**
     * 为给定的用户获取推荐列表
     *
     * @param preferences 偏好列表
     * @param userid 被推荐的用户id
     * @param distance 使用哪种距离
     * @return 一个包含预估评分的推荐列表
     */
    public HashMap<Integer,Double> getRecommendations(int userid, IDistance distance);
}
