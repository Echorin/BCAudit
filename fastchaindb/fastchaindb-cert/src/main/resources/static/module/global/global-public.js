var paramUrl={
	bsUrl:'/controls/query/listbsview.json',
	defalutModalW:820,
	defalutModalH:508
}
/*图片类型*/
var fileTypes = new Array("jpg","mpeg","png","bmp");//定义可支持的文件类型数组
/*屏幕大小*/
var windows_width = window.screen.width;
var windows_height = window.screen.height;
/**
 * 验证函数 email: phone:区号+号码，区号以0开头，3位或4位 mobile:11位数字，以1开头 money:货币 number:数字date:日期
 * account:字母、数字、下划线组成，字母开头，4-16位
 */
function dataValid(obj) {
	this.obj=obj;
	this.val=obj.val();
	this.formats=obj.attr("format");
	this.dbfield=obj.attr("dbfield");
	this.des = obj.attr("des");
	this.errmsg="";
	this.init();
}

dataValid.prototype = {
	init:function(){
    	if (!this.isNull(this.formats)) {
    		// 删除警告信息
    		this.obj.removeClass("warningclass");
    		//$("#err_"+this.dbfield).remove();//去掉提示框
    		
	    	var formatSplit=this.formats.split('||');
	    	for(var i=0;i<formatSplit.length;i++){
	    		 if(!this.check(formatSplit[i])){
	    		 	return false;
	    		 }
	    	}
    	}
	},
	check:function(format){
		if(this.isNull(this.des)){
			this.des=this.dbfield;
		}
  		// 判断是否为空
		if (format == "notempty") {
       	 	if (this.isNull(this.val)) {
       	 		 // var t=this.des+"格式有误";
       	 		 this.errmsg="请输入"+this.des;
	             this.obj.addClass("warningclass");
	             //this.obj.parent().append("<p id='err_"+this.dbfield+"' class='plaintext'>"+this.errmsg+"</p>");//去掉提示框
	             return false;
	       	}
	       	return true;
	    }
		
		// 如果为空不判断
		if(this.isNull(this.val)){
			return true;
		}
		
	    // 数字类型（单独判断，并且格式化）
    	if(format.indexOf("number")!=-1){
    		if(!strtools.isNumeric(this.obj.val()))
    		{
	    	 	this.errmsg=this.des+"格式输入有误";
				this.obj.addClass("warningclass");
				return false;
    		}
    		
    		var places=0;
    		var fmt=format.split(':');
    		if(fmt.length==2){
    			 if(strtools.isNull(fmt[1]))
					 places=0;
				 else
					 places=fmt[1].substr(0,1);
    		}
    		this.obj.val(this.fNumber(places));
    		return true;
    	}
    	
    	// 数字类型（单独判断，并且格式化）
    	if(format.indexOf("date")!=-1){
    		var places="yyyy-MM-dd";
    		var fmt=format.split('{');
    		if(fmt.length==2){
    			 if(strtools.isNull(fmt[1]))
					 places="yyyy-MM-dd";
				 else
					 places=fmt[1].substr(0,fmt[1].length-1);
    		}
    		this.obj.val((new Date(this.obj.val())).format(places));
    		return true;
    	}
    	
    	// 其他类型(手机、EMAIL)
    	if(!this.fReg(format)){
		 	// var t=this.des+"格式有误";
		 	this.errmsg=this.des+"格式输入有误";
			this.obj.addClass("warningclass");
			//this.obj.parent().append("<p id='err_"+this.dbfield+"' class='plaintext'>"+this.errmsg+"</p>");//去掉提示框
    		return false;
    	}
    	
    	return true;
	},
	// 验证是否是数字类型
	isNumeric : function(num){
        //return /^\d+(\.\d+)?$/.test(num);
		return /^-?[0-9]*.?[0-9]*$/.test(num);
    },
	fNumber:function(places){
		var s=strtools.fTrim(this.val);
	    if(!this.isNumeric(s))  {  
	    	return 0;
	    }
	    return parseFloat(s).toFixed(places)
	},
	fReg:function(format)
	{
		var reg;
		if(format=="email"){
			reg= /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
		}
		else if(format=="mobile"){
			reg= /^1\d{10}$/;
		}
		else if(format=="phone"){ 
			reg=/^0\d{2,3}-?\d{7,8}$/;
		}
		else if(format=="number"){
			reg= /^(-)?\d+(\.\d+)?$/;
		}
		else if(format=="money"){
			reg= /^[0-9]+[\.][0-9]{0,3}$/;
		}
		else if(format=="account"){
			reg= /^[a-zA-z]\w{3,15}$/;
		}
		else{
			return false;
		}
		return reg.test(this.val);
	},
	isNull:function(val){
		if(val==undefined || val==""){
			return true;
		}
		return false;
	}
}


/**
 * 公用方法
 */
var windows_top = 0;
var windows_left = 0;

var kevin={
	winopen:function(url, width, height) {

				
		if(width==undefined)
			width=paramUrl.defalutModalW;
		if(height==undefined)
			height=paramUrl.defalutModalH;
		
	    if (width == 0) width = windows_width;
	    if (height == 0) height = windows_height;
	    this.setcenter(width, height);
	    window.open(url, "", "left=" + windows_left + ",top=" + windows_top + ",width=" + width + ",height=" + height + ",dependent=no,scrollbars=yes,resizable=no,toolbar=no,status=no,directories=no,menubar=no,resizable=yes");
	},
	setcenter:function(width, height) {
	    if (windows_width > width)
	        windows_left = (windows_width - width) / 2;
	    if (windows_height > height)
	        windows_top = (windows_height - height) / 2 - 40;
	},
	loadProgress:function(){
		if (document.getElementById("progressBg") == null) {
		    var divloading1 = document.createElement('div');
		    divloading1.id = "progressBg";
		    divloading1.className = "progressBg";
		    document.body.appendChild(divloading1);
		
		    var divloading2 = document.createElement('div');
		    divloading2.id = "progressBar";
		    divloading2.className = "progressBar";
		    divloading2.innerHTML = "数据加载中，请稍等...";
		    document.body.appendChild(divloading2);
		}
		else {
		    document.getElementById("progressBg").style.display = "block";
		    document.getElementById("progressBar").style.display = "block";
		}
	},
	closeProgress:function(){
		if(document.getElementById("progressBg")!=null){
		    document.getElementById("progressBg").style.display = "none";
		    document.getElementById("progressBar").style.display = "none";
	    }
	},/* option:{id:'1'} */
	openModal:function(url,title,width,height,maxmin){
		parent.openModal(url,title,width,height,maxmin);
	},
	closeModal:function(fnName,param){
		parent.closeModal(fnName,param);
	},
	openBsModal:function(url){
		$('#myModal').modal({
		    remote: url
		}).on('hide.bs.modal', function () {
			$(this).removeData();
		});
	},
	closeBsModal:function(url){
		$('#myModal').modal('hide');
	},
	alert:function(msg,icon){
		if(icon==undefined)//笑脸
			icon=6;
		try
		{
			layer.msg(msg,{icon : icon,time : 2000});//,offset : [ '5px', window.screen.width-300+'px' ]
		}catch(ex){
			layer.msg(msg,{icon : icon,time : 2000});
		}
	},
	alertErr:function(msg,icon){
		if(icon==undefined)//笑脸
			icon=5;
		layer.msg(msg,{icon : icon,time : 2000,offset : [(window.screen.height/2)-150+'px',(window.screen.width/2)-20+'px' ]});
	},
	confirm : function(content, url, datamap, fn) {
		layer.confirm(content, {
			icon : 3, title : '提示'
		},function(index) {
			$.ajaxjson(url, datamap, function(d) {
				if(d.result == 1) {
                    kevin.alert(d.msg);
				}else {
					layer.close(index);
					fn(d);
				}
	        });
		});
	},
	//删除
	delObj:function(tableurl,key,fn){
		var url = basepath+"/controls/query/listdelete.json";
        var dataMap = { tableurl:tableurl, formCmd: "delete", formPkid: key };

        kevin.confirm("是否删除？", url, dataMap, function(d) {
        	fn(d);
        });
	},
	//检测上下限
	checkLimit:function(lowerLimit, topLimit) {
		if(parseFloat(lowerLimit) > parseFloat(topLimit)) {
			return false;
		}
		return true;
	},
	//获取查询条件
	// 获取表单数据，生成JSON,isnull=false;值为空不取
	getQueryObj:function(formid,cmdtxt) {
		if (cmdtxt == undefined)
			cmdtxt = "";
		var query = {}; //行数据
		var filter = []; //对象
		query["cmdtxt"] = cmdtxt;
		$("#" + formid+" :input").each(function () {
			if ($(this).attr("dbfield") != undefined) {
				var rows = {}; //行数据
				if (!strtools.isNull($(this).val())) {
				    rows["value"] = strtools.getVal(($(this).val()).trim());
				    rows["dbfield"] = strtools.getVal($(this).attr("dbfield"));
					rows["compare"] = strtools.getVal($(this).attr("compare"));
					rows["logical"] = strtools.getVal($(this).attr("logical"));
					filter.push(rows);
				}
			}
		});
		query["filter"] = filter;
		return query;
	},
	// 获取表单数据，生成JSON,isnull=false;值为空不取
	getFormObj:function(formid) {
	    var flag = true;
	    var errmsg="";
	    var json = "";
	    var item = {};
	    
	    // $("#" +
		// formid).find("select,input,radio,textarea,checkbox").each(function ()
		// {
	   $("#" + formid+" :input").each(function () {
	   
	    	// 只读属性(某些字段只显示不提交)
	    	if($(this).attr("readmod") != undefined){
	    		return;
	    	}
	 
	        if ($(this).attr("dbfield") != undefined) {
	        	// 验证
	        	var err = new dataValid($(this)).errmsg;
				if (err!="") {
					errmsg+=err+"<br>";
	            	flag=false;
	            }
	            
	           var field = $(this).attr("dbfield");
	           if ($(this).attr("type") == "checkbox") {
	        	   //高版本prop,低版本attr
	        	   if ($(this).prop("checked")) {
	                   item[field] = 1;
	               }
	               else {
	                   item[field] = 0;
	               }
	           }
	           else
	           {
	        	  var fmt=$(this).attr("format");
	        	  if (fmt != undefined){
	        		  if( fmt.indexOf("number")!=-1 || fmt.indexOf("date")!=-1) {
		        		  if ( strtools.isNull($(this).val())) {
		        			  item[field] = null;
		        		  }
		        		  else{
		        			  item[field] = $(this).val();
		        		  }
	        		  }
	        		  else
		        	  {
		        		   item[field] = $(this).val();
		        	  }
	              }
	        	  else
	        	  {
	        		   item[field] = $(this).val();
	        	  }
	           }
	        }
	    });
	    
	    if (item == "") {
	        errmsg = errmsg + "<br/>表单必须输入";
	        flag=false;
	    }
	    
	    return [flag,item,errmsg]

	    /*
		 * debugger; if (flag == false) return false; else { return item; }
		 */
	},
	loadForm:function(formid, data) {
		if(data==null)return;
	    var obj = data;//eval('(' + data + ')')["rows"][0];//[{"name":"aaa","email":"123","loginFlag":"1"}]
	    $("#" + formid).find("select,input,radio,textarea,span,em").each(function () {
		//$("#" + formid+" :input").each(function () {
	    	if($(this).attr("dbfield") == undefined)
	    		return;
	    	
	    	if(obj[$(this).attr("dbfield")]==undefined)
	    		return;
	    	
	        var vl = obj[$(this).attr("dbfield")];
	        var tagName = $(this)[0].tagName;
	        
	        //判断类型
	        if (tagName == "EM" || tagName == "SPAN") {
	        	$(this).html(kevin.loadFormData($(this),vl));
	        }
	        else if (tagName == "SELECT") {
	            $(this).val(vl);
	        }
	        else {
	            if ($(this).attr("type") == "checkbox") {
	                if (vl == "1") {
	                    $(this).attr("checked",true);
	                }
	                else {
	                    $(this).attr("checked",false);
	                }
	            }
	            else {
	                $(this).val(kevin.loadFormData($(this),vl));
	            }
	        }
	    });
	},
	loadFormData:function(evt,val){
		 var format=evt.attr("format");
		 if (format != undefined && format!="") {
			 if(format.indexOf("number")!=-1){
				 var places=0;
				 var fmt=format.split(':');
				 if(fmt.length==2){
					 if(strtools.isNull(fmt[1]))
						 places=0;
					 else
						 places=fmt[1].substr(0,1);
				 }
				 return strtools.fNumber(val,places);
			 }
			 else if(format.indexOf("date")!=-1){
	    		var places="yyyy-MM-dd";
	    		var fmt=format.split('{');
	    		if(fmt.length==2){
	    			 if(strtools.isNull(fmt[1]))
						 places="yyyy-MM-dd";
					 else
						 places=fmt[1].substr(0,fmt[1].length-1);
	    		}
	    		return (new Date(val)).format(places);
	    	}
		 }
		 return val;
	},
	getIEVersion:function() {
        var sAgent = window.navigator.userAgent;
        var Idx = sAgent.indexOf("MSIE");
        // If IE, return version number.
        if (Idx > 0)
            return parseInt(sAgent.substring(Idx+ 5, sAgent.indexOf(".", Idx)));
        // If IE 11 then look for Updated user agent string.
        else if (!!navigator.userAgent.match(/Trident\/7\./))
            return 11;
        else
            return 0; //It is not IE
    },
	getBetweenDate:function(target, base, fn) {
		var text = "实际取样时间与取样要求时间相差了" + Math.abs(parseInt(target.substring(11, 13)) - parseInt(base))+ "个小时";
		parent.layer.confirm(text, {
			icon : 3, title : '提示'
		},function(index) {
			fn();
		});
	}
}

/**
 * 字符工具
 */
var strtools={
    yesNo:function(obj){
        if(obj==undefined || obj==""){
            return "否";
        }
        if(obj=="true"){
        	return "是";
		}
        return "否";
    },
	isNull:function(obj){
		if(obj==undefined || obj==""){
			return true;
		}
		return false;
	},
	getVal:function(obj){
		if(this.isNull(obj)){
			return "";
		}
		return obj;
	},
	isNumeric:function(obj){
        //return /^\d+(\.\d+)?$/.test(obj);//正数
		//return /^-?[0-9]*.?[0-9]*$/.test(obj);
	    return !isNaN(obj);
    },
    isImage: function(obj){
    	if(strtools.isNull(obj))return false;
    	var newFileName = obj.split('.');
    	newFileName = newFileName[newFileName.length-1];
    	for(var i=0;i<fileTypes.length;i++){
    	　　if(fileTypes[i] == newFileName.toLowerCase()){
    	　　　　return true;
    	　　}
    	}
    	return false;
    },
	fMoney:function(number, places) {
	    number = number || 0;
	    places = !isNaN(places = Math.abs(places)) ? places : 2;
	    var thousand = ",";
	    var decimal = ".";
	    var negative = number < 0 ? "-" : "",
	            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
	            j = (j = i.length) > 3 ? j % 3 : 0;
	    return negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
	},
	fMoneyToNumber:function(money) {
	    return parseFloat(money.replace(/[^0-9-.]/g, ''));
	},
	fNumber:function(obj, places){
		if(places==undefined)places=0;
		if(this.isNull(obj))return;
		if(!this.isNumeric(obj))return 0;
	    return parseFloat(obj).toFixed(places)
	},
	fTrim:function(str){ 
        return str.replace(/(^\s*)|(\s*$)/g, ""); 
	},
	numAdd:function(arg1, arg2) {  
	    var r1, r2, m;  
	    try {  
	        r1 = arg1.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	        r1 = 0;  
	    }  
	    try {  
	        r2 = arg2.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	        r2 = 0;  
	    }  
	    m = Math.pow(10, Math.max(r1, r2));  
	    return (arg1 * m + arg2 * m) / m;  
	},
	numSub:function(arg1, arg2) {  
	    var r1, r2, m, n;  
	    try {  
	        r1 = arg1.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	        r1 = 0;  
	    }  
	    try {  
	        r2 = arg2.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	        r2 = 0;  
	    }  
	    m = Math.pow(10, Math.max(r1, r2));  
	     //last modify by deeka  
	     //动态控制精度长度  
	    n = (r1 >= r2) ? r1 : r2;  
	    return ((arg1 * m - arg2 * m) / m).toFixed(n);  
	},
	//乘法函数  
	numMul:function(arg1, arg2) {  
	    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();  
	    try {  
	        m += s1.split(".")[1].length;  
	    }  
	    catch (e) {  
	    }  
	    try {  
	        m += s2.split(".")[1].length;  
	    }  
	    catch (e) {  
	    }  
	    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);  
	},
	//除法函数  
	numDiv:function(arg1, arg2) {  
	    var t1 = 0, t2 = 0, r1, r2;  
	    try {  
	        t1 = arg1.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	    }  
	    try {  
	        t2 = arg2.toString().split(".")[1].length;  
	    }  
	    catch (e) {  
	    }  
	    with (Math) {  
	        r1 = Number(arg1.toString().replace(".", ""));  
	        r2 = Number(arg2.toString().replace(".", ""));  
	        return (r1 / r2) * pow(10, t2 - t1);  
	    }  
	},
	/**
	 * js截取字符串，中英文都能用
	 * 
	 * @param str：需要截取的字符串
	 * @param len:
	 *            需要截取的长度
	 */
	cutstr:function(str, len) {
	    var str_length = 0;
	    var str_len = 0;
	    str_cut = new String();
	    str_len = str.length;
	    for (var i = 0; i < str_len; i++) {
	        a = str.charAt(i);
	        str_length++;
	        if (escape(a).length > 4) {
	            // 中文字符的长度经编码之后大于4
	            str_length++;
	        }
	        str_cut = str_cut.concat(a);
	        if (str_length >= len) {
	            str_cut = str_cut.concat("...");
	            return str_cut;
	        }
	    }
	    // 如果给定字符串小于指定长度，则返回源字符串；
	    if (str_length < len) {
	        return str;
	    }
	},
	strlen:function(str) {
		var len = 0;
        for (var i = 0; i < str.length; i++) {
            var c = str.charCodeAt(i);
            //单字节加1 
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
                len++;
            }
            else {
                len += 2;
            }
        }
        return len;
	},
	dateCmp:function(dt1,dt2){//dt1>dt2 false,否则true
		 var oDate1 = new Date(dt1);
		 var oDate2 = new Date(dt2);
		 if(oDate1.getTime() >= oDate2.getTime()){
		        return false;
		 }
		 return true;
	},
	//时间格式转换
    formatDate:function(value, fmt) {
        if (value == null || value == "") {
            return "";
        }
        value = value.replace(/-/g, "/")
        var date = new Date(value);
        if (!fmt) {
            fmt = 'yyyy-MM-dd HH:mm:ss';
        }

        var o = {
            "M+": date.getMonth() + 1, //月份
            "d+": date.getDate(), //日
            "H+": date.getHours(), //小时
            "m+": date.getMinutes(), //分
            "s+": date.getSeconds(), //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));

        return fmt;
    }
}

//给Number类型增加一个add加法函数，，使用时直接用 .add 即可完成计算，如num1.add(num2)
Number.prototype.add = function (arg) {  
    return strtools.numAdd(arg, this);  
};  
//给Number类型增加一个mul乘法函数，使用时直接用 .mul 即可完成计算。   
Number.prototype.mul = function (arg) {  
    return strtools.numMul(arg, this);  
};   
//给Number类型增加一个sub减法函数，，使用时直接用 .sub 即可完成计算。   
Number.prototype.sub = function (arg) {  
    return strtools.numSub(this, arg);  
};
//给Number类型增加一个div除法函数，，使用时直接用 .div 即可完成计算。   
Number.prototype.div = function (arg) {  
    return strtools.numDiv(this, arg);  
};
//格式化小数位，四舍五入
Number.prototype.toFixed2=function (d) { 
	var s=this+""; 
    if(!d)d=0; 
     if(s.indexOf(".")==-1)s+="."; 
     s+=new Array(d+1).join("0"); 
     if(new RegExp("^(-|\\+)?(\\d+(\\.\\d{0,"+(d+1)+"})?)\\d*$").test(s)){
         var s="0"+RegExp.$2,pm=RegExp.$1,a=RegExp.$3.length,b=true;
        if(a==d+2){
             a=s.match(/\d/g); 
              if(parseInt(a[a.length-1])>4){
               for(var i=a.length-2;i>=0;i--){
                     a[i]=parseInt(a[i])+1;
                      if(a[i]==10){
                       a[i]=0;
                     b=i!=1;
                  }else break;
                }
            }
             s=a.join("").replace(new RegExp("(\\d+)(\\d{"+d+"})\\d$"),"$1.$2");

         }if(b)s=s.substr(1); 
        return (pm+s).replace(/\.$/,"");
     }
     return this+"";           
};

/**
 * HashTable var map = new HashTable();map.add(key,value);
 */
function HashTable() {
    var size = 0;
    var entry = new Object();
    this.add = function (key, value) {
        if (!this.containsKey(key)) {
            size++;
        }
        entry[key] = value;
    }
    this.getValue = function (key) {
        return this.containsKey(key) ? entry[key] : null;
    }
    this.remove = function (key) {
        if (this.containsKey(key) && (delete entry[key])) {
            size--;
        }
    }
    this.containsKey = function (key) {
        return (key in entry);
    }
    this.containsValue = function (value) {
        for (var prop in entry) {
            if (entry[prop] == value) {
                return true;
            }
        }
        return false;
    }
    this.getValues = function () {
        var values = new Array();
        for (var prop in entry) {
            values.push(entry[prop]);
        }
        return values;
    }
    this.getKeys = function () {
        var keys = new Array();
        for (var prop in entry) {
            keys.push(prop);
        }
        return keys;
    }
    this.getSize = function () {
        return size;
    }
    this.clear = function () {
        size = 0;
        entry = new Object();
    }
}


/**
 * 对Date的扩展，将 Date 转化为指定格式的String 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q)
 * 可以用 1-2 个占位符 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * eg: (newDate()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 (new
 * Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04 (new
 * Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04 (new
 * Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04 (new
 * Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */       
Date.prototype.format=function(fmt) {
	if(fmt==null)return "";
    var o = {        
    "M+" : this.getMonth()+1, // 月份
    "d+" : this.getDate(), // 日
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, // 小时
    "H+" : this.getHours(), // 小时
    "m+" : this.getMinutes(), // 分
    "s+" : this.getSeconds(), // 秒
    "q+" : Math.floor((this.getMonth()+3)/3), // 季度
    "S" : this.getMilliseconds() // 毫秒
    };        
    var week = {        
    "0" : "\u65e5",        
    "1" : "\u4e00",        
    "2" : "\u4e8c",        
    "3" : "\u4e09",        
    "4" : "\u56db",        
    "5" : "\u4e94",        
    "6" : "\u516d"       
    };        
    if(/(y+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));        
    }        
    if(/(E+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);        
    }        
    for(var k in o){        
        if(new RegExp("("+ k +")").test(fmt)){        
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
        }        
    }        
    return fmt;        
}
