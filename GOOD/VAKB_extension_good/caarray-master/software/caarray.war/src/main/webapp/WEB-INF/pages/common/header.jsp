<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<a href="#content" id="navskip">Skip to Page Content</a>
<div id="nciheader">
    <div id="ncilogo"><a href="http://www.cancer.gov" target="_blank"><img src="<c:url value="/images/logotype.gif"/>" width="283" height="37" alt="Logo: National Cancer Institute" /></a></div>
    <div id="nihtag"><a href="http://www.cancer.gov" target="_blank"><img src="<c:url value="/images/tagline.gif"/>" width="295" height="37" alt="Logo: U.S. National Institutes of Health | www.cancer.gov" /></a></div>
</div>
<div id="caarrayheader">
    <div id="caarraylogo"><a href="<c:url value="/" />"><img src="<c:url value="/images/logo_caarray.gif"/>" width="172" height="46" alt="Logo: caArray - Array Data Management System" /></a></div>
    <c:if test="${pageContext.request.remoteUser != null && isUserHasRole == true}">
        <div id="topsearch">
            <s:form action="/search/basicSearch.action" theme="simple">
                <table>
                    <tr>
                        <td colspan="3" class="alignright">
                            <s:hidden name="searchType" value="COMBINATION_SEARCH"/>&nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td><s:textfield name="keyword"/></td>
                        <td>
                            <s:select name="categoryCombo" headerKey="-1" headerValue="Please Select..." list="#{}">
                                  <s:optgroup label="Search Experiments"
                                        list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8deae2fba3e3e4e5a3e3eee4a3eeececffffecf4a3fae8efa3eceef9e4e2e3a3dee8ecffeee5cceef9e4e2e3cdeae8f9dee8ecffeee5ceecf9e8eae2ffe4e8fe">[email protected]</a>()"
                                        listValue="%{getText(label)}" listKey="value" />
                                  <s:optgroup label="Search Samples"
                                        list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2a4d455c044443420444494304494b4b58584b53045d4f48044b495e43454404794f4b5849426b495e4345446a4d4f5e794f4b5849427943475a464f684345474f">[email protected]</a>tricCategories()"
                                        listValue="%{getText(label)}" listKey="value" />
                            </s:select>
                        </td>
                        <td><s:submit value="Search"/></td>
                    </tr>
                </table>
                </s:form>
        </div>
    </c:if>
</div>
<div id="infobar">
    <div id="rightinfo">
        <c:set var='repoInfo' value='Git URL: ${initParam["gitUrl"]}'/>
        <c:if test='${not empty initParam["gitRevision"]}'>
            <c:set var='repoInfo' value='${repoInfo}, revision: ${initParam["gitRevision"]}'/>
        </c:if>
        <span title="<c:out value='${repoInfo}'/>">Build <c:out value='${initParam["caarrayVersion"]}'/></span>
        <span class="headerbar">|</span>  Node: <span><c:out value='${initParam["nodeName"]}'/></span>
        <c:if test="${pageContext.request.remoteUser != null}">
            <span class="headerbar">|</span> Welcome, <c:out value='${pageContext.request.remoteUser}'/>
            <span class="headerbar">|</span> <a href="<c:url value="/logout.action" />"><span>Logout</span></a>
        </c:if>
    </div>
</div>
