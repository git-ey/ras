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
					
					<form action="report/${msg }.do" name="Form" id="Form" method="post">
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
						    <tr>
								<td style="width:100px;text-align: center;padding-top: 13px;" colspan="6"><b>基本参数</b></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">资产负债表日:</td>
								<td><input type="text" name="PERIOD" id="PERIOD" maxlength="30" title="资产负债表日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">管理公司:</td>
								<td>
								    <select class="chosen-select form-control" name="FIRM_CODE" id="FIRM_CODE" data-placeholder="请选择公司" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${companyList}" var="var" varStatus="vs">
									        <option value="${var.COMPANY_CODE}" <c:if test="${pd.FIRM_CODE == var.COMPANY_CODE}">selected</c:if>>${var.SHORT_NAME}</option>
									    </c:forEach>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td>
									<select class="chosen-select form-control" name="FUND_ID" id="FUND_ID" data-placeholder="请选择基金" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${fundList}" var="var" varStatus="vs">
									        <option value="${var.FUND_ID}" <c:if test="${pd.FUND_ID == var.FUND_ID}">selected</c:if>>${var.FUND_ID}</option>
									    </c:forEach>
								  	</select>
								</td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">货基:</td>
								<td>
								    <select class="chosen-select form-control" name="MF" id="MF" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="N" <c:if test="${pd.MF == 'N'}">selected</c:if>>否</option>
								        <option value="Y" <c:if test="${pd.MF == 'Y'}">selected</c:if>>是</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">ETF:</td>
								<td>
								    <select class="chosen-select form-control" name="ETF" id="ETF" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="N" <c:if test="${pd.ETF == 'N'}">selected</c:if>>否</option>
								        <option value="Y" <c:if test="${pd.ETF == 'Y'}">selected</c:if>>是</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">分级:</td>
								<td>
								    <select class="chosen-select form-control" name="STRUCTURED" id="STRUCTURED" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="N" <c:if test="${pd.STRUCTURED == 'N'}">selected</c:if>>N-不分级</option>
								        <option value="T" <c:if test="${pd.STRUCTURED == 'T'}">selected</c:if>>T-真分级</option>
								        <option value="F" <c:if test="${pd.STRUCTURED == 'F'}">selected</c:if>>F-假分级</option>
								    </select>
								</td>
							</tr>
						    <tr>
								<td style="width:100px;text-align: center;padding-top: 13px;" colspan="6"><b>模板参数选择</b></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">第一段:</td>
								<td>
								    <select class="chosen-select form-control" name="P1" id="P1" data-placeholder="请选模板" style="vertical-align:top;width: 98%;">
									    <c:forEach items="${p1List}" var="var" varStatus="vs">
									        <option value="${var.PARAGRAPH_CODE}" <c:if test="${pd.P1 == var.PARAGRAPH_CODE}">selected</c:if>>${var.PARAGRAPH_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">第二段:</td>
								<td>
								    <select class="chosen-select form-control" name="P2" id="P2" data-placeholder="请选模板" style="vertical-align:top;width: 98%;">
									    <c:forEach items="${p2List}" var="var" varStatus="vs">
									        <option value="${var.PARAGRAPH_CODE}" <c:if test="${pd.P2 == var.PARAGRAPH_CODE}">selected</c:if>>${var.PARAGRAPH_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">第三段:</td>
								<td>
								    <select class="chosen-select form-control" name="P3" id="P3" data-placeholder="请选模板" style="vertical-align:top;width: 98%;">
									    <c:forEach items="${p3List}" var="var" varStatus="vs">
									        <option value="${var.PARAGRAPH_CODE}" <c:if test="${pd.P3 == var.PARAGRAPH_CODE}">selected</c:if>>${var.PARAGRAPH_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">第四段:</td>
								<td>
								    <select class="chosen-select form-control" name="P4" id="P4" data-placeholder="请选模板" style="vertical-align:top;width: 98%;">
									    <c:forEach items="${p4List}" var="var" varStatus="vs">
									        <option value="${var.PARAGRAPH_CODE}" <c:if test="${pd.P4 == var.PARAGRAPH_CODE}">selected</c:if>>${var.PARAGRAPH_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">第五段:</td>
								<td>
								    <select class="chosen-select form-control" name="P5" id="P5" data-placeholder="请选模板" style="vertical-align:top;width: 98%;">
									    <c:forEach items="${p5List}" var="var" varStatus="vs">
									        <option value="${var.PARAGRAPH_CODE}" <c:if test="${pd.P5 == var.PARAGRAPH_CODE}">selected</c:if>>${var.PARAGRAPH_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">导出路径:</td>
								<td><input type="text" name="OUTBOND_PATH" id="OUTBOND_PATH" value="${outbondPath}" maxlength="480" title="导出路径" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">导出</a>
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
			if($("#PERIOD").val()==""){
				$("#PERIOD").tips({
					side:3,
		            msg:'请输入资产负债表日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PERIOD").focus();
			return false;
			}
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入管理公司',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
			<!--
			if($("#MF").val()==""){
				$("#MF").tips({
					side:3,
		            msg:'请输入货基',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MF").focus();
			return false;
			}
			if($("#ETF").val()==""){
				$("#ETF").tips({
					side:3,
		            msg:'请输入ETF',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ETF").focus();
			return false;
			}
			if($("#STRUCTURED").val()==""){
				$("#STRUCTURED").tips({
					side:3,
		            msg:'请输入分级',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STRUCTURED").focus();
			return false;
			}
			-->
			if($("#P1").val()==""){
				$("#P1").tips({
					side:3,
		            msg:'请输入第一段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#P1").focus();
			return false;
			}
			if($("#P2").val()==""){
				$("#P2").tips({
					side:3,
		            msg:'请输入第二段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#P2").focus();
			return false;
			}
			if($("#P3").val()==""){
				$("#P3").tips({
					side:3,
		            msg:'请输入第三段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#P3").focus();
			return false;
			}
			if($("#P4").val()==""){
				$("#P4").tips({
					side:3,
		            msg:'请输入第四段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#P4").focus();
			return false;
			}
			if($("#P5").val()==""){
				$("#P5").tips({
					side:3,
		            msg:'请输入第五段',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#P5").focus();
			return false;
			}
			if($("#OUTBOND_PATH").val()==""){
				$("#OUTBOND_PATH").tips({
					side:3,
		            msg:'请输入导出路径',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#OUTBOND_PATH").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
			
			//下拉框
			if(!ace.vars['touch']) {
				$('.chosen-select').chosen({allow_single_deselect:true}); 
				$(window)
				.off('resize.chosen')
				.on('resize.chosen', function() {
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				}).trigger('resize.chosen');
				$(document).on('settings.ace.chosen', function(e, event_name, event_val) {
					if(event_name != 'sidebar_collapsed') return;
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				});
				$('#chosen-multiple-style .btn').on('click', function(e){
					var target = $(this).find('input[type=radio]');
					var which = parseInt(target.val());
					if(which == 2) $('#form-field-select-4').addClass('tag-input-style');
					 else $('#form-field-select-4').removeClass('tag-input-style');
				});
			}
			
			
			//复选框全选控制
			var active_class = 'active';
			$('#simple-table > thead > tr > th input[type=checkbox]').eq(0).on('click', function(){
				var th_checked = this.checked;//checkbox inside "TH" table header
				$(this).closest('table').find('tbody > tr').each(function(){
					var row = this;
					if(th_checked) $(row).addClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', true);
					else $(row).removeClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', false);
				});
			});
			
		});
		</script>
</body>
</html>