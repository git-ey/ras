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
					
					<form action="reporttemplate/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="REPORTTEMPLATE_ID" id="REPORTTEMPLATE_ID" value="${pd.REPORTTEMPLATE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">报告章节:</td>
								<td><input type="text" name="CHAPTER" id="CHAPTER" value="${pd.CHAPTER}" maxlength="255" placeholder="这里输入报告章节" title="报告章节" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">报告标题:</td>
								<td><input type="text" name="TITLE" id="TITLE" value="${pd.TITLE}" maxlength="255" placeholder="这里输入报告标题" title="报告标题" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">组件数量:</td>
								<td><input type="number" name="ITEM_QUANTITY" id="ITEM_QUANTITY" value="${pd.ITEM_QUANTITY}" maxlength="32" placeholder="这里输入组件数量" title="组件数量" style="width:49%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">组件序号:</td>
								<td><input type="text" name="ITEM_SEQ" id="ITEM_SEQ" value="${pd.ITEM_SEQ}" maxlength="30" placeholder="这里输入组件序号" title="组件序号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">组件名称:</td>
								<td><input type="text" name="ITEM_NAME" id="ITEM_NAME" value="${pd.ITEM_NAME}" maxlength="240" placeholder="这里输入组件名称" title="组件名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">子序号:</td>
								<td><input type="text" name="SUB_SEQ" id="SUB_SEQ" value="${pd.SUB_SEQ}" maxlength="30" placeholder="这里输入子序号" title="子序号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">组件类型:</td>
								<td>
								    <select class="chosen-select form-control" name="ITEM_TYPE" id="ITEM_TYPE" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="TB" <c:if test="${pd.ITEM_TYPE == 'TB'}">selected</c:if>>Table</option>
								        <option value="TX" <c:if test="${pd.ITEM_TYPE == 'TX'}">selected</c:if>>Text</option>
								        <option value="NT" <c:if test="${pd.ITEM_TYPE == 'NT'}">selected</c:if>>Note</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">组件代码:</td>
								<td><input type="text" name="ITEM_CODE" id="ITEM_CODE" value="${pd.ITEM_CODE}" maxlength="60" placeholder="这里输入组件代码" title="组件代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td>
								    <select class="chosen-select form-control" name="STATUS" id="STATUS" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="N" <c:if test="${pd.STATUS == 'N'}">selected</c:if>>New</option>
								        <option value="P" <c:if test="${pd.STATUS == 'P'}">selected</c:if>>Pending</option>
								        <option value="I" <c:if test="${pd.STATUS == 'I'}">selected</c:if>>In Process</option>
								        <option value="C" <c:if test="${pd.STATUS == 'C'}">selected</c:if>>Confirmed</option>
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
			if($("#CHAPTER").val()==""){
				$("#CHAPTER").tips({
					side:3,
		            msg:'请输入报告章节',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CHAPTER").focus();
			return false;
			}
			if($("#TITLE").val()==""){
				$("#TITLE").tips({
					side:3,
		            msg:'请输入报告标题',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TITLE").focus();
			return false;
			}
			if($("#ITEM_QUANTITY").val()==""){
				$("#ITEM_QUANTITY").tips({
					side:3,
		            msg:'请输入组件数量',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM_QUANTITY").focus();
			return false;
			}
			if($("#ITEM_SEQ").val()==""){
				$("#ITEM_SEQ").tips({
					side:3,
		            msg:'请输入组件序号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM_SEQ").focus();
			return false;
			}
			if($("#ITEM_NAME").val()==""){
				$("#ITEM_NAME").tips({
					side:3,
		            msg:'请输入组件名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM_NAME").focus();
			return false;
			}
			if($("#SUB_SEQ").val()==""){
				$("#SUB_SEQ").tips({
					side:3,
		            msg:'请输入子序号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUB_SEQ").focus();
			return false;
			}
			if($("#ITEM_TYPE").val()==""){
				$("#ITEM_TYPE").tips({
					side:3,
		            msg:'请输入组件类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM_TYPE").focus();
			return false;
			}
			if($("#ITEM_CODE").val()==""){
				$("#ITEM_CODE").tips({
					side:3,
		            msg:'请输入组件代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ITEM_CODE").focus();
			return false;
			}
			if($("#STATUS").val()==""){
				$("#STATUS").tips({
					side:3,
		            msg:'请输入状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STATUS").focus();
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