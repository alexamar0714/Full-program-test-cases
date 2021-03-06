<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url value="/protected/ajax/project/files/listUnimportedForm.action" var="listUnimportedFormUrl" />
<c:url value="/protected/ajax/project/files/upload/uploadInBackground.action" var="uploadInBackgroundUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

<c:url value="/ajax/project/listTab/Samples/jsonList.action" var="samplesJsonListUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>
<c:url value="/ajax/project/listTab/Hybridizations/jsonList.action" var="hybsJsonListUrl">
    <c:param name="project.id" value="${project.id}"/>
</c:url>

<c:url value="/ajax/project/files/importTreeNodesJson.action" var="nodesJsonUrl"/>
<c:url value="/protected/ajax/project/files/validateSelectedImportFiles.action" var="validateImportFilesUrl"/>

<script type="text/javascript">
    fileTypeLookup = new Object();
    fileNameLookup = new Object();
    fileSizeLookup = new Object();
    fileStatusLookup = new Object();
    jobSize = 0;
    jobNumFiles = 0;
    SDRF_FILE_TYPE = '<s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="73141c055d1d1a1b5d1d101a5d1012120101120a5d171c1e121a1d5d151a1f165d351a1f16270a03162116141a0007010a333e323436">[email protected]</a>_TAB_SDRF"/>';
    IDF_FILE_TYPE = '<s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fe999188d0909796d0909d97d09d9f9f8c8c9f87d09a91939f9790d09897929bd0b897929baa878e9bac9b99978d8a8c87beb3bfb9bb">[email protected]</a>_TAB_IDF"/>';
    GPR_FILE_TYPE = '<s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4c2b233a6222252462222f25622f2d2d3e3e2d35623c20392b25223f622b2922293c2534620b3c3e042d222820293e0c0b1c1e">[email protected]</a>_FILE_TYPE"/>';
    UPLOADING_FILE_STATUS = '<s:property value="@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1770786139797e7f3979747e397476766565766e3973787a767e7939717e7b7239517e7b724463766362645742475b5856535e5950">[email protected]</a>"/>';
    <c:forEach items="${files}" var="file">
    fileTypeLookup['${file.id}'] = '${file.fileType}';
    fileNameLookup['${file.id}'] = '${caarrayfn:escapeJavaScript(file.name)}';
    fileSizeLookup['${file.id}'] = ${file.uncompressedSize};
    fileStatusLookup['${file.id}'] = '${file.fileStatus}';
    </c:forEach>
    <c:forEach items="${selectedFileIds}" var="sid">
      <c:if test="${sid.class.name == 'java.lang.Long'}">
      if(${sid}) {
        toggleFileInJob(true, ${sid});
      }
      </c:if>
    </c:forEach>

    toggleAllFiles = function(checked, theform) {
        numElements = theform.elements.length;
        for (i = 0; i < numElements; i++) {
             var element = theform.elements[i];
             if ("checkbox" == element.type && element.checked != checked && element.id.indexOf('chk') >= 0) {
                 element.checked = checked;
                 toggleFileInJob(checked, element.value);
             }
        }
    }

    toggleFileInJob = function(checked, id) {
        var size = fileSizeLookup[id];
    if (checked) {
      jobNumFiles++;
      jobSize += size;
    } else {
      jobNumFiles--;
      jobSize -= size;
    }
    updateJobSummary();
    }

    updateJobSummary = function() {
        var jobSizeContent = $('jobSizeContent');
        if(jobSizeContent) {
            jobSizeContent.innerHTML= jobNumFiles + " Files, " +formatFileSize(jobSize);
        }
    }

  checkRefFileSelection = function(findRefUrl) {
    var count = 0;
    numElements = $('selectFilesForm').elements.length;
    for (i = 0; i < numElements; i++) {
         var element = $('selectFilesForm').elements[i];
         if ("checkbox" == element.type && element.checked && element.id.indexOf('chk') >= 0) {
                count++;
                var type = fileTypeLookup[element.value];
                if (type != IDF_FILE_TYPE) {
                  alert('Only an IDF file may be selected.');
                  return false;
                }
                if (fileStatusLookup[element.value] == UPLOADING_FILE_STATUS) {
                  alert('A selected file is still uploading.');
                  return false;
                }
         }
    }

    if (count > 1) {
      alert('Only one IDF file may be selected.');
      return false;
     }

    return TabUtils.submitTabFormToUrl('selectFilesForm', findRefUrl, 'tabboxlevel2wrapper');
  }

  deleteFiles = function(deleteUrl) {
      var formElts = document.getElementById('datatable').getElementsByTagName('input');
      var hasUploadingFile = $A(formElts).any(function(elt) {
          return elt.checked && (elt.id.lastIndexOf('chk') > -1) && fileStatusLookup[elt.value] == UPLOADING_FILE_STATUS;
      });
      if (hasUploadingFile && !confirm("A file you have selected is still in the Uploading status."
              + " Attempting to delete a file in the middle of an upload could result in a corrupted file."
              + " Are you sure you want to delete the selected file(s)?")) {
          return false;
      }

      return TabUtils.submitTabFormToUrl('selectFilesForm', deleteUrl, 'tabboxlevel2wrapper');
  }
  
  passOnSelectedFiles = function(findRefUrl) {
    var checkboxes = $('selectFilesForm').selectedFileIds || {};
    var params = new Object();
    params['selectedFileIds'] = new Array();
    for (i = 0; i < checkboxes.length; ++i) {
      if (checkboxes[i].checked) {
        params['selectedFileIds'].push(checkboxes[i].value);
      }
    }
    TabUtils.showSubmittingText();
    Caarray.submitAjaxForm('selectFilesForm', 'tabboxlevel2wrapper', { url: findRefUrl, extraArgs: params});
  }

    unimportedFilterCallBack = function() {
        TabUtils.hideLoadingText();
    }

    doFilter = function() {
    	jobNumFiles = 0;
        jobSize = 0;
        var checkboxIds = $('selectFilesForm').__checkbox_selectedFileIds || {};
        TabUtils.disableFormCheckboxes(checkboxIds);
        TabUtils.showLoadingTextKeepMainContent();
        Caarray.submitAjaxForm('selectFilesForm', 'unimportedForm', {url: '${listUnimportedFormUrl}', onComplete: unimportedFilterCallBack});
    }

    openUploadWindow = function() {
        uploadWindow = window.open('${uploadInBackgroundUrl}', '_blank', "width=685,height=350,left=0,top=0,toolbar,scrollbars,resizable,status=yes");
    }

    isMageTabImport = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            return elt.checked && (elt.id.lastIndexOf('chk') > -1) && (fileTypeLookup[elt.value] == SDRF_FILE_TYPE || fileTypeLookup[elt.value] == IDF_FILE_TYPE);
        });
    }

    isGprImport = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            return elt.checked && (elt.id.lastIndexOf('chk') > -1) && (fileTypeLookup[elt.value] == GPR_FILE_TYPE);
        });
    }
    
    checkAnyFilesSelected = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).any(function(elt) {
            if(elt.id.lastIndexOf('chk') < 0) {
             return false;
            }
            return elt.checked;
        });
    }

    getSelectedFileNames = function() {
        var formElts = document.getElementById('datatable').getElementsByTagName('input');
        return $A(formElts).select(function(elt) {
          if(elt.id.lastIndexOf('chk') < 0) {
             return false;
            }
            return elt.checked;
        }).map(function(elt) {
            return fileNameLookup[elt.value];
        });
    }

    importFiles = function(importUrl) {
        if (!checkAnyFilesSelected()) {
            alert("At least one file must be selected");
        } else if (isGprImport() && !isMageTabImport()) {
            alert("Genepix GPR files can only be imported/validated as part of a MAGE-TAB dataset with at least one IDF and SDRF file.");
        } else if (isMageTabImport()) {
            openImportDescDialog(importUrl);
        } else {
            openImportDialog(importUrl);
        }
    }

    validateFiles = function(validateUrl) {
        if (!checkAnyFilesSelected()) {
            alert("At least one file must be selected");
        } else if (isGprImport() && !isMageTabImport()) {
            alert("Genepix GPR files can only be imported/validated as part of a MAGE-TAB dataset with at least one IDF and SDRF file.");
        } else {
            TabUtils.submitTabFormToUrl('selectFilesForm', validateUrl, 'tabboxlevel2wrapper');
        }
    }

    doImportFiles = function(importUrl, importDescription, createChoice, newAnnotationName, selectedNodes, selectedNodesType) {
        var formData = Form.serialize('selectFilesForm');
        var extraArgs = new Object();
        if (importDescription) {
            extraArgs['importDescription'] = importDescription;
        }
        if (createChoice) {
            extraArgs['targetAnnotationOption'] = createChoice;
        }
        if (newAnnotationName && newAnnotationName.length > 0) {
            extraArgs['newAnnotationName'] = newAnnotationName;
        }
        if (selectedNodes && selectedNodes.length > 0) {
           extraArgs['targetNodeIds'] = new Array();
           for (var i = 0; i < selectedNodes.length; i++) {
               extraArgs['targetNodeIds'].push(selectedNodes[i]);
           }
           extraArgs["nodeType"] = selectedNodesType;
        }
        formData = formData + '&' + Hash.toQueryString(extraArgs);
        TabUtils.showSubmittingText();
        new Ajax.Request('${validateImportFilesUrl}', {
            onSuccess: function(result) {
                var json = eval('(' + result.responseText + ')');
                if (json.validated || confirm(json.confirmMessage)) {
                    Caarray.submitAjaxForm('selectFilesForm', 'tabboxlevel2wrapper', { url: importUrl, extraArgs : extraArgs});
                } else {
                    TabUtils.hideSubmittingText();
                }
            },
            parameters: formData
        });
    }

    getCheckedNodeType = function(root) {
        var nd = ExtTreeUtils.findDescendent(root, "checked", true);
        return (nd == null ? null : nd.attributes.nodeType);
    }

    disableNodesWithOtherNodeType = function(parent, nodeType) {
        ExtTreeUtils.setEnabledStatus(parent, false, function(node) {
            var shouldDisable =  (node.attributes.nodeType != nodeType && node.attributes.checked != undefined);
            return shouldDisable;
        });
    }
    
    openImportDescDialog = function(importUrl) {
        var importDescriptionDialog = new Ext.Window({
            title: 'Enter Description of Import',
            closable: true,
            width: 300,
            height: 200,
            modal: true,
            bodyStyle: 'padding:5px',
            items: [{
                xtype: 'textarea',
                id: 'importDescription',
                name: 'importDescription',
                width: 280,
                height: 125,
                autoScroll: true,
                hideLabel: true
            }],
            buttons: [{
                text: 'Import',
                listeners: {
                    'click' : {
                        fn: function() {
                            doImportFiles(importUrl, $('importDescription').value);
                            importDescriptionDialog.close();
                        }
                    }
                }
            },{
                text: 'Cancel',
                listeners: {
                'click' : {
                    fn: function() { importDescriptionDialog.close(); }
                }
            }
            }]
        });
        importDescriptionDialog.show();
    }

    openImportDialog = function(importUrl) {
        var treeLoader = new Ext.tree.TreeLoader({
            dataUrl: '${nodesJsonUrl}'
        });
        treeLoader.on("beforeload", function(tl, node) {
            this.baseParams["project.id"] = '${project.id}';
            this.baseParams["nodeType"] = node.attributes.nodeType;
        }, treeLoader);
        // when new nodes are loaded, disable them if a different node type is selected
        // also add a handler to each to handle disabling different node types if checked
        treeLoader.on("load", function(tl, node) {
            if (ExtTreeUtils.hasCheckedNodes(node.getOwnerTree())) {
                disableNodesWithOtherNodeType(node, getCheckedNodeType(node.getOwnerTree().getRootNode()));
            }
            node.childNodes.each(function(childNode) {
                childNode.on("checkchange", function(nd, checked) {
                    if (checked) {
                        disableNodesWithOtherNodeType(nd.getOwnerTree().getRootNode(), nd.attributes.nodeType);
                    } else if (!ExtTreeUtils.hasCheckedNodes(nd.getOwnerTree())) {
                        ExtTreeUtils.setEnabledStatus(nd.getOwnerTree().getRootNode(), true);
                    }
                }, childNode);
           });
        }, treeLoader);

        // prevent selecting of nodes - instead we use the checkboxes and checked status for each node to select them
        var treeSelModel = new Ext.tree.DefaultSelectionModel();
        treeSelModel.on("beforeselect", function(selModel, node, oldNode) {
            return false;
        });

        var tree = new Ext.tree.TreePanel({
            animate:true,
            autoScroll:true,
            enableDD:false,
            containerScroll: true,
            hidden: true,
            hideMode: 'visibility',
            border: false,
            bodyStyle:'padding-top: 5px; padding-left: 15px',
            loader: treeLoader,
            rootVisible: false,
            selModel: treeSelModel
        });
        new Ext.tree.TreeSorter(tree, { property : "sort"});

        // set the root node and expand one level to get the categories (root node itself is invisible)
        var root = new Ext.tree.AsyncTreeNode({
            text: 'Experiment',
            nodeType: 'ROOT',
            sort: 'Experiment',
            draggable:false, // disable root node dragging
            id:'ROOT'
        });
        tree.setRootNode(root);
        root.expand();

        // set up the form panel to handle autocreation selection and target node selection
        var formPanel = new Ext.FormPanel({
            autoWidth: true,
            height: 500,
            url:'save-form.php',
            bodyStyle:'padding:5px 5px 0',
            labelAlign: 'right',
            autoScroll: true,
            border: false,
            items: [{
                                layout: 'form',
                                border: false,
                                width: 'auto',
                                items: [
                                            {
                                                html: '<p>For the ' + getSelectedFileNames().length + ' selected file(s), please identify how biomaterial and '
                                                    + ' hybridization annotations should be created or mapped',
                                                border: false
                                            },
                                            {
                                                xtype: 'radio',
                                                boxLabel: 'Autocreate annotation sets (Source -> Sample -> Extract -> Labeled'
                                                    + ' Extract -> Hybridization) for each selected file',
                                                id: 'create_choice_autocreate_per_file',
                                                name: 'create_choice',
                                                inputValue: '<s:property value="@gov.nih.nci<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a28cc1c3c3d0d0c3db8cc3d2d2cecbc1c3d6cbcdcc8cc3d0d0c3dbc6c3d6c38ce6c3d6c3ebcfd2cdd0d6f6c3d0c5c7d6e3cccccdd6c3d6cbcdccedd2d6cbcdcce2">[email protected]</a>AUTOCREATE_PER_FILE"/>',
                                                itemCls: 'create_choice_form_item',
                                                hideLabel: true
                                            },
                                            {
                                                html: 'Note:  For GenePix and Illumina files, in which >1 biomaterial may be'
                                                + ' represented in a given file, biomaterial annotations will be generated'
                                                + ' based on the number of samples in the file.<p>',
                                                border: false
                                            },
                                            {
                                                xtype: 'radio',
                                                boxLabel: 'Autocreate a single annotation set (Source -> Sample -> Extract -> Labeled'
                                                    + ' Extract -> Hybridization) for all selected files',
                                                id: 'create_choice_autocreate_single',
                                                name: 'create_choice',
                                                inputValue: '<s:property value="@gov.nih.nci<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c6e8a5a7a7b4b4a7bfe8a7b6b6aaafa5a7b2afa9a8e8a7b4b4a7bfa2a7b2a7e882a7b2a78fabb6a9b4b292a7b4a1a3b287a8a8a9b2a7b2afa9a889b6b2afa9a886">[email protected]</a>AUTOCREATE_SINGLE"/>',
                                                itemCls: 'create_choice_form_item',
                                                hideLabel: true
                                            },
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Name for created annotations',
                                                id: 'autocreate_single_annotation_name',
                                                name: 'autocreate_single_annotation_name',
                                                itemCls: 'create_choice_indented_item'
                                            },
                                            {
                                                xtype: 'radio',
                                                boxLabel: 'Associate selected file(s) to existing biomaterial or hybridization',
                                                listeners: {
                                                    'check' : {
                                                        fn: function(theradio,ischecked) {
                                                            formPanel.findById('experiment_design_tree_instructions').setVisible(ischecked);
                                                            tree.setVisible(ischecked);
                                                        }
                                                   }
                                                },
                                                id: 'create_choice_associate_to_biomaterials',
                                                name: 'create_choice',
                                                inputValue: '<s:property value="@gov.nih.nci<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3d135e5c5c4f4f5c44135c4d4d51545e5c49545253135c4f4f5c44595c495c13795c495c74504d524f49695c4f5a58497c535352495c49545253724d495452537d">[email protected]</a>ASSOCIATE_TO_NODES"/>',
                                                itemCls: 'create_choice_form_item',
                                                hideLabel: true
                                            },
                                            {
                                                html: 'Note that biomaterials and hybridizations will be autocreated downstream'
                                                    + ' of the selected node in the annotation set.  Be sure to select the most'
                                                    + ' specific node in the annotation set.',
                                                border: false,
                                                hidden: true,
                                                id: 'experiment_design_tree_instructions'
                                            },
                                            tree,
                                            {
                                                xtype: 'textarea',
                                                fieldLabel: 'Import Description',
                                                id: 'importDescription',
                                                name: 'importDescription',
                                                width: 280,
                                                height: 125,
                                                autoScroll: true,
                                            }
                                       ]
                            }
            ]
        });

        var annotationDialog = new Ext.Window({
            title: 'Import Options',
            closable:true,
            width:650,
            height:570,
            modal: true,
            items: [formPanel],
            buttons: [{
                text: 'Import',
                listeners: {
                    'click' : {
                        fn: function() {
                            var createChoiceRadios = [$('create_choice_autocreate_per_file'), $('create_choice_autocreate_single'), $('create_choice_associate_to_biomaterials')];
                            var selectedCreateChoiceRadio = $A(createChoiceRadios).find(function(elt) { return elt.checked });
                            if (!selectedCreateChoiceRadio) {
                                alert('You must select an import option');
                                return;
                            }
                            var newAnnotationName = $('autocreate_single_annotation_name').value;
                            if ($('create_choice_autocreate_single').checked && (!newAnnotationName || newAnnotationName.length == 0)) {
                                alert("You must enter a value for the new annotation name");
                                return;
                            }
                            var checkedNodes = ExtTreeUtils.getCheckedNodes(tree.getRootNode(), 'entityId');
                            var checkedNodesType = getCheckedNodeType(tree.getRootNode());
                            doImportFiles(importUrl, $('importDescription').value, selectedCreateChoiceRadio.value, newAnnotationName, checkedNodes, checkedNodesType);
                            annotationDialog.close();
                        }
                    }
                }
            },{
                text: 'Cancel',
                listeners: {
                'click' : {
                    fn: function() { annotationDialog.close(); }
                }
            }
            }]
        });
        annotationDialog.show();
    }
</script>

<caarray:tabPane subtab="true" submittingPaneMessageKey="experiment.files.processing">

    <div class="boxpad2">
        <h3><fmt:message key="project.tabs.unimportedFiles" /></h3>
        <c:if test="${!project.locked && caarrayfn:canFullWrite(project, caarrayfn:currentUser()) && (!project.importingData)}">
            <div class="addlink">
                <fmt:message key="experiment.data.upload" var="uploadLabel" />
                <caarray:linkButton actionClass="add" text="${uploadLabel}" onclick="openUploadWindow()"/>
            </div>
        </c:if>
    </div>

    <div id="uploadInProgressDiv" style="display: none;">
        <fmt:message key="data.file.upload.inProgress"/>
    </div>

    <div id="tree" style="float:left; margin:20px; border:1px solid #c3daf9; width:250px; height:300px; display: none"></div>

  <div class="tableboxpad" id="unimportedForm">
      <%@ include file="/WEB-INF/pages/project/files/listUnimportedForm.jsp" %>
    </div>

    <c:if test="${!project.locked && caarrayfn:canWrite(project, caarrayfn:currentUser())}">
        <caarray:actions divclass="actionsthin">
            <c:if test="${(!project.importingData)}">
                <c:url value="/protected/ajax/project/files/deleteFiles.action" var="deleteUrl" />
                <caarray:linkButton actionClass="delete" text="Delete" onclick="deleteFiles('${deleteUrl}');" />
                <c:url value="/protected/ajax/project/files/unpackFiles.action" var="unpackUrl" />
                <caarray:linkButton actionClass="import" text="Unpack Archive" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${unpackUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/editFiles.action" var="editUrl" />
                <caarray:linkButton actionClass="edit" text="Change File Type" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${editUrl}', 'tabboxlevel2wrapper');" />
                <c:url value="/protected/ajax/project/files/findRefFiles.action" var="findRefUrl" />
                <caarray:linkButton actionClass="validate" text="Select Referenced Files" onclick="checkRefFileSelection('${findRefUrl}');" />
                <c:url value="/protected/ajax/project/files/validateFiles.action" var="validateUrl" />
                <caarray:linkButton actionClass="validate" text="Validate" onclick="validateFiles('${validateUrl}');" />
                <c:url value="/protected/ajax/project/files/importFiles.action" var="importUrl"/>
                <caarray:linkButton actionClass="import" text="Import" onclick="importFiles('${importUrl}');" />
                <c:url value="/protected/ajax/project/files/addSupplementalFiles.action" var="supplementalUrl"/>
                <caarray:linkButton actionClass="import" text="Add Supplemental Files" onclick="TabUtils.submitTabFormToUrl('selectFilesForm', '${supplementalUrl}', 'tabboxlevel2wrapper');" />
            </c:if>
            <c:url value="/protected/ajax/project/files/listUnimportedWithoutClearingCheckboxes.action" var="unimportedUrl"/>
            <caarray:linkButton actionClass="import" text="Refresh Status" onclick="passOnSelectedFiles('${unimportedUrl}');" />
        </caarray:actions>
    </c:if>
</caarray:tabPane>
