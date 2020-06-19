<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Sample" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Samples/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id currentSample.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.samples.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Sample" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.samples.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentSample.materialType"/>
            <display:column titleKey="experiment.samples.sources">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.sources}" relatedEntityName="Source" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="experiment.samples.extracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Sample" item="${row}" actions="!edit,!!copy,!delete"
                isSubtab="true"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.we<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="75175b1416011c1a1b5b05071a1f1016015b341706010714160125071a1f10160125071a011a161a19341b1b1a0114011c1a1b391c06012114173416011c1a1b35">[email protected]</a>isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="Sample" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
