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
					
					<form action="fomularconfig/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="FOMULARCONFIG_ID" id="FOMULARCONFIG_ID" value="${pd.FOMULARCONFIG_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">测试类型:</td>
								<td><input type="text" autocomplete="off" name="TEST_TYPE" id="TEST_TYPE" value="${pd.TEST_TYPE}" maxlength="32" title="测试类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">公式类型:</td>
								<td><input type="text" autocomplete="off" name="FORMULA_TYPE" id="FORMULA_TYPE" value="${pd.FORMULA_TYPE}" maxlength="32" title="公式类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">序号:</td>
								<td><input type="number" autocomplete="off" name="SEQ" id="SEQ" value="${pd.SEQ}" maxlength="32" title="序号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="32" title="基金代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">权重:</td>
								<td><input type="number" autocomplete="off" name="WEIGHT" id="WEIGHT" value="${pd.WEIGHT}" maxlength="32" title="权重" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基准类型:</td>
								<td><input type="text" autocomplete="off" name="BASE_TYPE" id="BASE_TYPE" value="${pd.BASE_TYPE}" maxlength="32" title="基准类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">数据来源类型:</td>
								<td><input type="text" autocomplete="off" name="DATA_TYPE" id="DATA_TYPE" value="${pd.DATA_TYPE}" maxlength="32" title="数据来源类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">指数代码:</td>
								<td><input type="text" autocomplete="off" name="INDEX_CODE" id="INDEX_CODE" value="${pd.INDEX_CODE}" maxlength="32" title="指数代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">数值（计算）:</td>
								<td><input type="number" autocomplete="off" name="VALUE" id="VALUE" value="${pd.VALUE}" maxlength="32" title="数值（计算）" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">数据来源:</td>
								<td><input type="text" autocomplete="off" name="DATA_FROM" id="DATA_FROM" value="${pd.DATA_FROM}" maxlength="32" title="数据来源" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">是否启用:</td>
								<td>
									<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">备注:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="32" title="备注" style="width:98%;"/></td>
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
			if($("#TEST_TYPE").val()==""){
				$("#TEST_TYPE").tips({
					side:3,
		            msg:'请输入测试类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TEST_TYPE").focus();
			return false;
			}
			if($("#FORMULA_TYPE").val()==""){
				$("#FORMULA_TYPE").tips({
					side:3,
		            msg:'请输入公式类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FORMULA_TYPE").focus();
			return false;
			}
			if($("#SEQ").val()==""){
				$("#SEQ").tips({
					side:3,
		            msg:'请输入序号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEQ").focus();
			return false;
			}
			if($("#FUND_ID").val()==""){
				$("#FUND_ID").tips({
					side:3,
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
			return false;
			}
			if($("#WEIGHT").val()==""){
				$("#WEIGHT").tips({
					side:3,
		            msg:'请输入权重',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#WEIGHT").focus();
			return false;
			}
			if($("#BASE_TYPE").val()==""){
				$("#BASE_TYPE").tips({
					side:3,
		            msg:'请输入基准类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BASE_TYPE").focus();
			return false;
			}
			if($("#DATA_TYPE").val()==""){
				$("#DATA_TYPE").tips({
					side:3,
		            msg:'请输入数据来源类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATA_TYPE").focus();
			return false;
			}
			// if($("#INDEX_CODE").val()==""){
			// 	$("#INDEX_CODE").tips({
			// 		side:3,
		    //         msg:'请输入指数代码',
		    //         bg:'#AE81FF',
		    //         time:2
		    //     });
			// 	$("#INDEX_CODE").focus();
			// return false;
			// }
			// if($("#VALUE").val()==""){
			// 	$("#VALUE").tips({
			// 		side:3,
		    //         msg:'请输入数值（计算）',
		    //         bg:'#AE81FF',
		    //         time:2
		    //     });
			// 	$("#VALUE").focus();
			// return false;
			// }
			if($("#DATA_FROM").val()==""){
				$("#DATA_FROM").tips({
					side:3,
		            msg:'请输入数据来源',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATA_FROM").focus();
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