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
					
					<form action="fund/trxrule/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUNDTRXRULE_ID" id="FUNDTRXRULE_ID" value="${pd.FUNDTRXRULE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" readonly value="${pd.FUND_ID}" maxlength="100" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">期间:</td>
								<td>
								    <select class="chosen-select form-control" name="PERIOD" id="PERIOD" data-placeholder="请选择期间" style="vertical-align:top;width: 120px;">
									    <c:forEach items="${periodList}" var="var" varStatus="vs">
									        <option value="${var.PERIOD}" <c:if test="${pd.PERIOD == var.PERIOD}">selected</c:if>>${var.PERIOD_NAME}</option>
									    </c:forEach>
								  	</select>
							    </td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">类型:</td>
								<td>
								    <select class="chosen-select form-control" name="TYPE" id="TYPE" data-placeholder="请选择类型" style="vertical-align:top;width: 120px;">
									    <option value="确认日" <c:if test="${pd.TYPE == '确认日'}">selected</c:if>>确认日</option>
								        <option value="划款日" <c:if test="${pd.TYPE == '划款日'}">selected</c:if>>划款日</option>
								  	</select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">申购款（代销）:</td>
								<td><input type="number" name="ATTR1" id="ATTR1" value="${pd.ATTR1}" maxlength="32" placeholder="代表T+N的N" title="申购款（代销）" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">申购款（直销）:</td>
								<td><input type="number" name="ATTR2" id="ATTR2" value="${pd.ATTR2}" maxlength="32" placeholder="代表T+N的N" title="申购款（直销）" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">申购款:</td>
								<td><input type="number" name="ATTR3" id="ATTR3" value="${pd.ATTR3}" maxlength="32" placeholder="代表T+N的N" title="申购款" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">转入款:</td>
								<td><input type="number" name="ATTR4" id="ATTR4" value="${pd.ATTR4}" maxlength="32" placeholder="代表T+N的N" title="转入款" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">赎回款:</td>
								<td><input type="number" name="ATTR5" id="ATTR5" value="${pd.ATTR5}" maxlength="32" placeholder="代表T+N的N" title="赎回款" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">转出款:</td>
								<td><input type="number" name="ATTR6" id="ATTR6" value="${pd.ATTR6}" maxlength="32" placeholder="代表T+N的N" title="转出款" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">赎回费:</td>
								<td><input type="number" name="ATTR7" id="ATTR7" value="${pd.ATTR7}" maxlength="32" placeholder="代表T+N的N" title="赎回费" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">转换费:</td>
								<td><input type="number" name="ATTR8" id="ATTR8" value="${pd.ATTR8}" maxlength="32" placeholder="代表T+N的N" title="转换费" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">后端申购费:</td>
								<td><input type="number" name="ATTR9" id="ATTR9" value="${pd.ATTR9}" maxlength="32" placeholder="代表T+N的N" title="后端申购费" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">状态:</td>
								<td><input type="text" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="30" placeholder="这里输入状态" title="状态" style="width:98%;"/></td>
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
			if($("#TYPE").val()==""){
				$("#TYPE").tips({
					side:3,
		            msg:'请输入类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TYPE").focus();
			return false;
			}
			if($("#ATTR4").val()==""){
				$("#ATTR4").tips({
					side:3,
		            msg:'请输入转入款',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR4").focus();
			return false;
			}
			if($("#ATTR5").val()==""){
				$("#ATTR5").tips({
					side:3,
		            msg:'请输入赎回款',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR5").focus();
			return false;
			}
			if($("#ATTR6").val()==""){
				$("#ATTR6").tips({
					side:3,
		            msg:'请输入转出款',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR6").focus();
			return false;
			}
			if($("#ATTR7").val()==""){
				$("#ATTR7").tips({
					side:3,
		            msg:'请输入赎回费',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR7").focus();
			return false;
			}
			if($("#ATTR8").val()==""){
				$("#ATTR8").tips({
					side:3,
		            msg:'请输入转换费',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR8").focus();
			return false;
			}
			if($("#ATTR9").val()==""){
				$("#ATTR9").tips({
					side:3,
		            msg:'请输入后端申购费',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR9").focus();
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