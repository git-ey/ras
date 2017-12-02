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
					
					<form action="fundrelatedparty/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUNDRELATEDPARTY_ID" id="FUNDRELATEDPARTY_ID" value="${pd.FUNDRELATEDPARTY_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="100" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">关联方名称:</td>
								<td><input type="text" name="PARTY_FULL_NAME" id="PARTY_FULL_NAME" value="${pd.PARTY_FULL_NAME}" maxlength="240" placeholder="这里输入关联方名称" title="关联方名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">关联方简称1:</td>
								<td><input type="text" name="PARTY_SHORT_NAME_1" id="PARTY_SHORT_NAME_1" value="${pd.PARTY_SHORT_NAME_1}" maxlength="120" placeholder="这里输入关联方简称1" title="关联方简称1" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">关联方简称2:</td>
								<td><input type="text" name="PARTY_SHORT_NAME_2" id="PARTY_SHORT_NAME_2" value="${pd.PARTY_SHORT_NAME_2}" maxlength="120" placeholder="这里输入关联方简称2" title="关联方简称2" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">关联方简称3:</td>
								<td><input type="text" name="PARTY_SHORT_NAME_3" id="PARTY_SHORT_NAME_3" value="${pd.PARTY_SHORT_NAME_3}" maxlength="120" placeholder="这里输入关联方简称3" title="关联方简称3" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">与本基金的关系:</td>
								<td><input type="text" name="RELATIONSHIP" id="RELATIONSHIP" value="${pd.RELATIONSHIP}" maxlength="240" placeholder="这里输入备注7" title="备注7" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" name="STOCK_CODE" id="STOCK_CODE" value="${pd.STOCK_CODE}" maxlength="240" placeholder="这里输入股票代码" title="股票代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="1200" placeholder="这里输入债券代码" title="债券代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_CODE" id="FUND_CODE" value="${pd.FUND_CODE}" maxlength="240" placeholder="这里输入基金代码" title="基金代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">关联方ID:</td>
								<td><input type="text" name="PARTY_ID" id="PARTY_ID" value="${pd.PARTY_ID}" maxlength="255" title="关联方ID" style="width:98%;"/></td>
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
			if($("#PARTY_FULL_NAME").val()==""){
				$("#PARTY_FULL_NAME").tips({
					side:3,
		            msg:'请输入关联方名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARTY_FULL_NAME").focus();
			return false;
			}
			if($("#PARTY_SHORT_NAME_1").val()==""){
				$("#PARTY_SHORT_NAME_1").tips({
					side:3,
		            msg:'请输入关联方简称1',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARTY_SHORT_NAME_1").focus();
			return false;
			}
			if($("#PARTY_SHORT_NAME_2").val()==""){
				$("#PARTY_SHORT_NAME_2").tips({
					side:3,
		            msg:'请输入关联方简称2',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARTY_SHORT_NAME_2").focus();
			return false;
			}
			if($("#PARTY_SHORT_NAME_3").val()==""){
				$("#PARTY_SHORT_NAME_3").tips({
					side:3,
		            msg:'请输入关联方简称3',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARTY_SHORT_NAME_3").focus();
			return false;
			}
			if($("#RELATIONSHIP").val()==""){
				$("#RELATIONSHIP").tips({
					side:3,
		            msg:'请输入备注7',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RELATIONSHIP").focus();
			return false;
			}
			if($("#STOCK_CODE").val()==""){
				$("#STOCK_CODE").tips({
					side:3,
		            msg:'请输入股票代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_CODE").focus();
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
			if($("#FUND_CODE").val()==""){
				$("#FUND_CODE").tips({
					side:3,
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_CODE").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入备注11',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
			return false;
			}
			if($("#STATUS").val()==""){
				$("#STATUS").tips({
					side:3,
		            msg:'请输入与本基金的关系',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STATUS").focus();
			return false;
			}
			if($("#PARTY_ID").val()==""){
				$("#PARTY_ID").tips({
					side:3,
		            msg:'请输入关联方ID',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARTY_ID").focus();
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