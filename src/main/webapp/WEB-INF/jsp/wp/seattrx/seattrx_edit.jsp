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
					
					<form action="seattrx/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="SEATTRX_ID" id="SEATTRX_ID" value="${pd.SEATTRX_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">顺序编码:</td>
								<td><input type="number" name="SEQ" id="SEQ" value="${pd.SEQ}" maxlength="255" placeholder="这里输入顺序编码" title="顺序编码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="255" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td>
								    <select class="chosen-select form-control" name="PERIOD" id="PERIOD" data-placeholder="请选择期间" style="vertical-align:top;width: 120px;">
									    <c:forEach items="${periodList}" var="var" varStatus="vs">
									        <option value="${var.PERIOD}" <c:if test="${pd.PERIOD == var.PERIOD}">selected</c:if>>${var.PERIOD_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">券商名称:</td>
								<td><input type="text" name="AGENCY_NAME" id="AGENCY_NAME" value="${pd.AGENCY_NAME}" maxlength="255" placeholder="这里输入券商名称" title="券商名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交额_股票:</td>
								<td><input type="number" name="AMOUNT_STOCK" id="AMOUNT_STOCK" value="${pd.AMOUNT_STOCK}" maxlength="32" placeholder="这里输入成交额_股票" title="成交额_股票" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交额_债券:</td>
								<td><input type="number" name="AMOUNT_BOND" id="AMOUNT_BOND" value="${pd.AMOUNT_BOND}" maxlength="32" placeholder="这里输入成交额_债券" title="成交额_债券" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交额_回购:</td>
								<td><input type="number" name="AMOUNT_REPO" id="AMOUNT_REPO" value="${pd.AMOUNT_REPO}" maxlength="32" placeholder="这里输入成交额_回购" title="成交额_回购" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交额_权证:</td>
								<td><input type="number" name="AMOUNT_WARRANT" id="AMOUNT_WARRANT" value="${pd.AMOUNT_WARRANT}" maxlength="32" placeholder="这里输入成交额_权证" title="成交额_权证" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交额_基金:</td>
								<td><input type="number" name="AMOUNT_FUND" id="AMOUNT_FUND" value="${pd.AMOUNT_FUND}" maxlength="32" placeholder="这里输入成交额_基金" title="成交额_基金" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">实付佣金:</td>
								<td><input type="number" name="ACTUAL_COMMISSION" id="ACTUAL_COMMISSION" value="${pd.ACTUAL_COMMISSION}" maxlength="32" placeholder="这里输入实付佣金" title="实付佣金" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
								<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td>
									<select class="chosen-select form-control" name="STATUS" id="STATUS" data-placeholder="请选择" style="width:49%;">
								    <option value="INITIAL" <c:if test="${pd.STATUS == 'INITIAL'}">selected</c:if>>INITIAL</option>
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
			if($("#SEQ").val()==""){
				$("#SEQ").tips({
					side:3,
		            msg:'请输入顺序编码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEQ").focus();
			return false;
			}
			if($("#FUND_ID").val()==""){
				$("#FUND_ID").tips({
					side:3,
		            msg:'请输入基金ID',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
			return false;
			}
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
			if($("#AGENCY_NAME").val()==""){
				$("#AGENCY_NAME").tips({
					side:3,
		            msg:'请输入券商名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#AGENCY_NAME").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入是否启用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
			return false;
			}
			if($("#STATUS").val()==""){
				$("#STATUS").tips({
					side:3,
		            msg:'请输入状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STATUS").focus();
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