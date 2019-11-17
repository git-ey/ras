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
					
					<form action="dailysettlement/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="DAILYSETTLEMENT_ID" id="DAILYSETTLEMENT_ID" value="${pd.DAILYSETTLEMENT_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="60" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="20" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">日期:</td>
								<td><input class="span10 date-picker" name="TRX_DATE" id="TRX_DATE" value="${pd.TRX_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="日期" title="日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">业务名称:</td>
								<td><input type="text" autocomplete="off" name="TRX_TYPE" id="TRX_TYPE" value="${pd.TRX_TYPE}" maxlength="60" title="业务名称" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">销售商名称:</td>
								<td><input type="text" autocomplete="off" name="DEALER" id="DEALER" value="${pd.DEALER}" maxlength="255" title="销售商名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交份额:</td>
								<td><input type="number" autocomplete="off" name="TRX_QUANTITY" id="TRX_QUANTITY" value="${pd.TRX_QUANTITY}" maxlength="32" title="成交份额" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">成交金额:</td>
								<td><input type="number" autocomplete="off" name="TRX_AMOUNT" id="TRX_AMOUNT" value="${pd.TRX_AMOUNT}" maxlength="32" title="成交金额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易费用:</td>
								<td><input type="number" autocomplete="off" name="TRX_FEE" id="TRX_FEE" value="${pd.TRX_FEE}" maxlength="32" title="交易费用" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">后收费:</td>
								<td><input type="number" autocomplete="off" name="BACKEND_FEE" id="BACKEND_FEE" value="${pd.BACKEND_FEE}" maxlength="32"  title="后收费" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">确认金额:</td>
								<td><input type="number" autocomplete="off" name="CONFIRMED_AMOUNT" id="CONFIRMED_AMOUNT" value="${pd.CONFIRMED_AMOUNT}" maxlength="32" title="确认金额" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td><input type="text" autocomplete="off" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="30" title="状态" style="width:98%;"/></td>
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
			if($("#TRX_DATE").val()==""){
				$("#TRX_DATE").tips({
					side:3,
		            msg:'请输入日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_DATE").focus();
			return false;
			}
			if($("#TRX_TYPE").val()==""){
				$("#TRX_TYPE").tips({
					side:3,
		            msg:'请输入业务名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_TYPE").focus();
			return false;
			}
			if($("#DEALER").val()==""){
				$("#DEALER").tips({
					side:3,
		            msg:'请输入销售商名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DEALER").focus();
			return false;
			}
			if($("#TRX_QUANTITY").val()==""){
				$("#TRX_QUANTITY").tips({
					side:3,
		            msg:'请输入成交份额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_QUANTITY").focus();
			return false;
			}
			if($("#TRX_AMOUNT").val()==""){
				$("#TRX_AMOUNT").tips({
					side:3,
		            msg:'请输入成交金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_AMOUNT").focus();
			return false;
			}
			if($("#TRX_FEE").val()==""){
				$("#TRX_FEE").tips({
					side:3,
		            msg:'请输入交易费用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_FEE").focus();
			return false;
			}
			if($("#BACKEND_FEE").val()==""){
				$("#BACKEND_FEE").tips({
					side:3,
		            msg:'请输入后收费',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BACKEND_FEE").focus();
			return false;
			}
			if($("#CONFIRMED_AMOUNT").val()==""){
				$("#CONFIRMED_AMOUNT").tips({
					side:3,
		            msg:'请输入确认金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONFIRMED_AMOUNT").focus();
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