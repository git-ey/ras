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
					
					<form action="lomd/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="LOMD_ID" id="LOMD_ID" value="${pd.LOMD_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="30" title="公司代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" autocomplete="off" name="TRX_CODE" id="TRX_CODE" value="${pd.TRX_CODE}" maxlength="30" title="股票代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成功认购日:</td>
								<td><input class="span10 date-picker" name="START_DATE" id="START_DATE" value="${pd.START_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="成功认购日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">可流通日:</td>
								<td><input class="span10 date-picker" name="LIFTING_DATE" id="LIFTING_DATE" value="${pd.LIFTING_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="可流通日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">来源:</td>
								<td><input type="text" autocomplete="off" name="LOMD_SOURCE" id="LOMD_SOURCE" value="${pd.LOMD_SOURCE}" maxlength="32" title="来源" style="width:49%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">折扣率数字:</td>
								<td><input type="number" autocomplete="off" name="LOMD" id="LOMD" value="${pd.LOMD}" maxlength="32" title="折扣率数字" style="width:49%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
									<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
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
			if($("#TRX_CODE").val()==""){
				$("#TRX_CODE").tips({
					side:3,
		            msg:'请输入股票代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_CODE").focus();
			return false;
			}
			if($("#START_DATE").val()==""){
				$("#START_DATE").tips({
					side:3,
		            msg:'请输入成功认购日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#START_DATE").focus();
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
			if($("#LOMD_SOURCE").val()==""){
				$("#LOMD_SOURCE").tips({
					side:3,
		            msg:'请输入来源',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LOMD_SOURCE").focus();
			return false;
			}
			if($("#LOMD").val()==""){
				$("#LOMD").tips({
					side:3,
		            msg:'请输入折扣率数字',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LOMD").focus();
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