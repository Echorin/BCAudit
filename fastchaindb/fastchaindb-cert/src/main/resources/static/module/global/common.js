var windows_width = window.screen.width;
var windows_height = window.screen.height;
var windows_top = 0;
var windows_left = 0;

var openWidth=860;//打开窗口宽度
var openHeight=520;//打开窗口高度
var loginout="/system/user/loginout.html";

function clearpage(objstr) {
    if ($) {
        $(':text,select,textarea,:hidden,:checkbox', '#' + objstr).val("");
    }
}
function winopenfull(url) {
    window.open(url, "big", "fullscreen=yes");
}
function winopen(url, width, height) {
	if(width==undefined)
		width=openWidth;
	if(height==undefined)
		height=openHeight;

    if (width == 0) width = window.screen.width;
    if (height == 0) height = window.screen.height;
    setcenter(width, height);
    window.open(url, "", "left=" + windows_left + ",top=" + windows_top + ",width=" + width + ",height=" + height + ",dependent=no,scrollbars=yes,resizable=no,toolbar=no,status=no,directories=no,menubar=no,resizable=yes");
}
function setcenter(width, height) {
    if (windows_width > width)
        windows_left = (windows_width - width) / 2;
    if (windows_height > height)
        windows_top = (windows_height - height) / 2 - 40;
}
/*数字格式化成货币*/
function fMoney(number, places) {
    number = number || 0;
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    var thousand = ",";
    var decimal = ".";
    var negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
    return negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
}
/*货币格式化成数字*/
function fNumber(money) {
    return parseFloat(money.replace(/[^0-9-.]/g, ''));
}

function formatDate(v,f)
{
	return v;
}

/*公共函数
var clientRequest = new Object();clientRequest = getRequest();var pkid = clientRequest["id"];
*/
function getRequest() {
    var url = location.search; //获取url中"?"符后的字串

    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        if (str.indexOf("&") != -1) {
            strs = str.split("&");
            for (var i = 0; i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
            }
        } else {
            var key = str.substring(0, str.indexOf("="));
            var value = str.substr(str.indexOf("=") + 1);
            theRequest[key] = unescape(value);
        }
    }
    return theRequest;
}

function clsDialog(){
	parent.clsDialog();
}

function alertCls(msg,isBind){
	alertMsg(msg);
    setTimeout(function () {

    	if(isBind==undefined){
    		parent.clsDialog("loadPage");
    	}else{
    		parent.clsDialog();
    	}
    	
    }, 1000);
}



//弹出消息框,type:error,warning,success,tips
function alertMsg(msg,type) {
	 var timer=1000;
	 if(type!=undefined){
		 timer=2000;
     }
 
    var obj = document.getElementById("alertMsg");
    if (obj == null) {
        var divMsg = document.createElement('div');
        divMsg.id = "alertMsg";
        divMsg.innerHTML = msg;
        divMsg.className = "alertmsg";
        document.body.appendChild(divMsg);
        
        setTimeout(function () {
            document.getElementById("alertMsg").style.display = "none";
        }, timer);
    }
    else {
    	if(obj.style.display=="none"){
	    	obj.style.display = "";
	    	obj.innerHTML = msg;
	    	
	    	  setTimeout(function () {
	              document.getElementById("alertMsg").style.display = "none";
	          }, timer);
    	}
    }
}

/*HashTable
var map = new HashTable();map.add(key,value);
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
* js截取字符串，中英文都能用
* @param str：需要截取的字符串
* @param len: 需要截取的长度
*/
function cutstr(str, len) {
    var str_length = 0;
    var str_len = 0;
    str_cut = new String();
    str_len = str.length;
    for (var i = 0; i < str_len; i++) {
        a = str.charAt(i);
        str_length++;
        if (escape(a).length > 4) {
            //中文字符的长度经编码之后大于4
            str_length++;
        }
        str_cut = str_cut.concat(a);
        if (str_length >= len) {
            str_cut = str_cut.concat("...");
            return str_cut;
        }
    }
    //如果给定字符串小于指定长度，则返回源字符串；
    if (str_length < len) {
        return str;
    }
}
/**     
* 对Date的扩展，将 Date 转化为指定格式的String     
* 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q) 可以用 1-2 个占位符     
* 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)     
* eg:     
* (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423     
* (new Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04     
* (new Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04     
* (new Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04     
* (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18     
*/       
Date.prototype.format=function(fmt) {        
    var o = {        
    "M+" : this.getMonth()+1, //月份        
    "d+" : this.getDate(), //日        
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时        
    "H+" : this.getHours(), //小时        
    "m+" : this.getMinutes(), //分        
    "s+" : this.getSeconds(), //秒        
    "q+" : Math.floor((this.getMonth()+3)/3), //季度        
    "S" : this.getMilliseconds() //毫秒        
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

function loadProgress ()
{
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
}
function closeProgress ()
{
	if(document.getElementById("progressBg")!=null)
	{
	    document.getElementById("progressBg").style.display = "none";
	    document.getElementById("progressBar").style.display = "none";
    }
}

$.ajaxSave=function (url, dataMap, fnSuccess) {
	$.ajaxjson(url, dataMap, function (d) {
		if(d.result==0){
			alert("操作成功");
			window.close();
			window.opener.bindreload();
		}
		else
		{
			fnSuccess(d);
		}
	});
}

$.ajaxjson = function (url, dataMap, fnSuccess) {
	loadProgress(); 
    $.ajax({
        type: "POST",
        url: url,
        async: false,
        data: dataMap,
        dataType: "json",
        beforeSend: function () { },
        complete: function () {closeProgress();},
        error:function(d){
        	//登录过期
        	if(d.responseText=="未登录"){
        		parent.userLogin();
        	}else{
        		alert(d.responseText);
        	}
        },
        success: function (data) { 
	         closeProgress();
	         var d = eval(data);
	         if(d.result==2){
	        	 parent.userLogin();
	         	return;
	         }
	         else if(d.result==-1){
	         	alert(d.msg);
	         	return;
	         }
	         fnSuccess(d);
        }
    });
}

/** 目前就查询列表控件用到 */
$.ajaxjsonComplete = function (url, dataMap, fnComplete,fnSuccess) {
	loadProgress(); 
    $.ajax({
        type: "POST",
        url: url,
        async: true,
        data: dataMap,
        dataType: "json",
        beforeSend: function () { },
        complete: function (d) {closeProgress();fnComplete(d);},
        error:function(d){
        	//登录过期
        	if(d.responseText!=undefined){
	        	if(d.responseText=="未登录"){
	        		parent.userLogin();
	        	}else{
	        		alert(d.responseText);
	        	}
        	}
        },
        success: function (d) { 
	         var dt = eval(d);
	         if(dt.result==2){
	         	parent.userLogin();
	         }
	         else if(dt.result==-1){
	         	alert(dt.msg);
	         }
	         fnSuccess(dt);
        }
    });
}


//TableID:锁定表，FixColumnNumber：锁定列个数，width：显示的宽度，height：显示的高度
function FixTable(TableID, FixColumnNumber, width, height) {
    //fixtable_tableLayout：最外框
    //fixtable_tableHead:锁定头
    //fixtable_tableData:表格内容（包含头信息只是在下层）

//    if ($("#" + TableID + "_tableLayout").length == 0) {
//        $("#" + TableID).before("<div>缺少" + TableID + "_tableLayout</div>");
//        $("#" + TableID).remove();
//        return;
//    }


    if ($("#" + TableID + "_tableLayout").length != 0) {
        $("#" + TableID + "_tableLayout").before($("#" + TableID));
        $("#" + TableID + "_tableLayout").empty();
    }
    else {
        $("#" + TableID).before("<div>缺少" + TableID + "_tableLayout</div>");
        $("#" + TableID).remove();
        return;
    }
    //重新对fixtable_tableLayout进行赋值
    $('<div id="' + TableID + '_tableHead"></div>' + '<div id="' + TableID + '_tableData"></div>').appendTo("#" + TableID + "_tableLayout");

    //重新赋值宽度    
    var dema = $("#"+TableID).width();
    if(width>dema){
    	$("#"+TableID).width(width);
    	$("#" + TableID).find('thead tr').eq(0).find('th').each(function () {
	        var col = $(this);
	        col.width(col.outerWidth(true));
	    });
	}
    else{
    	dema=0;
	    $("#" + TableID).find('thead tr').eq(0).find('th').each(function () {
	        var col = $(this);
	        if ($(this).attr('style') == undefined) {
	            dema = dema + col.outerWidth(true);
	        }
	        else {
	            //隐藏列，宽度不加
	            if ($(this).attr('style').indexOf("none") == -1) {
	                dema = dema + col.outerWidth(true);
	            }
	        }
	    });
    }
    
    if(navigator.appName == "Microsoft Internet Explorer")
    	dema = width - 18;
    else
    	dema = width - 17;
    
    //copy 数据到fixtable_tableHead,固定宽度信息
    var oldtable = $("#" + TableID);
    $("#" + TableID + "_tableData").append(oldtable);
    var tableHeadClone = oldtable.clone(true);
    tableHeadClone.attr("id", TableID + "_tableHeadClone");
    $("#" + TableID + "_tableHead").append(tableHeadClone);
    $("#" + TableID + "_tableHead tbody").remove();

    $("#" + TableID + "_tableLayout table").each(function () {
        $(this).css("margin", "0");
    });

    //设置左右滚动条
    $("#" + TableID + "_tableData").scroll(function () {
        $("#" + TableID + "_tableHead").scrollLeft($("#" + TableID + "_tableData").scrollLeft());
    });

    $("#" + TableID + "_tableHead").css({ "overflow": "hidden", "width": width - 17, "position": "relative", "z-index": "45", "background-color": "Silver" });
    $("#" + TableID + "_tableData").css({ "overflow": "scroll", "width": width, "height": height, "position": "relative", "z-index": "35", "-webkit-overflow-scrolling": "touch" });
    $("#" + TableID + "_tableHead").offset($("#" + TableID + "_tableLayout").offset());
    $("#" + TableID + "_tableData").offset($("#" + TableID + "_tableLayout").offset());

    $("#" + TableID + "_tableHeadClone").width(dema);
    $("#" + TableID).width(dema);

    $("#" + TableID + "_tableLayout").width(width);
    $("#" + TableID + "_tableLayout").height(height);
}

function fMoney(number, places) {
    number = number || 0;
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    var thousand = ",";
    var decimal = ".";
    var negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
    return negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
}

function fNumber(money) {
    return parseFloat(money.replace(/[^0-9-.]/g, ''));
}
//文本验证
function checkNum(obj,places)  
{  
	var val=obj.value;
	if(places==undefined)places=2;
	if(val=="")return;
    //验证必须是数字  
    if(isNaN(val))  {  
        obj.value=0;
    }
    else{
    	obj.value= parseFloat(val).toFixed(places);
	}
}

//值验证
function checkNumber(obj,places){
	if(places==undefined)places=2;
	if(obj=="")return;
    //验证必须是数字  
    if(isNaN(obj))  {  
    	obj=0;
    }
    return parseFloat(obj).toFixed(places)
}

(function ($) {
	  
     window.UM = {};
      /**
      * 将str中的html符号转义,默认将转义''&<">''四个字符，可自定义reg来确定需要转义的字符
      * @name unhtml
      * @grammar UM.utils.unhtml(str);  => String
      * @grammar UM.utils.unhtml(str,reg)  => String
      * @example
      * var html = '<body>You say:"你好！Baidu & UEditor!"</body>';
      * UM.utils.unhtml(html);   ==>  &lt;body&gt;You say:&quot;你好！Baidu &amp; UEditor!&quot;&lt;/body&gt;
      * UM.utils.unhtml(html,/[<>]/g)  ==>  &lt;body&gt;You say:"你好！Baidu & UEditor!"&lt;/body&gt;
      */

      var utils = UM.utils = {

          /**
          * 将str中的html符号转义,默认将转义''&<">''四个字符，可自定义reg来确定需要转义的字符
          * @name unhtml
          * @grammar UM.utils.unhtml(str);  => String
          * @grammar UM.utils.unhtml(str,reg)  => String
          * @example
          * var html = '<body>You say:"你好！Baidu & UEditor!"</body>';
          * UM.utils.unhtml(html);   ==>  &lt;body&gt;You say:&quot;你好！Baidu &amp; UEditor!&quot;&lt;/body&gt;
          * UM.utils.unhtml(html,/[<>]/g)  ==>  &lt;body&gt;You say:"你好！Baidu & UEditor!"&lt;/body&gt;
          */
          unhtml: function (str, reg) {
              return str ? str.replace(reg || /[&<">'](?:(amp|lt|quot|gt|#39|nbsp);)?/g, function (a, b) {
                  if (b) {
                      return a;
                  } else {
                      return {
                          '<': '&lt;',
                          '&': '&amp;',
                          '"': '&quot;',
                          '>': '&gt;',
                          "'": '&#39;',
                          "\n": '\\n',
                          "\r": '\\r'
                      }[a]
                  }

              }) : '';
          },
          /**
          * 将str中的转义字符还原成html字符
          * @name html
          * @grammar UM.utils.html(str)  => String   //详细参见<code><a href = '#unhtml'>unhtml</a></code>
          */
          html: function (str) {
              return str ? str.replace(/&((g|l|quo)t|amp|#39);/g, function (m) {
                  return {
                      '&lt;': '<',
                      '&amp;': '&',
                      '&quot;': '"',
                      '&gt;': '>',
                      '&#39;': "'"
                  }[m]
              }) : '';
          }
     }

  })(jQuery)
  

/*
velocity模版格式化

Double dj="1234.666"
$numbertool.format("0.00",$!{dj})->1234.67
$numbertool.format("￥,000.00",$!{dj})->￥1,234.67

Date dt="2015-05-06 16:08:00"		
$datetool.format("yyyy-MM-dd",$!{dt})->2015-05-06	
$datetool.format("yyyy-MM-dd HH:mm",$!{dt})->2015-05-06 16:08
*/

