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
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="SEEBOND_ID" id="SEEBOND_ID" value="${pd.SEEBOND_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="255" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值日:</td>
								<td><input class="span10 date-picker" name="VALUE_DATE" id="VALUE_DATE" value="${pd.VALUE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="估值日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">上海代码:</td>
								<td><input type="text" autocomplete="off" name="SHH_CODE" id="SHH_CODE" value="${pd.SHH_CODE}" maxlength="255" title="上海代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">深圳代码:</td>
								<td><input type="text" autocomplete="off" name="SHZ_CODE" id="SHZ_CODE" value="${pd.SHZ_CODE}" maxlength="255" title="深圳代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">银行间代码:</td>
								<td><input type="text" autocomplete="off" name="INTER_BANK_CODE" id="INTER_BANK_CODE" value="${pd.INTER_BANK_CODE}" maxlength="255" title="银行间代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">全价1:</td>
								<td><input type="number" autocomplete="off" name="CALCULATION_PRICE1" id="CALCULATION_PRICE1" value="${pd.CALCULATION_PRICE1}" maxlength="32" title="全价" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">净价1:</td>
								<td><input type="number" autocomplete="off" name="CLEAN_PRICE1" id="CLEAN_PRICE1" value="${pd.CLEAN_PRICE1}" maxlength="32" title="净价1" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">到期收益率1:</td>
								<td><input type="number" autocomplete="off" name="YIELD_TO_MATURITY1" id="YIELD_TO_MATURITY1" value="${pd.YIELD_TO_MATURITY1}" maxlength="32" title="到期收益率1" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">修正久期1:</td>
								<td><input type="number" autocomplete="off" name="MODIFIED_DURATION1" id="MODIFIED_DURATION1" value="${pd.MODIFIED_DURATION1}" maxlength="32" title="修正久期1" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">凸性1:</td>
								<td><input type="number" autocomplete="off" name="CONVEXITY1" id="CONVEXITY1" value="${pd.CONVEXITY1}" maxlength="32" title="凸性1" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">全价2:</td>
								<td><input type="number" autocomplete="off" name="CALCULATION_PRICE2" id="CALCULATION_PRICE2" value="${pd.CALCULATION_PRICE2}" maxlength="32" title="全价2" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">净价2:</td>
								<td><input type="number" autocomplete="off" name="CLEAN_PRICE2" id="CLEAN_PRICE2" value="${pd.CLEAN_PRICE2}" maxlength="32" title="净价2" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">到期收益率2:</td>
								<td><input type="number" autocomplete="off" name="YIELD_TO_MATURITY2" id="YIELD_TO_MATURITY2" value="${pd.YIELD_TO_MATURITY2}" maxlength="32" title="到期收益率2" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">修正久期2:</td>
								<td><input type="number" autocomplete="off" name="MODIFIED_DURATION2" id="MODIFIED_DURATION2" value="${pd.MODIFIED_DURATION2}" maxlength="32" title="修正久期2" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">凸性2:</td>
								<td><input type="number" autocomplete="off" name="CONVEXITY2" id="CONVEXITY2" value="${pd.CONVEXITY2}" maxlength="32" title="凸性2" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">推荐:</td>
								<td><input type="text" autocomplete="off" name="RECOMMENDATION " id="RECOMMENDATION " value="${pd.RECOMMENDATION }" maxlength="32" title="推荐" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">应收利息:</td>
								<td><input type="number" autocomplete="off" name="ACCRUED_INTEREST" id="ACCRUED_INTEREST" value="${pd.ACCRUED_INTEREST}" maxlength="32" title="应收利息" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">预计利息:</td>
								<td><input type="number" autocomplete="off" name="ESTIMATED_COUPON" id="ESTIMATED_COUPON" value="${pd.ESTIMATED_COUPON}" maxlength="32" title="预计利息" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">保留:</td>
								<td><input type="number" autocomplete="off" name="RESERVE" id="RESERVE" value="${pd.RESERVE}" maxlength="32" title="保留" style="width:98%;"/></td>
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