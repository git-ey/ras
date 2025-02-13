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
	<script type="text/javascript" src="static/js/jquery.js"></script>
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/index/top.jsp"%>
	<!-- 日期框 -->
	<link rel="stylesheet" href="static/ace/css/datepicker.css" />
	<!-- 树形下拉框start -->
	<script type="text/javascript" src="plugins/selectZtree/selectTree.js"></script>
	<script type="text/javascript" src="plugins/selectZtree/framework.js"></script>
	<link rel="stylesheet" type="text/css" href="plugins/selectZtree/import_fh.css"/>
	<script type="text/javascript" src="plugins/selectZtree/ztree/ztree.js"></script>
	<link type="text/css" rel="stylesheet" href="plugins/selectZtree/ztree/ztree.css"></link>
	<!-- 树形下拉框end -->
	
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
					
					<form action="staff/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="STAFF_ID" id="STAFF_ID" value="${pd.STAFF_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">姓名:</td>
								<td><input type="text" autocomplete="off" name="NAME" id="NAME" value="${pd.NAME}" maxlength="50" placeholder="这里输入姓名" title="姓名" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">员工编号:</td>
								<td><input type="text" autocomplete="off" name="BIANMA" id="BIANMA" value="${pd.BIANMA}" maxlength="100" placeholder="这里输入编码" title="编码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">性别:</td>
								<td>
									<select name="SEX" id="SEX" style="width:98%;">
										<option <c:if test="${pd.SEX == '男'}">selected</c:if>>男</option>
										<option <c:if test="${pd.SEX == '女'}">selected</c:if>>女</option>
									</select>
								</td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">英文:</td>
								<td><input type="text" autocomplete="off" name="NAME_EN" id="NAME_EN" value="${pd.NAME_EN}" maxlength="50" placeholder="这里输入英文" title="英文" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">婚否:</td>
								<td>
									<select name="MARITAL" id="MARITAL" style="width:98%;">
										<option <c:if test="${pd.MARITAL == '未婚'}">selected</c:if>>未婚</option>
										<option <c:if test="${pd.MARITAL == '已婚'}">selected</c:if>>已婚</option>
									</select>
								</td>
								<td style="width:75px;text-align: right;padding-top: 13px;">民族:</td>
								<td><input type="text" autocomplete="off" name="NATION" id="NATION" value="${pd.NATION}" maxlength="10" placeholder="这里输入民族" title="民族" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">出生日期:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="BIRTHDAY" id="BIRTHDAY" value="${pd.BIRTHDAY}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="出生日期" title="出生日期" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">政治面貌:</td>
								<td><input type="text" autocomplete="off" name="POLITICAL" id="POLITICAL" value="${pd.POLITICAL}" maxlength="30" placeholder="这里输入政治面貌" title="政治面貌" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">入团时间:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="PJOINTIME" id="PJOINTIME" value="${pd.PJOINTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="入团时间" title="入团时间" style="width:100%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">身份证号:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="SFID" id="SFID" value="${pd.SFID}" maxlength="20" placeholder="这里输入身份证号" title="身份证号" style="width:98%;"/></td>
							</tr>
						</table>
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">所在部门:</td>
								<td>
									<input type="hidden" name="DEPARTMENT_ID" id="DEPARTMENT_ID" value="${pd.DEPARTMENT_ID}"/>
									<div class="selectTree" id="selectTree"></div>
								</td>
								<td style="width:75px;text-align: right;padding-top: 13px;">所在岗位:</td>
								<td><input type="text" autocomplete="off" name="POST" id="POST" value="${pd.POST}" maxlength="30" placeholder="这里输入现岗位" title="现岗位" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">岗位类别:</td>
								<td><input type="text" autocomplete="off" name="JOBTYPE" id="JOBTYPE" value="${pd.JOBTYPE}" maxlength="30" placeholder="这里输入岗位类别" title="岗位类别" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">工作职责:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="FUNCTIONS" id="FUNCTIONS" value="${pd.FUNCTIONS}" maxlength="255" placeholder="这里输入职责" title="职责" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">工作时间:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="JOBJOINTIME" id="JOBJOINTIME" value="${pd.JOBJOINTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="首次参加工作时间" title="参加工作时间" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">入职时间:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="DJOINTIME" id="DJOINTIME" value="${pd.DJOINTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="进本单位时间" title="进本单位时间" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">上岗时间:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="POJOINTIME" id="POJOINTIME" value="${pd.POJOINTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="上岗时间" title="上岗时间" style="width:98%;"/></td>
							</tr>
						</table>
						
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">邮箱:</td>
								<td><input type="text" autocomplete="off" name="EMAIL" id="EMAIL" value="${pd.EMAIL}" maxlength="50" placeholder="这里输入邮箱" title="邮箱" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">电话:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="TEL" id="TEL" value="${pd.TEL}" maxlength="20" placeholder="这里输入电话" title="电话" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">籍贯:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="FADDRESS" id="FADDRESS" value="${pd.FADDRESS}" maxlength="100" placeholder="这里输入籍贯" title="籍贯" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">现住址:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="ADDRESS" id="ADDRESS" value="${pd.ADDRESS}" maxlength="100" placeholder="这里输入现住址" title="现住址" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">毕业学校:</td>
								<td><input type="text" autocomplete="off" name="SCHOOL" id="SCHOOL" value="${pd.SCHOOL}" maxlength="30" placeholder="这里输入毕业学校" title="毕业学校" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">最高学历:</td>
								<td><input type="text" autocomplete="off" name="EDUCATION" id="EDUCATION" value="${pd.EDUCATION}" maxlength="10" placeholder="这里输入学历" title="学历" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">专业:</td>
								<td><input type="text" autocomplete="off" name="MAJOR" id="MAJOR" value="${pd.MAJOR}" maxlength="30" placeholder="这里输入专业" title="专业" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">职业职称:</td>
								<td><input type="text" autocomplete="off" name="FTITLE" id="FTITLE" value="${pd.FTITLE}" maxlength="30" placeholder="这里输入职称" title="职称" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">资格证书:</td>
								<td colspan="10"><input type="text" autocomplete="off" name="CERTIFICATE" id="CERTIFICATE" value="${pd.CERTIFICATE}" maxlength="30" placeholder="这里输入职业资格证" title="职业资格证" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">合同时长:</td>
								<td><input type="number" autocomplete="off" name="CONTRACTLENGTH" id="CONTRACTLENGTH" value="${pd.CONTRACTLENGTH}" maxlength="32" placeholder="输入劳动合同时长" title="劳动合同时长" style="width:86%;"/>&nbsp;年</td>
								<td style="width:75px;text-align: right;padding-top: 13px;">签订日期:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="CSTARTTIME" id="CSTARTTIME" value="${pd.CSTARTTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="签订日期" title="签订日期" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">终止日期:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="CENDTIME" id="CENDTIME" value="${pd.CENDTIME}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="终止日期" title="终止日期" style="width:98%;"/></td>
							</tr>
						</table>
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">备注:</td>
								<td><input type="text" autocomplete="off" name="BZ" id="BZ" value="${pd.BZ}" maxlength="255" placeholder="这里输入备注" title="备注" style="width:98%;"/></td>
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
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>

		<script type="text/javascript">
			jQuery.browser = {};
			(function () {
				jQuery.browser.msie = false;
				jQuery.browser.version = 0;
				if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
					jQuery.browser.msie = true;
					jQuery.browser.version = RegExp.$1;
				}
			})();
		</script>

		<script type="text/javascript">
		$(top.hangge());
		//保存
		function save(){
			if($("#NAME").val()==""){
				$("#NAME").tips({
					side:3,
		            msg:'请输入姓名',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#NAME").focus();
			return false;
			}
			if($("#BIANMA").val()==""){
				$("#BIANMA").tips({
					side:3,
		            msg:'请输入编码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BIANMA").focus();
			return false;
			}
			if($("#DEPARTMENT_ID").val()==""){
				$("#DEPARTMENT_ID").tips({
					side:3,
		            msg:'请输入部门',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DEPARTMENT_ID").focus();
			return false;
			}
			if($("#SEX").val()==""){
				$("#SEX").tips({
					side:3,
		            msg:'请输入性别',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEX").focus();
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
		
		//下拉树
		var defaultNodes = {"treeNodes":${zTreeNodes}};
		function initComplete(){
			//绑定change事件
			$("#selectTree").bind("change",function(){
				if(!$(this).attr("relValue")){
			      //  top.Dialog.alert("没有选择节点");
			    }else{
					//alert("选中节点文本："+$(this).attr("relText")+"<br/>选中节点值："+$(this).attr("relValue"));
					$("#DEPARTMENT_ID").val($(this).attr("relValue"));
			    }
			});
			//赋给data属性
			$("#selectTree").data("data",defaultNodes);  
			$("#selectTree").render();
			$("#selectTree2_input").val("${null==depname?'请选择':depname}");
		}

		</script>
</body>
</html>