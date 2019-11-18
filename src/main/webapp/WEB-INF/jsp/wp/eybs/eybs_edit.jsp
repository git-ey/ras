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
					
					<form action="eybs/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="BS_ID" id="BS_ID" value="${pd.BS_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">表项:</td>
								<td><input type="text" autocomplete="off" name="BSCODE" id="BSCODE" value="${pd.BSCODE}" maxlength="50" placeholder="这里输入表项" title="表项" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">说明:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPSION" id="DESCRIPSION" value="${pd.DESCRIPSION}" maxlength="255" placeholder="这里输入说明" title="说明" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">期初金额:</td>
								<td><input type="number" autocomplete="off" name="BEG_BALANCE" id="BEG_BALANCE" value="${pd.BEG_BALANCE}" maxlength="32" placeholder="这里输入期初金额" title="期初金额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末余额:</td>
								<td><input type="number" autocomplete="off" name="END_BALANCE" id="END_BALANCE" value="${pd.END_BALANCE}" maxlength="32" placeholder="这里输入期末余额" title="期末余额" style="width:98%;"/></td>
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
			if($("#BS_DATE").val()==""){
				$("#BS_DATE").tips({
					side:3,
		            msg:'请输入日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BS_DATE").focus();
			return false;
			}
			if($("#BSCODE").val()==""){
				$("#BSCODE").tips({
					side:3,
		            msg:'请输入表项',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BSCODE").focus();
			return false;
			}
			if($("#DESCRIPSION").val()==""){
				$("#DESCRIPSION").tips({
					side:3,
		            msg:'请输入说明',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DESCRIPSION").focus();
			return false;
			}
			if($("#BEG_BALANCE").val()==""){
				$("#BEG_BALANCE").tips({
					side:3,
		            msg:'请输入期初金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BEG_BALANCE").focus();
			return false;
			}
			if($("#END_BALANCE").val()==""){
				$("#END_BALANCE").tips({
					side:3,
		            msg:'请输入期末余额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#END_BALANCE").focus();
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