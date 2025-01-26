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
					
					<form action="importconfigcell/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="IMPORTCONFIGCELL_ID" id="IMPORTCONFIGCELL_ID" value="${pd.IMPORTCONFIGCELL_ID}"/>
						<input type="hidden" name="IMPORTCONFIG_ID" id="IMPORTCONFIG_ID" value="${pd.IMPORTCONFIG_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">对应列号:</td>
								<td><input type="number" autocomplete="off" name="NUMBER" id="NUMBER" value="${pd.NUMBER}" maxlength="32" placeholder="这里输入对应的序列" title="对应的序列" style="width:60%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">存储KEY:</td>
								<td><input type="text" autocomplete="off" name="MAPKEY" id="MAPKEY" value="${pd.MAPKEY}" maxlength="60" placeholder="这里输入存储的KEY" title="存储的KEY" style="width:60%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">列说明:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="32" placeholder="这里输入列说明" title="列说明" style="width:60%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">单元格类型:</td>
								<td>
								    <select class="chosen-select form-control" name="CELLTYPE" id="id" data-placeholder="请选择" style="width:60%;">
								    <option value="Int" <c:if test="${pd.CELLTYPE == 'Int'}">selected</c:if>>Int</option>
								    <option value="Float" <c:if test="${pd.CELLTYPE == 'Float'}">selected</c:if>>Float</option>
								    <option value="String" <c:if test="${pd.CELLTYPE == 'String'}">selected</c:if>>String</option>
								    <option value="Date" <c:if test="${pd.CELLTYPE == 'Date'}">selected</c:if>>Date</option>
								    <option value="BigDecimal" <c:if test="${pd.CELLTYPE == 'BigDecimal'}">selected</c:if>>BigDecimal</option>
									</select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">日期格式:</td>
								<td><input type="text" name="DATE_FORMAT" id="DATE_FORMAT" value="${pd.DATE_FORMAT}" maxlength="60" placeholder="如yyyy-MM-dd" title="时间格式" style="width:60%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">允许为空:</td>
								<td>
								    <label style="float:left;padding-left: 8px;padding-top:7px;">
										<input name="NULLABLE" type="radio" class="ace" id="form-field-radio1" value="0" <c:if test="${pd.NULLABLE == 0 or pd.NULLABLE == null }">checked="checked"</c:if>/>
										<span class="lbl">允许</span>
									</label>
									<label style="float:left;padding-left: 5px;padding-top:7px;">
										<input name="NULLABLE" type="radio" class="ace" id="form-field-radio2" value="1" <c:if test="${pd.NULLABLE == 1 }">checked="checked"</c:if>/>
										<span class="lbl">不允许</span>
									</label>
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
			if($("#NUMBER").val()==""){
				$("#NUMBER").tips({
					side:3,
		            msg:'请输入对应的序列',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#NUMBER").focus();
			return false;
			}
			if($("#MAPKEY").val()==""){
				$("#MAPKEY").tips({
					side:3,
		            msg:'请输入存储的KEY',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MAPKEY").focus();
			return false;
			}
			if($("#CELLTYPE").val()==""){
				$("#CELLTYPE").tips({
					side:3,
		            msg:'请输入单元格类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CELLTYPE").focus();
			return false;
			}
			if($("#NULLABLE").val()==""){
				$("#NULLABLE").tips({
					side:3,
		            msg:'请输入是否允许为空',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#NULLABLE").focus();
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