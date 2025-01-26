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
					
					<form action="stocklimitinfo/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="STOCKLIMITINFO_ID" id="STOCKLIMITINFO_ID" value="${pd.STOCKLIMITINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="60" title="公司代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" title="基金代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="60" title="科目代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" autocomplete="off" name="STOCK_CODE" id="STOCK_CODE" value="${pd.STOCK_CODE}" maxlength="30" title="股票代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">股票名称:</td>
								<td><input type="text" autocomplete="off" name="STOCK_NAME" id="STOCK_NAME" value="${pd.STOCK_NAME}" maxlength="60" title="股票名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="30" title="交易市场" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">子类型:</td>
								<td><input type="text" autocomplete="off" name="SUB_TYPE" id="SUB_TYPE" value="${pd.SUB_TYPE}" maxlength="60" title="子类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末交易状态:</td>
								<td><input type="text" autocomplete="off" name="TRX_STATUS" id="TRX_STATUS" value="${pd.TRX_STATUS}" maxlength="30" title="期末交易状态" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">流通受限类型:</td>
								<td><input type="text" autocomplete="off" name="RESTRICT_TYPE" id="RESTRICT_TYPE" value="${pd.RESTRICT_TYPE}" maxlength="60" title="流通受限类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">认购日:</td>
								<td><input class="span10 date-picker" name="SUBSCRIBE_DATE" id="SUBSCRIBE_DATE" value="${pd.SUBSCRIBE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="认购日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">认购价格:</td>
								<td><input type="number" autocomplete="off" name="SUBSCRIBE_PRICE" id="SUBSCRIBE_PRICE" value="${pd.SUBSCRIBE_PRICE}" maxlength="32" title="认购价格" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">可流通日:</td>
								<td><input class="span10 date-picker" name="LEFTING_DATE" id="LEFTING_DATE" value="${pd.LEFTING_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="可流通日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">停牌日期:</td>
								<td><input class="span10 date-picker" name="SUSPENSION_DATE" id="SUSPENSION_DATE" value="${pd.SUSPENSION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="停牌日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">停牌原因:</td>
								<td><input type="text" autocomplete="off" name="SUSPENSION_INFO" id="SUSPENSION_INFO" value="${pd.SUSPENSION_INFO}" maxlength="255" title="停牌原因" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">复牌日期:</td>
								<td><input class="span10 date-picker" name="RESUMPTION_DATE" id="RESUMPTION_DATE" value="${pd.RESUMPTION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="复牌日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">复牌开盘单价:</td>
								<td><input type="text" autocomplete="off" name="RESMPATION_OPEN_PRICE" id="RESMPATION_OPEN_PRICE" value="${pd.RESMPATION_OPEN_PRICE}" maxlength="255" title="复牌开盘单价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">Createdby:</td>
								<td><input type="text" autocomplete="off" name="CREATOR" id="CREATOR" value="${pd.CREATOR}" maxlength="255" title="Createdby" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">ReviewedBy:</td>
								<td><input type="text" autocomplete="off" name="REVIEWER" id="REVIEWER" value="${pd.REVIEWER}" maxlength="255" title="ReviewedBy" style="width:98%;"/></td>
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
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入公司代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
			if($("#FUND_ID").val()==""){
				$("#FUND_ID").tips({
					side:3,
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
			return false;
			}
			if($("#ACCOUNT_NUM").val()==""){
				$("#ACCOUNT_NUM").tips({
					side:3,
		            msg:'请输入科目代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACCOUNT_NUM").focus();
			return false;
			}
			if($("#STOCK_CODE").val()==""){
				$("#STOCK_CODE").tips({
					side:3,
		            msg:'请输入股票代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_CODE").focus();
			return false;
			}
			if($("#STOCK_NAME").val()==""){
				$("#STOCK_NAME").tips({
					side:3,
		            msg:'请输入股票名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_NAME").focus();
			return false;
			}
			if($("#MARKET").val()==""){
				$("#MARKET").tips({
					side:3,
		            msg:'请输入交易市场',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET").focus();
			return false;
			}
			if($("#SUB_TYPE").val()==""){
				$("#SUB_TYPE").tips({
					side:3,
		            msg:'请输入子类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUB_TYPE").focus();
			return false;
			}
			if($("#TRX_STATUS").val()==""){
				$("#TRX_STATUS").tips({
					side:3,
		            msg:'请输入期末交易状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_STATUS").focus();
			return false;
			}
			if($("#LEFTING_DATE").val()==""){
				$("#LEFTING_DATE").tips({
					side:3,
		            msg:'请输入可流通日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LEFTING_DATE").focus();
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