/**
 * Traditional Chinese translation for bootstrap-datepicker
 * Rung-Sheng Jang <<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8eeaefe0e7ebe2cee7a3fafcebe0eaa0ede1a0eded">[email protected]</a>>
 * FrankWu  <<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5d3b2f3c33362a286c6d6d1d3a303c3431733e3230">[email protected]</a>> Fix more appropriate use of Traditional Chinese habit
 */
;(function($){
	$.fn.datepicker.dates['zh-TW'] = {
		days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
		daysShort: ["週日", "週一", "週二", "週三", "週四", "週五", "週六", "週日"],
		daysMin:  ["日", "一", "二", "三", "四", "五", "六", "日"],
		months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
		monthsShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
		today: "今天",
		format: "yyyy年mm月dd日",
		weekStart: 1
	};
}(jQuery));
