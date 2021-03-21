var mapComboxMultiple = new HashTable();
function comboxDiv(wd) {
    if (document.getElementById("_smanDisp") == null) {
        //遮挡层
        var divstr = document.createElement('div');
        divstr.id = "combox-mask";
        divstr.style.width = wd + "px";
        divstr.className = "combox-mask";
        document.body.appendChild(divstr);

        divstr = document.createElement('div');
        divstr.id = "_smanDisp";
        divstr.style.width = wd + "px";
        divstr.className = "combox";
        document.body.appendChild(divstr);
    }
    else {
        $("#_smanDisp").width(wd);
    }
}


$(document).bind("click", function (e) {
    var target = $(e.target);
    //alert(target.closest(".combox").length+"->"+target.closest(".combox-mask").length);
    //INPUT,DIV事件对象中class不包含combox,自动隐藏下拉层
    if (target.closest(".combox").length == 0) {
        closeDivPage();
    }
});

//var url = "../usercontrols/combo_action.ashx?fs=array&limit=15";
//obj：控件属性，comboindex：字段索引，defquery：查询条件，other：其他
function comboxBind(obj, combourl, width, defquery, other) {
    var id = obj.id;

    if (width == undefined) {
        width = 200;
    }

    var type = "0";//0:单选,1:多选
    if ($("#" + id).hasClass("multiple")) {
        type = "1";
        width += 104;//总宽度-4，4为边框
    }

    comboxDiv(width); //生成对象
    selectPubClnt(id, 1, combourl, defquery, other, type);
}

//defclear:N[列表用到，清空当前值+隐藏值],控件ID[表单用到，清空对应的ID的值]
function comboxCheck(obj, combourl, defclear, defquery) {
    if (obj.value == "") {
        $("#" + obj.id).val(""); //显示框
        $("#" + obj.id.replace("txt", "hid")).val(""); //form,查询条件中的隐藏框
        $("#" + obj.id.replace("show", "")).val(""); //列表中清空
        if (defclear != undefined) {
            $(defclear).val("");
        }
        return;
    }

    var url = basepath + "/controls/query/comboxcheck.json";
    var dataMap = { combourl: combourl, defquery: defquery, query: obj.value };
    $.ajaxjson(url, dataMap, function (d) {
        if (d.result == "0") {
            $("#" + obj.id).val(""); //显示框
            $("#" + obj.id.replace("txt", "hid")).val(""); //form,查询条件中的隐藏框
            $("#" + obj.id.replace("show", "")).val(""); //列表中清空
            if (defclear != undefined) {
                $(defclear).val("");
            }
        }
    });
}

//parm1 字段
//parm2 查询字段
//parm3 字段ids
//parm4 默认条件
//parm5 默认排序
//parm6 其他
function selectPubClnt(id, page, combourl, defquery, other,type) {
    if (combourl == undefined) combourl = "";
    if (defquery == undefined) defquery = "";
    if (other == undefined) other = "";

    var objouter = document.getElementById("_smanDisp");
    var objInput = document.getElementById(id);
    var oldvalue = "";
    var selectedIndex = -1;
    var intTmp;
    var pagesize=10;

    function checkKeyCode(e) {
        if (true) {
            var keyCode = window.event ? event.keyCode : e.which;
            oldvalue = objInput.value;
            if (keyCode == 40 || keyCode == 38) { //40Down，38up,上下翻页
                var isUp = false
                if (keyCode == 40) {
                    isUp = true;
                }
                chageSelection(isUp)
            }
            else if (keyCode == 13 || keyCode == 8 || keyCode == 32)//13回车触发,8回格,32空格，也执行命令
            {
                checkAndShow();
            }
            else {
                //输入值触发比较耗性能（可以通过combourl控制判断，量大的回车查询）
                checkAndShow();
            }
        }
        divPosition()
    }


    //objInput.onkeyup = checkKeyCode;
    //objInput.onkeydown = checkKeyCode;

    objInput.oninput = checkKeyCode;
    objInput.onpropertychange = checkKeyCode;

    if (parseInt(page) >= 1) checkAndShow();
    //objInput.onblur = onElenBlur;

    objInput.onfocus = function focusFun() {
        page = "1";
        objInput.select();
        //checkAndShow();
        objInput.focus();
    }


    function checkAndShow() {
        var strInput = objInput.value;
        //alert(strInput+"-"+oldvalue);

        divPosition();
        selectedIndex = -1;
        PmSelectLists(page, combourl, defquery, other);
        function PmSelectLists(page, combourl, defquery, other) {
            //parm1 检索字段
            //parm2 字段
            //parm3 字段ids
            //parm4 默认条件
            //parm5 默认排序
            //parm6 其他

        	
        	if(type=="1"){
        		pagesize=100;
	            if (mapComboxMultiple.containsKey(id)) {
	                var option = mapComboxMultiple.getValue(id);
	                objouter.innerHTML = option;
	                objouter.style.display = '';
	                $(".combox-mask").show();
	                return;
	            }
        	}
        	
        	var url = basepath+"/controls/query/combox.json";
 	        var dataMap={ pageindex: page, combourl: combourl, defquery: defquery, other: other, query: strInput,pagesize:pagesize};
 	        $.ajaxjson(url,dataMap,function(d){
 	        	//var cont = { "list": [{ "text": "Brett", "value": "1" }, { "text": "Jason", "value": "2" }, { "text": "Kevin", "value": "3"}], "page": { "currentPage": "1", "pageSize": "2", "recordCount": "3", "pageCount": "2","keyCount":"1"} };
 	        	Setre(strInput, objouter,JSON.parse(d.content));
 	        });
        }
        function Setre(strInput, objouter, re) {
            var allpage = re.pageCount; //所有页
            var pageno = re.currentPage; //本页
            var allcount = re.recordCount; //记录条数
            var keycount = re.keyCount; //记录条数

            //单选
            var option = "<div class=\"combox\">";
            if (type == "0") {
                for (intTmp = 0; intTmp < re.list.length; intTmp++) {
                   	var mc=unescape(re.list[intTmp].mc);
                	var dm=re.list[intTmp].dm;
                    if(keycount>1)
                        option += addOption(mc, dm, strInput, intTmp);
                    else
                        option += addOption(mc, mc, strInput, intTmp);
                }
                //加入分页
                option += addSplitPage(parseInt(pageno), parseInt(allpage), parseInt(allcount)) + "</div>";
                
            }
            else {
                var itemwidth =$("#" + id).attr("itemwidth");
                if (itemwidth == undefined) {
                    itemwidth = 100;
                }

                //生成头
                option += "<div class=\"divckbhead\"><div class=\"divckbheadall\"><em>全选<em><input id=\"allmenu" + id + "\" onclick=\"chooseMultipleAll(this,'" + id + "')\" type=\"checkbox\"></div>";
                option += "<div class=\"divckbheaditem\" onclick=\"closeDivPage()\">[关 闭]</div>";
                option += "<div class=\"divckbheaditem\" onclick=\"clearMultipleAll('" + id + "')\">[清 空]</div>";
                option += "<div class=\"divckbheaditem\" onclick=\"selectMultipleDiv('" + id + "')\">[确 定]</div>";
                option += "</div><div class=\"divckbbody\">";  //生成数据项
                for (intTmp = 0; intTmp < re.list.length; intTmp++) {
                	var mc=unescape(re.list[intTmp].mc);
                	var dm=re.list[intTmp].dm;
                	
                    if (keycount > 1)
                        option += "<div class=\"divckbbodyitem\" style=\"width:"+itemwidth + "px\"><input type=\"checkbox\" name=\"ckbmenu" + id + "\" mc=\"" + mc + "\" value=\"" + dm + "\">" + mc + "</div>";
                    else
                        option += "<div class=\"divckbbodyitem\" style=\"width:" + itemwidth + "px\"><input type=\"checkbox\" name=\"ckbmenu" + id + "\" mc=\"" + mc + "\" value=\"" + mc + "\">" + mc + "</div>";
                }
                option += "</div>";

                mapComboxMultiple.add(id, option);
            }
            objouter.innerHTML = option;
            objouter.style.display = '';
            $(".combox-mask").show();
        };
        //name:显示值，value:隐藏值，keyw:查询关键字，len:字符个数，rs：流水号
        function addOption(name, value, keyw, rs) {
            var v = unescape(name);
            v = v.replace(keyw, "<b><font color=red>" + keyw + "</font></b>");
            v = "<div class=\"divc\" id=\"sdiv" + rs + "_" + id + "\"  onmousedown=\"javascript:selectVal('" + name + "','" + id + "',this)\" >" + v + "</div><input type='hidden' value='" + value + "'  id=\"shid" + rs + "_" + id + "\" \>";
            return v;
        }
        //id, page, combourl, defquery, other
        function addSplitPage(pageno, pages, allcount) {
            var pagetext = "";
            pagetext += pageno + "/" + pages + " 共 " + allcount + " "; //page records
            
            if (parseInt(pageno) > 1) {
                pagetext += "<a id='a1' class='cirs' href=\"javascript:shownextPage('" + id + "','" + (parseInt(pageno) - 1) + "','" + combourl + "','" + defquery + "','" + other + "','" + type + "');\">上一页</a> ";
            }
            if (parseInt(pageno) < parseInt(pages)) {
                pagetext += "<a id='a2' class='cirs' href=\"javascript:shownextPage('" + id + "','" + (parseInt(pageno) + 1) + "','" + combourl + "','" + defquery + "','" + other + "','" + type + "');\">下一页</a>";
            }
            pagetext = "<div class=\"page\" align=center id=''  >" + pagetext + " <a href=javascript:closeDivPage()> [关闭]</a></div>";
            return pagetext;
        }
    }
    function divPosition() {
        var objouter = document.getElementById("_smanDisp");
        objouter.style.top = getAbsoluteHeight(objInput) + getAbsoluteTop(objInput) + "px";
        objouter.style.left = getAbsoluteLeft(objInput) + "px";
    }

    function chageSelection(isUp) {
        if (objouter.style.display == 'none') {
            objouter.style.display = '';
        } else {
            if (isUp) {
                selectedIndex++
            } else {
                selectedIndex--
            }
        }
        var maxIndex = objouter.children.length - 1;
        if (selectedIndex < 0) {
            selectedIndex = 0
        }
        if (selectedIndex > maxIndex) {
            selectedIndex = maxIndex
        }
        for (intTmp = 0; intTmp <= maxIndex; intTmp++) {
            if (intTmp == selectedIndex - 1) {
                objouter.children[intTmp].className = "sman_selectedStyle";
                if (objouter.children[intTmp].innerText != "") {
                    onmouseoverVal(objouter.children[intTmp].innerText, objInput.name, objInput.id, objouter.children[intTmp]);
                }

            } else {
                objouter.children[intTmp].className = "";
            }
        }
    }

    function outSelection(Index, id) {
        if (!objouter.children[Index]) return;
        var str = objouter.children[Index];
        doGetPubClntSelectValue(str.innerText, str.id, id);
    }
}

function chooseMultipleAll(obj, id) {
    if (obj.checked == true) {
        $("input[name='ckbmenu" + id + "']").each(function () { $(this).attr("checked", true); });
    } else {
        $("input[name='ckbmenu" + id + "']").each(function () { $(this).attr("checked", false); });
    }
}
function clearMultipleAll(id) {
    $("input[name='ckbmenu" + id + "']").removeAttr("checked");
    document.getElementById("allmenu" + id).checked = false;
    //var hidid = id.replace("txt", "hid");
    //$("#" + id).val("");
    //$("#" + hidid).val("");
}
function selectMultipleDiv(id) {
    var str = "", strmc = "";
    $("input[name='ckbmenu" + id + "']:checked").each(function () {
        str = str + "," + $(this).val();
        strmc = strmc + "," + $(this).attr("mc");
    });

    if (str != "") {
        str = str.substring(1, str.length);
        strmc = strmc.substring(1, strmc.length);
    }

    var hidid = id.replace("txt", "hid");
    $("#" + id).val(strmc);
    $("#" + hidid).val(str);

    closeDivPage();
}


//点击内容取值
function selectVal(val, id, obj) {
    doGetPubClntSelectValue(val, id, obj);
    closeDivPage();
}
//选择获取内容取值
function onmouseoverVal(val, id, obj) {
    doGetPubClntSelectValue(val, id, obj);
}
//val:返回值，id:控件ID,必须txt打头,obj:选中对象
function doGetPubClntSelectValue(val, id, obj) {
    debugger;
    document.getElementById(id).value = RTrim(val);
    $("#" + id.replace("txt", "hid")).val($("#" + obj.id.replace("sdiv", "shid")).val());
    return true;
}
//关闭下拉框
function closeDivPage() {
    $(".combox-mask").hide();
    var objouter = document.getElementById("_smanDisp");
    if (objouter == undefined) return;
    objouter.style.display = 'none';
}
function shownextPage(obj, pid, combourl, defquery, other, type) {
    selectPubClnt(obj, pid, combourl, defquery, other, type);
}

function getAbsoluteHeight(ob) {
    return ob.offsetHeight
}
function getAbsoluteWidth(ob) {
    return ob.offsetWidth
}
function getAbsoluteLeft(ob) {
    var scrleft = 0;
    //列表中用到
    if (document.getElementById("divlist") != null && ob.id.indexOf("_txt") == -1) {
        scrleft = document.getElementById("divlist").scrollLeft;
    }
    var s_el = 0;
    el = ob;
    while (el) {
        s_el = s_el + el.offsetLeft;
        el = el.offsetParent;
    };

    try {
        //跟随左右拉动
        s_el = parseInt(s_el) + parseInt(document.all.div_ct.offsetLeft);
    }
    catch (e) {
        s_el = parseInt(s_el) - parseInt(scrleft);
    }

    //s_el = parseInt(s_el) - parseInt(scrleft);

    return s_el;
}
function getAbsoluteTop(ob) {
    var s_el = 0;
    el = ob;
    while (el) {
        s_el = s_el + el.offsetTop;
        el = el.offsetParent;
    };

    try {
        //跟随上下拉动
        s_el = parseInt(s_el) - parseInt(document.all.div_cm.scrollTop);
    }
    catch (e) {
    }

    return s_el + 1;
}
function $N() {
    return document.getElementsByName(arguments[0]);
}
function $T() { // $T('input','box')
    if (arguments.length == 1) {
        return document.getElementsByTagName(arguments[0]);
    } else if (arguments.length == 2) {
        if (typeof (arguments[1]) == "object") {
            return arguments[1].getElementsByTagName(arguments[0]);
        } else {
            return $(arguments[1]).getElementsByTagName(arguments[0]);
        }
    }
}
function RTrim(_str) {
    _str = unescape(_str);
    var whitespace = new String(" \t\n\r");
    var s = new String(_str);
    if (whitespace.indexOf(s.charAt(s.length - 1)) != -1) {
        var i = s.length - 1;
        while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1) {
            i--;
        }
        s = s.substring(0, i + 1);
    }

    return s.toString();
}