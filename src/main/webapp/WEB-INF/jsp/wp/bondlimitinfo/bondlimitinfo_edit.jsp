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
					
					<form action="bondlimitinfo/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="BONDLIMITINFO_ID" id="BONDLIMITINFO_ID" value="${pd.BONDLIMITINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="30" title="公司代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" title="基金代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">货基:</td>
								<td><input type="text" name="MMF" id="MMF" value="${pd.MMF}" maxlength="10" title="货基" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="255" title="科目代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="60" title="债券代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券名称:</td>
								<td><input type="text" name="BOND_NAME" id="BOND_NAME" value="${pd.BOND_NAME}" maxlength="240" title="债券名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易市场:</td>
								<td><input type="text" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="30" title="交易市场" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">子类型:</td>
								<td><input type="text" name="SUB_TYPE" id="SUB_TYPE" value="${pd.SUB_TYPE}" maxlength="30" title="子类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末交易状态:</td>
								<td><input type="text" name="TRX_STATUS" id="TRX_STATUS" value="${pd.TRX_STATUS}" maxlength="60" title="期末交易状态" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">流通受限类型:</td>
								<td><input type="text" name="RESTRICT_TYPE" id="RESTRICT_TYPE" value="${pd.RESTRICT_TYPE}" maxlength="255" title="流通受限类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成功认购日:</td>
								<td><input class="span10 date-picker" name="SUBSCRIBE_DATE" id="SUBSCRIBE_DATE" value="${pd.SUBSCRIBE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="成功认购日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">认购价格:</td>
								<td><input type="number" name="SUBSCRIBE_PRICE" id="SUBSCRIBE_PRICE" value="${pd.SUBSCRIBE_PRICE}" maxlength="32" title="认购价格" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">可流通日:</td>
								<td><input class="span10 date-picker" name="LIFTING_DATE" id="LIFTING_DATE" value="${pd.LIFTING_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="可流通日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">停牌日期:</td>
								<td><input class="span10 date-picker" name="SUSPENSION_DATE" id="SUSPENSION_DATE" value="${pd.SUSPENSION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="停牌日期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">停牌原因:</td>
								<td><input type="text" name="SUSPENSION_INFO" id="SUSPENSION_INFO" value="${pd.SUSPENSION_INFO}" maxlength="255" title="停牌原因" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">复牌日期:</td>
								<td><input class="span10 date-picker" name="RESUMPTION_DATE" id="RESUMPTION_DATE" value="${pd.RESUMPTION_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="复牌日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">复牌开盘单价:</td>
								<td><input type="number" name="RESMPATION_OPEN_PRICE" id="RESMPATION_OPEN_PRICE" value="${pd.RESMPATION_OPEN_PRICE}" maxlength="32" title="复牌开盘单价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">CreatedBy:</td>
								<td><input type="text" name="CREATOR" id="CREATOR" value="${pd.CREATOR}" maxlength="255" title="CreatedBy" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">ReviewedBy:</td>
								<td><input type="text" name="REVIEWER" id="REVIEWER" value="${pd.REVIEWER}" maxlength="255" title="ReviewedBy" style="width:98%;"/></td>
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
			<!--
			if($("#MMF").val()==""){
				$("#MMF").tips({
					side:3,
		            msg:'请输入货基',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MMF").focus();
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
			if($("#BOND_NAME").val()==""){
				$("#BOND_NAME").tips({
					side:3,
		            msg:'请输入债券名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_NAME").focus();
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
			if($("#RESTRICT_TYPE").val()==""){
				$("#RESTRICT_TYPE").tips({
					side:3,
		            msg:'请输入流通受限类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RESTRICT_TYPE").focus();
			return false;
			}
			if($("#SUBSCRIBE_DATE").val()==""){
				$("#SUBSCRIBE_DATE").tips({
					side:3,
		            msg:'请输入成功认购日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUBSCRIBE_DATE").focus();
			return false;
			}
			if($("#SUBSCRIBE_PRICE").val()==""){
				$("#SUBSCRIBE_PRICE").tips({
					side:3,
		            msg:'请输入认购价格',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUBSCRIBE_PRICE").focus();
			return false;
			}
			if($("#LIFTING_DATE").val()==""){
				$("#LIFTING_DATE").tips({
					side:3,
		            msg:'请输入可流通日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LIFTING_DATE").focus();
			return false;
			}
			if($("#SUSPENSION_DATE").val()==""){
				$("#SUSPENSION_DATE").tips({
					side:3,
		            msg:'请输入停牌日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUSPENSION_DATE").focus();
			return false;
			}
			if($("#SUSPENSION_INFO").val()==""){
				$("#SUSPENSION_INFO").tips({
					side:3,
		            msg:'请输入停牌原因',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUSPENSION_INFO").focus();
			return false;
			}
			if($("#RESUMPTION_DATE").val()==""){
				$("#RESUMPTION_DATE").tips({
					side:3,
		            msg:'请输入复牌日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RESUMPTION_DATE").focus();
			return false;
			}
			if($("#RESMPATION_OPEN_PRICE").val()==""){
				$("#RESMPATION_OPEN_PRICE").tips({
					side:3,
		            msg:'请输入复牌开盘单价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RESMPATION_OPEN_PRICE").focus();
			return false;
			}
			if($("#CREATOR").val()==""){
				$("#CREATOR").tips({
					side:3,
		            msg:'请输入CreatedBy',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CREATOR").focus();
			return false;
			}
			if($("#REVIEWER").val()==""){
				$("#REVIEWER").tips({
					side:3,
		            msg:'请输入ReviewedBy',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#REVIEWER").focus();
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