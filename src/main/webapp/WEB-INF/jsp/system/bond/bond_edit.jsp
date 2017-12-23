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
					
					<form action="bond/${msg }.do" name="Form" id="Form" method="post">
					    <input type="hidden" name="BONDINFO_ID" id="BONDINFO_ID" value="${pd.BONDINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="10" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">数据来源:</td>
								<td><input type="text" name="DATA_SOURCE" id="DATA_SOURCE" value="${pd.DATA_SOURCE}" maxlength="20" placeholder="这里输入数据来源" title="数据来源" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="30" placeholder="这里输入债券代码" title="债券代码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">债券简称:</td>
								<td><input type="text" name="SHORT_NAME" id="SHORT_NAME" value="${pd.SHORT_NAME}" maxlength="120" placeholder="这里输入债券简称" title="债券简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">债券全称:</td>
								<td><input type="text" name="FULL_NAME" id="FULL_NAME" value="${pd.FULL_NAME}" maxlength="240" placeholder="这里输入债券全称" title="债券全称" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">债券类型:</td>
								<td><input type="text" name="BOND_TYPE" id="BOND_TYPE" value="${pd.BOND_TYPE}" maxlength="60" placeholder="这里输入债券类型" title="债券类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">上市市场:</td>
								<td><input type="text" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="60" placeholder="这里输入上市市场" title="上市市场" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">市场类型:</td>
								<td><input type="text" name="MARKET_TYPE" id="MARKET_TYPE" value="${pd.MARKET_TYPE}" maxlength="60" placeholder="这里输入市场类型" title="市场类型" style="width:98%;"/></td>
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
			if($("#PERIOD").val()==""){
				$("#PERIOD").tips({
					side:3,
		            msg:'请输入期间',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PERIOD").focus();
			return false;
			}
			if($("#DATA_SOURCE").val()==""){
				$("#DATA_SOURCE").tips({
					side:3,
		            msg:'请输入数据来源',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATA_SOURCE").focus();
			return false;
			}
			if($("#BOND_CODE").val()==""){
				$("#BOND_CODE").tips({
					side:3,
		            msg:'请输入债券代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_CODE").focus();
			return false;
			}
			if($("#SHORT_NAME").val()==""){
				$("#SHORT_NAME").tips({
					side:3,
		            msg:'请输入债券简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHORT_NAME").focus();
			return false;
			}
			if($("#FULL_NAME").val()==""){
				$("#FULL_NAME").tips({
					side:3,
		            msg:'请输入债券全称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FULL_NAME").focus();
			return false;
			}
			if($("#BOND_TYPE").val()==""){
				$("#BOND_TYPE").tips({
					side:3,
		            msg:'请输入债券类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_TYPE").focus();
			return false;
			}
			if($("#MARKET").val()==""){
				$("#MARKET").tips({
					side:3,
		            msg:'请输入上市市场',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET").focus();
			return false;
			}
			if($("#MARKET_TYPE").val()==""){
				$("#MARKET_TYPE").tips({
					side:3,
		            msg:'请输入市场类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET_TYPE").focus();
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