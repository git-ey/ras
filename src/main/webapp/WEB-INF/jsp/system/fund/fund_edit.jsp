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
					
					<form action="fund/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_CODE" id="FUND_CODE" value="${pd.FUND_CODE}" maxlength="120" placeholder="这里输入基金代码" title="基金代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">管理公司:</td>
								<td>
								    <select class="chosen-select form-control" name="FIRM_CODE" id="FIRM_CODE" data-placeholder="请选择公司" style="vertical-align:top;width: 120px;">
									    <c:forEach items="${companyList}" var="var" varStatus="vs">
									        <option value="${var.COMPANY_CODE}" <c:if test="${pd.FIRM_CODE == var.COMPANY_CODE}">selected</c:if>>${var.SHORT_NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金简称:</td>
								<td><input type="text" name="SHORT_NAME" id="SHORT_NAME" value="${pd.SHORT_NAME}" maxlength="60" placeholder="这里输入基金简称" title="基金简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金全称:</td>
								<td><input type="text" name="FULL_NAME" id="FULL_NAME" value="${pd.FULL_NAME}" maxlength="240" placeholder="这里输入基金全称" title="基金全称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金原名:</td>
								<td><input type="text" name="FULL_NAME_ORIGINAL" id="FULL_NAME_ORIGINAL" value="${pd.FULL_NAME_ORIGINAL}" maxlength="240" placeholder="这里输入基金原名" title="基金原名" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">帐套号:</td>
								<td><input type="text" name="LEDGER_NUM" id="LEDGER_NUM" value="${pd.LEDGER_NUM}" maxlength="60" placeholder="这里输入帐套号" title="帐套号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">TA基金名称:</td>
								<td><input type="text" name="TA_NAME" id="TA_NAME" value="${pd.TA_NAME}" maxlength="240" placeholder="这里输入TA基金名称" title="TA基金名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">财务系统:</td>
								<td>
								    <select class="chosen-select form-control" name="FIN_SYSTEM" id="FIN_SYSTEM" data-placeholder="请选择" style="width:49%;">
								    <option value="HS" <c:if test="${pd.FIN_SYSTEM == 'HS'}">selected</c:if>>恒生</option>
								    <option value="JSZ" <c:if test="${pd.FIN_SYSTEM == 'JSZ'}">selected</c:if>>金手指</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">分级:</td>
								<td>
								    <select class="chosen-select form-control" name="STRUCTURED" id="STRUCTURED" data-placeholder="请选择" style="width:98%;">
								    <option value="N" <c:if test="${pd.STRUCTURED == 'N'}">selected</c:if>>N-不分级</option>
								    <option value="T" <c:if test="${pd.STRUCTURED == 'T'}">selected</c:if>>T-真分级</option>
								    <option value="F" <c:if test="${pd.STRUCTURED == 'F'}">selected</c:if>>F-假分级</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">保本:</td>
								<td>
								    <select class="chosen-select form-control" name="GUARANTEED" id="GUARANTEED" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.GUARANTEED == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.GUARANTEED == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">封闭:</td>
								<td>
								    <select class="chosen-select form-control" name="CLOSED" id="CLOSED" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.CLOSED == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.CLOSED == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">沪港:</td>
								<td>
								    <select class="chosen-select form-control" name="SHHK" id="SHHK" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.SHHK == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.SHHK == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">QD:</td>
								<td>
								    <select class="chosen-select form-control" name="QD" id="QD" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.QD == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.QD == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">货基:</td>
								<td>
								    <select class="chosen-select form-control" name="MF" id="MF" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.MF == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.MF == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">指数:</td>
								<td>
								    <select class="chosen-select form-control" name="IDX" id="IDX" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.IDX == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.IDX == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">LOF:</td>
								<td>
								    <select class="chosen-select form-control" name="LOF" id="LOF" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.LOF == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.LOF == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">ETF:</td>
								<td>
								    <select class="chosen-select form-control" name="ETF" id="ETF" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ETF == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ETF == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">FOF:</td>
								<td>
								    <select class="chosen-select form-control" name="FOF" id="FOF" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.FOF == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.FOF == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">货币量纲:</td>
								<td><input type="number" name="UNIT" id="UNIT" value="${pd.UNIT}" maxlength="32" placeholder="数字，按万元披露的维护成10000/按元披露的维护成1" title="货币量纲" style="width:49%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">合同生效日:</td>
								<td><input class="span10 date-picker" name="DATE_FROM" id="DATE_FROM" value="${pd.DATE_FROM}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="合同生效日" title="合同生效日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金终止日:</td>
								<td><input class="span10 date-picker" name="DATE_TO" id="DATE_TO" value="${pd.DATE_TO}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="基金终止日" title="基金终止日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金转型日:</td>
								<td><input class="span10 date-picker" name="DATE_TRANSFORM" id="DATE_TRANSFORM" value="${pd.DATE_TRANSFORM}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="基金转型日" title="基金转型日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">存款期限账龄:</td>
								<td>
								    <select class="chosen-select form-control" name="DEPOSIT_TERM_PERIOD" id="DEPOSIT_TERM_PERIOD" data-placeholder="请选择账龄" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${termList}" var="var" varStatus="vs">
									        <option value="${var.TERMHEAD_ID}" <c:if test="${pd.DEPOSIT_TERM_PERIOD == var.TERMHEAD_ID}">selected</c:if>>${var.NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">利率风险敞口账龄:</td>
								<td>
								    <select class="chosen-select form-control" name="INTEREST_RATE_PERIOD" id="INTEREST_RATE_PERIOD" data-placeholder="请选择账龄" style="vertical-align:top;width: 98%;">
									    <option value=""></option>
									    <c:forEach items="${termList}" var="var" varStatus="vs">
									        <option value="${var.TERMHEAD_ID}" <c:if test="${pd.INTEREST_RATE_PERIOD == var.TERMHEAD_ID}">selected</c:if>>${var.NAME}</option>
									    </c:forEach>
								  	</select>
								</td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">其他负债披露类型:</td>
								<td><input type="text" name="OTHER_LIABILITIES" id="OTHER_LIABILITIES" value="${pd.OTHER_LIABILITIES}" maxlength="120" placeholder="这里输入其他负债披露类型" title="其他负债披露类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
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
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入管理公司代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
			if($("#SHORT_NAME").val()==""){
				$("#SHORT_NAME").tips({
					side:3,
		            msg:'请输入基金简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHORT_NAME").focus();
			return false;
			}
			if($("#FULL_NAME").val()==""){
				$("#FULL_NAME").tips({
					side:3,
		            msg:'请输入基金全称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FULL_NAME").focus();
			return false;
			}
			if($("#FIN_SYSTEM").val()==""){
				$("#FIN_SYSTEM").tips({
					side:3,
		            msg:'请输入财务系统',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIN_SYSTEM").focus();
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
			if($("#DATE_FROM").val()==""){
				$("#DATE_FROM").tips({
					side:3,
		            msg:'请输入合同生效日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_FROM").focus();
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