<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:choose>
    <c:when test="${accessProfile.publicProfile}">
        <fmt:message var="profileOwnerName" key="project.permissions.publicProfile"/>
    </c:when>
    <c:when test="${accessProfile.groupProfile}">
        <fmt:message var="profileOwnerName" key="project.permissions.groupProfile">
            <fmt:param value="${accessProfile.group.group.groupName}"/>
        </fmt:message>
    </c:when>
</c:choose>
<fmt:message key="experiment.files.selectAllCheckBox" var="checkboxAll">
    <fmt:param value="selectAllCheckbox" />
    <fmt:param value="'profileForm'" />
</fmt:message>
    <table class="searchresults" cellspacing="0" style="height: auto; width: 400px; overflow-x: hidden">
        <tr>
            <th>Control Access to Specific Content for ${profileOwnerName}</th>
        </tr>
        <tr>
            <td class="filterrow" style="border-bottom: 1px solid #999">
                <s:label for="profileForm_accessProfile_securityLevel" value="Experiment Access" theme="simple"/>
                <c:choose>
                    <c:when test="false">
                        <span id="profileForm_accessProfile_securityLevel"><fmt:message key="${accessProfile.securityLevel.resourceKey}"/></span>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${accessProfile.publicProfile}">
                                <s:set var="secLevels" value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="deb9b1a8f0b0b7b6f0b0bdb7f0bdbfbfacacbfa7f0bab1b3bfb7b0f0aebbacb3b7adadb7b1b0adf08dbbbdabacb7aaa792bba8bbb29eaeabbcb2b7bd92bba8bbb2">[email protected]</a>s()"/>
                            </c:when>
                            <c:otherwise>
                                <s:set var="secLevels" value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e6818990c8888f8ec888858fc88587879494879fc882898b878f88c89683948b8f95958f898895c8b5838593948f929faa8390838aa685898a8a87848994879289">[email protected]</a>rGroupLevels()"/>                            
                            </c:otherwise>
                        </c:choose>
                        <s:select required="true" name="accessProfile.securityLevel" tabindex="1"
                            list="%{#secLevels}" listValue="%{getText(resourceKey)}"
                            onchange="PermissionUtils.changeExperimentAccess(this)"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>

    <div class="datatable" style="padding-bottom: 0px">
        <div class="scrolltable" style="height: auto; max-height: 500px; width: 400px; overflow-x: hidden">
        <table class="searchresults permissiontable" cellspacing="0">
            <tbody id="access_profile_samples"
                <c:if test="${!accessProfile.securityLevel.sampleLevelPermissionsAllowed}">style="display:none"</c:if>
            >
        <tr>
             <td class="left">
                <s:label><b><fmt:message key="search.keyword"/>:</b></s:label>
             </td>
             <td colspan="2" class="right">
                <s:textfield name="permSampleKeyword"/>
             </td>
        </tr>
        <tr>
            <td class="left">
                    <s:label><b><fmt:message key="search.category"/>:</b></s:label>
            </td>
            <td colspan="2" class="right">
                    <s:select name="permSampleSearch" label="Search Samples"
                            list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ef888099c1818687c1818c86c18c8e8e9d9d8e96c1988a8dc18e8c9b868081c19f9d80858a8c9bc1bf9d80858a8c9bbf8a9d82869c9c8680819cae8c9b868081af">[email protected]</a>getSearchSampleCategories()"
                            listValue="%{getText(label)}" listKey="value"/>
            </td>
        </tr>
        <tr id="characteristic_category_dropdown_id" 
		        <c:if test="${!selectedArbitraryCharacteristicCategory}">style="display:none"</c:if> >
            <td class="left">
	            <s:label><b><fmt:message key="search.category.arbitraryCharacteristic.label"/>:</b></s:label>
            </td>
            <td colspan="2" class="right">
	            <s:select name="arbitraryCharacteristicCategoryId" label="Search Samples"
		                list="arbitraryCharacteristicCategories"
                        listKey="id" listValue="name" />
            </td>
        </tr>
        <tr class="odd">
            <td class="left">&nbsp;</td>
            <td colspan="2" class="right">
                    <caarray:action actionClass="search" text="Search" onclick="PermissionUtils.listSampleProfile()" />
            </td>
        </tr>
        <c:choose>
        <c:when test="${sampleResultsCount > 0}">
        <tr>
            <td class="left">
                <s:label><b><fmt:message key="project.permissions.selectSecLevel"/>:</b>&nbsp;&nbsp;</s:label>
            </td>
            <td colspan="2" class="right">
                <s:select required="true" name="securityChoices" tabindex="1" cssClass="sample_security_level"
                    list="accessProfile.securityLevel.sampleSecurityLevels" listValue="%{getText(resourceKey)}"/>
            </td>
        </tr>
        <tr>
            <td colspan="3">
              <%@ include file="/WEB-INF/pages/project/permissions/list.jsp" %>
            </td>
        </tr>
        </c:when>
        <c:when test="${sampleResultsCount == 0}">
         <tr>
            <td colspan="3">
              <fmt:message key="project.permissions.search.noResults"/>
            </td>
         </tr>
        </c:when>
        </c:choose>

        </tbody>
        </table>

        </div>
    </div>
