<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="LabeledExtract" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/LabeledExtracts/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id currentLabeledExtract.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.labeledExtracts.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="LabeledExtract" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column property="description" sortProperty="DESCRIPTION" titleKey="experiment.labeledExtracts.description" sortable="true" />
            <display:column property="materialType.value" titleKey="currentLabeledExtract.materialType"/>
            <display:column titleKey="experiment.labeledExtracts.relatedExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.extracts}" relatedEntityName="Extract" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="experiment.labeledExtracts.relatedHybridizations">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.hybridizations}" relatedEntityName="Hybridization" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="LabeledExtract" item="${row}" actions="!edit,!!copy,!delete" 
                isSubtab="true"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.we<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7715591614031e1819590705181d1214035936150403051614032705181d121403270518031814181b361919180316031e18193b1e04032316153614031e181937">[email protected]</a>isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="LabeledExtract" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>                        
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
