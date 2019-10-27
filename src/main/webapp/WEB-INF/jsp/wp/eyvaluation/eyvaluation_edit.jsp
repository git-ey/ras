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
					
					<form action="eyvaluation/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="VALUATION_ID" id="VALUATION_ID" value="${pd.VALUATION_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="100" placeholder="F+基金官方代码+#+2位顺序编码，如F600001#01" title="基金ID" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="10" placeholder="4位年" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">EY科目代码:</td>
								<td><input type="text" autocomplete="off" name="EY_ACCOUNT_NUM" id="EY_ACCOUNT_NUM" value="${pd.EY_ACCOUNT_NUM}" maxlength="255" placeholder="根据映射表映射" title="EY科目代码" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="30" placeholder="这里输入币种" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">汇率:</td>
								<td><input type="number" autocomplete="off" name="EXCHANGE_RATE" id="EXCHANGE_RATE" value="${pd.EXCHANGE_RATE}" maxlength="32" placeholder="这里输入汇率" title="汇率" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">数量:</td>
								<td><input type="number" autocomplete="off" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="32" placeholder="这里输入数量" title="数量" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">单位成本:</td>
								<td><input type="number" autocomplete="off" name="UNIT_COST" id="UNIT_COST" value="${pd.UNIT_COST}" maxlength="32" placeholder="这里输入单位成本" title="单位成本" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成本_原币:</td>
								<td><input type="number" autocomplete="off" name="TOTAL_COST_ENTERED" id="TOTAL_COST_ENTERED" value="${pd.TOTAL_COST_ENTERED}" maxlength="32" placeholder="这里输入成本_原币" title="成本_原币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">成本_本位币:</td>
								<td><input type="number" autocomplete="off" name="TOTAL_COST_CNY" id="TOTAL_COST_CNY" value="${pd.TOTAL_COST_CNY}" maxlength="32" placeholder="这里输入成本_本位币" title="成本_本位币" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">行情收市价:</td>
								<td><input type="number" autocomplete="off" name="UNIT_PRICE" id="UNIT_PRICE" value="${pd.UNIT_PRICE}" maxlength="32" placeholder="这里输入行情收市价" title="行情收市价" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">市值_原币:</td>
								<td><input type="number" autocomplete="off" name="MKT_VALUE_ENTERED" id="MKT_VALUE_ENTERED" value="${pd.MKT_VALUE_ENTERED}" maxlength="32" placeholder="这里输入市值_原币" title="市值_原币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">市值_本位币:</td>
								<td><input type="number" autocomplete="off" name="MKT_VALUE_CNY" id="MKT_VALUE_CNY" value="${pd.MKT_VALUE_CNY}" maxlength="32" placeholder="这里输入市值_本位币" title="市值_本位币" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">估值增值_原币:</td>
								<td><input type="number" autocomplete="off" name="APPRECIATION_ENTERED" id="APPRECIATION_ENTERED" value="${pd.APPRECIATION_ENTERED}" maxlength="32" placeholder="这里输入估值增值_原币" title="估值增值_原币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">估值增值_本位币:</td>
								<td><input type="number" autocomplete="off" name="APPRECIATION_CNY" id="APPRECIATION_CNY" value="${pd.APPRECIATION_CNY}" maxlength="32" placeholder="这里输入估值增值_本位币" title="估值增值_本位币" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">停牌信息:</td>
								<td><input type="text" autocomplete="off" name="SUSPENSION_INFO" id="SUSPENSION_INFO" value="${pd.SUSPENSION_INFO}" maxlength="1200" placeholder="这里输入停牌信息" title="停牌信息" style="width:98%;"/></td>
							</tr>
							<tr>	
								<td style="width:100px;text-align: right;padding-top: 13px;">备注:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="480" placeholder="这里输入备注" title="备注" style="width:98%;"/></td>
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
			if($("#VDATE").val()==""){
				$("#VDATE").tips({
					side:3,
		            msg:'请输入估值日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VDATE").focus();
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
			if($("#QUANTITY").val()==""){
				$("#QUANTITY").tips({
					side:3,
		            msg:'请输入数量',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#QUANTITY").focus();
			return false;
			}
			if($("#UNIT_COST").val()==""){
				$("#UNIT_COST").tips({
					side:3,
		            msg:'请输入单位成本',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT_COST").focus();
			return false;
			}
			if($("#TOTAL_COST_ENTERED").val()==""){
				$("#TOTAL_COST_ENTERED").tips({
					side:3,
		            msg:'请输入成本_原币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TOTAL_COST_ENTERED").focus();
			return false;
			}
			if($("#TOTAL_COST_CNY").val()==""){
				$("#TOTAL_COST_CNY").tips({
					side:3,
		            msg:'请输入成本_本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TOTAL_COST_CNY").focus();
			return false;
			}
			if($("#UNIT_PRICE").val()==""){
				$("#UNIT_PRICE").tips({
					side:3,
		            msg:'请输入行情收市价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT_PRICE").focus();
			return false;
			}
			if($("#MKT_VALUE_ENTERED").val()==""){
				$("#MKT_VALUE_ENTERED").tips({
					side:3,
		            msg:'请输入市值_原币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MKT_VALUE_ENTERED").focus();
			return false;
			}
			if($("#MKT_VALUE_CNY").val()==""){
				$("#MKT_VALUE_CNY").tips({
					side:3,
		            msg:'请输入市值_本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MKT_VALUE_CNY").focus();
			return false;
			}
			if($("#APPRECIATION_ENTERED").val()==""){
				$("#APPRECIATION_ENTERED").tips({
					side:3,
		            msg:'请输入估值增值_原币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#APPRECIATION_ENTERED").focus();
			return false;
			}
			if($("#APPRECIATION_CNY").val()==""){
				$("#APPRECIATION_CNY").tips({
					side:3,
		            msg:'请输入估值增值_本位币',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#APPRECIATION_CNY").focus();
			return false;
			}
			if($("#SUSPENSION_INFO").val()==""){
				$("#SUSPENSION_INFO").tips({
					side:3,
		            msg:'请输入停牌信息',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUSPENSION_INFO").focus();
			return false;
			}
			if($("#DESCRIPTION").val()==""){
				$("#DESCRIPTION").tips({
					side:3,
		            msg:'请输入备注',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DESCRIPTION").focus();
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
			if($("#ATTR1").val()==""){
				$("#ATTR1").tips({
					side:3,
		            msg:'请输入属性1',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR1").focus();
			return false;
			}
			if($("#ATTR2").val()==""){
				$("#ATTR2").tips({
					side:3,
		            msg:'请输入属性2',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR2").focus();
			return false;
			}
			if($("#ATTR3").val()==""){
				$("#ATTR3").tips({
					side:3,
		            msg:'请输入属性3',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR3").focus();
			return false;
			}
			if($("#ATTR4").val()==""){
				$("#ATTR4").tips({
					side:3,
		            msg:'请输入属性4',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR4").focus();
			return false;
			}
			if($("#ATTR5").val()==""){
				$("#ATTR5").tips({
					side:3,
		            msg:'请输入属性5',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR5").focus();
			return false;
			}
			if($("#ATTR6").val()==""){
				$("#ATTR6").tips({
					side:3,
		            msg:'请输入属性6',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ATTR6").focus();
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