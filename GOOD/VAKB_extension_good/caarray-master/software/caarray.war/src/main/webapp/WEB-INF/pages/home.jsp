<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
<head>
    <meta name="showLoginSidebar" content="true"/>
    <script type="text/javascript">
            populateCategory = function() {
                if ($('searchTypeSEARCH_BY_SAMPLE').checked == true) {
                    $('selectSampleCat').show();
                    $('selectExpCat').hide();
                    checkSampleCategorySelection();
                } else if ($('searchTypeSEARCH_BY_EXPERIMENT').checked == true) {
                    $('selectExpCat').show();
                    $('selectSampleCat').hide();
                    hideOtherCharacteristics();
                    checkExpCategorySelection();
                } else {
                    $('selectExpCat').hide();
                    $('selectSampleCat').hide();
                    hideOtherCharacteristics();
                }
            }

            checkSampleCategorySelection = function() {
                if ($('selectSampleCat').value == 'OTHER_CHARACTERISTICS') {
                    showOtherCharacteristics();
                    hideOrganism();
                    showKeywordTxtBox();
                } else if ($('selectSampleCat').value == 'SAMPLE_ORGANISM') {
                    hideKeywordTxtBox();
                    showOrganism();
                    hideOtherCharacteristics();
                } else {
                    showKeywordTxtBox();
                    hideOrganism();
                    hideOtherCharacteristics();
                }
            }

            checkExpCategorySelection = function() {
                if ($('selectExpCat').value == 'ORGANISM') {
                    hideKeywordTxtBox();
                    showOrganism();
                    hideOtherCharacteristics();
                } else {
                    showKeywordTxtBox();
                    hideOrganism();
                    hideOtherCharacteristics();
                }
            }

            showKeywordTxtBox = function() {
                $('keywordTxtField').show();
                $('keywordlabel').show();
            }

            hideKeywordTxtBox = function() {
                $('keywordTxtField').hide();
                $('keywordlabel').hide();
            }

            showOrganism = function() {
                $('orgField').show();
                $('orgLabel').show();
            }

            hideOrganism = function() {
                $('orgField').hide();
                $('orgLabel').hide();
            }

            showOtherCharacteristics = function() {
                $('otherCharsField').show();
                $('otherCharsLabel').show();
            }

            hideOtherCharacteristics = function() {
                $('otherCharsField').hide();
                $('otherCharsLabel').hide();
            }
            
            checkFields = function() {

                var trimmedKeyword = trim($('keywordTxtField').value);

                if (trimmedKeyword.length > 0) {
                  var re = new RegExp('<s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="03646c752d6d6a6b2d6d606a2d6062627171627a2d7466612d6260776a6c6d2d50666271606b4260776a6c6d4348465a544c5147">[email protected]</a>_REGEX_WEB"/>', 'g');
                  var OK = re.exec(trimmedKeyword);
                  if (OK == null) {
                      alert('Cannot use special characters in search keyword.');
                      return false;
                  }
                }

                if ( (($('searchTypeSEARCH_BY_EXPERIMENT').checked == true && $('selectExpCat').value == 'ORGANISM') ||
                     ($('searchTypeSEARCH_BY_SAMPLE').checked == true && $('selectSampleCat').value == 'SAMPLE_ORGANISM')) &&
                     $F('orgField') == -1) {
                        alert('An organism must be selected.');
                        return false;
                }
                else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                  $('selectSampleCat').value == 'OTHER_CHARACTERISTICS' &&
                  $F('otherCharsField') == -1) {
                        alert('A characteristic must be selected.');
                        return false;
                }
                else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                    $('selectSampleCat').value == 'SAMPLE_EXTERNAL_ID' &&
                    trimmedKeyword.length < 1 ) {
                    alert('An external sample id must be at least 1 character long.');
                    return false;
                } else if ($('searchTypeSEARCH_BY_SAMPLE').checked == true &&
                    $('selectSampleCat').value != 'OTHER_CHARACTERISTICS' &&
                    $('selectSampleCat').value != 'SAMPLE_ORGANISM' &&
                    $('selectSampleCat').value != 'SAMPLE_EXTERNAL_ID' &&
                    trimmedKeyword.length < 3 ) {
                    alert('Keyword must be at least 3 characters long.');
                    return false;
                }



                $('searchform').submit();

            }

    </script>

</head>
<body>
    <div class="homebanner"><img src="<c:url value="/images/banner_caarray.jpg"/>" width="598" height="140" alt="" /></div>
    <h1>Welcome to the caArray Data Portal</h1>
    <caarray:helpPrint/>
    <p><strong>caArray</strong> is an open-source, web and programmatically accessible microarray data management system that supports the annotation of microarray data using <a href="http://www.fged.org/projects/mage-tab">MAGE-TAB</a> and web-based forms. Data and annotations may be kept private to the owner, shared with user-defined collaboration groups, or made public. The NCI instance of <a href="https://array.nci.nih.gov">caArray</a> hosts many cancer-related public datasets available for download.</p>

    <div id="browsesearchwrapper">
         <s:actionerror/>
        <div id="browseboxhome" style="margin-bottom: 20px">
            <h2 class="tanbar">Browse caArray</h2>
            <div class="boxpad">
                <table class="alttable" summary="layout" cellspacing="0">
                    <c:forEach items="${browseItems}" var="row" varStatus="rowStatus">
                        <tr class="${rowStatus.count % 2 == 0 ? 'even' : 'odd'}">
                            <td>
                                <c:choose>
                                    <c:when test="${!empty row.category}">
                                        <c:url value="/browse.action" var="browseLink">
                                            <c:param name="category" value="${row.category}"/>
                                        </c:url>
                                    <a href="${browseLink}"><fmt:message key="${row.category.resourceKey}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="${row.resourceKey}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${!empty row.category}">
                                        <c:url value="/browse.action" var="browseLink">
                                            <c:param name="category" value="${row.category}"/>
                                        </c:url>
                                        <a href="${browseLink}">${row.count}</a>
                                    </c:when>
                                    <c:otherwise>${row.count}</c:otherwise>                                
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        <div id="searchboxhome" >
            <h2 class="tanbar">Search caArray</h2>
            <div class="boxpad">
                <s:form id="searchform" action="/search/basicSearch.action" cssClass="alttable" onsubmit="checkFields()">
`                    <tr>
                        <td align="right">
                            <s:label theme="simple"><b><fmt:message key="search.type"/>:</b></s:label>
                        </td>
                        <td style="white-space:nowrap">
                            <s:radio id="searchType" name="searchType" key="search.type"
                            list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="12757d643c7c7b7a3c7c717b3c7173736060736b3c6577703c7371667b7d7c3c41777360717a5371667b7d7c5275776641777360717a466b627741777e7771667b">[email protected]</a>on()"
                            listValue="%{getText(label)}"
                            listKey="value"
                            value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="41262e376f2f28296f2f22286f222020333320386f252e2c20282f6f3224203322296f1224203322291538312412242d242235282e2f01120400130209">[email protected]</a>_BY_EXPERIMENT"
                            onclick="populateCategory()" theme="simple"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                            <s:label theme="simple"><b><fmt:message key="search.category"/>:</b></s:label>
                        </td>
                        <td>
                            <label for="selectExpCat">
                            <s:select id="selectExpCat" name="categoryExp"
                            list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b1d6dec79fdfd8d99fdfd2d89fd2d0d0c3c3d0c89fc6d4d39fd0d2c5d8dedf9fe2d4d0c3d2d9f0d2c5d8dedff1d6d4c5e2d4d0c3d2d9f2d0c5d4d6dec3d8d4c2">[email protected]</a>()" listValue="%{getText(label)}"
                            listKey="value" value="EXPERIMENT_ID" theme="simple" onchange="checkExpCategorySelection()" />
                            </label>

                            <label for="selectSampleCat">
                            <s:select id="selectSampleCat" name="categorySample"
                            list="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c7a0a8b1e9a9aeafe9a9a4aee9a4a6a6b5b5a6bee9b0a2a5e9a6a4b3aea8a9e994a2a6b5a4af86a4b3aea8a987a0a2b394a2a6b5a4af85aea8aaa2b3b5aea484a6">[email protected]</a>tegories()" listValue="%{getText(label)}"
                            listKey="value" value="SAMPLE_NAME"  cssStyle="display:none;" theme="simple" onchange="checkSampleCategorySelection()"/>
                            </label>
                        </td>
                    </tr>

                     <tr>
                        <td align="right">
                            <div id="otherCharsLabel" style="display:none"><b><label for="otherCharsField">Characteristic:</label></b></div>
                        </td>
                        <td>
                            <s:select id="otherCharsField" name="selectedCategory"
	                            list="categories" listValue="name"
    	                        listKey="id" cssStyle="display:none;" theme="simple"/>
                        </td>
                     </tr>

                     <tr>
                        <td align="right">
                            <div id="keywordlabel"><b><label for="keywordTxtField"><fmt:message key="search.keyword"/>:</label></b></div>
                        </td>
                        <td>
                            <s:textfield id="keywordTxtField" name="keyword" key="search.keyword" theme="simple"/>
                        </td>
                     </tr>

                     <tr>
                        <td align="right">
                            <div id="orgLabel" style="display:none"><b><label for="orgField">Organism:</label></b></div>
                        </td>
                        <td>
                            <s:select id="orgField" name="selectedOrganism"
	                            list="organisms" listValue="scientificName"
    	                        listKey="id" cssStyle="display:none;" theme="simple"/>
                        </td>
                     </tr>
                    <tr>
                        <td colspan="2" class="centered">
                    <del class="btnwrapper">
                        <ul id="btnrow">
                            <caarray:action onclick="checkFields()" actionClass="search" text="Search"/>
                         </ul>
                     </del>
                        </td>
                    </tr>
                    <input type="submit" class="enableEnterSubmit"/>
                </s:form>
                <caarray:focusFirstElement formId="searchform"/>
           </div>
        </div>
    </div>
    <div class="clear"></div>

    </body>
</html>
