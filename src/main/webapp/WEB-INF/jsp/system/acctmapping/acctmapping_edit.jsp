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
					
					<form action="acctmapping/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="ACCTMAPPING_ID" id="ACCTMAPPING_ID" value="${pd.ACCTMAPPING_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="255" placeholder="唯一性标识,如F600001#01" title="基金ID" style="width:98%;"/></td>
							    <td style="width:80px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="255" placeholder="这里输入科目代码" title="科目代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">科目名称:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_DESCRIPTION" id="ACCOUNT_DESCRIPTION" value="${pd.ACCOUNT_DESCRIPTION}" maxlength="255" placeholder="这里输入科目名称" title="科目名称" style="width:98%;"/></td>
								<td style="width:80px;text-align: right;padding-top: 13px;">几级科目:</td>
								<td><input type="number" autocomplete="off" name="LEVEL" id="LEVEL" value="${pd.LEVEL}" maxlength="32" placeholder="这里输入几级科目" title="几级科目" style="width:98%;"/></td>							
							</tr>
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="255" placeholder="默认CNY，外币科目采用外币代码" title="币种" style="width:98%;"/></td>
								<td style="width:80px;text-align: right;padding-top: 13px;">科目属性:</td>
								<td><input type="text" autocomplete="off" name="TYPE" id="TYPE" value="${pd.TYPE}" maxlength="255" placeholder="标识科目属性" title="科目属性" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">记账科目:</td>
								<td>
									<select class="chosen-select form-control" name="ENTERABLE" id="ENTERABLE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ENTERABLE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ENTERABLE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:80px;text-align: right;padding-top: 13px;">EY科目ID:</td>
								<td><input type="text" autocomplete="off" name="EY_ACCOUNT_NUM" id="EY_ACCOUNT_NUM" value="${pd.EY_ACCOUNT_NUM}" maxlength="255" placeholder="EY标准一级科目代码" title="EY科目ID" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">属性1:</td>
								<td><input type="text" autocomplete="off" name="ATTR1" id="ATTR1" value="${pd.ATTR1}" maxlength="255" placeholder="这里输入属性1" title="属性1" style="width:98%;"/></td>
								<td style="width:80px;text-align: right;padding-top: 13px;">属性2:</td>
								<td><input type="text" autocomplete="off" name="ATTR2" id="ATTR2" value="${pd.ATTR2}" maxlength="255" placeholder="这里输入属性2" title="属性2" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:80px;text-align: right;padding-top: 13px;">属性3:</td>
								<td><input type="text" autocomplete="off" name="ATTR3" id="ATTR3" value="${pd.ATTR3}" maxlength="255" placeholder="这里输入属性3" title="属性3" style="width:98%;"/></td>
								<td style="width:80px;text-align: right;padding-top: 13px;">属性4:</td>
								<td><input type="text" autocomplete="off" name="ATTR4" id="ATTR4" value="${pd.ATTR4}" maxlength="255" placeholder="这里输入属性4" title="属性4" style="width:98%;"/></td>
							</tr>
							<tr>
							    <td style="width:80px;text-align: right;padding-top: 13px;">属性5:</td>
								<td><input type="text" autocomplete="off" name="ATTR5" id="ATTR5" value="${pd.ATTR5}" maxlength="255" placeholder="这里输入属性5" title="属性5" style="width:98%;"/></td>
							    <td style="width:80px;text-align: right;padding-top: 13px;">属性6:</td>
								<td><input type="text" autocomplete="off" name="ATTR6" id="ATTR6" value="${pd.ATTR6}" maxlength="255" placeholder="这里输入属性6" title="属性6" style="width:98%;"/></td>
							</tr>
							<tr>
							    <td style="width:80px;text-align: right;padding-top: 13px;">是否启用:</td>
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
			if($("#ACCOUNT_DESCRIPTION").val()==""){
				$("#ACCOUNT_DESCRIPTION").tips({
					side:3,
		            msg:'请输入科目名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACCOUNT_DESCRIPTION").focus();
			return false;
			}
			if($("#LEVEL").val()==""){
				$("#LEVEL").tips({
					side:3,
		            msg:'请输入几级科目',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LEVEL").focus();
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
			if($("#TYPE").val()==""){
				$("#TYPE").tips({
					side:3,
		            msg:'请输入科目属性',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TYPE").focus();
			return false;
			}
			if($("#ENTERABLE").val()==""){
				$("#ENTERABLE").tips({
					side:3,
		            msg:'请输入记账科目',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTERABLE").focus();
			return false;
			}
			if($("#EY_ACCOUNT_NUM").val()==""){
				$("#EY_ACCOUNT_NUM").tips({
					side:3,
		            msg:'请输入EY科目ID',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#EY_ACCOUNT_NUM").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入是否启用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
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