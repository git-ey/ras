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
					
					<form action="stockexchangerate/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="STOCKEXCHANGERATE_ID" id="STOCKEXCHANGERATE_ID" value="${pd.STOCKEXCHANGERATE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易所:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="255" placeholder="这里输入交易所" title="交易所" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">印花税率:</td>
								<td><input type="number" autocomplete="off" name="RATE_STAMP" id="RATE_STAMP" value="<fmt:formatNumber value='${pd.RATE_STAMP}' type='number' pattern='0.0000000'/>" maxlength="32" placeholder="这里输入印花税率" title="印花税率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">经手费率:</td>
								<td><input type="number" autocomplete="off" name="RATE_BROKERAGE" id="RATE_BROKERAGE" value="<fmt:formatNumber value='${pd.RATE_BROKERAGE}' type='number' pattern='0.0000000'/>" maxlength="32" placeholder="这里输入经手费率" title="经手费率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">证管费率:</td>
								<td><input type="number" autocomplete="off" name="RATE_SEC" id="RATE_SEC" value="<fmt:formatNumber value='${pd.RATE_SEC}' type='number' pattern='0.0000000'/>" maxlength="32" placeholder="这里输入证管费率" title="证管费率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">过户费率:</td>
								<td><input type="number" autocomplete="off" name="RATE_TRANSFER" id="RATE_TRANSFER" value="<fmt:formatNumber value='${pd.RATE_TRANSFER}' type='number' pattern='0.0000000'/>" maxlength="32" placeholder="这里输入过户费率" title="过户费率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">启动:</td>
							    <td>
									<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
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
			if($("#MARKET").val()==""){
				$("#MARKET").tips({
					side:3,
		            msg:'请输入交易所',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET").focus();
			return false;
			}
			if($("#RATE_STAMP").val()==""){
				$("#RATE_STAMP").tips({
					side:3,
		            msg:'请输入印花税率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RATE_STAMP").focus();
			return false;
			}
			if($("#RATE_BROKERAGE").val()==""){
				$("#RATE_BROKERAGE").tips({
					side:3,
		            msg:'请输入经手费率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RATE_BROKERAGE").focus();
			return false;
			}
			if($("#RATE_SEC").val()==""){
				$("#RATE_SEC").tips({
					side:3,
		            msg:'请输入证管费率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RATE_SEC").focus();
			return false;
			}
			if($("#RATE_TRANSFER").val()==""){
				$("#RATE_TRANSFER").tips({
					side:3,
		            msg:'请输入过户费率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RATE_TRANSFER").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入启动',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
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