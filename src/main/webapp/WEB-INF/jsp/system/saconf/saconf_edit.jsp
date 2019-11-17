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
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="SACONF_ID" id="SACONF_ID" value="${pd.SACONF_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">排序:</td>
								<td><input type="number" autocomplete="off" name="SEQ" id="SEQ" value="${pd.SEQ}" maxlength="32" placeholder="这里输入排序" title="排序" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">科目类型:</td>
								<td>
									<select class="chosen-select form-control" name="ACCOUNT_TYPE" id="ACCOUNT_TYPE" data-placeholder="请选择" style="width:49%;">
								        <option value="BS" <c:if test="${pd.ACCOUNT_TYPE == 'BS'}">selected</c:if>>BS</option>
								        <option value="PL" <c:if test="${pd.ACCOUNT_TYPE == 'PL'}">selected</c:if>>PL</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">表项:</td>
								<td><input type="text" autocomplete="off" name="AFS_CODE" id="AFS_CODE" value="${pd.AFS_CODE}" maxlength="30" placeholder="这里输入表项" title="表项" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">表项名称:</td>
								<td><input type="text" autocomplete="off" name="ITEM" id="ITEM" value="${pd.ITEM}" maxlength="60" placeholder="这里输入表项名称" title="表项名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">交易量是否大:</td>
								<td>
									<select class="chosen-select form-control" name="VOLUME" id="VOLUME" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.VOLUME == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.VOLUME == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">交易复杂程度是否为高:</td>
								<td>
									<select class="chosen-select form-control" name="COMPLEXITY" id="COMPLEXITY" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.COMPLEXITY == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.COMPLEXITY == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">是否包括重要判断:</td>
								<td>
									<select class="chosen-select form-control" name="JUDGEMENT" id="JUDGEMENT" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.JUDGEMENT == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.JUDGEMENT == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">是否存在重大风险:</td>
								<td>
									<select class="chosen-select form-control" name="RISK" id="RISK" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.RISK == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.RISK == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">是否为投资者重大关注科目:</td>
								<td>
									<select class="chosen-select form-control" name="CONCERN" id="CONCERN" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.CONCERN == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.CONCERN == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
									<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">说明:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="255" placeholder="这里输入说明" title="说明" style="width:98%;"/></td>
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