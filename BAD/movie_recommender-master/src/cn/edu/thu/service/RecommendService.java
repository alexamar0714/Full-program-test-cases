package cn.edu.thu.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import cn.edu.thu.bean.Movie;
import cn.edu.thu.util.SQLUtil;

/**
 * @author zhf 
 * @email <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="443e2c226a302c31042329252d286a272b29">[email protected]</a>
 * @version 创建时间：2014年6月11日 上午11:52:01
 */
public class RecommendService {
	
	SQLUtil sq = new SQLUtil();
	
	public ArrayList<Movie> getMovieInfoList(List<RecommendedItem> list){
		return sq.getMovieInfoList(list);
	}
	
	public ArrayList<Movie> getMovieInfoList(int userid) throws SQLException{
		return sq.getUserRatings(userid);
	}

}
