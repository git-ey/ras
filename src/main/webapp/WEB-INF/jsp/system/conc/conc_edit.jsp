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
					
					<form action="conc/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="CONC_ID" id="CONC_ID" value="${pd.CONC_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">程序代码:</td>
								<td><input type="text" name="CONC_CODE" id="CONC_CODE" value="${pd.CONC_CODE}" maxlength="60" placeholder="这里输入程序代码" title="程序代码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">程序描述:</td>
								<td><input type="text" name="CONC_DESCRIPTION" id="CONC_DESCRIPTION" value="${pd.CONC_DESCRIPTION}" maxlength="255" placeholder="这里输入程序描述" title="程序描述" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">程序类型:</td>
								<td>
								    <select class="chosen-select form-control" name="CONC_TYPE" id="CONC_TYPE" data-placeholder="请选择" style="width:49%;">
								        <option value="P" <c:if test="${pd.CONC_TYPE == 'Y'}">selected</c:if>>存储过程</option>
								    </select>
								</td>
								<td style="width:75px;text-align: right;padding-top: 13px;">程序入口:</td>
								<td><input type="text" name="CONC_PROGRAM" id="CONC_PROGRAM" value="${pd.CONC_PROGRAM}" maxlength="255" placeholder="这里输入程序入口" title="程序入口" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ENABLED_FLAG" id="ENABLED_FLAG" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ENABLED_FLAG == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ENABLED_FLAG == 'N'}">selected</c:if>>否</option>
								    </select>
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

<c:if test="${'edit' == msg }">
	<div>
		<iframe name="treeFrame" id="treeFrame" frameborder="0" src="<%=basePath%>/concparam/list.do?CONC_ID=${pd.CONC_ID}" style="margin:0 auto;width:805px;height:310px;;"></iframe>
	</div>
</c:if>

<footer>
<div style="width: 100%;padding-bottom: 2px;" class="center">
	<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
	<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
</div>
</footer>

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
			if($("#CONC_CODE").val()==""){
				$("#CONC_CODE").tips({
					side:3,
		            msg:'请输入程序代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONC_CODE").focus();
			return false;
			}
			if($("#CONC_DESCRIPTION").val()==""){
				$("#CONC_DESCRIPTION").tips({
					side:3,
		            msg:'请输入程序描述',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONC_DESCRIPTION").focus();
			return false;
			}
			if($("#CONC_TYPE").val()==""){
				$("#CONC_TYPE").tips({
					side:3,
		            msg:'请输入程序类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONC_TYPE").focus();
			return false;
			}
			if($("#CONC_PROGRAM").val()==""){
				$("#CONC_PROGRAM").tips({
					side:3,
		            msg:'请输入程序入口',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONC_PROGRAM").focus();
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