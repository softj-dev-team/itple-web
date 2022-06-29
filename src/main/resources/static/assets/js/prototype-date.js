/**
 * Date객체의 포맷한 날짜를 반환한다. 로컬PC의 날자임으로 사용시 주의.
 * =============================================================================
 * Letters Component
 * -----------------------------------------------------------------------------
 * yyyy Year MM Month dd Date HH Hour mm Minute SS Second sss Milli-Second
 * =============================================================================
 */
Date.prototype.format = function(pattern) {
	var self = this;
	//return pattern.replace(new RegExp("(yyyy|MM|dd|HH|mm|SS|sss)", "g"), function($1) {
	return pattern.replace(new RegExp("(yyyy|yy|MM|dd|HH|mm|ss|SSS)", "g"), function($1) {
		switch ($1) {
		case "yyyy":
			var year = self.getFullYear();
			return year.toString();
		case "yy":
			var year = (self.getFullYear() % 1000);
			return year.toString();
		case "MM":
			var month = self.getMonth() + 1;
			return month.toString().lpad(2);
		case "dd":
			var date = self.getDate();
			return date.toString().lpad(2);
		case "HH":
			var hour = self.getHours();
			return hour.toString().lpad(2);
		case "mm":
			var minute = self.getMinutes();
			return minute.toString().lpad(2);
		case "ss":
			var second = self.getSeconds();
			return second.toString().lpad(2);
		case "SSS":
			var millisecond = self.getMilliseconds();
			return millisecond.toString().lpad(3);
		default:
			return "";
		}
	});
}

/** 현제 객체에 count만큼의 날자를 더한다. */ 
Date.prototype.plusDate = function(count) {
	this.setDate(this.getDate()+count);
	return this;
}

/**
 * 마지막 일자를 반환한다. Usage: date.getLastDate()
 */
Date.prototype.getLastDate = function() {
	var year = this.getFullYear();
	var month = this.getMonth() + 1;

	switch (month) {
	case 4:
	case 6:
	case 9:
	case 11:
		return 30;
	case 2:
		if (year % 4 == 0 && year % 100 != 0) {
			return 29;
		}
		if (year % 400 == 0) {
			return 29;
		}
		return 28;
	}
	return 31;
};

/** 해당 일 기준으로 한주가 시작되는 일을 리턴한다. */
Date.prototype.getWeekStartDate = function() {
	var today = this;
	var nowDayOfWeek = today.getDay();
	return today.plusDate(-nowDayOfWeek);
};

/** 현제 객체에 count만큼의 월을 더한다. */ 
Date.prototype.plusMonth = function(count) {
	this.setDate(1);
	this.setMonth(this.getMonth()+count);
	return this;
};

/** 이번주의 시작 / 종료일을 반환한다. */ 
Date.prototype.nowWeek = function() {
	var now = this;
	var nowDayOfWeek = now.getDay(); 
	var nowDay = now.getDate(); 
    var nowMonth = now.getMonth(); 
    var nowYear = now.getYear(); 
    nowYear += (nowYear < 2000) ? 1900 : 0; 
    var weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek); 
    var weekEndDate = new Date(nowYear, nowMonth, nowDay + (6 - nowDayOfWeek)); 
	return [weekStartDate,weekEndDate];
};

/** 해당 월의 1일을 리턴한다. */
Date.prototype.trimDate = function() {
	var now = this;
    var nowMonth = now.getMonth(); 
    var nowYear = now.getYear(); 
    nowYear += (nowYear < 2000) ? 1900 : 0; 
    return new Date(nowYear, nowMonth, 1);
};
/** 해당 월의 마지막날을 리턴한다. */
Date.prototype.trimLastDate = function() {
	var now = this;
	var nowMonth = now.getMonth(); 
	var nowYear = now.getYear(); 
	nowYear += (nowYear < 2000) ? 1900 : 0; 
	return new Date(nowYear, nowMonth, this.getLastDate());
};

/** 해당 년의 1월 1일을 리턴한다. */
Date.prototype.trimYear = function() {
	var now = this;
    var nowYear = now.getYear(); 
    nowYear += (nowYear < 2000) ? 1900 : 0; 
    return new Date(nowYear, 0, 1);
};
 

/** 해당날짜와 파라미터 날짜의 일수 차이 **/
Date.prototype.diffDate = function(other) {
	try{
		var now = this.getTime();
		var param = other.getTime();
		
		var diffTime = now - param;
		return Math.round(diffTime/(1000*60*60*24));
	}catch(e){
		console.log(e);
		return 0;
	}
	
};

/** 해당날짜의 요일을 리턴 **/
Date.prototype.dayOfWeek = function(other) {
	try{
		var now = this;
		var dayOfWeek = ['일','월','화','수','목','금','토'];
		
		return dayOfWeek[now.getDay()];
	}catch(e){
		console.log(e);
		return 0;
	}
	
};