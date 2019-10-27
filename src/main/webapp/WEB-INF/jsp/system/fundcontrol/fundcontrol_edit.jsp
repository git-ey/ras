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
					
					<form action="fundcontrol/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUNDCONTROL_ID" id="FUNDCONTROL_ID" value="${pd.FUNDCONTROL_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="60" title="基金ID" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票投资收益构成:</td>
								<td>
								    <select class="chosen-select form-control" name="STOCK_ALL" id="STOCK_ALL" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.STOCK_ALL == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.STOCK_ALL == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票买卖差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="STOCK_BS" id="STOCK_BS" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.STOCK_BS == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.STOCK_BS == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票赎回差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="STOCK_R" id="STOCK_R" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.STOCK_R == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.STOCK_R == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票申购差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="STOCK_P" id="STOCK_P" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.STOCK_P == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.STOCK_P == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券投资收益构成:</td>
								<td>
								    <select class="chosen-select form-control" name="BOND_ALL" id="BOND_ALL" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.BOND_ALL == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.BOND_ALL == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券买卖差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="BOND_BS" id="BOND_BS" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.BOND_BS == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.BOND_BS == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券赎回差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="BOND_R" id="BOND_R" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.BOND_R == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.BOND_R == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券申购差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="BOND_P" id="BOND_P" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.BOND_P == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.BOND_P == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属投资收益构成:</td>
								<td>
								    <select class="chosen-select form-control" name="GOLD_ALL" id="GOLD_ALL" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.GOLD_ALL == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.GOLD_ALL == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属买卖差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="GOLD_BS" id="GOLD_BS" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.GOLD_BS == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.GOLD_BS == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属赎回差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="GOLD_R" id="GOLD_R" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.GOLD_R == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.GOLD_R == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属申购差价收入:</td>
								<td>
								    <select class="chosen-select form-control" name="GOLD_P" id="GOLD_P" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.GOLD_P == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.GOLD_P == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">利率风险敏感性:</td>
								<td>
								    <select class="chosen-select form-control" name="RISK_S_INT" id="RISK_S_INT" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.RISK_S_INT == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.RISK_S_INT == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">价格风险敏感性:</td>
								<td>
								    <select class="chosen-select form-control" name="RISK_S_PRICE" id="RISK_S_PRICE" data-placeholder="请选择" style="width:98%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.RISK_S_PRICE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.RISK_S_PRICE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">价格风险敞口:</td>
								<td>
								    <select class="chosen-select form-control" name="RISK_E_PRICE" id="RISK_E_PRICE" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.RISK_E_PRICE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.RISK_E_PRICE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:140px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:98%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
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
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
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