/// <reference path="../../../../../applibs/sdk/jOptimization-2.1.3.min.js" />
/// <reference path="../../../../../applibs/sdk/json.js" />
/// <reference path="../../../../../applibs/sdk/date.js" />
/// <reference path="../../../../../applibs/sdk/baiduTpls.js" />
/// <reference path="../../../../../applibs/sdk/base64.js" />
/// <reference path="../../../../../applibs/sdk/hhls.js" />
/// <reference path="../../../../../applibs/sdk/hhac.js" />
/// <reference path="../../../../../applibs/sdk/hhls_wxConfirm.js" />
/// <reference path="../../../../../applibs/bootstrap-3.3.5-dist/js/bootstrap.min.js" /> 
/// <reference path="../Pipeline/Pipeline.js" />
/// <reference path="../Chart/Chart.js" />
/// <reference path="../Index/Index.js" />
/// <reference path="../../../../../applibs/bootstrap-3.3.5-dist/datetimepicker/js/bootstrap-datetimepicker.js" /> 

// setTimeout
var tEstimation, tEvaluation, tProsess, tLayout, tOrdered, tSampling;

var Optimization = {
    Datas: {
        LeftIndex: -1,
        Process: [],
        CurState: [],
        CurPipeline: [],
        OrderedLayout: [],
        CurrentLayout: [],
        Statistic: [],
        Estimate_Sta: [],
        clickDiv: '<div id="divMenu" style="position:fixed;" onMouseLeave ="Optimization.doClickDiv(0)"> '
                        + '<button class="btn btn-xs btn-warning"> Accept</button>'
                        + '</div>',
    },
    Tpls: {
        tplPage: { P: "Modules/Optimization/tplPage.html", C: "" },
        tplContent: { P: "Modules/Optimization/tplContent.html", C: "" },
    },
    Load: function () {
        var me = Optimization;
        try {
            me.Datas.LeftIndex = -1;
            hhls.GetTpls(me.Tpls, function () {
                me.Refresh();
            });
        }
        catch (e) {; }
    },
    Refresh: function () {
        var me = Optimization;
        try {
            if (Pipeline.Datas.Pipelines.length == 0) {
                $.get("http://127.0.0.1:8080/rw/getPipelineData", { filter: "orc" }, function (data) {
                    Pipeline.Datas.Pipelines = hhls.getJsonObj(data);
                    var aHtml = bt(me.Tpls.tplPage.C, { tplData: Pipeline.Datas.Pipelines });
                    hhls.fillElement(".divPage", aHtml);
                    me.doChoose(0);
                });
            } else {
                var aHtml = bt(me.Tpls.tplPage.C, { tplData: Pipeline.Datas.Pipelines });
                hhls.fillElement(".divPage", aHtml);
                me.doChoose(0);
            }
        }
        catch (E) {; }
    },
    /*divLeftPan*/
    doChoose: function (aIndex) {
        var me = Optimization;
        try {
            if (aIndex != me.Datas.LeftIndex) {
                $("#txtInfo").val("");
                me.Datas.LeftIndex = aIndex;
                var aItems = $(".divLeftPanBody ul li");
                aItems.removeClass("active");
                $(aItems[me.Datas.LeftIndex]).addClass("active");
                hhls.fillElement(".Ordered", '');

                if (Pipeline.Datas.Pipelines.length > 0) {
                    me.Datas.CurPipeline = Pipeline.Datas.Pipelines[me.Datas.LeftIndex];
                    me.ClearDisplay();
                    me.ShowOrdered();
                    me.ShowCurrentLayout();
                    me.ShowOrderedLayoutInfo();
                    me.ShowEstimation();
                    me.ShowEvaluation();
                    me.ShowProsess();

                    me.RefreshOrdered();
                    me.RefreshProcess();
                    me.RefreshLayout();
                    me.RefreshEvaluation();
                    me.RefreshEstimate();
                } else {
                    var aHtml = '<div class="divPan "><div class="divPanBody" style="padding:0"><div style="color:#ff0000;padding: 20px;"><i class="fa fa-warning"></i> No Pipeline Record !</div></div></div>';
                    hhls.fillElement(".divMain", aHtml);
                }
            } else {

            }
        }
        catch (E) {; }
    },
    ClearDisplay: function (aIndex) {
        var me = Optimization;
        try {
            hhls.fillElement(".tBodyDisplay", 'Loading...');
            $(".tBodyDisplay").css("height", "");

            hhls.fillElement(".tFooterDisplay", 'Loading...');
            $(".tFooterDisplay").css("height", "");

            hhls.fillElement(".tBodyProcess", '<div style="padding:15px;">Loading...</div>');

            clearTimeout(tOrdered);
            clearTimeout(tSampling);
            clearTimeout(tEstimation);
            clearTimeout(tEvaluation);
            clearTimeout(tProsess);
        }
        catch (E) {; }
    },
    /*Sampling*/
    doSampling: function () {
        var me = Optimization;
        try {
            $.post(Index.Action.doSampling, { pno: me.Datas.CurPipeline.no }, function (data, status) {
                //alert("Sampling " + status);
                Init.Web_Toast("Sampling " + status, 1);
            });
        }
        catch (E) {; }
    },
    /*Workload Upload*/
    doUpload: function (e) {
        var me = Optimization;
        try {
            $(".Ordered").removeClass("text-green").addClass("text-red");
            hhls.fillElement(".Ordered", '<i class="fa fa-flag"></i> Optimization Started');

            var formData = new FormData($("#evaluateForm")[0]);
            if (formData.get("workload").name == "") {
                //alert("Please select corresponding workload file!");
                Init.Web_Toast("Please select corresponding workload file!", 1);
                return;
            }
            var pno = me.Datas.CurPipeline.no;
            formData.append("pno", pno);

            $.ajax({
                url: $("#evaluateForm").attr('action'),
                type: $("#evaluateForm").attr('method'),
                data: formData,
                processData: false,
                contentType: false,
                success: function (data) {
                    //$("#txtWorkload").css("display", "none"); 
                    //alert("Uploaded succeed.");
                    Init.Web_Toast("Uploaded succeed.", 1);
                },
                error: function (jXHR, textStatus, errorThrown) {
                    //alert("Error: Upload failed. " + errorThrown);
                    Init.Web_Toast("Error: Upload failed. " + errorThrown, 1);
                }
            });
        }
        catch (e) {; }
    },
    doOpenFile: function (e) {
        var me = Pipeline;
        try {
            var workloadPath = e.value;
            if (workloadPath.length > 0) {
                $("#txtWorkload").css("display", "");
                me.Datas.UploadIndex = 1;
            } else {
                $("#txtWorkload").css("display", "none");
            }
        }
        catch (e) {; }
    },
    doClear: function () {
        var me = Optimization;
        try {
            $("#txtSQL").val("");
        }
        catch (E) {; }
    },
    doExcute: function () {
        var me = Optimization;
        try {
            var sql = $("#txtSQL").val();
            $.post(Index.Action.queryUpload, { query: sql, pno: me.Datas.CurPipeline.no }, function (data, status) {
                //alert("Upload sql " + status);
                Init.Web_Toast("Upload sql " + status, 1);
                $("#txtSQL").val("");
            });
        }
        catch (E) {; }
    },
    /*Layout Strategy*/
    doAccept: function () {
        var me = Optimization;
        try {
            $.post(Index.Action.accept, { pno: me.Datas.CurPipeline.no }, function (data, status) {
                //alert("Accept Optimization " + status);
                Init.Web_Toast("Accept Optimization " + status, 1);
                clearTimeout(tEvaluation);
                me.ShowCurrentLayout();
            });
        }
        catch (E) {; }
    },
    doOptimization: function () {
        var me = Optimization;
        try {
            $.post(Index.Action.optimization, { pno: me.Datas.CurPipeline.no }, function (data, status) {
                //alert("Optimization Optimization " + status);
                Init.Web_Toast("Optimization Optimization " + status, 1);
                tLayout = setInterval(function () {
                    me.ShowOrderedLayoutInfo();
                }, 10 * 1000);

                tOrdered = setInterval(function () {
                    me.ShowOrdered();
                }, 10 * 1000);
            });
        }
        catch (E) {; }
    },
    RefreshOrdered: function () {
        var me = Optimization;
        try {
            tOrdered = setInterval(function () {
                me.ShowOrdered();
            }, 10 * 1000);
        }
        catch (E) {; }
    },
    ShowOrdered: function () {
        var me = Optimization;
        try {
            $.get(Index.Action.getOrdered, { pno: me.Datas.CurPipeline.no }, function (data) {
                if (data != "") {
                    var paddleft = data.replace(/[^0-9]/ig, "");
                    if (paddleft != "") {
                        var num = parseInt(paddleft) / 10;
                        if (num >= 97) {  // data.indexOf("98")
                            hhls.fillElement(".Ordered", data);
                            clearTimeout(tOrdered);
                            hhls.fillElement(".Ordered", '<i class="fa fa-flag"></i> Optimization Finished');
                            $(".Ordered").removeClass("text-red").addClass("text-green");
                            me.RefreshLayout();
                            clearTimeout(tEstimation);
                        } else {
                            me.ShowEstimation();
                            hhls.fillElement(".Ordered", data);
                            $(".Ordered").removeClass("text-green").addClass("text-red");
                        }
                    } else {
                            $(".Ordered").removeClass("text-green").addClass("text-red");
                            hhls.fillElement(".Ordered", '<i class="fa fa-flag"></i> Optimization Started');
                    }
                } else {
                    hhls.fillElement(".Ordered", '');
                }
            });
        }
        catch (E) {; }
    },
    RefreshLayout: function () {
        var me = Optimization;
        try {
            tLayout = setInterval(function () {
                me.ShowOrderedLayoutInfo();
            }, 10 * 1000);
        }
        catch (E) {; }
    },
    ShowCurrentLayout: function () {
        var me = Optimization;
        try {
            var aHtml = "";
            var aItem = "";
            $.get(Index.Action.getCurrentLayout, { pno: me.Datas.CurPipeline.no }, function (data) {
                me.Datas.CurrentLayout = hhls.getJsonObj(data);
                hhls.fillElement(".Currents", '<li class="Current" style="list-style:none; font-size:14px;font-weight:bold;color:#33D4D6">[ Current Layout ]</li>');
                aItem = me.Datas.CurrentLayout;
                if (aItem.columnOrder == "0") {
                    aHtml = me.GetLayouInfo(aItem, " NO", "Current");
                } else {
                    aHtml = me.GetLayouInfo(aItem, " YES", "Current");
                }
                $(".Current").after(aHtml);
            });
        }
        catch (E) {; }
    },
    ShowOrderedLayoutInfo: function () {
        var me = Optimization;
        try {
            var aItemLists = "";
            var aItem = "";
            var aHtml = "";
            $.get(Index.Action.getOrderedLayout, { pno: me.Datas.CurPipeline.no }, function (data) {
                me.Datas.OrderedLayout = hhls.getJsonObj(data);
                hhls.fillElement(".Optimizeds", '<li class="Optimized" style="list-style:none; font-size:14px;font-weight:bold;color:#A483E5">[ Optimized Layout ]</li>');
                if (me.Datas.OrderedLayout.count > 0){
                aItemLists = me.Datas.OrderedLayout.layouts;
                if (aItemLists.length > 1) {
                    aItem = aItemLists[aItemLists.length - 1];
                    if (Init.CompareDate(me.Datas.CurrentLayout.time, aItem.time)) {
                        if (aItem.columnOrder == "0") {
                            aHtml = me.GetLayouInfo(aItem, " No", "Optimized");
                        } else {
                            aHtml = me.GetLayouInfo(aItem, " YES", "Optimized");
                        }
                        $(".Optimized").after(aHtml);
                        clearTimeout(tLayout);
                    }
                }
                }
            });
        }
        catch (E) {; }
    },
    GetLayouInfo: function (aItem, flag, type) {
        var me = Optimization;
        try {
            var aHtml = '<li>Format: ' + aItem.format + '</li>'
                                            //+ '<li>Column Order: <span class="text-red"> ' + flag + '</span></li>'
                                            + '<li>Column Order:' + flag + '</li>'
                                            + '<li>Row Group Size: ' + aItem.rowGroupSize + ' (MB)</li>'
                                            + '<li>Compression CodeC: ' + aItem.compression + '</li>'
                                            + '<li>Time: ' + aItem.time + '</li>';
            if (type == "Optimized") {
                aHtml += '<li>Optimized Count: ' + me.Datas.OrderedLayout.count + '</li>';
            }
            return aHtml;
        }
        catch (E) {; }
    },
    RefreshEstimate: function () {
        var me = Optimization;
        try {
            tEstimation = setInterval(function () {
                me.ShowEstimation();
            }, 10 * 1000);
        }
        catch (E) {; }
    },
    ShowEstimation: function () {
        var me = Optimization;
        try {
            me.Datas.Estimate_Sta = [];
            var aOption = Charts.tplCharts.tplScatter;
            $.get(Index.Action.getEstimate_Sta, { pno: me.Datas.CurPipeline.no }, function (data) {
                aOption.legend.data = [];
                aOption.series = [];
                aOption.title.text = 'Estimated Performance';
                me.Datas.Estimate_Sta = hhls.getJsonObj(data);
                var aItem = "";
                for (var i in me.Datas.Estimate_Sta) {
                    aItem = me.Datas.Estimate_Sta[i];
                    aOption.legend.data.push(aItem.name);
                    var aInfo = {
                        name: '',
                        type: 'scatter',
                        data: [
                        ],
                        markPoint: {
                            data: [
                                { type: 'max', name: 'Maximal Value' },
                                { type: 'min', name: 'Minimum Value' }
                            ]
                        },
                        markLine: {
                            data: [
                                { type: 'average', name: 'Average ' }
                            ]
                        }
                    };
                    aInfo.name = aItem.name;
                    aInfo.data = aItem.list;
                    aOption.series.push(aInfo);
                }
                if (me.Datas.Estimate_Sta.length > 0 && aItem.list.length > 0) {
                    me.doDrawEstimate_Sta(aOption);
                    //clearTimeout(tEstimation);
                }
                else {
                    hhls.fillElement(".tFooterDisplay", '<div style="color:#ff0000;padding: 20px;"><i class="fa fa-warning"></i> No Estimation Display !</div>');
                    $(".tFooterDisplay").css("height", "");
                }
            });
        }
        catch (E) {; }
    },
    doDrawEstimate_Sta: function (aOption) {
        var me = Optimization;
        try {
            $(".tFooterDisplay").css("height", "380px");
            var aCharts = echarts.init($(".tFooterDisplay")[0], 'macarons');
            aCharts.on('click', function (param) {
                if (param.name == "") {

                }
            });
            aCharts.setOption(aOption);
        }
        catch (E) {; }
    },
    /*Evaluation*/
    doClickDiv: function (aIndex) {
        var me = Optimization;
        try {
            if (aIndex == 0) {
                $("#divMenu").css("display", "none");
            } else if (aIndex == 1) {

            }
        }
        catch (E) {; }
    },
    RefreshEvaluation: function () {
        var me = Optimization;
        try {
            tEvaluation = setInterval(function () {
                me.ShowEvaluation();
            }, 10 * 1000);
        }
        catch (E) {; }
    },
    ShowEvaluation: function () {
        var me = Optimization;
        try {
            me.Datas.Statistic = [];
            var aOption = Charts.tplCharts.tplScatter;
            $.get(Index.Action.getStatistic, { pno: me.Datas.CurPipeline.no }, function (data) {
                aOption.legend.data = [];
                aOption.series = [];
                aOption.title.text = 'Validated Performance';
                me.Datas.Statistic = hhls.getJsonObj(data);
                var aItem = "";
                for (var i in me.Datas.Statistic) {
                    aItem = me.Datas.Statistic[i];
                    aOption.legend.data.push(aItem.name);
                    var aInfo = {
                        name: '',
                        type: 'scatter',
                        data: [
                        ],
                        markPoint: {
                            data: [
                                { type: 'max', name: 'Maximal Value' },
                                { type: 'min', name: 'Minimum Value' }
                            ]
                        },
                        markLine: {
                            data: [
                                { type: 'average', name: 'Average ' }
                            ]
                        }
                    };
                    aInfo.name = aItem.name;
                    aInfo.data = aItem.list;
                    aOption.series.push(aInfo);
                }
                if (me.Datas.Statistic.length > 0 && aItem.list.length > 0)
                    me.doDraw(aOption);
                else {
                    hhls.fillElement(".tBodyDisplay", '<div style="color:#ff0000;padding: 20px;"><i class="fa fa-warning"></i> No Evaluation Display !</div>');
                    $(".tBodyDisplay").css("height", "");
                }
            });
        }
        catch (E) {; }
    },
    doDraw: function (aOption) {
        var me = Optimization;
        try {
            $(".tBodyDisplay").css("height", "380px");
            var aCharts = echarts.init($(".tBodyDisplay")[0], 'shine');
            aCharts.on('click', function (param) {
                if (param.name == "") {
                    $.get(Index.Action.getQuery, { rowid: param.data[0], pno: me.Datas.CurPipeline.no }, function (data) {
                        $("#txtInfo").val(data);
                    });
                }
                //$("#txtInfo").val(hhls.getJsonObj(param.data));
                //me.Datas.DotInfo = param;
                //alert(param);
                //$("body").append(me.Datas.clickDiv);
                //var menu = document.getElementById("divMenu");
                //var aMenu = $("#divMenu button")
                //aMenu.removeAttr("onclick");
                //aMenu.attr("onclick", "Optimization.doAccept(" + param + ");");

                //var event = param.event;
                //var pageX = event.pageX;
                //var pageY = event.pageY;
                //var pageX = event.offsetX;
                //var pageY = event.offsetY;
                // 获得相对距离
                //var h = $(".tBodyDisplay").position().top;
                //var w = $(".tBodyDisplay").position().left;
                //menu.style.left = pageX + w + 'px';
                //menu.style.top = pageY + h + 'px';
                //menu.style.display = "block"; 
            });
            aCharts.setOption(aOption);
        }
        catch (E) {; }
    },
    doEvaluate: function () {
        var me = Optimization;
        try {
            clearTimeout(tEstimation);
            $.post(Index.Action.startEvaluation, { pno: me.Datas.CurPipeline.no }, function (data, status) {
                Init.Web_Toast("Start to Validate...", 1);
                me.RefreshEvaluation();
            });
        }
        catch (E) {; }
    },
    /*Pipeline Process Timeline*/
    RefreshProcess: function () {
        var me = Optimization;
        try {
            tProsess = setInterval(function () {
                me.ShowProsess();
            }, 5 * 1000);
        }
        catch (E) {; }
    },
    ShowProsess: function () {
        var me = Optimization;
        try {
            $.get(Index.Action.getProcessState, { pno: me.Datas.CurPipeline.no }, function (data) {
                me.Datas.Process = hhls.getJsonObj(data);
                for (var i in me.Datas.Process) {
                    var aItem = me.Datas.Process[i];
                    if (aItem.pipelineNo == me.Datas.CurPipeline.no) {
                        me.Datas.CurState = aItem.pipelineState;
                        break;
                    } else {
                        me.Datas.CurState = [];
                    }
                }
                var aHtml = bt(me.Tpls.tplContent.C, { tplData: me.Datas.CurState });
                hhls.fillElement(".tBodyProcess", aHtml);
                // set execute once later
                var flag = false;
                var aItem = "";
                for (var i in me.Datas.CurState) {
                    aItem = me.Datas.CurState[i];
                    if (aItem.desc == "Sampling Finished") {
                        hhls.fillElement(".Sampling", '<i class="fa fa-flag"></i> Sampling Finished');
                        $(".Sampling").removeClass("text-danger").addClass("text-success");
                        flag = true;
                        break;
                    }
                    //if (aItem.desc == "Optimization Finished") {
                    //    hhls.fillElement(".Ordered", '<i class="fa fa-flag"></i> Optimization Finished');
                    //    $(".Ordered").removeClass("text-red").addClass("text-green");
                    //    break;
                    //}
                    //if (aItem.desc == "Optimization Started") {
                    //    $(".Ordered").removeClass("text-green").addClass("text-red");
                    //    hhls.fillElement(".Ordered", '<i class="fa fa-flag"></i> Optimization Started');
                    //    break;
                    //}
                }
                if (aItem.desc == "Optimization Finished") {
                    //clearTimeout(tOrdered);
                    me.ShowEstimation();
                    clearTimeout(tEstimation);
                } else if (aItem.desc == "Optimization Started") {
                    me.ShowOrdered();
                    me.ShowEstimation();
                } else if (aItem.desc == "Evaluation Started") {
                    me.ShowOrdered();
                    //me.ShowEvaluation();
                }
                if (!flag) {
                    hhls.fillElement(".Sampling", '<i class="fa fa-flag"></i> Sampling Started');
                    $(".Sampling").removeClass("text-success").addClass("text-danger");
                }
            });
        }
        catch (E) {; }
    },
    doShowDtl: function (aIndex) {
        var me = Optimization;
        try {
            var aInfo = me.Datas.CurState[aIndex];
            var aUrl = "detail.html?time=" + aInfo.time + "&desc=" + aInfo.desc + "&pno=" + me.Datas.CurPipeline.no;
            window.open(aUrl);
        }
        catch (E) {; }
    },

};
