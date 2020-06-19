<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<s:set name="msgText" value="%{getText('experiment.hybridizations.confirmDelete')}"/>

<caarray:tabPane subtab="true">
    <caarray:projectListTabHeader entityName="Hybridization" isSubtab="true"/>

    <c:url value="/ajax/project/listTab/Hybridizations/load.action" var="sortUrl">
        <c:param name="project.id" value="${project.id}" />
    </c:url>

    <div class="tableboxpad">
    <ajax:displayTag id="datatable" ajaxFlag="true" tableClass="searchresults" preFunction="TabUtils.showLoadingTextKeepMainContent" postFunction="TabUtils.hideLoadingText">
        <display:table class="searchresults" cellspacing="0" list="${pagedItems}"
            requestURI="${sortUrl}" id="row" excludedParams="project.id currentHybridization.id">
            <caarray:displayTagProperties/>
            <display:setProperty name="pagination.sort.param" value="pagedItems.sortCriterion" />
            <display:setProperty name="pagination.sortdirection.param" value="pagedItems.sortDirection" />
            <display:setProperty name="pagination.pagenumber.param" value="pagedItems.pageNumber" />
            <display:column titleKey="experiment.hybridizations.name" sortable="true" sortProperty="NAME">
                <caarray:projectListTabActionLink linkContent="${row.name}" entityName="Hybridization" action="view" itemId="${row.id}" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="experiment.hybridizations.relatedLabeledExtract">
                <caarray:projectListTabRelatedItemsLinks relatedItems="${row.labeledExtracts}" relatedEntityName="LabeledExtract" nameProperty="name" isSubtab="true" maxWidth="30"/>
            </display:column>
            <display:column titleKey="experiment.files.uncompressedSize">
                <fmt:formatNumber value="${row.uncompressedSizeOfDataFiles / 1024}" maxFractionDigits="0"/>
            </display:column>
            <caarray:projectListTabActionColumns entityName="Hybridization" item="${row}" actions="!edit,!delete" isSubtab="true" 
                confirmText="${msgText}"/>
            <s:set name="showDownloadGroups" value="%{@gov.nih.nci.caarray.we<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="61034f000215080e0f4f11130e0b0402154f200312151300021531130e0b04021531130e150e020e0d200f0f0e150015080e0f2d081215350003200215080e0f21">[email protected]</a>isWillPerformDownloadByGroups(#attr.row.getAllDataFiles())}"/>
            <caarray:projectListTabDownloadColumn entityName="Hybridization" itemId="${row.id}" showDownloadGroups="${showDownloadGroups}"/>
        </display:table>
    </ajax:displayTag>

    </div>
</caarray:tabPane>
