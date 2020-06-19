<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Source" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Sources/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id currentSource.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.sources.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Source" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.sources.description" sortable="true" />
            <display:column property="tissueSite.value" titleKey="currentSource.tissueSite"/>
            <display:column titleKey="experiment.sources.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Source" item="${row}" actions="!edit,!!copy,!delete" 
                isSubtab="true"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.we<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fa98d49b998e939594d48a8895909f998ed4bb98898e889b998eaa8895909f998eaa88958e95999596bb9494958e9b8e939594b693898eae9b98bb998e939594ba">[email protected]</a>isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="Source" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>                        
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
