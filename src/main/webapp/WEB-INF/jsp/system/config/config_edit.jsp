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
					
					<form action="config/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="CONFIG_ID" id="CONFIG_ID" value="${pd.CONFIG_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">配置代码:</td>
								<td><input type="text" name="CONFIG_CODE" id="CONFIG_CODE" value="${pd.CONFIG_CODE}" maxlength="120" placeholder="这里输入配置代码" title="配置代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">配置值:</td>
								<td><input type="text" name="CONFIG_VALUE" id="CONFIG_VALUE" value="${pd.CONFIG_VALUE}" maxlength="480" placeholder="这里输入配置值" title="配置值" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
								<div class="col-sm-9">
									<label style="float:left;padding-left: 8px;padding-top:7px;">
										<input name="ENABLED_FLAG" type="radio" class="ace" id="form-field-radio1" value="Y" <c:if test="${pd.ENABLED_FLAG == 'Y' }">checked="checked"</c:if>/>
										<span class="lbl"> 是</span>
									</label>
									<label style="float:left;padding-left: 5px;padding-top:7px;">
										<input name="ENABLED_FLAG" type="radio" class="ace" id="form-field-radio2" value="N" <c:if test="${pd.ENABLED_FLAG == 'N' }">checked="checked"</c:if>/>
										<span class="lbl"> 否</span>
									</label>
								</div>
								</td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
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
			if($("#CONFIG_CODE").val()==""){
				$("#CONFIG_CODE").tips({
					side:3,
		            msg:'请输入配置代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONFIG_CODE").focus();
			return false;
			}
			if($("#CONFIG_VALUE").val()==""){
				$("#CONFIG_VALUE").tips({
					side:3,
		            msg:'请输入配置值',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONFIG_VALUE").focus();
			return false;
			}
			if($("#ENABLED_FLAG").val()==""){
				$("#ENABLED_FLAG").tips({
					side:3,
		            msg:'请输入是否启用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENABLED_FLAG").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
		});
		</script>
</body>
</html>