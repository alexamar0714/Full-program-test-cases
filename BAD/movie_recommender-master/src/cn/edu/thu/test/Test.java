package cn.edu.thu.test;

import java.util.HashMap;

import cn.edu.thu.recommender.UserBasedRecommender;
import cn.edu.thu.similarity.EuclideanDistance;
import cn.edu.thu.similarity.IDistance;
import cn.edu.thu.util.PrintUtil;

/**
 * @author zhf 
 * @email <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="710b19175f05190431161c10181d5f121e1c">[email protected]</a>
 * @version 创建时间：2014年6月9日 下午5:05:09
 */
public class Test {
	public static void testEuclideanDistance() {
		double dis = new EuclideanDistance().getDistance(121, 131);
		System.out.println("dis = "+dis);
	}
	public static void testUserBasedRecommender() {
		UserBasedRecommender ubr = new UserBasedRecommender();
		IDistance distance = new EuclideanDistance();
		HashMap<Integer,Double> map = ubr.getRecommendations(1, distance);
		PrintUtil.print(map);
	}
}
