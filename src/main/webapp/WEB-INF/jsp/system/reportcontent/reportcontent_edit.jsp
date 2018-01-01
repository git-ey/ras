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
					
					<form action="reportcontent/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="REPORTCONTENT_ID" id="REPORTCONTENT_ID" value="${pd.REPORTCONTENT_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="60" title="基金代码" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">财务报表负责人的职位描述1:</td>
								<td><input type="text" name="FS_ATTR1" id="FS_ATTR1" value="${pd.FS_ATTR1}" maxlength="800" title="财务报表负责人的职位描述1" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">财务报表负责人的职位描述2:</td>
								<td><input type="text" name="FS_ATTR2" id="FS_ATTR2" value="${pd.FS_ATTR2}" maxlength="800" title="财务报表负责人的职位描述2" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">财务报表负责人的职位描述3:</td>
								<td><input type="text" name="FS_ATTR3" id="FS_ATTR3" value="${pd.FS_ATTR3}" maxlength="800" title="财务报表负责人的职位描述3" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">应支付关联方的佣金NOTES:</td>
								<td><input type="text" name="COMISSION_NOTE" id="COMISSION_NOTE" value="${pd.COMISSION_NOTE}" maxlength="800" title="应支付关联方的佣金NOTES" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">管理费描述1:</td>
								<td><input type="text" name="MF_ATTR1" id="MF_ATTR1" value="${pd.MF_ATTR1}" maxlength="800" title="管理费描述1" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">管理费描述2:</td>
								<td><input type="text" name="MF_ATTR2" id="MF_ATTR2" value="${pd.MF_ATTR2}" maxlength="800" title="管理费描述2" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">管理费描述3:</td>
								<td><input type="text" name="MF_ATTR3" id="MF_ATTR3" value="${pd.MF_ATTR3}" maxlength="800" title="管理费描述3" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">管理费描述4:</td>
								<td><input type="text" name="MF_ATTR4" id="MF_ATTR4" value="${pd.MF_ATTR4}" maxlength="800" title="管理费描述4" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">管理费描述5:</td>
								<td><input type="text" name="MF_ATTR5" id="MF_ATTR5" value="${pd.MF_ATTR5}" maxlength="800" title="管理费描述5" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">托管费描述1:</td>
								<td><input type="text" name="CF_ATTR1" id="CF_ATTR1" value="${pd.CF_ATTR1}" maxlength="800" title="托管费描述1" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">托管费描述2:</td>
								<td><input type="text" name="CF_ATTR2" id="CF_ATTR2" value="${pd.CF_ATTR2}" maxlength="800" title="托管费描述2" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">托管费描述3:</td>
								<td><input type="text" name="CF_ATTR3" id="CF_ATTR3" value="${pd.CF_ATTR3}" maxlength="800" title="托管费描述3" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">托管费描述4:</td>
								<td><input type="text" name="CF_ATTR4" id="CF_ATTR4" value="${pd.CF_ATTR4}" maxlength="800" title="托管费描述4" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">托管费描述5:</td>
								<td><input type="text" name="CF_ATTR5" id="CF_ATTR5" value="${pd.CF_ATTR5}" maxlength="800" title="托管费描述5" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">利率风险的敏感性分析:</td>
								<td><input type="text" name="IR_ATTR1" id="IR_ATTR1" value="${pd.IR_ATTR1}" maxlength="800" title="利率风险的敏感性分析" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">利率风险变动上升:</td>
								<td><input type="text" name="IR_UP" id="IR_UP" value="${pd.IR_UP}" maxlength="800" title="利率风险变动上升" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">利率风险变动下降:</td>
								<td><input type="text" name="IR_DOWN" id="IR_DOWN" value="${pd.IR_DOWN}" maxlength="800" title="利率风险变动下降" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">价格风险变动上升:</td>
								<td><input type="text" name="PR_UP" id="PR_UP" value="${pd.PR_UP}" maxlength="800" title="价格风险变动上升" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">价格风险变动下降:</td>
								<td><input type="text" name="PR_DOWN" id="PR_DOWN" value="${pd.PR_DOWN}" maxlength="800" title="价格风险变动下降" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">其他价格风险:</td>
								<td><input type="text" name="PR_ATTR1" id="PR_ATTR1" value="${pd.PR_ATTR1}" maxlength="800" title="其他价格风险" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">其他价格风险敞口:</td>
								<td><input type="text" name="PR_ATTR2" id="PR_ATTR2" value="${pd.PR_ATTR2}" maxlength="800" title="其他价格风险敞口" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">他价格风险的敏感性分析:</td>
								<td><input type="text" name="PR_ATTR3" id="PR_ATTR3" value="${pd.PR_ATTR3}" maxlength="800" title="他价格风险的敏感性分析" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">公允价值所属层次间的重大变动:</td>
								<td><input type="text" name="FV_ATTR1" id="FV_ATTR1" value="${pd.FV_ATTR1}" maxlength="800" title="公允价值所属层次间的重大变动" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">启用:</td>
								<td><input type="text" name="ACTIVE" id="ACTIVE" value="${pd.ACTIVE}" maxlength="10" title="启用" style="width:98%;"/></td>
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