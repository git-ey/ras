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
					
					<form action="valueationindex/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="VALUEATIONINDEX_ID" id="VALUEATIONINDEX_ID" value="${pd.VALUEATIONINDEX_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="10" title="公司代码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否货基:</td>
								<td><input type="text" autocomplete="off" name="MMF" id="MMF" value="${pd.MMF}" maxlength="10" title="是否货基" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">投资标的:</td>
								<td><input type="text" autocomplete="off" name="TYPE" id="TYPE" value="${pd.TYPE}" maxlength="255" title="投资标的" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">交易市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="255" title="交易市场" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">子类型:</td>
								<td><input type="text" autocomplete="off" name="SUB_TYPE" id="SUB_TYPE" value="${pd.SUB_TYPE}" maxlength="255" title="子类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末交易状态:</td>
								<td><input type="text" autocomplete="off" name="TRX_STATUS" id="TRX_STATUS" value="${pd.TRX_STATUS}" maxlength="30" title="期末交易状态" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">计息方式:</td>
								<td><input type="text" autocomplete="off" name="INTEREST_MODE" id="INTEREST_MODE" value="${pd.INTEREST_MODE}" maxlength="255" title="计息方式" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">索引估值类型代码:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE_INDEX" id="VAL_TYPE_INDEX" value="${pd.VAL_TYPE_INDEX}" maxlength="60" title="索引估值类型代码" style="width:98%;"/></td>
								
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值类型代码:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE_CODE" id="VAL_TYPE_CODE" value="${pd.VAL_TYPE_CODE}" maxlength="255" title="估值类型代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值类型:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE_DEXS" id="VAL_TYPE_DEXS" value="${pd.VAL_TYPE_DEXS}" maxlength="255" title="估值类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值基价来源:</td>
								<td><input type="text" autocomplete="off" name="VAL_BASE_SOURCE" id="VAL_BASE_SOURCE" value="${pd.VAL_BASE_SOURCE}" maxlength="255" title="估值基价来源" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值基价字段:</td>
								<td><input type="text" autocomplete="off" name="VAL_COLUMN" id="VAL_COLUMN" value="${pd.VAL_COLUMN}" maxlength="255" title="估值基价字段" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">三层次类型:</td>
								<td><input type="text" autocomplete="off" name="THREE_LEVEL" id="THREE_LEVEL" value="${pd.THREE_LEVEL}" maxlength="255" title="三层次类型" style="width:98%;"/></td>
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
			if($("#TYPE").val()==""){
				$("#TYPE").tips({
					side:3,
		            msg:'请输入投资标的',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TYPE").focus();
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
			if($("#INTEREST_MODE").val()==""){
				$("#INTEREST_MODE").tips({
					side:3,
		            msg:'请输入计息方式',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_MODE").focus();
			return false;
			}
			if($("#VAL_TYPE_CODE").val()==""){
				$("#VAL_TYPE_CODE").tips({
					side:3,
		            msg:'请输入估值类型代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_TYPE_CODE").focus();
			return false;
			}
			if($("#VAL_TYPE_DEXS").val()==""){
				$("#VAL_TYPE_DEXS").tips({
					side:3,
		            msg:'请输入估值类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_TYPE_DEXS").focus();
			return false;
			}
			if($("#VAL_BASE_SOURCE").val()==""){
				$("#VAL_BASE_SOURCE").tips({
					side:3,
		            msg:'请输入估值基价来源',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_BASE_SOURCE").focus();
			return false;
			}
			if($("#VAL_COLUMN").val()==""){
				$("#VAL_COLUMN").tips({
					side:3,
		            msg:'请输入估值基价字段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_COLUMN").focus();
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