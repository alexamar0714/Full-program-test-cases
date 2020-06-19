package cn.edu.thu.util;

import java.sql.SQLException;
import java.util.List;

import cn.edu.thu.bean.Rating;

/**
 * @author zhf 
 * @email <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5e243638702a362b1e39333f3732703d3133">[email protected]</a>
 * @version 创建时间：2014年6月9日 上午11:08:51
 */
public class RatingReader {
	SQLUtil  sq = new SQLUtil();
	
	/**
	 * 读取给定用户的评分数据
	 */
	public List<Rating> readUserItemRatings(int userid) {
		try {
			return sq.getRatings(userid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取所有用户的评分数据
	 */
	public List<Rating> readAllUserItemRatings() {
		try {
			return sq.getRatings();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
