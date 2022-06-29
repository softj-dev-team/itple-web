
/** 해당 Array내용물을 map으로 간주하고 key값이 value와 일치하는것을 찾아낸다. */ 
Array.prototype.findByKey = function(keyName,value) {
	var array = this;
	for(var i=0;i<array.length;i++){
		var obj = array[i];
		if(obj==null) continue;
		if(obj[keyName]==value) return  obj;
	}
	return null;
};

/** addAll과 비슷하다. 단일객체/리스트 모두 추가 가능하다.  */
Array.prototype.pushAll = function() {
	for(var i=0;i<arguments.length;i++){
		var arg = arguments[i];
		if(arg==null) continue;
		if(arg instanceof Array){
			for(var index = 0; index < arg.length ; index ++) this.push(arg[index]); 
		}else this.push(arg);
	}
	return this;
};

//추가
/** 자바의 subList를 흉내낸다.  */
Array.prototype.subList = function(start,length) {
	var list = this;
	var result = [];
	if(list.length < start) throw new Error('start의 크기가 너무 큽니다');
	for(var i=start;i<list.length;i++){
		if(result.length >= length) break;
		result.push(list[i]);
	}
	return result;
};

/** 해당 키값만 쏙 빼서 리턴 - 일단 1뎁스만 */
Array.prototype.getKeys = function(key) {
	var list = this;
	var result = [];
	list.forEach(function(item) {
		result.push(item[key]);
	});
	return result;
};

/** 다른 리스트 안에 없는 것만 리턴 - key값 비교 */
Array.prototype.getUniques = function(key, other) {
	var list = this;
	var result = [];
	var keys = other.getKeys(key);
	list.forEach(function(item) {
		if($.inArray(item[key], keys) == -1) result.push(item);
	});
	return result;
};

/** 다른 리스트 안에 없는 것만 리턴 - key값 비교 */
Array.prototype.getUniques2 = function(key, other) {
	var list = this;
	var result = [];
	list.forEach(function(item) {
		if($.inArray(item[key], other) == -1) result.push(item);
	});
	return result;
};

Array.prototype.addNullObject = function(length, nullObj) {
	for(var i = this.length; i < length; i++) {
		this.push(nullObj || {});
	}
	return this;
}