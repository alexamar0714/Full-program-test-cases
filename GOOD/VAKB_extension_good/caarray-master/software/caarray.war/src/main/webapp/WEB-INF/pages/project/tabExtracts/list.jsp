<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Extract" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Extracts/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id currentExtract.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.extracts.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Extract" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.extracts.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentExtract.materialType"/>
            <display:column titleKey="experiment.extracts.relatedSamples">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.samples}" relatedEntityName="Sample" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="experiment.extracts.labeledExtracts">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Extract" item="${row}" actions="!edit,!!copy,!delete" 
                isSubtab="true"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.we<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1674387775627f7978386664797c7375623857746562647775624664797c737562466479627975797a577878796277627f79785a7f65624277745775627f797856">[email protected]</a>isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="Extract" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
