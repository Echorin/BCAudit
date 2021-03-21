function QueryTable(tableurl,controlid,isfix,url,height) {

    this.tableurl = tableurl;
    this.controlid = controlid;
    this.filter="";
    this.sort="";
    this.pageindex=1;
    
    //是否锁定头部
    if(isfix==undefined)
    	this.isfix=true;
    else
    	this.isfix=isfix;
    
    //请求地址
    if(url==undefined)
    	this.url=basepath+"/controls/query/listview.json";
    else
    	this.url=basepath+url;
    
    //列表高度
    if(height==undefined)
    	this.height=0;
    else
    	this.height=height;

    this.dosearch=function(pageindex,filter){
    	this.pageindex=pageindex;
        this.filter=JSON.stringify(filter);
        this.dopage(this.pageindex);
    }

    /*页面排序*/
    this.dosort=function(key) {
        if (this.sort.indexOf("desc") == -1) {
            this.sort = key + " desc";
        }
        else {
            this.sort = key + " asc";
        }
        this.dopage(1);
    }

    //第几页
    this.dogo=function() {
        var _value = document.getElementById("txtGoto_" + this.controlid).value;
        if (_value == "") {
            alert("请输入页数！");
            return false;
        }
        if (isNaN(_value)) {
            alert("请输入页数！");
            return false;
        }
        this.dopage(_value);
    }

    //分页
    this.dopage=function(pageindex) {
		this.pageindex=pageindex;
        url=this.url;//(ajax请求后，内部变量值会丢失需要提前赋值下)
        isfix=this.isfix;
        height=this.height;
        var dataMap = { tableurl: this.tableurl, filter: this.filter, pageindex: this.pageindex, sort: this.sort, controlid: this.controlid};
        $.ajaxjsonComplete(url, dataMap,function (data) {
        	if($("#" + controlid + "_tableData").height()==0){
        		return;
        	}
        	
            //总高度-查询框高度-分隔符号
            var h = height;
            if(h==0){
            	h=$(window).height() - $("#searchtable").height() - 50;
            }
            
            //是否锁定表头
            if (isfix) {
                var w = $(".search").width() + 12;
                FixTable(controlid, 0, w, h);
            }
            else {
                $(".fixtable").css({
                    "width": "100%",
                    "border": "1px solid #ccc"
                });
                $("#" + controlid + "_tableData").css({
                    "height": h
                });
            }
        },
        function (d) {
            $("#" + controlid + "_tableData").html(d.content);
            $("#" + controlid + "_tablePage").html(d.page);
        });
    }
    
    /*
    title:标题
    action:modify,view,delete,check(修改、查看、删除)
    actionurl:连接URL
    key：删除ID
    width：暂时不用,直接用openWidth
    height：暂时不用,直接用openHeight
    mode:打开方式【link弹出连接】，【默认dialog】
    */
    this.cmdpage= function(title,action, actionurl, key, mode) {
    	actionurl=basepath+actionurl;
        if (action == "delete") {
            if (window.confirm("确定删除吗?")) {
                this.cmddata(action,key);
            }
        }
        else if (action == "check") {
         	this.cmddata(action,key);
        }
        else {
        	//alert(openWidth+"$"+openHeight+"$"+mode);
        	if(mode=="link")
        		winopen(actionurl,0,0);
        	else
        		parent.openDialog(title,actionurl, openWidth, openHeight);
        }
    }

    //删除数据
  this.cmddata= function(action,key) {
	    var pageindex=this.pageindex;
     	var url = basepath+"/controls/query/listdelete.json";
        var dataMap = { tableurl: this.tableurl, formCmd: action, formPkid: key };
        $.ajaxjson(url, dataMap, function (d) {
            if (d.result == 0) {
            	//页面上声明查询控件变量必须为fixtable
            	fixtable.dopage(pageindex);
            }
            else {
                alert(d.content);
            }
        });
    }

    this.bindreload= function () {
        this.dopage(this.pageindex);
    }
}

function bindreload(){
	//页面上声明查询控件变量必须为fixtable
	fixtable.bindreload();
}

//获取ID
function getCheckID() {
    var idlist = "";
    $(".fixtable .combox").each(function () {
        if ($(this).attr("checked")) {
            idlist += "," + $(this).val();
        }
    });
    return idlist;
}

function MinTable(url, controlid,fn) {
        this.url = url;
        this.ControlID = controlid;
        this.filter = "";
        this.sort = "";
        this.pageindex = 1;

        this.dosearch = function (pageindex, filter) {
            this.pageindex = pageindex;
            this.filter = JSON.stringify(filter);
            this.dopage(this.pageindex);
        }

        /*页面排序*/
        this.dosort = function (key) {
            if (this.sort.indexOf("desc") == -1) {
                this.sort = key + " desc";
            }
            else {
                this.sort = key + " asc";
            }
            this.dopage(1);
        }

        //第几页
        this.dogo = function () {
            var _value = document.getElementById("txtGoto_" + this.controlid).value;
            if (_value == "") {
                alert("请输入页数！");
                return false;
            }
            if (isNaN(_value)) {
                alert("请输入页数！");
                return false;
            }
            this.dopage(_value);
        }

        //分页
        this.dopage = function (pageindex) {
            this.pageindex = pageindex;
			var control= this.ControlID;
            var dataMap = { filter: this.filter, pageindex: this.pageindex, sort: this.sort};
            $.ajaxjson(this.url, dataMap, function (d) {
                fn(d);
                setpage(d.pageNo, d.count, d.pageSize,control);
            });
        }
        
       function setpage(CurrentPageIndex, RowCount,PageSize,ControlID) {
            var AllCurrentPageIndex = 0;
            var PageNext = 0;
            var PagePre = 0;

            if (CurrentPageIndex < 1) {
                CurrentPageIndex = 1;
            }

            // 计算总页数
            if (PageSize > 0) {
                AllCurrentPageIndex = parseInt(RowCount / PageSize);
                AllCurrentPageIndex = ((RowCount % PageSize) != 0 ? AllCurrentPageIndex + 1 : AllCurrentPageIndex);
                AllCurrentPageIndex = (AllCurrentPageIndex == 0 ? 1 : AllCurrentPageIndex);
            }

            if (CurrentPageIndex > AllCurrentPageIndex)
                CurrentPageIndex = AllCurrentPageIndex;
                
            PageNext = CurrentPageIndex + 1;
            PagePre = CurrentPageIndex - 1;

            var currentpagestr = "<div class=\"fixpage\" >当前第" + CurrentPageIndex + "页&nbsp;&nbsp;";
            currentpagestr += CurrentPageIndex > 1 ? "<a href='javascript:void(0)' onclick=\""+ ControlID + "+.dopage('1')\">首页</a>&nbsp;<a href='javascript:void(0)' onclick=\""
+ ControlID + ".dopage('" + PagePre + "')\">上一页</a>&nbsp;"
: "<a href='javascript:void(0)'>首页</a>&nbsp;<a href='javascript:void(0)'>上一页</a>&nbsp;";
            currentpagestr += CurrentPageIndex != AllCurrentPageIndex ? "&nbsp;&nbsp;<a href='javascript:void(0)' onclick=\"" + ControlID
+ ".dopage('" + PageNext + "')\" >下一页</a>&nbsp;<a href='javascript:void(0)' onclick=\"" + ControlID
+ ".dopage('" + AllCurrentPageIndex + "')\">末页</a>" : "<a href='javascript:void(0)'>下一页</a>&nbsp;<a href='javascript:void(0)'>末页</a>";
            currentpagestr += "共" + AllCurrentPageIndex + "页&nbsp;&nbsp;共" + RowCount + "条信息&nbsp;&nbsp;每页" + PageSize
+ "条记录&nbsp;&nbsp;";
            currentpagestr += "&nbsp;&nbsp;<input type='text' id='txtGoto_" + ControlID
+ "' class=\"cinput ciwa\" />&nbsp;<input type='button' onclick=\"" + ControlID
+ ".dogo()\" name='btnGo' value='Go' class=\"cbtn\" /></div>";

            //<div class="simpage"></div>simtable
            $(".fixpage").html(currentpagestr);
        }

}

function SelectTable(tableurl,controlid) {
    this.tableurl = tableurl;
    this.controlid = controlid;
    this.filter="";
    this.sort="";
    this.pageindex=1;
     
    this.dosearch=function(pageindex,filter){
    	this.pageindex=pageindex;
        this.filter=JSON.stringify(filter);
        this.dopage(this.pageindex);
    }

    /*页面排序*/
    this.dosort=function(key) {
        if (this.sort.indexOf("desc") == -1) {
            this.sort = key + " desc";
        }
        else {
            this.sort = key + " asc";
        }
        this.dopage(1);
    }

    //第几页
    this.dogo=function() {
        var _value = document.getElementById("txtGoto_" + this.controlid).value;
        if (_value == "") {
            alert("请输入页数！");
            return false;
        }
        if (isNaN(_value)) {
            alert("请输入页数！");
            return false;
        }
        this.dopage(_value);
    }

    //分页
    this.dopage=function(pageindex) {
		this.pageindex=pageindex;
        var url="/query/selectdata";//(ajax请求后，内部变量值会丢失需要提前赋值下)
        var dataMap = { tableurl: this.tableurl, filter: this.filter, pageindex: this.pageindex, sort: this.sort, controlid: this.controlid};
        $.ajaxjsonComplete(url, dataMap,function (data) {
        },
        function (d) {
            $("#" + controlid + "_tableData").html(d.content);
            $("#" + controlid + "_tablePage").html(d.page);
            var h = 335;
            $(".fixtable").css({"width": "100%","border": "1px solid #ccc"});
            $("#" + controlid + "_tableData").css({"height": h,"overflow":"auto"});
        });
    }
}
