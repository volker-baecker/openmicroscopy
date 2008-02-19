<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://myfaces.apache.org/sandbox" prefix="s"%>

<f:loadBundle basename="ome.admin.bundle.messages" var="msg" />

<c:if
	test="${sessionScope.LoginBean.mode && sessionScope.LoginBean.role}">
	<f:view>
		<div id="hello"><h:form id="log">
			<h1><h:outputText value="#{msg.headerHello} #{sessionScope.LoginBean.username}" />!
			<h:commandLink action="#{LoginBean.logout}"
				title="#{msg.headerLogout}">
				<h:outputText value=" #{msg.headerLogout}" />
			</h:commandLink></h1>		
		</h:form></div>
		<div id="addform"><h:form id="group">

			<h2><h:outputText value="#{msg.groupsEditIn}" />'<h:outputText
				value="#{IAGManagerBean.group.name}" />'</h2>

			<h:message styleClass="errorText" id="groupError" for="group" />
			<br />

			<s:selectManyPicklist size="20" styleClass="manypick"
				value="#{IAGManagerBean.selectedExperimenters}">
				<f:selectItems value="#{IAGManagerBean.experimenters}" />
			</s:selectManyPicklist>

			<br />

			<h:commandButton id="submitAdd"
				action="#{IAGManagerBean.saveInToGroup}" value="#{msg.groupsSave}" />

		</h:form></div>
	</f:view>
</c:if>
