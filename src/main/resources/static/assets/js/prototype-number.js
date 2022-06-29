
/** Usage: (123.45234).toDecimal(2) => 123.45 */
Number.prototype.toDecimal = function(range) {
	if(range==null) range = 0;
	if (range == 0) {
		return Math.round(this);
	} else {
		range = Math.pow(10.0, range);
		return Math.round(this * range) / range;
	}
};

///** 
// *  오픈소스로 확장한 숫자형
// * ex) alert( format( "#,##0.####", 1234567.890)); //output: 1,234,567.89 */
//Number.prototype.format = function(pattern) {
//	if(pattern==null) pattern =  "#,##0.##";
//	return format(pattern,this);
//};

/**
 * 숫자와 문자를 구분하지 않기 위해 추가
 */
Number.prototype.toNumber = function(){
	return this;
};


/**
 * 밀리초를 간단한 시분초로 나타냄.
 */
Number.prototype.toTime = function(f1){
	var ms = this;
	if(ms < 60 * 1000){ //초단위
		if(f1==null) f1 = "0";
		return (ms / 1000).format(f1)+'초';
	}else if(ms < 60 * 60 * 1000){ //분단위
		var s = Math.round(ms / 1000);
		var mm = Math.floor(s / 60);
		var ss = s - mm*60;
		return mm+'분 ' + (ss + '초').lpad(3,' ');
	}else if(ms < 60 * 60 * 24 * 1000){ //시간단위
		var m = Math.round(ms / 1000 / 60);
		var hh = Math.floor(m / 60);
		var mm = m - hh*60;
		return hh+'시 ' + (mm + '분').lpad(3,' ');
	}
    //}else if(ms < 60 * 60 * 24 * 1000){ //일단위
	var h = Math.round(ms / 1000 / 60 / 60);
	var dd = Math.floor(h / 24);
	var hh = h - dd*24;
	return dd+'일 ' + (hh + '시').lpad(3,' ');
};

Number.prototype.twoLength = function(){
	if(this < 10){
		return "0"+this;
	}else{
		return this;
	}
};