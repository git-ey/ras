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
					
					<form action="jeline/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="JELINE_ID" id="JELINE_ID" value="${pd.JELINE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="120" placeholder="唯一性标识,如F600001#01" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="10" placeholder="余额表期间，4位年" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证序号:</td>
								<td><input type="text" name="SEQUENCE_NUM" id="SEQUENCE_NUM" value="${pd.SEQUENCE_NUM}" maxlength="60" placeholder="凭证序号" title="凭证序号" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">唯一凭证号:</td>
								<td><input type="text" name="UNIQUE_JE_NUM" id="UNIQUE_JE_NUM" value="${pd.UNIQUE_JE_NUM}" maxlength="120" placeholder="编码规则：#+8位入账日期+#+凭证编号+#" title="唯一凭证号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">入账日期:</td>
								<td><input class="span10 date-picker" name="EFFECTIVE_DATE" id="EFFECTIVE_DATE" value="${pd.EFFECTIVE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="入账日期" title="入账日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">行号:</td>
								<td><input type="text" name="LINE_NUM" id="LINE_NUM" value="${pd.LINE_NUM}" maxlength="10" placeholder="凭证行号" title="行号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="120" placeholder="客户的科目代码" title="科目代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目说明:</td>
								<td><input type="text" name="ACCOUNT_DESCRIPTION" id="ACCOUNT_DESCRIPTION" value="${pd.ACCOUNT_DESCRIPTION}" maxlength="255" placeholder="客户的科目说明" title="科目说明" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="20" placeholder="代码，原币字段默认为CNY" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">汇率:</td>
								<td><input type="number" name="EXCHANGE_RATE" id="EXCHANGE_RATE" value="${pd.EXCHANGE_RATE}" maxlength="32" placeholder="这里输入汇率" title="汇率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">借方_原币:</td>
								<td><input type="number" name="ENTERED_DR" id="ENTERED_DR" value="${pd.ENTERED_DR}" maxlength="32" placeholder="原币借方" title="借方_原币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">贷方_原币:</td>
								<td><input type="number" name="ENTERED_CR" id="ENTERED_CR" value="${pd.ENTERED_CR}" maxlength="32" placeholder="原币贷方" title="贷方_原币" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">借方_本位币:</td>
								<td><input type="number" name="DR_AMOUNT" id="DR_AMOUNT" value="${pd.DR_AMOUNT}" maxlength="32" placeholder="本位币借方" title="借方_本位币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">贷方_本位币:</td>
								<td><input type="number" name="CR_AMOUNT" id="CR_AMOUNT" value="${pd.CR_AMOUNT}" maxlength="32" placeholder="本位币贷方" title="贷方_本位币" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">数量:</td>
								<td><input type="text" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="120" placeholder="这里输入数量" title="数量" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">摘要:</td>
								<td><input type="text" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="255" placeholder="这里输入摘要" title="摘要" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">制单人:</td>
								<td><input type="text" name="MAKER" id="MAKER" value="${pd.MAKER}" maxlength="255" placeholder="这里输入制单人" title="制单人" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">审核人:</td>
								<td><input type="text" name="CHECKER" id="CHECKER" value="${pd.CHECKER}" maxlength="255" placeholder="这里输入审核人" title="审核人" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证类别:</td>
								<td><input type="text" name="CATEGORY" id="CATEGORY" value="${pd.CATEGORY}" maxlength="255" placeholder="这里输入凭证类别" title="凭证类别" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
									<select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td>
									<select class="chosen-select form-control" name="STATUS" id="STATUS" data-placeholder="请选择" style="width:49%;">
								    <option value="INITIAL" <c:if test="${pd.STATUS == 'INITIAL'}">selected</c:if>>INITIAL</option>
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
			if($("#SEQUENCE_NUM").val()==""){
				$("#SEQUENCE_NUM").tips({
					side:3,
		            msg:'请输入凭证序号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEQUENCE_NUM").focus();
			return false;
			}
			if($("#UNIQUE_JE_NUM").val()==""){
				$("#UNIQUE_JE_NUM").tips({
					side:3,
		            msg:'请输入唯一凭证号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIQUE_JE_NUM").focus();
			return false;
			}
			if($("#EFFECTIVE_DATE").val()==""){
				$("#EFFECTIVE_DATE").tips({
					side:3,
		            msg:'请输入入账日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#EFFECTIVE_DATE").focus();
			return false;
			}
			if($("#LINE_NUM").val()==""){
				$("#LINE_NUM").tips({
					side:3,
		            msg:'请输入行号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LINE_NUM").focus();
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
			if($("#EXCHANGE_RATE").val()==""){
				$("#EXCHANGE_RATE").tips({
					side:3,
		            msg:'请输入汇率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#EXCHANGE_RATE").focus();
			return false;
			}
			if($("#ENTERED_DR").val()==""){
				$("#ENTERED_DR").tips({
					side:3,
		            msg:'请输入借方_原币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTERED_DR").focus();
			return false;
			}
			if($("#ENTERED_CR").val()==""){
				$("#ENTERED_CR").tips({
					side:3,
		            msg:'请输入贷方_原币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTERED_CR").focus();
			return false;
			}
			if($("#DR_AMOUNT").val()==""){
				$("#DR_AMOUNT").tips({
					side:3,
		            msg:'请输入借方_本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DR_AMOUNT").focus();
			return false;
			}
			if($("#CR_AMOUNT").val()==""){
				$("#CR_AMOUNT").tips({
					side:3,
		            msg:'请输入贷方_本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CR_AMOUNT").focus();
			return false;
			}
			if($("#DESCRIPTION").val()==""){
				$("#DESCRIPTION").tips({
					side:3,
		            msg:'请输入摘要',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DESCRIPTION").focus();
			return false;
			}
			if($("#MAKER").val()==""){
				$("#MAKER").tips({
					side:3,
		            msg:'请输入制单人',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MAKER").focus();
			return false;
			}
			if($("#CHECKER").val()==""){
				$("#CHECKER").tips({
					side:3,
		            msg:'请输入审核人',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CHECKER").focus();
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