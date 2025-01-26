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
					
					<form action="importconfig/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="IMPORTCONFIG_ID" id="IMPORTCONFIG_ID" value="${pd.IMPORTCONFIG_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">模版代码:</td>
								<td><input type="text" autocomplete="off" name="IMPORT_TEMP_CODE" id="IMPORT_TEMP_CODE" value="${pd.IMPORT_TEMP_CODE}" maxlength="60" placeholder="这里输入导入模版代码" title="导入模版代码" style="width:98%;"/></td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">模板名称:</td>
								<td><input type="text" autocomplete="off" name="IMPORT_TEMP_NAME" id="IMPORT_TEMP_NAME" value="${pd.IMPORT_TEMP_NAME}" maxlength="240" placeholder="这里输入导入模板名称" title="导入模板名称" style="width:98%;"/></td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">模板类型:</td>
								<td>
								    <select class="chosen-select form-control" name="TEMP_TYPE" id="TEMP_TYPE" data-placeholder="请选择" style="width:49%;">
								    <option value="HS" <c:if test="${pd.TEMP_TYPE == 'HS'}">selected</c:if>>恒生</option>
								    <option value="JSZ" <c:if test="${pd.TEMP_TYPE == 'JSZ'}">selected</c:if>>金手指</option>
								    <option value="SG" <c:if test="${pd.TEMP_TYPE == 'SG'}">selected</c:if>>手工</option>
								    <option value="JM" <c:if test="${pd.TEMP_TYPE == 'JM'}">selected</c:if>>界面</option>
								    </select>
								</td>
							</tr>
							<tr>
							<td style="width:100px;text-align: right;padding-top: 13px;">读取Sheet页:</td>
								<td><input type="number" autocomplete="off" name="SHEET_NO" id="SHEET_NO" value="${pd.SHEET_NO == null ? 0 : pd.SHEET_NO}" maxlength="32" placeholder="起始页为0" style="width:49%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">读取起始行:</td>
								<td><input type="number" autocomplete="off" name="START_ROW_NO" id="START_ROW_NO" value="${pd.START_ROW_NO}" maxlength="32" placeholder="这里输入读取的起始行" title="读取的起始行" style="width:49%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">文件类型:</td>
								<td>
								    <select class="chosen-select form-control" name="IMPORT_FILE_TYPE" id="IMPORT_FILE_TYPE" data-placeholder="请选择" style="width:49%;">
								    <option value="EXCEL" <c:if test="${pd.IMPORT_FILE_TYPE == 'EXCEL'}">selected</c:if>>EXCEL</option>
								    <option value="CSV" <c:if test="${pd.IMPORT_FILE_TYPE == 'CSV'}">selected</c:if>>CSV</option>
								    </select>
								</td>
						   </tr>
							<tr>
							    <td style="width:100px;text-align: right;padding-top: 13px;">导入目标表:</td>
								<td><input type="text" autocomplete="off" name="TABLE_NAME" id="TABLE_NAME" value="${pd.TABLE_NAME}" maxlength="60" placeholder="这里输入导入目标表" title="导入目标表" style="width:98%;"/></td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">文件名格式:</td>
							    <td><input type="text" autocomplete="off" name="FILENAME_FROMAT" id="FILENAME_FROMAT" value="${pd.FILENAME_FROMAT}" maxlength="60" placeholder="这里输入文件名格式" title="文件名格式" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">行过滤规则:</td>
								<td><input type="text" autocomplete="off" name="IGNORE_RULE" id="IGNORE_RULE" value="${pd.IGNORE_RULE}" maxlength="60" placeholder="列号1:^$,列号2:[\u4e00-\u9fa5]" title="行过滤规则" style="width:98%;"/></td>
							</tr>
							<tr>
							    <td style="width:100px;text-align: right;padding-top: 13px;">名称解析段:</td>
								<td><input type="text" autocomplete="off" name="NAME_SECTION" id="NAME_SECTION" value="${pd.NAME_SECTION}" maxlength="240" placeholder="段1,段2" title="文件名解析段" style="width:98%;"/></td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ENABLED_FLAG" id="ENABLED_FLAG" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ENABLED_FLAG == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ENABLED_FLAG == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							    <td style="width:100px;text-align: right;padding-top: 13px;">执行存储过程:</td>
							    <td><input type="text" autocomplete="off" name="CALLABLE" id="CALLABLE" value="${pd.CALLABLE}" maxlength="240" placeholder="MySQL存储过程" title="存储过程" style="width:98%;"/></td>
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
<footer>
<div style="width: 100%;padding-bottom: 2px;" class="center">
	<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
	<c:if test="${'edit' == msg }">
	    <a class="btn btn-mini btn-success" onclick="sqlView('${pd.IMPORTCONFIG_ID}');">查看表脚本</a>
	</c:if>
	<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
</div>
</footer>
<c:if test="${'edit' == msg }">
	<div>
		<iframe name="treeFrame" id="treeFrame" frameborder="0" src="<%=basePath%>/importconfigcell/list.do?IMPORTCONFIG_ID=${pd.IMPORTCONFIG_ID}" style="margin:0 auto;width:1005px;height:368px;;"></iframe>
	</div>
</c:if>
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
			if($("#IMPORT_TEMP_CODE").val()==""){
				$("#IMPORT_TEMP_CODE").tips({
					side:3,
		            msg:'请输入导入模版代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IMPORT_TEMP_CODE").focus();
			return false;
			}
			if($("#IMPORT_TEMP_NAME").val()==""){
				$("#IMPORT_TEMP_NAME").tips({
					side:3,
		            msg:'请输入导入模板名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IMPORT_TEMP_NAME").focus();
			return false;
			}
			if($("#START_ROW_NO").val()==""){
				$("#START_ROW_NO").tips({
					side:3,
		            msg:'请输入读取的起始行',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#START_ROW_NO").focus();
			return false;
			}
			if($("#IMPORT_FILE_TYPE").val()==""){
				$("#IMPORT_FILE_TYPE").tips({
					side:3,
		            msg:'请输入导入文件类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IMPORT_FILE_TYPE").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		//修改
		function sqlView(Id){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="表创建SQL";
			 diag.URL = '<%=path%>/importconfig/goSqlView.do?IMPORTCONFIG_ID='+Id;
			 diag.Width = 500;
			 diag.Height = 300;
			 diag.Modal = true;				//有无遮罩窗口
			 diag.CancelEvent = function(){ //关闭事件
				 if(diag.innerFrame.contentWindow.document.getElementById('zhongxin').style.display == 'none'){
					 tosearch();
				}
				diag.close();
			 };
			 diag.show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
		});
		</script>
</body>
</html>