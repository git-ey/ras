<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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

					<form action="workpaper/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="WORKPAPER_ID" id="WORKPAPER_ID" value="${pd.WORKPAPER_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">资产负债表日:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" maxlength="30" placeholder="yyyymmdd" title="资产负债表日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">管理公司:</td>
								<td>
								    <select class="chosen-select form-control" name="FIRM_CODE" id="FIRM_CODE" data-placeholder="请选择公司" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${companyList}" var="var" varStatus="vs">
									        <option value="${var.COMPANY_CODE}" <c:if test="${pd.FIRM_CODE == var.COMPANY_CODE}">selected</c:if>>${var.SHORT_NAME}</option>
									    </c:forEach>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td>
									<select class="chosen-select form-control" name="FUND_ID" id="FUND_ID" data-placeholder="请选择基金" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${fundList}" var="var" varStatus="vs">
									        <option value="${var.FUND_ID}" <c:if test="${pd.FUND_ID == var.FUND_ID}">selected</c:if>>${var.FUND_ID}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">导出路径:</td>
								<td><input type="text" autocomplete="off" name="OUTBOND_PATH" id="OUTBOND_PATH" value="${outbondPath}" maxlength="480" title="导出路径" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">底稿类型:</td>
								<td>
								    <select class="chosen-select form-control" name="WP_TYPE" id="WP_TYPE" data-placeholder="请选择" style="width:49%;">
								        <option value="C" <c:if test="${pd.WP_TYPE == 'C'}">selected</c:if>>C</option>
								        <option value="E" <c:if test="${pd.WP_TYPE == 'E'}">selected</c:if>>E</option>
								        <option value="G" <c:if test="${pd.WP_TYPE == 'G'}">selected</c:if>>G</option>
								        <option value="H" <c:if test="${pd.WP_TYPE == 'H'}">selected</c:if>>H</option>
								        <option value="H_SUM" <c:if test="${pd.WP_TYPE == 'H_SUM'}">selected</c:if>>H旗下</option>
								        <option value="I" <c:if test="${pd.WP_TYPE == 'I'}">selected</c:if>>I</option>
								        <option value="N" <c:if test="${pd.WP_TYPE == 'N'}">selected</c:if>>N</option>
								        <option value="P" <c:if test="${pd.WP_TYPE == 'P'}">selected</c:if>>P</option>
								        <option value="T" <c:if test="${pd.WP_TYPE == 'T'}">selected</c:if>>T</option>
								        <option value="U" <c:if test="${pd.WP_TYPE == 'U'}">selected</c:if>>U</option>
								        <option value="V" <c:if test="${pd.WP_TYPE == 'V'}">selected</c:if>>V</option>
								        <option value="O" <c:if test="${pd.WP_TYPE == 'O'}">selected</c:if>>O</option>
										<option value="SA" <c:if test="${pd.WP_TYPE == 'SA'}">selected</c:if>>SA</option>
										<option value="全部底稿" <c:if test="${pd.WP_TYPE == '全部底稿'}">selected</c:if>>全部底稿</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">特殊底稿:</td>
								<td>
									<%--<select class="chosen-select form-control" name="WP_TYPE_C" id="WP_TYPE_C" data-placeholder="请选择" style="width:49%;">--%>
										<%--<option value="" <c:if test="${pd.WP_TYPE_C == ''}">selected</c:if>></option>--%>
										<%--<option value="BJ" <c:if test="${pd.WP_TYPE_C == 'BJ'}">selected</c:if>>特殊底稿</option>--%>
									<%--</select>--%>
										<label>
											<input type="checkbox" name="WP_TYPE_C" id="WP_TYPE_C_BJ" value="BJ" ${pd.WP_TYPE_C == 'BJ' ? 'checked' : ''}>
										</label>
								</td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">导出</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
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
</div>
<!-- /.main-container -->


	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript">
		$(top.hangge());
		//保存
		function save(){
			if($("#PERIOD").val()==""){
				$("#PERIOD").tips({
					side:3,
		            msg:'请输入资产负债表日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PERIOD").focus();
			return false;
			}
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入管理公司',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}

		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
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
		});
		</script>
</body>
</html>
