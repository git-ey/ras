﻿<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- jsp文件头和头部 -->
<%@ include file="../../system/index/top.jsp"%>
<!-- 日期框 -->
<link rel="stylesheet" href="static/ace/css/datepicker.css" />
</head>
<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							
						<!-- 检索  -->
						<form action="dataexport/list.do" method="post" name="Form" id="Form">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<table style="margin-top:5px;">
							<tr>
								<td>
									<div class="nav-search">
										<span class="input-icon">
											<input type="text" placeholder="这里输入关键词" class="nav-search-input" id="nav-search-input" autocomplete="off" name="keywords" value="${pd.keywords }" placeholder="这里输入关键词"/>
											<i class="ace-icon fa fa-search nav-search-icon"></i>
										</span>
									</div>
								</td>
								<td style="padding-left:2px;text-align:right;width:10%;">期间:</td>
								<td style="vertical-align:top;padding-left:2px;width:5%;">
								 	<input class="nav-search-input" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" type="text" placeholder="期间" title="期间"/>
								</td>
								<td style="padding-left:2px;text-align:right;width:10%;">管理公司:</td>
								<td style="vertical-align:top;padding-left:2px;width:5%;">
								 	<select class="chosen-select form-control" name="FIRM_CODE" id="FIRM_CODE" data-placeholder="请选择公司" style="vertical-align:top;width: 120px;">
									    <option value=""></option>
									    <c:forEach items="${companyList}" var="var" varStatus="vs">
									        <option value="${var.COMPANY_CODE}" <c:if test="${pd.FIRM_CODE == var.COMPANY_CODE}">selected</c:if>>${var.SHORT_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="padding-left:2px;text-align:right;width:10%;">基金代码:</td>
								<td style="vertical-align:top;padding-left:2px;width:10%;">
								    <input type="text" autocomplete="off" class="nav-search-input" name="FUND_ID" value="${pd.FUND_ID }" placeholder="这里输入基金代码"/>
								</td>
								<c:if test="${QX.cha == 1 }">
								<td style="vertical-align:top;padding-left:2px"><a class="btn btn-light btn-xs" onclick="tosearch();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a></td>
								</c:if>
								<!-- 
								<td style="vertical-align:top;">
									<c:if test="${QX.toExcel == 1 }"><td style="vertical-align:top;padding-left:2px;"><a class="btn btn-light btn-xs" onclick="exportAll('确定要导出选中的数据吗?');" title="批量导出"><i id="nav-search-icon" class="ace-icon fa fa-download bigger-110 nav-search-icon blue"></i></a></td></c:if>
								</td>
								 -->
								<td style="padding-left:2px;text-align:right;width:20%;"></td>
							</tr>
						</table>
						<!-- 检索  -->
						<table id="simple-table" class="table table-striped table-bordered table-hover" style="margin-top:5px;">	
							<thead>
								<tr>
									<th class="center">基金ID</th>
									<th class="center">期间</th>
									<th class="center">基金简称</th>
									<th class="center">管理公司</th>
									<th class="center" colspan="13">底稿</th>
									<th class="center">报告</th>
									<th class="center">下载</th>
								</tr>
							</thead>
													
							<tbody>
							<!-- 开始循环 -->	
							<c:choose>
								<c:when test="${not empty varList}">
									<c:if test="${QX.cha == 1 }">
									<c:forEach items="${varList}" var="var" varStatus="vs">
										<tr>
											<td class='center'>${var.FUND_ID}</td>
											<td class='center'>${var.PERIOD}</td>
											<td class='center'>${var.SHORT_NAME}</td>
											<td class='center'>${var.MGR_COMPANY}</td>
											<c:choose>  
                                              <c:when test="${var.CFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('C','${var.FUND_ID}','${var.PERIOD}');">C</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">C</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.EFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('E','${var.FUND_ID}','${var.PERIOD}');">E</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">E</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.GFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('G','${var.FUND_ID}','${var.PERIOD}');">G</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">G</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.HFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('H','${var.FUND_ID}','${var.PERIOD}');">H</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">H</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                             <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('HX','${var.FUND_ID}','${var.PERIOD}');">H旗下基金</a></td>
                                            <c:choose>  
                                              <c:when test="${var.IFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('I','${var.FUND_ID}','${var.PERIOD}');">I</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">I</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.NFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('N','${var.FUND_ID}','${var.PERIOD}');">N</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">N</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.PFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('P','${var.FUND_ID}','${var.PERIOD}');">P</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">P</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.TFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('T','${var.FUND_ID}','${var.PERIOD}');">T</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">T</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.UFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('U','${var.FUND_ID}','${var.PERIOD}');">U</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">U</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.VFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('V','${var.FUND_ID}','${var.PERIOD}');">V</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">V</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <c:choose>  
                                              <c:when test="${var.OFLAG > 0 }">
                                                <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('O','${var.FUND_ID}','${var.PERIOD}');">O</a></td>
                                              </c:when>
                                               <c:otherwise>
                                                 <td class="center"><a class="btn btn-mini">O</a></td>
                                               </c:otherwise>
                                            </c:choose>
                                            <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('SA','${var.FUND_ID}','${var.PERIOD}');">SA</a></td>
                                            <td class="center"><a class="btn btn-mini btn-success" onclick="wpExport('Report','${var.FUND_ID}','${var.PERIOD}');">报告</a></td>
									       <td class='center'>
                                               <a class="btn btn-light btn-xs" onclick="toDownload('${var.FUND_ID}','${var.PERIOD}');" title="导出文件"><i id="nav-search-icon" class="ace-icon fa fa-download bigger-110 nav-search-icon green"></i></a>
                                           </td>
										</tr>
									</c:forEach>
									</c:if>
									<c:if test="${QX.cha == 0 }">
										<tr>
											<td colspan="100" class="center">您无权查看</td>
										</tr>
									</c:if>
								</c:when>
								<c:otherwise>
									<tr class="main_info">
										<td colspan="100" class="center" >没有相关数据</td>
									</tr>
								</c:otherwise>
							</c:choose>
							</tbody>
						</table>
						<div class="page-header position-relative">
						<table style="width:100%;">
							<tr>
								<td style="vertical-align:top;"><div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div></td>
							</tr>
						</table>
						</div>
						</form>
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->

		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
		$(top.hangge());//关闭加载状态
		//检索
		function tosearch(){
			top.jzts();
			$("#Form").submit();
		}
		$(function() {
		
			//日期框
			$('.date-picker').datepicker({
				autoclose: true,
				todayHighlight: true
			});
			
			//下拉框
			if(!ace.vars['touch']) {
				$('.chosen-select').chosen({allow_single_deselect:true}); 
				$(window)
				.off('resize.chosen')
				.on('resize.chosen', function() {
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				}).trigger('resize.chosen');
				$(document).on('settings.ace.chosen', function(e, event_name, event_val) {
					if(event_name != 'sidebar_collapsed') return;
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				});
				$('#chosen-multiple-style .btn').on('click', function(e){
					var target = $(this).find('input[type=radio]');
					var which = parseInt(target.val());
					if(which == 2) $('#form-field-select-4').addClass('tag-input-style');
					 else $('#form-field-select-4').removeClass('tag-input-style');
				});
			}
			
			
			//复选框全选控制
			var active_class = 'active';
			$('#simple-table > thead > tr > th input[type=checkbox]').eq(0).on('click', function(){
				var th_checked = this.checked;//checkbox inside "TH" table header
				$(this).closest('table').find('tbody > tr').each(function(){
					var row = this;
					if(th_checked) $(row).addClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', true);
					else $(row).removeClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', false);
				});
			});
		});
		
		//导出
		function wpExport(type,fundId,peroid){
			window.location.href="<%=basePath%>wpExport/"+type+".do?FUND_ID="+fundId+"&PEROID="+peroid;
		}
		
		//下载
		function toDownload(fundId,peroid){
			window.location.href="<%=basePath%>wpExport/download.do?FUND_ID="+fundId+"&PEROID="+peroid;
		}
		
		//批量导出
		function exportAll(msg){
			bootbox.confirm(msg, function(result) {
				if(result) {
					var str = '';
					for(var i=0;i < document.getElementsByName('ids').length;i++){
					  if(document.getElementsByName('ids')[i].checked){
					  	if(str=='') str += document.getElementsByName('ids')[i].value;
					  	else str += ',' + document.getElementsByName('ids')[i].value;
					  }
					}
					if(str==''){
						bootbox.dialog({
							message: "<span class='bigger-110'>您没有选择任何内容!</span>",
							buttons: 			
							{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
						});
						$("#zcheckbox").tips({
							side:1,
				            msg:'点这里全选',
				            bg:'#AE81FF',
				            time:8
				        });
						return;
					}else{
						if(msg == '确定要删除选中的数据吗?'){
							top.jzts();
							$.ajax({
								type: "POST",
								url: '<%=basePath%>dataexport/exportAll.do?tm='+new Date().getTime(),
						    	data: {DATA_IDS:str},
								dataType:'json',
								//beforeSend: validateData,
								cache: false,
								success: function(data){
									 $.each(data.list, function(i, list){
											tosearch();
									 });
								}
							});
						}
					}
				}
			});
		};
		
	</script>


</body>
</html>