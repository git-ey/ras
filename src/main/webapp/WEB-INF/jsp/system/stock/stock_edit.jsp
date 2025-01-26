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
					
					<form action="stock/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="STOCK_ID" id="STOCK_ID" value="${pd.STOCK_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" autocomplete="off" name="CODE" id="CODE" value="${pd.CODE}" maxlength="10" placeholder="这里输入股票代码" title="股票代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票简称:</td>
								<td><input type="text" autocomplete="off" name="NAME" id="NAME" value="${pd.NAME}" maxlength="30" placeholder="这里输入股票简称" title="股票简称" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">AMAC所属行业:</td>
								<td><input type="text" autocomplete="off" name="INDUSTRY" id="INDUSTRY" value="${pd.INDUSTRY}" maxlength="255" placeholder="这里输入AMAC所属行业" title="AMAC所属行业" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">估值日期:</td>
								<td><input class="span10 date-picker" name="VALUATION_DATE" id="VALUATION_DATE" value="${pd.VALUATION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="估值日期" title="估值日期" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">最近交易日:</td>
								<td><input class="span10 date-picker" name="RECENT_TRX_DATE" id="RECENT_TRX_DATE" value="${pd.RECENT_TRX_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="最近交易日" title="最近交易日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">行情收市价:</td>
								<td><input type="number" autocomplete="off" name="UNIT_PRICE" id="UNIT_PRICE" value="${pd.UNIT_PRICE}" maxlength="32" placeholder="这里输入行情收市价" title="行情收市价" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">停牌状态:</td>
								<td><input type="text" autocomplete="off" name="SUSPENSION" id="SUSPENSION" value="${pd.SUSPENSION}" maxlength="10" placeholder="这里输入停牌状态" title="停牌状态" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">停牌日期:</td>
								<td><input class="span10 date-picker" name="SUSPENSION_DATE" id="SUSPENSION_DATE" value="${pd.SUSPENSION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="停牌日期" title="停牌日期" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">停牌前日收盘价:</td>
								<td><input type="number" autocomplete="off" name="CLOSING_PRICE" id="CLOSING_PRICE" value="${pd.CLOSING_PRICE}" maxlength="32" placeholder="这里输入停牌前一日收盘价" title="停牌前一日收盘价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">停牌原因:</td>
								<td><input type="text" autocomplete="off" name="SUSPENSION_INFO" id="SUSPENSION_INFO" value="${pd.SUSPENSION_INFO}" maxlength="255" placeholder="这里输入停牌原因" title="停牌原因" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">复牌日期:</td>
								<td><input class="span10 date-picker" name="RESUMPTION_DATE" id="RESUMPTION_DATE" value="${pd.RESUMPTION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="复牌日期" title="复牌日期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">复牌开盘单价:</td>
								<td><input type="number" autocomplete="off" name="RESUMPTION_PRICE" id="RESUMPTION_PRICE" value="${pd.RESUMPTION_PRICE}" maxlength="32" placeholder="这里输入复牌开盘单价" title="复牌开盘单价" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">新股:</td>
								<td><input type="text" autocomplete="off" name="NEW_FLAG" id="NEW_FLAG" value="${pd.NEW_FLAG}" maxlength="10" placeholder="这里输入新股" title="新股" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">新股可流通日:</td>
								<td><input class="span10 date-picker" name="AFLOAT_DATE" id="AFLOAT_DATE" value="${pd.AFLOAT_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="新股可流通日" title="新股可流通日" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">状态:</td>
								<td><input type="text" autocomplete="off" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="30" placeholder="这里输入状态" title="状态" style="width:98%;"/></td>
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
			if($("#CODE").val()==""){
				$("#CODE").tips({
					side:3,
		            msg:'请输入股票代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CODE").focus();
			return false;
			}
			if($("#NAME").val()==""){
				$("#NAME").tips({
					side:3,
		            msg:'请输入股票简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#NAME").focus();
			return false;
			}
			if($("#INDUSTRY").val()==""){
				$("#INDUSTRY").tips({
					side:3,
		            msg:'请输入AMAC所属行业',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INDUSTRY").focus();
			return false;
			}
			if($("#VALUATION_DATE").val()==""){
				$("#VALUATION_DATE").tips({
					side:3,
		            msg:'请输入估值日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUATION_DATE").focus();
			return false;
			}
			if($("#RECENT_TRX_DATE").val()==""){
				$("#RECENT_TRX_DATE").tips({
					side:3,
		            msg:'请输入最近交易日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RECENT_TRX_DATE").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入启用',
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