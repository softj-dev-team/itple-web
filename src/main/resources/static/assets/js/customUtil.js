var ut = {};

/** 빈값체크. null체크 때믄에 String의 프로토타입으로 만들지 못한다. */
ut.isEmpty = function(value){
	if(value==undefined) return true;
	if(value==null) return true;
	if(value instanceof Array && value.length ==0) return true;  //Array 체크는 typeof로 못한다.
	else if(typeof value == "string" && value == '') return true;
	else if(typeof value == "number" && value == 0) return true;
	else if(typeof value == 'object' && Object.keys(value).length == 0) return true; // new Date() 가 비어있는 걸로 나온다..
	//뭔가 더 추가
	return false;
};

ut.isNotEmpty = function(value){
	return !ut.isEmpty(value);
}

/** map이 Array에만 반응해서 하나 만들었다.  */
ut.collect = function(items,callback,ignoreEmpty){
	var result = [];
	$.each(items,function(k,v){
		var value = callback(k,v);
		if(ignoreEmpty && ut.isEmpty(value)) return;
		result.push(value);
	});
	return result;
};

/** 날짜 포맷 체크 */
ut.isValidDate = function(dateString, pattern) {
	var tmp = dateString.replace(/[^0-9]/g, '');
	var date = tmp.toDate(pattern);
	if(ut.isEmpty(date)) return false;
	return date.format(pattern) == tmp;
}

/** 날짜 비교(시작날짜, 종료날짜) */
ut.isValidDateRange = function(start, end, pattern) {
	var tmp = start.replace(/[^0-9]/g, '');
	var tmp2 = end.replace(/[^0-9]/g, '');
	return tmp.toDate(pattern) <= tmp2.toDate(pattern);
}

/** 날짜 기간 체크(일단위) */
ut.isValidDatePeriod = function(start, end, pattern, period) {
	var tmp = start.replace(/[^0-9]/g, '');
	var tmp2 = end.replace(/[^0-9]/g, '');
	return (tmp2.toDate(pattern) - tmp.toDate(pattern)) / (1000 * 60 * 60 * 24) <= period;
}

///** 
// * ie8 에서 indexOf 를 지원하지 않아서 만듬
// *  indexOf 와 달리 해당 값이 있으면 true, 없으면 false 값만 반환  
// * */
//ut.indexOf = function(list,key) {
//	for(var i = 0; i < list.length; i++) {
//		var tmp = list[i];
//		if(tmp == key) return true;
//	}
//	return false;
//};

/** Object.keys(obj).length  가 IE8 이하에 적용이 안되서 따로 만들었다.  2  */
ut.getObjectKeys =  function(obj){
	var array = new Array();
	if(obj instanceof Object){
		for(var oo in obj) array.push(oo);
	}
	return array;
};

///** 간단 테스트기. 이거 말고 콘솔로그를 사용하자.  */
//$.getInfo =  function(obj){
//	var str = '';
//	if(obj instanceof Object){
//		for(var oo in obj){
//			var value = obj[oo];
//			if(value==null)  str+= oo + '  :  is null\n'  ;
//			//else if( (value+'') .contains('function')) str+= oo + '  :  function\n'
//			else if( (value+'') .contains('function')) continue;
//			else str+= oo + '  :  ' + obj[oo] + '\n'  ;
//		}
//		alert(str);
//	}else{
//		alert("Object가 아님 : " + obj);
//	}
//};

///** 숫자만 허용되는 구간에서 가능한 키보드입력인지
//*  => 추후 양수버전 , 소수점 안되는 버전으로 수정?
//*   */
ut.isNumberKey = function(keyCode) {
	if(keyCode >= 48 && keyCode <= 57) return true;  // 숫자열 0 ~ 9 : 48 ~  57
	if(keyCode >= 96 && keyCode <= 105) return true; // 키패드 0 ~ 9 : 96 ~ 105
	//BackSpace ,  Delete ,소수점(.) 문자키배열 ,소수점(.) : 키패드
	//좌 화살표, 우 화살표,End 키,Home 키,Tab 키, -부호 
	var ignores = [8,46,110,190,37,39,35,36,9,109];
	for(var i=0;i<ignores.length;i++){
		if(ignores[i] == keyCode) return true;
	}
	return false;
};



/** object안에 paramsArray를 키로 조회하여 compareString를 포함하는지 조회한다.
자바스립트로 리스트 목록을 검색/필터할때 사용했다.
ex) {xtype:'textfield',enableKeyEvents:true,fieldLabel: '검색',labelWidth:30,id:'Query',value:'',listeners:{keyup:jobFilter}} */
ut.isObjectStringMatch = function(object,compareString,paramsArray){
	for(var i=0;i<paramsArray.length;i++){
		var key = paramsArray[i];
		var value = object[key];
		if(value==null) continue;
		if(value.containsIgnoreCase(compareString)) return true;
	}
	return false;
};

/** 동작 JS 로드 */
ut.loadJs =  function(filename){
	var fileref = document.createElement('script');
    fileref.setAttribute("type", "text/javascript");
    fileref.setAttribute("src", filename);
    if (typeof fileref != "undefined") document.getElementsByTagName("head")[0].appendChild(fileref);
};

/** 동적 css체인지 ex) ut.loadCss('/js/extjs/resources/css/ext-all-gray.css'); */ 
ut.loadCss =  function(filename){
	var fileref = document.createElement("link");
    fileref.setAttribute("rel", "stylesheet");
    fileref.setAttribute("type", "text/css");
    fileref.setAttribute("href", filename);
    if (typeof fileref != "undefined") document.getElementsByTagName("head")[0].appendChild(fileref);
};

/** 해당 query로 list를 필터하여 새로운 배열을 리턴한다. 객체는 동일하다. 
인메모리 검색필터를 만들때 사용한다. 향후 모듈화 하자
ex) listFilter(query,allItems,['name']); */
ut.listFilter = function(query,list,keyList){
	if(query==null || query=='') return  list;
	if(query instanceof Array){
		if(query.length == 0) return list;
	}else query = query.split(','); // ,로 구분한다. 

	var newArray = new Array();
	
	for(var j=0;j<query.length;j++){
		var thisQuery = query[j];
		if(thisQuery=='') continue;
		
		var method = ut.isObjectStringMatch;
		if(thisQuery.endsWith('%')) {
			method = ut.isObjectStringStartsWith;
			thisQuery = thisQuery.substring(0,thisQuery.length-1);
		}else if(thisQuery.startsWith('%')){
			method = ut.isObjectStringEndsWith;
			thisQuery = thisQuery.substring(1);
		}
		
		for(var i=0;i<list.length;i++){
			var item = list[i];
			
			if(method(item,thisQuery,keyList)){
				newArray.push(item);
				continue;
			}
		}
	}
	return newArray;
};

/** 각 각체의 key값으로 비교한다. key에 해당하는 value는 숫자 처럼 비교 가능한 값이여야 한다. */
ut.sortByKey = function(list,key){
	list.sort(function(a,b){
		var v1 = a[key];
		var v2 = b[key];
		if(v1==null || v2 == null || v1 == v2) return 0;
		return v1 > v2 ? -1 : 1;
	});
};

/** 객체의 key값들을 모두 더한다. null값은 스킵한다....   */
ut.objectValueSum = function(obj){
	var sum = 0;
	for(var i=1;i<arguments.length;i++){
		var key = arguments[i];
		var value = obj[key];
		if(value==null) continue;
		sum += value;
	}
	return sum;
};

/** 객체에 값들을 더하여 누적합계를 구한다. 통계 리스트의 마지막 합계를 구할때 사용된다.
 * attribute가 존재한다면 list에 추가 후 attribute를 추가입력해준다  */
ut.objectValueListSum = function(list,attribute,autoAdd){
	var sum = new Object();
	for(var i=0;i<list.length;i++){
		var item = list[i];
		for(var key in item){
			var value = item[key];
			if(value==null || typeof(value)!= 'number' ) continue;
			var exist = sum[key];
			if(exist==null) exist = 0;
			sum[key] = exist + value;
		}
	}
	if(attribute!=null){
		for(var key in attribute){
			sum[key] = attribute[key];
		}
	}
	if(autoAdd) list.push(sum);
	return sum;
};

/** 소수점이하 size 만큼 반올림한다.  NaN도 number타입을 리턴한다. ㅅㅂ */
ut.roundBy = function(value,size,defaultValue) {
	if(typeof(value) != 'number') return defaultValue;
	var length = Math.pow(10,size); 
	var result = Math.round(value * length) / length;
	if( result+'' == 'NaN' ) return defaultValue;
	return  result;
};

/** 가져옴.  상속 구현 소스 참고용 */
ut.inherit = function(child, parent) {
	var F = function () {};
	F.prototype = parent.prototype;
	child.prototype = new F();
	child.prototype.constructor = child;
	child.superproto = parent.prototype;
	return child;
};


/** 현재 서버의 루트를 가져온다.  
 * ex) http://121.126.250.132:8080/board/freeboard/updatePage  --> http://121.126.250.132:8080  */
ut.findHost = function(){
	return location.href.match('http://[^\/]*');
};

/** Ext 등에서 null,싱글,Array가 구분되지않고 나오는 경우가 있어서 만들었다. 
 * 뭐가 들어오든 Array로 만들어준다.
 * Array 프로토타입이 있어야 한다. */
	var array = [];
	ut.toArray = function(){
	for(var i=0;i<arguments.length;i++){
		var data = arguments[i];
		if(data==null) continue;
		if(data instanceof Array) array.pushAll(data);
		else array.push(data);
	}
	return array; 
};


/** 대상 객체(map)을 얕은 복사한다. */
ut.shallowCopy = function(data){
	var map = {};
	for(var key in data){
		map[key] = data[key];
	}
	return map; 
};

/**  json에서 트리를 따라가며 객체를 검색한다. 리턴은 배열로 나온다.
 * keys의 1번째는 검색대상, 2번째는 칠드런
ex) var result = searchJson(text,['data','children'],treeData);
뭐할대 쓰는애인지???
  */
ut.searchJson = function(value,keys,jsonList,result){
	if(result==null) result = [];
	var size = jsonList.length;
	for(var i=0;i<size;i++){
		var obj = jsonList[i];
		var name = obj[keys[0]];
		if(name.contains(value)) result.push(obj);
		var children = obj[keys[1]];
		if(children==null) continue;
		ut.searchJson(value,keys,children,result);
	}
	return result; 
};

/** Object.keys(obj).length  가 IE8 이하에 적용이 안되서 따로 만들었다.   */
ut.size =  function(obj){
	if(obj == null ) return 0; 
	if(obj instanceof Array) return obj.length;
	return ut.getObjectKeys(obj).length;
};

/**
 * 낙타표기법을 대문자 언더바구분 형식으로 변환
 */
ut.camel2under = function(str){
	str.replace(/([A-Z])/g, function(arg){
        return "_"+arg.toLowerCase();
    }).toUpperCase();
};

/** 객체를 path로 참조한다. */
ut.findByPath = function(object,path){
	var pathList =  path instanceof Array ?  path : path.split('.');
	var nowItem = object;
	for(var i=0;i<pathList.length;i++){
		if(nowItem==null) return object; //하나라도 매칭이 안되면 원래 객체 리턴
		var nowPath = pathList[i];
		nowItem = nowItem[nowPath];
	}
	return nowItem;
};

/** 바이트 단위를 예쁘게 잘라준다. EPM 화면조회 */
ut.byteToString = function(byte){
	var defaultUnit = 1024;
	var unit = 'KB';
	var value = ut.roundBy( byte / defaultUnit ,2);
	if(value > 1024){
		value = ut.roundBy( value /  defaultUnit ,2);
		unit = 'MB';
	}
	if(value > 1024){
		value = ut.roundBy( value /  defaultUnit, 2)
		unit = 'GB';
	}
	return value + unit;
};

/** freeTotal을 통으로 보여준다. */
ut.freeTotal = function(freeName,totalName){
	return function(value, row, index){
		var free = ut.findByPath(row,freeName);
		var total = ut.findByPath(row,totalName);
		var useRate = ut.roundBy( (total-free)/total * 100,2,0) ;
		var freeStr = ut.byteToString(free);
		var totalStr = ut.byteToString(total);
		var msg01 = freeStr + ' / ' + totalStr;
		var msg02 = ' (' + useRate + '%)';
		if(useRate > 80) msg02 = msg02.toSpanTag({cursor:'pointer',color:'red','font-weight':'bold'})
		else if(useRate > 50) msg02 = msg02.toSpanTag({cursor:'pointer',color:'blue','font-weight':'bold'})
		else msg02 = msg02.toSpanTag({cursor:'pointer',color:'green','font-weight':'bold'})
		return msg01+msg02;
	};
};

/** hitRate을 통으로 보여준다. */
ut.hitRate = function(hitName,missName){
	return function(value, row, index){
		var hitCount = ut.findByPath(row,hitName);
		hitCount = Number(hitCount);
		var missCount = ut.findByPath(row,missName);
		missCount = Number(missCount);
		var totalCount = hitCount+missCount;
		var hitRate =  ut.roundBy( hitCount / totalCount * 100 , 2 , 0);
		var msg01 = '전체 : ' + totalCount + ' / HIT : ' + hitCount;
		var msg02 = ' (' + hitRate + '% HIT)';
		if(hitRate < 20) msg02 = msg02.toSpanTag({cursor:'pointer',color:'red','font-weight':'bold'})
		else if(hitRate < 50) msg02 = msg02.toSpanTag({cursor:'pointer',color:'blue','font-weight':'bold'})
		else msg02 = msg02.toSpanTag({cursor:'pointer',color:'green','font-weight':'bold'})
		return msg01+msg02;
	};
};
ut.ratio = function(hitName,totalName){
	return function(value, row, index){
		var totalCount = Number(ut.findByPath(row,totalName));
		if(totalCount == 0) return '-';
		var hitCount = Number(ut.findByPath(row,hitName));
		var hitRate =  ut.roundBy( hitCount / totalCount * 100 , 1 , 0);
		var msg01 = ut.format(hitCount);
		var msg02 = ' (' + ut.format(hitRate) + '%)';
		if(hitRate < 20) msg02 = msg02.toSpanTag({cursor:'pointer',color:'red','font-weight':'bold'})
		else if(hitRate < 50) msg02 = msg02.toSpanTag({cursor:'pointer',color:'blue','font-weight':'bold'})
		else msg02 = msg02.toSpanTag({cursor:'pointer',color:'green','font-weight':'bold'})
		return msg01+msg02;
	};
};
ut.division = function(hitName,totalName){
	return function(value, row, index){
		var totalCount = Number(ut.findByPath(row,totalName));
		if(totalCount == 0) return '-';
		var hitCount = Number(ut.findByPath(row,hitName));
		return ut.format(hitCount / totalCount);
	}
}
ut.roundByDivision = function(hitName,totalName,size){
	return function(value, row, index){
		var totalCount = Number(ut.findByPath(row,totalName));
		if(totalCount == 0) return '-';
		var hitCount = Number(ut.findByPath(row,hitName));
		return ut.format(ut.roundBy(hitCount / totalCount, size));
	}
}

ut.escapeHtmlMap = {
	    '&': '&amp;',
	    '<': '&lt;',
	    '>': '&gt;',
	    '"': '&quot;',
	    "'": '&#39;',
	    '/': '&#x2F;'
};

/** 길가다 주워온 코드 */
ut.escapeHtml = function(html){
	return String(string).replace(/[&<>"'\/]/g, function fromEntityMap (s) {
		return ut.escapeHtmlMap[s];
	});
};

/** 
 * 숫자는 넘버 포매팅 해준다. 
 * 문자는 ENUM 변환을 시도해준다.
 * nullValue 디폴트값을 여기에 지정해주게되면 null 체크하여 사용하는 다른 모든 함수들이 제대로 작동하지않는다.
 * nullValue가 필요한경우 따로 지정해서 사용할것
 * */
ut.format = function(value, pattern, nullValue) {
	if(ut.isEmpty(pattern)) pattern =  "#,##0.##";
	if(value==null) return nullValue;
	else if(typeof value == "number") {
		return format(pattern,value);	
	}else if(typeof value == "string") {
		if(true) return value;
	}else if(typeof(value) == 'boolean'){
		return value ? 'Y' : 'N';
	}
	return value;
};

/** 
 * Object에서 dot으로 value를 가져온다.
 * */
ut.find = function(object,dots) {
	if(dots==null) dots = [];
	if(typeof dots == 'string') dots = dots.split('.');
	var chainRow = object;
	for(var i=0;i<dots.length;i++){
		var dot = dots[i];
		chainRow = chainRow[dot];
		if(chainRow==null) return null;
	}
	return chainRow;
};

/** 
 * ut.find 이후 ut.format를 해준다. 단축 메소드
 * nullValue 디폴트값을 여기에 지정해주게되면 null 체크하여 사용하는 다른 모든 함수들이 제대로 작동하지않는다.
 * nullValue가 필요한경우 지정해서 사용할것
 * */
ut.findValue = function(object,dots,pattern,nullValue) {
	if(ut.isEmpty(pattern)) pattern =  "#,##0.##";
	var data = ut.find(object,dots);
	return ut.format(data,pattern,nullValue);
};

/** 5를 입력할경우 0~4 까지의 랜덤한 숫자가 리턴된다. */
ut.random = function(max) {
	return Math.floor(Math.random() * max) ;
};

/** 문자열의 pixel 값이 리턴된다. */
ut.getTextWidth = function(text) {
	var tmp = $('<span />').html(text).appendTo($('body'));
	var width = tmp.width(); tmp.remove();
	return width;
}


/** jhkwon@11h11m.com을 @ 기준으로 3자리만 마스킹 maskText(test,'@',3);
* 기준이 없다면 maskText(test,null,3);*/
ut.maskText = function(text,op,num){
	var tmp = text.split(op);
	var idx = tmp[0].length
	var maskText = idx<=num? tmp[0].slice(0,1) : tmp[0].slice(0,idx-num);
	var maskMax = idx<=num? idx-1: num; 
	for(var i=0;i<maskMax;i++) maskText = maskText +'*';
	return maskText+op+tmp[1];
};

/** 동적으로 form 생성 - post 전송 시 사용 */
ut.postForm = function(url, param) {
	var form = $('<form>', { action: url, method: 'post' });
	for(var key in param) {
		form.append($('<input>', {
			type: 'hidden',
			name: key,
			value: param[key]
		}));
	}
	return form;
}

/** 동적으로 form 생성 - post 전송 시 사용 */
ut.postFormSubmit = function(url, param) {
	ut.postForm(url, param).appendTo('body').submit();
}

ut.tensCheck = function(numberData){
	return $.isNumeric(Number(numberData)) && Number(numberData) % 10 == 0;
}

//세자리 콤마
ut.numToComma = function(number){
	var parseNum = parseInt(number);
	if(parseNum){
		return parseNum.toLocaleString('en');
	}else{
		return number;
	}
}

//현재날짜 +-일수
ut.todayAdd = function(add){
	var diff = add == undefined || isNaN(add) ? 0 : add;
	var today = new Date();
	today.setDate(today.getDate() + diff);
	var y = today.getFullYear();
	var m = today.getMonth()+1 < 10 ? "0"+today.getMonth()+1 : today.getMonth()+1;
	var d = today.getDate() < 10 ? "0"+today.getDate() : today.getDate();
	
	return y+"-"+m+"-"+d;
}

//핸드폰 대시
ut.phoneHyphen = function(number){
	if(!number) return ''; 
	else if(number.length == 11) return number.substr(0,3)+"-"+number.substr(3,4)+"-"+number.substr(7,4);
	else if(number.length == 10) return number.substr(0,3)+"-"+number.substr(3,3)+"-"+number.substr(6,4);
	else return number;
}

//리다이렉트
ut.redirect = function(url, key, value){
	url += '?';
	for(var i in arguments){
		if(i == 0) continue;
		if(i%2 == 1) url +=  arguments[i]+"=";
		else url +=  arguments[i]+"&";
	}
	window.location.href = url;
}

//이메일체크
ut.isEmail = function(email) {
	var regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
	return regExp.test(email);	

}

/** 제이쿼리 커스텀 function */
//제이쿼리 ajax post요청
$.cpost = function(url, data, successFn, async){
	$.ajax({
		url: url,
		type: 'post',
		data: data,
		async: (!async ? true : false),
		success: successFn,
		error: function(e){
			console.log(e);
		}
	});
}

//제이쿼리 ajax get요청
$.cget = function(url, data, successFn, async){
	$.ajax({
		url: url,
		type: 'get',
		data: data,
		async: (!async ? true : false),
		success: successFn,
		error: function(e){
			console.log(e);
		}
	});
}

ut.ozToG = function (oz, type) {
	return type === 'gold'
		? BigNumber(oz).div(0.001).times(0.031103).toFixed(3, 3)
		: BigNumber(oz).div(0.001).times(0.031103).toFixed(3, 3);
}

ut.getParam = function(key){
	return new URLSearchParams(location.search).get(key);
}

ut.getByte = function(str){
	var byte = 0;
	for (var i=0; i<str.length; ++i) {
		(str.charCodeAt(i) > 127) ? byte += 2 : byte++ ;

	}
	return byte;
}

ut.nullToStr = function(str){
	return str ? str : '';
}

ut.clearForm = function(formId){
	//초기화
	$("#"+formId+" input[type=text]").each(function e(){$(this).val('');});
	$("#"+formId+" input[type=hidden]").each(function e(){$(this).val('');});
	$("#"+formId+" input[type=number]").each(function e(){$(this).val('0');});
	$("#"+formId+" input[type=password]").each(function e(){$(this).val('');});
	$("#"+formId+" input[type=checkbox]").each(function e(){$(this).prop("checked",false)});
	$("#"+formId+" input[type=radio]:eq(0)").prop("checked",true);
	$("#"+formId+" textarea").each(function e(){$(this).val('');});
	$("#"+formId+" select").each(function e(){
		$(this).find("option:eq(0)").prop("selected", true).parent().change()
	});
	$("#"+formId+" input[name=seq]").val(0);
}

ut.objInputMapping = function(formId, el={}){
	$("#"+formId+" input").each(function(){
		var name = $(this).attr("name");
		if(!el.hasOwnProperty(name)){
			return true;
		}

		var type = $(this).attr("type");
		switch (type){
			case "text":
			case "hidden":
			case "password":
			case "number":
				$(this).val(el[name]);
				break;
			case "radio":
			case "checkbox":
				$("#"+formId+" input[name="+name+"][value="+el[name]+"]").prop("checked",true);
				break;
		}
	});
	$("#"+formId+" select").each(function(){
		var name = $(this).attr("name");
		if(!el.hasOwnProperty(name)){
			return true;
		}
		$(this).val(el[name]).change();
	});
	$("#"+formId+" textarea").each(function(){
		var name = $(this).attr("name");
		if(!el.hasOwnProperty(name)){
			return true;
		}
		$(this).val(el[name]);
	});
}

ut.isEmptyForm = function(formId,excluedeNames = []){
	var isEmptyForm = false;
	$("#"+formId+" input").each(function(){
		if(excluedeNames.includes($(this).attr("name"))){
			return true;
		}
		if($(this).val() == ""){
			isEmptyForm = true;
			return false;
		}
	});
	return isEmptyForm;
}