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
					
					<form action="eybalance/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="EYBALANCE_ID" id="EYBALANCE_ID" value="${pd.EYBALANCE_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="255" placeholder="唯一性标识,如F600001#01" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="10" placeholder="余额表期间，4位年" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">日期:</td>
								<td><input class="span10 date-picker" name="BDATE" id="BDATE" value="${pd.BDATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="余额表日期，2017-12-31" title="日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY科目代码:</td>
								<td><input type="text" autocomplete="off" name="EY_ACCOUNT_NUM" id="EY_ACCOUNT_NUM" value="${pd.EY_ACCOUNT_NUM}" maxlength="255" placeholder="根据映射表映射" title="EY科目代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">应收/应付:</td>
								<td><input type="text" autocomplete="off" name="APAR" id="APAR" value="${pd.APAR}" maxlength="20" placeholder="这里输入AR/AP" title="应收/应付" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="30" placeholder="代码，原币字段默认为CNY" title="币种" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">汇率:</td>
								<td><input type="text" autocomplete="off" name="EXCHANGE_RATE" id="EXCHANGE_RATE" value="${pd.EXCHANGE_RATE}" maxlength="30" placeholder="CNY默认为1，其他币种填入科目" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">年初借贷方向:</td>
								<td><input type="text" autocomplete="off" name="BEGIN_DRCR" id="BEGIN_DRCR" value="${pd.BEGIN_DRCR}" maxlength="30" placeholder="借/贷/平" title="年初借贷方向" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">年初本位币:</td>
								<td><input type="number" autocomplete="off" name="BEGIN_BALANCE_CNY" id="BEGIN_BALANCE_CNY" value="${pd.BEGIN_BALANCE_CNY}" maxlength="32" placeholder="余额表年初数" title="年初本位币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">本年本位币借方:</td>
								<td><input type="number" autocomplete="off" name="DR_AMOUNT_CNY" id="DR_AMOUNT_CNY" value="${pd.DR_AMOUNT_CNY}" maxlength="32" placeholder="余额表累计借方" title="本年本位币借方" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">本年本位币贷方:</td>
								<td><input type="number" autocomplete="off" name="CR_AMOUNT_CNY" id="CR_AMOUNT_CNY" value="${pd.CR_AMOUNT_CNY}" maxlength="32" placeholder="余额表累计贷方" title="本年本位币贷方" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末借贷方向:</td>
								<td><input type="text" autocomplete="off" name="END_DRCR" id="END_DRCR" value="${pd.END_DRCR}" maxlength="30" placeholder="借/贷/平" title="年末借贷方向" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末本位币贷方:</td>
								<td><input type="number" autocomplete="off" name="END_BALANCE_CNY" id="END_BALANCE_CNY" value="${pd.END_BALANCE_CNY}" maxlength="32" placeholder="余额表期末余额" title="年末本位币贷方" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">年初外币:</td>
								<td><input type="number" autocomplete="off" name="BEGIN_BALANCE_ENTERED" id="BEGIN_BALANCE_ENTERED" value="${pd.BEGIN_BALANCE_ENTERED}" maxlength="32" placeholder="这里输入年初外币" title="年初外币" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">本年外币借方:</td>
								<td><input type="number" autocomplete="off" name="DR_AMOUNT_ENTERED" id="DR_AMOUNT_ENTERED" value="${pd.DR_AMOUNT_ENTERED}" maxlength="32" placeholder="这里输入本年外币借方" title="本年外币借方" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">本年外币贷方:</td>
								<td><input type="number" autocomplete="off" name="CR_AMOUNT_ENTERED" id="CR_AMOUNT_ENTERED" value="${pd.CR_AMOUNT_ENTERED}" maxlength="32" placeholder="这里输入本年外币贷方" title="本年外币贷方" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末外币:</td>
								<td><input type="number" autocomplete="off" name="END_BALANCE_ENTERED" id="END_BALANCE_ENTERED" value="${pd.END_BALANCE_ENTERED}" maxlength="32" placeholder="这里输入年末外币" title="年末外币" style="width:98%;"/></td>
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
								<td style="width:100px;text-align: right;padding-top: 13px;">属性1:</td>
								<td><input type="text" autocomplete="off" name="ATTR1" id="ATTR1" value="${pd.ATTR1}" maxlength="255" placeholder="这里输入属性1" title="属性1" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">属性2:</td>
								<td><input type="text" autocomplete="off" name="ATTR2" id="ATTR2" value="${pd.ATTR2}" maxlength="255" placeholder="这里输入属性2" title="属性2" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">属性3:</td>
								<td><input type="text" autocomplete="off" name="ATTR3" id="ATTR3" value="${pd.ATTR3}" maxlength="255" placeholder="这里输入属性3" title="属性3" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">属性4:</td>
								<td><input type="text" autocomplete="off" name="ATTR4" id="ATTR4" value="${pd.ATTR4}" maxlength="255" placeholder="这里输入属性4" title="属性4" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">属性5:</td>
								<td><input type="text" autocomplete="off" name="ATTR5" id="ATTR5" value="${pd.ATTR5}" maxlength="255" placeholder="这里输入属性5" title="属性5" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">属性6:</td>
								<td><input type="text" autocomplete="off" name="ATTR6" id="ATTR6" value="${pd.ATTR6}" maxlength="255" placeholder="这里输入属性6" title="属性6" style="width:98%;"/></td>
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
			if($("#BDATE").val()==""){
				$("#BDATE").tips({
					side:3,
		            msg:'请输入日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BDATE").focus();
			return false;
			}
			if($("#EY_ACCOUNT_NUM").val()==""){
				$("#EY_ACCOUNT_NUM").tips({
					side:3,
		            msg:'请输入EY科目代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#EY_ACCOUNT_NUM").focus();
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
			if($("#BEGIN_DRCR").val()==""){
				$("#BEGIN_DRCR").tips({
					side:3,
		            msg:'请输入年初借贷方向',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BEGIN_DRCR").focus();
			return false;
			}
			if($("#BEGIN_BALANCE_CNY").val()==""){
				$("#BEGIN_BALANCE_CNY").tips({
					side:3,
		            msg:'请输入年初本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BEGIN_BALANCE_CNY").focus();
			return false;
			}
			if($("#DR_AMOUNT_CNY").val()==""){
				$("#DR_AMOUNT_CNY").tips({
					side:3,
		            msg:'请输入本年本位币借方',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DR_AMOUNT_CNY").focus();
			return false;
			}
			if($("#CR_AMOUNT_CNY").val()==""){
				$("#CR_AMOUNT_CNY").tips({
					side:3,
		            msg:'请输入本年本位币贷方',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CR_AMOUNT_CNY").focus();
			return false;
			}
			if($("#END_DRCR").val()==""){
				$("#END_DRCR").tips({
					side:3,
		            msg:'请输入年末借贷方向',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#END_DRCR").focus();
			return false;
			}
			if($("#END_BALANCE_CNY").val()==""){
				$("#END_BALANCE_CNY").tips({
					side:3,
		            msg:'请输入年末本位币贷方',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#END_BALANCE_CNY").focus();
			return false;
			}
			if($("#BEGIN_BALANCE_ENTERED").val()==""){
				$("#BEGIN_BALANCE_ENTERED").tips({
					side:3,
		            msg:'请输入年初外币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BEGIN_BALANCE_ENTERED").focus();
			return false;
			}
			if($("#DR_AMOUNT_ENTERED").val()==""){
				$("#DR_AMOUNT_ENTERED").tips({
					side:3,
		            msg:'请输入本年外币借方',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DR_AMOUNT_ENTERED").focus();
			return false;
			}
			if($("#CR_AMOUNT_ENTERED").val()==""){
				$("#CR_AMOUNT_ENTERED").tips({
					side:3,
		            msg:'请输入本年外币贷方',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CR_AMOUNT_ENTERED").focus();
			return false;
			}
			if($("#END_BALANCE_ENTERED").val()==""){
				$("#END_BALANCE_ENTERED").tips({
					side:3,
		            msg:'请输入年末外币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#END_BALANCE_ENTERED").focus();
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
		            msg:'请输入ACTIVE',
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