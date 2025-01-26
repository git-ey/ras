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
					
					<form action="eyje/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="JE_ID" id="JE_ID" value="${pd.JE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="120" placeholder="这里输入基金ID" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="10" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证序号:</td>
								<td><input type="text" autocomplete="off" name="SEQUENCE_NUM" id="SEQUENCE_NUM" value="${pd.SEQUENCE_NUM}" maxlength="60" placeholder="这里输入凭证序号" title="凭证序号" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">唯一凭证号:</td>
								<td><input type="text" autocomplete="off" name="UNIQUE_JE_NUM" id="UNIQUE_JE_NUM" value="${pd.UNIQUE_JE_NUM}" maxlength="120" placeholder="这里输入唯一凭证号" title="唯一凭证号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">入账日期:</td>
								<td><input class="span10 date-picker" name="EFFECTIVE_DATE" id="EFFECTIVE_DATE" value="${pd.EFFECTIVE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="入账日期" title="入账日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="120" placeholder="这里输入科目代码" title="科目代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="20" placeholder="这里输入币种" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">汇率:</td>
								<td><input type="number" autocomplete="off" name="EXCHANGE_RATE" id="EXCHANGE_RATE" value="${pd.EXCHANGE_RATE}" maxlength="32" placeholder="这里输入汇率" title="汇率" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">借贷方向:</td>
								<td><input type="text" autocomplete="off" name="DRCR" id="DRCR" value="${pd.DRCR}" maxlength="30" placeholder="这里输入借贷方向" title="借贷方向" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">原币金额:</td>
								<td><input type="number" autocomplete="off" name="AMOUNT_ENTERED" id="AMOUNT_ENTERED" value="${pd.AMOUNT_ENTERED}" maxlength="32" placeholder="这里输入原币金额" title="原币金额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">本位币金额:</td>
								<td><input type="number" autocomplete="off" name="AMOUNT_CNY" id="AMOUNT_CNY" value="${pd.AMOUNT_CNY}" maxlength="32" placeholder="这里输入本位币金额" title="本位币金额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">借正贷负金额:</td>
								<td><input type="number" autocomplete="off" name="AMOUNT_CNY_DRCR" id="AMOUNT_CNY_DRCR" value="${pd.AMOUNT_CNY_DRCR}" maxlength="32" placeholder="这里输入借正贷负金额" title="借正贷负金额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">数量:</td>
								<td><input type="text" autocomplete="off" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="120" placeholder="这里输入数量" title="数量" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">摘要:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="255" placeholder="这里输入摘要" title="摘要" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">制单人:</td>
								<td><input type="text" autocomplete="off" name="MAKER" id="MAKER" value="${pd.MAKER}" maxlength="255" placeholder="这里输入制单人" title="制单人" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">审核人:</td>
								<td><input type="text" autocomplete="off" name="CHECKER" id="CHECKER" value="${pd.CHECKER}" maxlength="255" placeholder="这里输入审核人" title="审核人" style="width:98%;"/></td>
							</tr>
							<tr>
							    <td style="width:100px;text-align: right;padding-top: 13px;">记账人:</td>
								<td><input type="text" autocomplete="off" name="POSTER" id="POSTER" value="${pd.POSTER}" maxlength="60" placeholder="这里输入记账人" title="记账人" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证类别:</td>
								<td><input type="text" autocomplete="off" name="CATEGORY" id="CATEGORY" value="${pd.CATEGORY}" maxlength="255" placeholder="这里输入凭证类别" title="凭证类别" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
							        <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
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
			if($("#DRCR").val()==""){
				$("#DRCR").tips({
					side:3,
		            msg:'请输入借贷方向',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DRCR").focus();
			return false;
			}
			if($("#AMOUNT_CNY").val()==""){
				$("#AMOUNT_CNY").tips({
					side:3,
		            msg:'请输入本位币金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#AMOUNT_CNY").focus();
			return false;
			}
			if($("#AMOUNT_CNY_DRCR").val()==""){
				$("#AMOUNT_CNY_DRCR").tips({
					side:3,
		            msg:'请输入借正贷负金额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#AMOUNT_CNY_DRCR").focus();
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