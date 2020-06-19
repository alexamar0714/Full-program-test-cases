<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title><fmt:message key="protocols.mamage" /></title>
</head>
<body>
    <h1><fmt:message key="protocols.mamage" /></h1>
    <caarray:helpPrint/>
    <c:url value="/protected/ajax/protocol/list.action" var="protocolUrl" />
    <c:url value="/protected/ajax/vocabulary/list.action" var="protocolTypeUrl">
        <c:param name="category"><s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bfd8d0c991d1d6d791d1dcd691dcdedecdcddec691dbd0d2ded6d191cfcdd0d5dadccb91fac7cfdacdd6d2dad1cbf0d1cbd0d3d0d8c6fcdecbdad8d0cdc6ffefed">[email protected]</a>OTOCOL_TYPE" /></c:param>
    </c:url>

    <fmt:message key="protocols.tabs.protocol" var="protocolTitle" />
    <fmt:message key="vocabulary.tabs.PROTOCOL_TYPE" var="protocolTypeTitle" />

    <div class="padme">
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <caarray:tab caption="${protocolTitle}" baseUrl="${protocolUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'protocol'}" />
            <caarray:tab caption="${protocolTypeTitle}" baseUrl="${protocolTypeUrl}" defaultTab="${param.initialTab == 'PROTOCOL_TYPE'}" />
        </ajax:tabPanel>
    </div>
</body>
</html>