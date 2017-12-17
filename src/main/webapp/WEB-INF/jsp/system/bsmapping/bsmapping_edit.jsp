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
					
					<form action="bsmapping/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="BSMAPPING_ID" id="BSMAPPING_ID" value="${pd.BSMAPPING_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">表项:</td>
								<td><input type="text" name="BTCODE" id="BTCODE" value="${pd.BTCODE}" maxlength="255" title="表项" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">表项名称:</td>
								<td><input type="text" name="DESCRIPSION" id="DESCRIPSION" value="${pd.DESCRIPSION}" maxlength="255" title="表项名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">客户项名称:</td>
								<td><input type="text" name="BT_DSE_CLIENT" id="BT_DSE_CLIENT" value="${pd.BT_DSE_CLIENT}" maxlength="255" title="客户项名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">来源表:</td>
								<td>
								<select class="chosen-select form-control" name="SOURCETABLE" id="SOURCETABLE" data-placeholder="请选择" style="width:49%;">
								    <option value="" ></option>
								    <option value="C" <c:if test="${pd.SOURCETABLE == 'C'}">selected</c:if>>C</option>
								    <option value="H" <c:if test="${pd.SOURCETABLE == 'H'}">selected</c:if>>H</option>
								    <option value="P" <c:if test="${pd.SOURCETABLE == 'P'}">selected</c:if>>P</option>
								    <option value="E" <c:if test="${pd.SOURCETABLE == 'E'}">selected</c:if>>E</option>
								    <option value="G" <c:if test="${pd.SOURCETABLE == 'G'}">selected</c:if>>G</option>
								    <option value="N" <c:if test="${pd.SOURCETABLE == 'N'}">selected</c:if>>N</option>
								    <option value="T" <c:if test="${pd.SOURCETABLE == 'T'}">selected</c:if>>T</option>
								    <option value="I" <c:if test="${pd.SOURCETABLE == 'I'}">selected</c:if>>I</option>
								    <option value="U" <c:if test="${pd.SOURCETABLE == 'U'}">selected</c:if>>U</option>
								    <option value="V" <c:if test="${pd.SOURCETABLE == 'V'}">selected</c:if>>V</option>
							    </select>
							    </td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">披露名称:</td>
								<td><input type="text" name="REVEAL_ITEM" id="REVEAL_ITEM" value="${pd.REVEAL_ITEM}" maxlength="255" title="披露名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY科目代码:</td>
								<td><input type="text" name="EY_ACCOUNT_NUM" id="EY_ACCOUNT_NUM" value="${pd.EY_ACCOUNT_NUM}" maxlength="255" title="EY科目代码" style="width:98%;"/></td>
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
			if($("#BTCODE").val()==""){
				$("#BTCODE").tips({
					side:3,
		            msg:'请输入表项',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BTCODE").focus();
			return false;
			}
			if($("#DESCRIPSION").val()==""){
				$("#DESCRIPSION").tips({
					side:3,
		            msg:'请输入表项名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DESCRIPSION").focus();
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