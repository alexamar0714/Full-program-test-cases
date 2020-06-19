<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <title>Manage Vocabularies</title>
</head>
<body>
    <h1>
        Manage Vocabularies
    </h1>
    <caarray:helpPrint/>
    <c:url value="/protected/ajax/vocabulary/list.action" var="tissueSitesUrl">
        <c:param name="category"><s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="72151d045c1c1b1a5c1c111b5c1113130000130b5c161d1f131b1c5c02001d181711065c370a0217001b1f171c063d1c061d1e1d150b31130617151d000b323d20">[email protected]</a>GANISM_PART" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="tissueTypesUrl">
        <c:param name="category"><s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="adcac2db83c3c4c583c3cec483ceccccdfdfccd483c9c2c0ccc4c383dddfc2c7c8ced983e8d5ddc8dfc4c0c8c3d9e2c3d9c2c1c2cad4eeccd9c8cac2dfd4ede0ec">[email protected]</a>TERIAL_TYPE" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="cellTypesUrl">
        <c:param name="category"><s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d4b3bba2fababdbcfabab7bdfab7b5b5a6a6b5adfab0bbb9b5bdbafaa4a6bbbeb1b7a0fa91aca4b1a6bdb9b1baa09bbaa0bbb8bbb3ad97b5a0b1b3bba6ad949791">[email protected]</a>LL_TYPE" /></c:param>
    </c:url>
    <c:url value="/protected/ajax/vocabulary/list.action" var="conditionsUrl">
        <c:param name="category"><s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7f1810095111161751111c16511c1e1e0d0d1e06511b10121e1611510f0d10151a1c0b513a070f1a0d16121a110b30110b10131018063c1e0b1a18100d063f3b36">[email protected]</a>SEASE_STATE" /></c:param>
    </c:url>

    <fmt:message key="vocabulary.tabs.ORGANISM_PART" var="tissueSitesTitle" />
    <fmt:message key="vocabulary.tabs.MATERIAL_TYPE" var="tissueTypesTitle" />
    <fmt:message key="vocabulary.tabs.CELL_TYPE" var="cellTypesTitle" />
    <fmt:message key="vocabulary.tabs.DISEASE_STATE" var="conditionsTitle" />

    <c:if test="${param.initialTab != null && param.startWithEdit != null}">
        <c:set value="${param.startWithEdit}" scope="session" var="startWithEdit" />
        <c:set value="${param.returnProjectId}" scope="session" var="returnProjectId" />
        <c:set value="${param.returnInitialTab1}" scope="session" var="returnInitialTab1" />
        <c:set value="${param.returnInitialTab2}" scope="session" var="returnInitialTab2" />
        <c:set value="${param.returnInitialTab2Url}" scope="session" var="returnInitialTab2Url" />
    </c:if>

    <div class="padme">
        <ajax:tabPanel panelStyleId="tabs" panelStyleClass="tabs2" currentStyleClass="active" contentStyleId="tabboxwrapper" contentStyleClass="tabboxwrapper"
                postFunction="TabUtils.setSelectedTab" preFunction="TabUtils.preFunction">
            <caarray:tab caption="${tissueSitesTitle}" baseUrl="${tissueSitesUrl}" defaultTab="${param.initialTab == null || param.initialTab == 'ORGANISM_PART'}" />
            <caarray:tab caption="${tissueTypesTitle}" baseUrl="${tissueTypesUrl}" defaultTab="${param.initialTab == 'MATERIAL_TYPE'}" />
            <caarray:tab caption="${cellTypesTitle}" baseUrl="${cellTypesUrl}" defaultTab="${param.initialTab == 'CELL_TYPE'}" />
            <caarray:tab caption="${conditionsTitle}" baseUrl="${conditionsUrl}" defaultTab="${param.initialTab == 'DISEASE_STATE'}" />
        </ajax:tabPanel>
    </div>
</body>
</html>