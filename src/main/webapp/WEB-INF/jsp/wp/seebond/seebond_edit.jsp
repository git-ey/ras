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
					
					<form action="seebond/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="SEEBOND_ID" id="SEEBOND_ID" value="${pd.SEEBOND_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="255" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值日:</td>
								<td><input class="span10 date-picker" name="VALUE_DATE" id="VALUE_DATE" value="${pd.VALUE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="估值日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">上海代码:</td>
								<td><input type="text" autocomplete="off" name="SHH_CODE" id="SHH_CODE" value="${pd.SHH_CODE}" maxlength="255" title="上海代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">深圳代码:</td>
								<td><input type="text" autocomplete="off" name="SHZ_CODE" id="SHZ_CODE" value="${pd.SHZ_CODE}" maxlength="255" title="深圳代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">银行间代码:</td>
								<td><input type="text" autocomplete="off" name="INTER_BANK_CODE" id="INTER_BANK_CODE" value="${pd.INTER_BANK_CODE}" maxlength="255" title="银行间代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">全价:</td>
								<td><input type="number" autocomplete="off" name="CALCULATION_PRICE" id="CALCULATION_PRICE" value="${pd.CALCULATION_PRICE}" maxlength="32" title="全价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">到期收益率:</td>
								<td><input type="number" autocomplete="off" name="YIELD_TO_MATURITY" id="YIELD_TO_MATURITY" value="${pd.YIELD_TO_MATURITY}" maxlength="32" title="到期收益率" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">修正久期:</td>
								<td><input type="number" autocomplete="off" name="MODIFIED_DURATION" id="MODIFIED_DURATION" value="${pd.MODIFIED_DURATION}" maxlength="32" title="修正久期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">凸性:</td>
								<td><input type="number" autocomplete="off" name="CONVEXITY" id="CONVEXITY" value="${pd.CONVEXITY}" maxlength="32" title="凸性" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">净价:</td>
								<td><input type="number" autocomplete="off" name="CLEAN_PRICE" id="CLEAN_PRICE" value="${pd.CLEAN_PRICE}" maxlength="32" title="净价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">应收利息:</td>
								<td><input type="number" autocomplete="off" name="ACCRUED_INTEREST" id="ACCRUED_INTEREST" value="${pd.ACCRUED_INTEREST}" maxlength="32" title="应收利息" style="width:98%;"/></td>
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
			if($("#VALUE_DATE").val()==""){
				$("#VALUE_DATE").tips({
					side:3,
		            msg:'请输入估值日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VALUE_DATE").focus();
			return false;
			}
			if($("#SHH_CODE").val()==""){
				$("#SHH_CODE").tips({
					side:3,
		            msg:'请输入上海代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHH_CODE").focus();
			return false;
			}
			if($("#SHZ_CODE").val()==""){
				$("#SHZ_CODE").tips({
					side:3,
		            msg:'请输入深圳代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHZ_CODE").focus();
			return false;
			}
			if($("#INTER_BANK_CODE").val()==""){
				$("#INTER_BANK_CODE").tips({
					side:3,
		            msg:'请输入银行间代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTER_BANK_CODE").focus();
			return false;
			}
			if($("#CALCULATION_PRICE").val()==""){
				$("#CALCULATION_PRICE").tips({
					side:3,
		            msg:'请输入全价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CALCULATION_PRICE").focus();
			return false;
			}
			if($("#YIELD_TO_MATURITY").val()==""){
				$("#YIELD_TO_MATURITY").tips({
					side:3,
		            msg:'请输入到期收益率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#YIELD_TO_MATURITY").focus();
			return false;
			}
			if($("#MODIFIED_DURATION").val()==""){
				$("#MODIFIED_DURATION").tips({
					side:3,
		            msg:'请输入修正久期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MODIFIED_DURATION").focus();
			return false;
			}
			if($("#CONVEXITY").val()==""){
				$("#CONVEXITY").tips({
					side:3,
		            msg:'请输入凸性',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONVEXITY").focus();
			return false;
			}
			if($("#CLEAN_PRICE").val()==""){
				$("#CLEAN_PRICE").tips({
					side:3,
		            msg:'请输入净价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CLEAN_PRICE").focus();
			return false;
			}
			if($("#ACCRUED_INTEREST").val()==""){
				$("#ACCRUED_INTEREST").tips({
					side:3,
		            msg:'请输入应收利息',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACCRUED_INTEREST").focus();
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