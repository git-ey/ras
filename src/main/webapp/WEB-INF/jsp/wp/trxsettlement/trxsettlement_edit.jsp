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
					
					<form action="trxsettlement/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="TRXSETTLEMENT_ID" id="TRXSETTLEMENT_ID" value="${pd.TRXSETTLEMENT_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易日期:</td>
								<td><input class="span10 date-picker" name="TRANSACTION_DATE" id="TRANSACTION_DATE" value="${pd.TRANSACTION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="交易日期" title="交易日期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="10" placeholder="这里输入币种" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY买卖类型:</td>
								<td><input type="text" autocomplete="off" name="EY_BUYSELL_TYPE" id="EY_BUYSELL_TYPE" value="${pd.EY_BUYSELL_TYPE}" maxlength="255" placeholder="这里输入EY买卖类型" title="EY买卖类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY股债类型:</td>
								<td><input type="text" autocomplete="off" name="EY_STOCKBOND_TYPE" id="EY_STOCKBOND_TYPE" value="${pd.EY_STOCKBOND_TYPE}" maxlength="255" placeholder="这里输入EY股债类型" title="EY股债类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券代码:</td>
								<td><input type="text" autocomplete="off" name="STOCK_CODE" id="STOCK_CODE" value="${pd.STOCK_CODE}" maxlength="255" placeholder="这里输入证券代码" title="证券代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券名称:</td>
								<td><input type="text" autocomplete="off" name="STOCK_NAME" id="STOCK_NAME" value="${pd.STOCK_NAME}" maxlength="255" placeholder="这里输入证券名称" title="证券名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交数量:</td>
								<td><input type="number" autocomplete="off" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="32" placeholder="这里输入成交数量" title="成交数量" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交金额:</td>
								<td><input type="number" autocomplete="off" name="AMOUNT" id="AMOUNT" value="${pd.AMOUNT}" maxlength="32" placeholder="这里输入成交金额" title="成交金额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">席位佣金:</td>
								<td><input type="number" autocomplete="off" name="SEAT_COMMISSION" id="SEAT_COMMISSION" value="${pd.SEAT_COMMISSION}" maxlength="32" placeholder="这里输入席位佣金" title="席位佣金" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">印花税:</td>
								<td><input type="number" autocomplete="off" name="STAMPS" id="STAMPS" value="${pd.STAMPS}" maxlength="32" placeholder="这里输入印花税" title="印花税" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">经手费:</td>
								<td><input type="number" autocomplete="off" name="BROKERAGE_FEE" id="BROKERAGE_FEE" value="${pd.BROKERAGE_FEE}" maxlength="32" placeholder="这里输入经手费" title="经手费" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">过户费:</td>
								<td><input type="number" autocomplete="off" name="TRANSFER_FEE" id="TRANSFER_FEE" value="${pd.TRANSFER_FEE}" maxlength="32" placeholder="这里输入过户费" title="过户费" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">证管费:</td>
								<td><input type="number" autocomplete="off" name="SEC_FEE" id="SEC_FEE" value="${pd.SEC_FEE}" maxlength="32" placeholder="这里输入证管费" title="证管费" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">风险金:</td>
								<td><input type="number" autocomplete="off" name="CONTINGENCY_FEE" id="CONTINGENCY_FEE" value="${pd.CONTINGENCY_FEE}" maxlength="32" placeholder="这里输入风险金" title="风险金" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">其他费用:</td>
								<td><input type="number" autocomplete="off" name="OTHER_FEE" id="OTHER_FEE" value="${pd.OTHER_FEE}" maxlength="32" placeholder="这里输入其他费用" title="其他费用" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">券商过户费:</td>
								<td><input type="number" autocomplete="off" name="BROKERAGE_TRANSFER_FEE" id="BROKERAGE_TRANSFER_FEE" value="${pd.BROKERAGE_TRANSFER_FEE}" maxlength="32" placeholder="这里输入券商过户费" title="券商过户费" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券利息:</td>
								<td><input type="number" autocomplete="off" name="BOND_INTEREST" id="BOND_INTEREST" value="${pd.BOND_INTEREST}" maxlength="32" placeholder="这里输入债券利息" title="债券利息" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">回购收益:</td>
								<td><input type="number" autocomplete="off" name="REPO_EARNINGS" id="REPO_EARNINGS" value="${pd.REPO_EARNINGS}" maxlength="32" placeholder="这里输入回购收益" title="回购收益" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">实际清算金额:</td>
								<td><input type="number" autocomplete="off" name="NET_AMOUNT" id="NET_AMOUNT" value="${pd.NET_AMOUNT}" maxlength="32" placeholder="这里输入实际清算金额" title="实际清算金额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">业务类型:</td>
								<td><input type="text" autocomplete="off" name="TRX_TYPE" id="TRX_TYPE" value="${pd.TRX_TYPE}" maxlength="60" placeholder="这里输入业务类型" title="业务类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="120" placeholder="这里输入交易市场" title="交易市场" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易平台:</td>
								<td><input type="text" autocomplete="off" name="AGENT" id="AGENT" value="${pd.AGENT}" maxlength="120" placeholder="这里输入交易平台" title="交易平台" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">席位号:</td>
								<td><input type="text" autocomplete="off" name="SEAT_NUM" id="SEAT_NUM" value="${pd.SEAT_NUM}" maxlength="255" placeholder="这里输入席位号" title="席位号" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">均价:</td>
								<td><input type="number" autocomplete="off" name="AVERAGE_PRICE" id="AVERAGE_PRICE" value="${pd.AVERAGE_PRICE}" maxlength="32" placeholder="这里输入均价" title="均价" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY证券代码:</td>
								<td><input type="text" autocomplete="off" name="EY_SECURITY_CODE" id="EY_SECURITY_CODE" value="${pd.EY_SECURITY_CODE}" maxlength="255" placeholder="这里输入EY证券代码" title="EY证券代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY证券名称:</td>
								<td><input type="text" autocomplete="off" name="EY_SECURITY_NAME" id="EY_SECURITY_NAME" value="${pd.EY_SECURITY_NAME}" maxlength="255" placeholder="这里输入EY证券名称" title="EY证券名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
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
			if($("#TRANSACTION_DATE").val()==""){
				$("#TRANSACTION_DATE").tips({
					side:3,
		            msg:'请输入交易日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRANSACTION_DATE").focus();
			return false;
			}
			if($("#CURRENCY").val()==""){
				$("#CURRENCY").tips({
					side:3,
		            msg:'请输入币种',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CURRENCY").focus();
			return false;
			}
			if($("#STOCK_CODE").val()==""){
				$("#STOCK_CODE").tips({
					side:3,
		            msg:'请输入证券代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_CODE").focus();
			return false;
			}
			if($("#STOCK_NAME").val()==""){
				$("#STOCK_NAME").tips({
					side:3,
		            msg:'请输入证券名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_NAME").focus();
			return false;
			}
			if($("#NET_AMOUNT").val()==""){
				$("#NET_AMOUNT").tips({
					side:3,
		            msg:'请输入实际清算金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#NET_AMOUNT").focus();
			return false;
			}
			if($("#TRX_TYPE").val()==""){
				$("#TRX_TYPE").tips({
					side:3,
		            msg:'请输入业务类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_TYPE").focus();
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