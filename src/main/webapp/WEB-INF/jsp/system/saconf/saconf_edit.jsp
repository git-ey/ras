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
					
					<form action="saconf/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="SACONF_ID" id="SACONF_ID" value="${pd.SACONF_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">排序:</td>
								<td><input type="number" name="SEQ" id="SEQ" value="${pd.SEQ}" maxlength="32" placeholder="这里输入排序" title="排序" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">科目类型:</td>
								<td><input type="text" name="ACCOUNT_TYPE" id="ACCOUNT_TYPE" value="${pd.ACCOUNT_TYPE}" maxlength="30" placeholder="这里输入科目类型" title="科目类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">表项:</td>
								<td><input type="text" name="AFS_CODE" id="AFS_CODE" value="${pd.AFS_CODE}" maxlength="30" placeholder="这里输入表项" title="表项" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">表项名称:</td>
								<td><input type="text" name="ITEM" id="ITEM" value="${pd.ITEM}" maxlength="60" placeholder="这里输入表项名称" title="表项名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">交易量是否大:</td>
								<td><input type="text" name="VOLUME" id="VOLUME" value="${pd.VOLUME}" maxlength="10" placeholder="这里输入交易量是否大" title="交易量是否大" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">交易复杂程度是否为高:</td>
								<td><input type="text" name="COMPLEXITY" id="COMPLEXITY" value="${pd.COMPLEXITY}" maxlength="10" placeholder="这里输入交易复杂程度是否为高" title="交易复杂程度是否为高" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否包括重要判断:</td>
								<td><input type="text" name="JUDGEMENT" id="JUDGEMENT" value="${pd.JUDGEMENT}" maxlength="10" placeholder="这里输入是否包括重要判断" title="是否包括重要判断" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否存在重大风险:</td>
								<td><input type="text" name="RISK" id="RISK" value="${pd.RISK}" maxlength="10" placeholder="这里输入是否存在重大风险" title="是否存在重大风险" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">是否为投资者重大关注科目:</td>
								<td><input type="text" name="CONCERN" id="CONCERN" value="${pd.CONCERN}" maxlength="10" placeholder="这里输入是否为投资者重大关注科目" title="是否为投资者重大关注科目" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">启用:</td>
								<td><input type="text" name="ACTIVE" id="ACTIVE" value="${pd.ACTIVE}" maxlength="10" placeholder="这里输入启用" title="启用" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">说明:</td>
								<td><input type="text" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="255" placeholder="这里输入说明" title="说明" style="width:98%;"/></td>
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
		            msg:'请输入排序',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEQ").focus();
			return false;
			}
			if($("#ACCOUNT_TYPE").val()==""){
				$("#ACCOUNT_TYPE").tips({
					side:3,
		            msg:'请输入科目类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACCOUNT_TYPE").focus();
			return false;
			}
			if($("#AFS_CODE").val()==""){
				$("#AFS_CODE").tips({
					side:3,
		            msg:'请输入表项',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#AFS_CODE").focus();
			return false;
			}
			if($("#ITEM").val()==""){
				$("#ITEM").tips({
					side:3,
		            msg:'请输入表项名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM").focus();
			return false;
			}
			if($("#VOLUME").val()==""){
				$("#VOLUME").tips({
					side:3,
		            msg:'请输入交易量是否大',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VOLUME").focus();
			return false;
			}
			if($("#COMPLEXITY").val()==""){
				$("#COMPLEXITY").tips({
					side:3,
		            msg:'请输入交易复杂程度是否为高',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#COMPLEXITY").focus();
			return false;
			}
			if($("#JUDGEMENT").val()==""){
				$("#JUDGEMENT").tips({
					side:3,
		            msg:'请输入是否包括重要判断',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#JUDGEMENT").focus();
			return false;
			}
			if($("#RISK").val()==""){
				$("#RISK").tips({
					side:3,
		            msg:'请输入是否存在重大风险',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RISK").focus();
			return false;
			}
			if($("#CONCERN").val()==""){
				$("#CONCERN").tips({
					side:3,
		            msg:'请输入是否为投资者重大关注科目',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONCERN").focus();
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
			if($("#DESCRIPTION").val()==""){
				$("#DESCRIPTION").tips({
					side:3,
		            msg:'请输入说明',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DESCRIPTION").focus();
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