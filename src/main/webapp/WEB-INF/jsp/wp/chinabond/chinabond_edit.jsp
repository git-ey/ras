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
					
					<form action="chinabond/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="CHINABOND_ID" id="CHINABOND_ID" value="${pd.CHINABOND_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
							    <td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="255" title="期间" style="width:98%;"/></td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">流通场所:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="255" title="流通场所" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券简称:</td>
								<td><input type="text" autocomplete="off" name="BOND_NAME" id="BOND_NAME" value="${pd.BOND_NAME}" maxlength="255" title="债券简称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" autocomplete="off" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="255" title="债券代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值日期:</td>
								<td><input class="span10 date-picker" name="VALUE_DATE" id="VALUE_DATE" value="${pd.VALUE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="估值日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估价净价:</td>
								<td><input type="number" autocomplete="off" name="VALUATION_NET_PRICE" id="VALUATION_NET_PRICE" value="${pd.VALUATION_NET_PRICE}" maxlength="32" title="估价净价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">估价收益率:</td>
								<td><input type="number" autocomplete="off" name="VALUATION_RETURN" id="VALUATION_RETURN" value="${pd.VALUATION_RETURN}" maxlength="32"  title="估价收益率" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估价修正久期:</td>
								<td><input type="number" autocomplete="off" name="DURATION" id="DURATION" value="${pd.DURATION}" maxlength="32" title="估价修正久期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">估价凸性:</td>
								<td><input type="number" autocomplete="off" name="CONVEXITY" id="CONVEXITY" value="${pd.CONVEXITY}" maxlength="32" title="估价凸性" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">可信度:</td>
								<td><input type="text" autocomplete="off" name="RELIABILITY" id="RELIABILITY" value="${pd.RELIABILITY}" maxlength="255" title="可信度" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">日终估价全价:</td>
								<td><input type="number" autocomplete="off" name="VALUATION_PRICE_END" id="VALUATION_PRICE_END" value="${pd.VALUATION_PRICE_END}" maxlength="32" title="日终估价全价" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">日终应计利息:</td>
								<td><input type="number" autocomplete="off" name="INTEREST_END" id="INTEREST_END" value="${pd.INTEREST_END}" maxlength="32" title="日终应计利息" style="width:98%;"/></td>
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
			if($("#BOND_NAME").val()==""){
				$("#BOND_NAME").tips({
					side:3,
		            msg:'请输入债券简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_NAME").focus();
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
			if($("#VALUE_DATE").val()==""){
				$("#VALUE_DATE").tips({
					side:3,
		            msg:'请输入估值日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUE_DATE").focus();
			return false;
			}
			<!--
			if($("#VALUATION_NET_PRICE").val()==""){
				$("#VALUATION_NET_PRICE").tips({
					side:3,
		            msg:'请输入估价净价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUATION_NET_PRICE").focus();
			return false;
			}
			if($("#VALUATION_RETURN").val()==""){
				$("#VALUATION_RETURN").tips({
					side:3,
		            msg:'请输入估价收益率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUATION_RETURN").focus();
			return false;
			}
			if($("#DURATION").val()==""){
				$("#DURATION").tips({
					side:3,
		            msg:'请输入估价修正久期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DURATION").focus();
			return false;
			}
			if($("#CONVEXITY").val()==""){
				$("#CONVEXITY").tips({
					side:3,
		            msg:'请输入估价凸性',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONVEXITY").focus();
			return false;
			}
			if($("#RELIABILITY").val()==""){
				$("#RELIABILITY").tips({
					side:3,
		            msg:'请输入可信度',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RELIABILITY").focus();
			return false;
			}
			if($("#VALUATION_PRICE_END").val()==""){
				$("#VALUATION_PRICE_END").tips({
					side:3,
		            msg:'请输入日终估价全价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUATION_PRICE_END").focus();
			return false;
			}
			if($("#INTEREST_END").val()==""){
				$("#INTEREST_END").tips({
					side:3,
		            msg:'请输入日终应计利息',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_END").focus();
			return false;
			}
			-->
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