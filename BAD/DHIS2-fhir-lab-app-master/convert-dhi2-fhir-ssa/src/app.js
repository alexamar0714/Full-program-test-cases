import {GetAllOrganisationUnits,GetTrackedEntityInstances,GetTrackedEntityInstancesFromOrgunitList,
	GetTrackedEntitiesMapping,checkDirectory,GetPatientAttributesMapping,GetPractitionerAttributesMapping,
	GetSpecimenAttributesMapping,getOrderAttributesMapping,getObservationAttributesMapping,
	getDiagnosticReportAttributesMapping} from "./api";
import {Identifier,Organization,CodeableConcept,Address,Patient,HumanName,ContactPoint,Practitioner,
	Specimen,OrderEvent,Collection,Container,DiagnosticOrder,Observation,SampledData,Quantity,Period,
	Range,Ratio,DiagnosticReport,Entry,Bundle} from "./fhirStructure";
//import {fse} from "fs-extra";
//var fse=require("fs-extra");
//import java from 'java';

//var ArrayList = Java.type("java.util.ArrayList");

//var hl7Package= Packages.hapi-fhir-structures-dstu3.org.hl7.fhir.dstu3.model.Practitioner;
//var hl7model = Java.type("org.hl7.fhir.dstu3.model.Practitioner");

//Search for the orgunit with the relatedId in the list
var entitieTrackedMapping=GetTrackedEntitiesMapping();
var patientAttributesMapping= GetPatientAttributesMapping();
var practitionerAttributesMapping= GetPractitionerAttributesMapping();
var specimenAttributesMapping= GetSpecimenAttributesMapping();
var orderAttributesMapping= getOrderAttributesMapping();
var observationAttributesMapping= getObservationAttributesMapping();
var diagnosticReportAttributesMapping= getDiagnosticReportAttributesMapping();
//console.log(entitieTrackedMapping);
function SearchOrgUnitInThelist(idOrgUnit,OrgUnitsList)
{
	var orgUnitFound=null;
	for(var i=0;i<OrgUnitsList.length;i++)
	{
		if(OrgUnitsList[i].id==idOrgUnit)
		{
			orgUnitFound=OrgUnitsList[i];
			break;
		}
		else
		{
			continue;
		}

	}
	return orgUnitFound;
}
function BuildOrganizationHierarchy(OrgUnitsList)
{
	var organizationList=[];
	var baselevel1=1;
	//console.log("Trace: "+OrgUnitsList.length);
	for(var i=0; i<OrgUnitsList.length;i++)
	{
		//Search for the OrgUnit with the lowest level: 1
		var oOrgUnit= OrgUnitsList[i];
		//console.log("Trace: ");
		//console.log(oOrgUnit.level==baselevel1);
		if(oOrgUnit.level==baselevel1 && oOrgUnit.dimensionItemType=="ORGANISATION_UNIT")
		{
			//console.log("Trace: Enter1"+JSON.stringify(oOrgUnit));
			//initialisation
			var org={};
			org= Object.create(Organization);
			org.resourceType="Organization";
			var orgIdentifier={};
			orgIdentifier=Object.create(Identifier);
			//assignment of Identifier
			orgIdentifier.use="official";
			orgIdentifier.type={"text":"DHIS2 Internal Identifier"};
			orgIdentifier.system="DHIS2 ID System";
			orgIdentifier.value=oOrgUnit.id;
			//assignment of type
			var orgUnitCoding=Object.create(CodeableConcept);
			orgUnitCoding.coding=[{"system":"https://www.hl7.org/fhir"}];
			orgUnitCoding.text=oOrgUnit.shortName;
			org.type=orgUnitCoding;
			//assigment of OrgUnit
			org.id=oOrgUnit.id;
			org.meta={"lastUpdated":formatDateInZform (oOrgUnit.lastUpdated)};
			org.identifier=[orgIdentifier];
			org.name=oOrgUnit.name;
			organizationList.push(org);
			if(oOrgUnit.children.length>0)
			{
				//level 2
				//console.log("Trace: Enter2");
				var baselevel2=baselevel1+1;
				for(var j=0; j<oOrgUnit.children.length;j++){
				//initialisation
				var oOrgUnit2=SearchOrgUnitInThelist(oOrgUnit.children[j].id,OrgUnitsList);
				if(oOrgUnit2.level==baselevel2 && oOrgUnit2.dimensionItemType=="ORGANISATION_UNIT")
				{
					//console.log("#####################");
					var org2={};
					org2=Object.create(Organization);
					org2.resourceType="Organization";
					orgIdentifier={};
					orgIdentifier=Object.create(Identifier);
					//assignment of Identifier
					orgIdentifier.use="official";
					orgIdentifier.type={"text":"DHIS2 Internal Identifier"};
					orgIdentifier.system="DHIS2 ID System";
					orgIdentifier.value=oOrgUnit2.id;
					//assignment of type
					orgUnitCoding=Object.create(CodeableConcept);
					orgUnitCoding.coding=[{"system":"https://www.hl7.org/fhir"}];
					orgUnitCoding.text=oOrgUnit.shortName;
					org2.type=orgUnitCoding;
					//assigment of OrgUnit
					org2.id=oOrgUnit2.id;
					org2.meta={"lastUpdated": formatDateInZform(oOrgUnit2.lastUpdated)};
					org2.identifier=[orgIdentifier];
					org2.name=oOrgUnit2.name;
					org2.partOf={"reference":"Organization/"+oOrgUnit.id}
					//org2.partOf=oOrgUnit.id;
					organizationList.push(org2);
					if(oOrgUnit2.children.length>0)
					{
						//level 3
						var baselevel3=baselevel1+2;
						for(var k=0; k<oOrgUnit2.children.length;k++){
						//initialisation
						var oOrgUnit3=SearchOrgUnitInThelist(oOrgUnit2.children[k].id,OrgUnitsList);
						if(oOrgUnit3.level==baselevel3 && oOrgUnit3.dimensionItemType=="ORGANISATION_UNIT")
						{
							var org3={};
							org3=Object.create(Organization);
							org3.resourceType="Organization";
							orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"DHIS2 Internal Identifier"};
							orgIdentifier.system="DHIS2 ID System";
							orgIdentifier.value=oOrgUnit3.id;
							//assignment of type
							orgUnitCoding=Object.create(CodeableConcept);
							orgUnitCoding.coding=[{"system":"https://www.hl7.org/fhir"}];
							orgUnitCoding.text=oOrgUnit.shortName;
							org3.type=orgUnitCoding;
							//assigment of OrgUnit
							org3.id=oOrgUnit3.id;
							org3.meta={"lastUpdated":formatDateInZform(oOrgUnit3.lastUpdated)};
							org3.identifier=[orgIdentifier];
							org3.name=oOrgUnit3.name;
							//org3.partOf=oOrgUnit2.id;
							org3.partOf={"reference":"Organization/"+oOrgUnit2.id}
							organizationList.push(org3);
							if(oOrgUnit3.children.length>0)
							{
								//level 4
								var baselevel4=baselevel1+3;
								for(var l=0; l<oOrgUnit3.children.length;l++){
								//initialisation
								//var oOrgUnit4= oOrgUnit3.children[l];
								var oOrgUnit4=SearchOrgUnitInThelist(oOrgUnit3.children[l].id,OrgUnitsList);
								if(oOrgUnit4.level==baselevel4 && oOrgUnit4.dimensionItemType=="ORGANISATION_UNIT")
								{
									var org4={};
									org4=Object.create(Organization);
									org4.resourceType="Organization";
									orgIdentifier={};
									orgIdentifier=Object.create(Identifier);
									//assignment of Identifier
									orgIdentifier.use="official";
									orgIdentifier.type={"text":"DHIS2 Internal Identifier"};
									orgIdentifier.system="DHIS2 ID System";
									orgIdentifier.value=oOrgUnit4.id;
									//assignment of type
									orgUnitCoding=Object.create(CodeableConcept);
									orgUnitCoding.coding=[{"system":"https://www.hl7.org/fhir"}];
									orgUnitCoding.text=oOrgUnit.shortName;
									org4.type=orgUnitCoding;
									//assigment of OrgUnit
									org4.id=oOrgUnit4.id;
									org4.meta={"lastUpdated":formatDateInZform(oOrgUnit4.lastUpdated)};
									org4.identifier=[orgIdentifier];
									org4.name=oOrgUnit4.name;
									//org4.partOf=oOrgUnit3.id;
									org4.partOf={"reference":"Organization/"+oOrgUnit3.id}
									organizationList.push(org4);
									//console.log("org4: "+JSON.stringify(organizationList[3]));
								}
								}
								//console.log("org3: "+JSON.stringify(organizationList[2]));
							}
						}
						
						
						}
					}
				}
				else{
					continue;
					}
				
				}
			}
			
		}
		else
		{
			continue;
		}
		//if(oOrgUnit.)
	}
	
	return organizationList;
}

function formatDateInZform(originalDate)
{
	var formatedDate="";
	var dateComponants=[];
	var dateComponants=originalDate.split("+");
	if(dateComponants.length>0)
	{
		formatedDate=dateComponants[0];//+"+00:00"
		//formatedDate+="+00:00";
		if(formatedDate.includes("Z")||formatedDate.includes("z"))
		{
			formatedDate=formatedDate
		}
		else
		{
			formatedDate+="+00:00";
		}
	}
	return formatedDate;
}
function getAssociatedGenderValueSet(_genderValue)
{
	var valueSet="";
	if(_genderValue.toLowerCase()=="m"|| _genderValue.toLowerCase()=="male")
	{
		valueSet="male";
	}
	else if(_genderValue.toLowerCase()=="f"|| _genderValue.toLowerCase()=="female")
	{
		valueSet="female";
	}
	return valueSet;
}

function GetAssociatedFhirResource(oTrackedEntity)
	{
		const entityCode=oTrackedEntity.trackedEntity;
		var entityObject=null;
		switch(entityCode)
		{
			case entitieTrackedMapping.patient:
				//extract patient attribute
				//console.log(oTrackedEntity);
				var oPatient={};
				oPatient= Object.create(Patient);
				oPatient.resourceType="Patient";
				oPatient.id=oTrackedEntity.trackedEntityInstance;
				oPatient.meta={"lastUpdated": formatDateInZform(oTrackedEntity.lastUpdated)};
				oPatient.active=true;
				//oPatient.managingOrganization=oTrackedEntity.orgUnit;
				oPatient.managingOrganization={"reference":"Organization/"+oTrackedEntity.orgUnit}
				var oName={};
				oName= Object.create(HumanName);
				oName.resourceType="HumanName";
				oName.use="official";
				//
				var oContact={};
				oContact= Object.create(ContactPoint);
				oContact.resourceType="ContactPoint";
				var oAddress={};
				oAddress= Object.create(Address);
				oAddress.resourceType="Address";
				
				var listOfIdentifier=[];
				var firstEntry=false;
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case patientAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"Medical Record Number"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case patientAttributesMapping.name_family:
							oName.family=oTrackedEntity.attributes[i].value;
							oName.text+=oTrackedEntity.attributes[i].value+" ";
						break;
						case patientAttributesMapping.name_given:
							oName.given=oTrackedEntity.attributes[i].value;
							oName.text+=oTrackedEntity.attributes[i].value+" ";
						break;
						case patientAttributesMapping.telecom_phone:
							oContact.system="phone";
							oContact.value=oTrackedEntity.attributes[i].value;
							oContact.use="home";
							oContact.rank="1";
							oPatient.telecom=[oContact];
						break;
						case patientAttributesMapping.telecom_email:
							oContact.system="email";
							oContact.value=oTrackedEntity.attributes[i].value;
							oContact.use="home";
							oContact.rank="2";
							oPatient.telecom=[oContact];
						break;
						case patientAttributesMapping.gender:
							if(getAssociatedGenderValueSet(oTrackedEntity.attributes[i].value)!="")
							{
								oPatient.gender=getAssociatedGenderValueSet(oTrackedEntity.attributes[i].value);
							}
						break;
						case patientAttributesMapping.birthDate:
							oPatient.birthDate=oTrackedEntity.attributes[i].value;
						break;
						case patientAttributesMapping.address:
							oAddress.text=oTrackedEntity.attributes[i].value
							oPatient.address=[oAddress];
						break;
						
					}
				}//fin for
				oPatient.identifier=listOfIdentifier;
				//oPatient.active=true;
				oPatient.deceasedBoolean=false;
				oPatient.name=[oName];
				entityObject=oPatient;
				break;
				
			case entitieTrackedMapping.provider:
				//extract patient attribute
				var oPractitioner={};
				oPractitioner= Object.create(Practitioner);
				oPractitioner.resourceType="Practitioner";
				oPractitioner.id=oTrackedEntity.trackedEntityInstance;
				oPractitioner.meta={"lastUpdated": formatDateInZform(oTrackedEntity.lastUpdated)};
				oPractitioner.active=true;
				oPractitioner.practitionerRole=[{"managingOrganization": {"reference":"Organization/"+oTrackedEntity.orgUnit}}];
				var oName={};
				oName= Object.create(HumanName);
				oName.resourceType="HumanName";
				oName.use="official";
				//
				var oContact={};
				oContact= Object.create(ContactPoint);
				oContact.resourceType="ContactPoint";
				
				var listOfIdentifier=[];
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case practitionerAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"License Number"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case practitionerAttributesMapping.name_family:
							oName.family=oTrackedEntity.attributes[i].value;
							oName.text+=oTrackedEntity.attributes[i].value+" ";
						break;
						case practitionerAttributesMapping.name_given:
							oName.given=oTrackedEntity.attributes[i].value;
							oName.text+=oTrackedEntity.attributes[i].value+" ";
						break;
						case practitionerAttributesMapping.telecom_phone:
							oContact.system="phone";
							oContact.value=oTrackedEntity.attributes[i].value;
							oContact.use="home";
							oContact.rank="1";
							oPractitioner.telecom=[oContact];
						break;
						case practitionerAttributesMapping.telecom_email:
							oContact.system="email";
							oContact.value=oTrackedEntity.attributes[i].value;
							oContact.use="home";
							oContact.rank="2";
							oPractitioner.telecom=[oContact];
						break;
						case practitionerAttributesMapping.gender:
							if(getAssociatedGenderValueSet(oTrackedEntity.attributes[i].value)!="")
							{
								oPractitioner.gender=getAssociatedGenderValueSet(oTrackedEntity.attributes[i].value);
							}
						break;
					}
					
				}
				oPractitioner.identifier=listOfIdentifier;
				oPractitioner.name=oName;
				entityObject=oPractitioner;
				break;
			case entitieTrackedMapping.specimen:
				var oSpecimen={};
				oSpecimen= Object.create(Specimen);
				oSpecimen.resourceType="Specimen";
				oSpecimen.id=oTrackedEntity.trackedEntityInstance;
				oSpecimen.meta={"lastUpdated": formatDateInZform(oTrackedEntity.lastUpdated)};
				oSpecimen.active=true;
				var listOfIdentifier=[];
				var listOfTraitment=[];
				var oConceptProcedure={};
				oConceptProcedure= Object.create(CodeableConcept);
				var oConceptCollectionMethod={};
				oConceptCollectionMethod= Object.create(CodeableConcept);
				var oConceptBodySite={};
				oConceptBodySite= Object.create(CodeableConcept);
				//oConceptProcedure.
				var oTraitment={
					"description":"",
					"procedure":{}
					};
				var oCollection={};
				oCollection= Object.create(Collection);
				var oContainer={};
				oContainer= Object.create(Container);
				
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case specimenAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"Specimen Identification"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case specimenAttributesMapping.status:
							oSpecimen.status=oTrackedEntity.attributes[i].value;
							oSpecimen.status="available";
							break;
						case specimenAttributesMapping.subject:
							oSpecimen.subject={"reference":"Patient/"+oTrackedEntity.attributes[i].value};
							break;
						case specimenAttributesMapping.accession:
							var oIdentifier={};
							oIdentifier=Object.create(Identifier);
							oIdentifier.use="official";
							oIdentifier.type={"text":"Lab Identification"};
							oIdentifier.system="http://hl7.org/fhir";
							oIdentifier.value=oTrackedEntity.attributes[i].value;
							oSpecimen.accession=oIdentifier;
							break;
						case specimenAttributesMapping.receivedTime:
							oSpecimen.receivedTime=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.collectedDateTime:
							oCollection.collectedDateTime=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.collection_quantity_unit:
							oCollection.quantity.unit=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.collection_quantity_value:
							oCollection.quantity.value=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.collection_method:
							oConceptCollectionMethod.text=oTrackedEntity.attributes[i].value;
							oCollection.method=oConceptCollectionMethod;
							break;
						case specimenAttributesMapping.collection_bodySite:
							oConceptBodySite.text=oTrackedEntity.attributes[i].value;
							oCollection.bodySite=oConceptBodySite;
							break;
						case specimenAttributesMapping.container_capacity_unit:
							oContainer.capacity.unit=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.container_capacity_value:
							oContainer.capacity.value=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.container_description:
							oContainer.description=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.traitment_description:
							oTraitment.description=oTrackedEntity.attributes[i].value;
							break;
						case specimenAttributesMapping.traitment_procedure:
							oConceptProcedure.text=oTrackedEntity.attributes[i].value;
							oTraitment.procedure=oConceptProcedure;
							break;
						case specimenAttributesMapping.container_identifier:
							var oIdentifier={};
							oIdentifier=Object.create(Identifier);
							//assignment of Identifier
							oIdentifier.use="official";
							oIdentifier.type={"text":"Container Identification"};
							oIdentifier.system="http://hl7.org/fhir";
							oIdentifier.value=oTrackedEntity.attributes[i].value;
							oContainer.Identifier=[oIdentifier];
							break;
					}
				}
				oSpecimen.Identifier=listOfIdentifier;
				//oSpecimen.status="available";
				oSpecimen.collection=oCollection;
				oSpecimen.treatment=[oTraitment];
				oSpecimen.Container=[oContainer];
				entityObject=oSpecimen;
				break;
			case entitieTrackedMapping.order:
				var oOrder={};
				oOrder= Object.create(DiagnosticOrder);
				oOrder.resourceType="DiagnosticOrder";
				oOrder.id=oTrackedEntity.trackedEntityInstance;
				oOrder.meta={"lastUpdated": formatDateInZform(oTrackedEntity.lastUpdated)};
				//oOrder.
				var listOfIdentifier=[];
				var oOrderEvent={};
				oOrderEvent= Object.create(OrderEvent);
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case orderAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"Order Identification"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case orderAttributesMapping.subject:
							oOrder.subject={"reference":"Patient/"+oTrackedEntity.attributes[i].value};
							break;
						case orderAttributesMapping.orderer:
							oOrder.orderer={"reference":"Practitioner/"+oTrackedEntity.attributes[i].value};
							break;
						case orderAttributesMapping.encounter:
							oOrder.encounter={"reference":"Encounter/"+oTrackedEntity.attributes[i].value};
							break;
						case orderAttributesMapping.reason:
							var oConcept={};
							oConcept= Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oOrder.reason=[oConcept];
							break;
						case orderAttributesMapping.supportingInformation:
							oOrder.supportingInformation=[oTrackedEntity.attributes[i].value];
							break;
						case orderAttributesMapping.specimen:
							oOrder.specimen={"reference":"Specimen/"+oTrackedEntity.attributes[i].value};
							break;
						case orderAttributesMapping.status:
							oOrder.status=oTrackedEntity.attributes[i].value;
							break;
						case orderAttributesMapping.priority:
							oOrder.priority=oTrackedEntity.attributes[i].value;
							break;
						case orderAttributesMapping.orderEvent_dateTime:
							oOrderEvent.dateTime=oTrackedEntity.attributes[i].value;
							break;
						case orderAttributesMapping.orderEvent_status:
							oOrderEvent.status=oTrackedEntity.attributes[i].value;
							break;
						case orderAttributesMapping.OrderEventDescription:
							var oConcept={};
							oConcept= Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oOrderEvent.description=oConcept;
							break;
						case orderAttributesMapping.item:
							var oConcept={};
							oConcept= Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oOrder.item=[oConcept];
							break;
						case orderAttributesMapping.note:
							oOrder.note={"text":oTrackedEntity.attributes[i].value};
							break;
						
					}
					
				}
				oOrder.Identifier=listOfIdentifier;
				oOrder.event=[oOrderEvent];
				entityObject=oOrder;
				break;
			case entitieTrackedMapping.observation:
				var oObservation={};
				oObservation= Object.create(Observation);
				oObservation.resourceType="Observation";
				oObservation.id=oTrackedEntity.trackedEntityInstance;
				oObservation.meta={"lastUpdated": formatDateInZform(oTrackedEntity.lastUpdated)};
				//oOrder.
				var listOfIdentifier=[];
				var oSampledData={};
				oSampledData= Object.create(SampledData);
				var oPeriodEffective={};
				oPeriodEffective= Object.create(Period);
				var oPeriodResult={};
				oPeriodResult= Object.create(Period);
				var oValueQuantity={};
				oValueQuantity= Object.create(Quantity);
				var oOriginQuantity={};
				oOriginQuantity= Object.create(Quantity);
				var oObservationRange={};
				oObservationRange=Object.create(Range);
				var oObservationRatio={};
				oObservationRatio=Object.create(Ratio);
				var oBodySiteConcept={};
				oBodySiteConcept=Object.create(CodeableConcept);
				var oAbsentRaisonConcept={};
				oAbsentRaisonConcept=Object.create(CodeableConcept);
				
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case observationAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"Observation Identification"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case observationAttributesMapping.status:
							oObservation.status=oTrackedEntity.attributes[i].value;
							break;
						case observationAttributesMapping.category:
							var  oConcept={};
							oConcept= Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oObservation.category=oConcept;
							break;
						case observationAttributesMapping.code:
							var  oConcept={};
							oConcept= Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oObservation.code=oConcept;
							break;
						case observationAttributesMapping.subject:
							oObservation.subject={"reference":"Patient/"+oTrackedEntity.attributes[i].value};
							break;
						case observationAttributesMapping.encounter:
							oObservation.encounter={"reference":"Encounter/"+oTrackedEntity.attributes[i].value};
							break;
						case observationAttributesMapping.effectiveDateTime:
							oObservation.effectiveDateTime=oTrackedEntity.attributes[i].value;
							break;
						case observationAttributesMapping.effectivePeriod_dateSup:
							oPeriodEffective.end= formatDateInZform(oTrackedEntity.attributes[i].value);
							break;
						case observationAttributesMapping.effectivePeriod_dateInf:
							oPeriodEffective.start=formatDateInZform(oTrackedEntity.attributes[i].value);
							break;
						case observationAttributesMapping.issued:
							oObservation.issued=oTrackedEntity.attributes[i].value;
							break;
						case observationAttributesMapping.performer:
								oObservation.performer=[{"reference":"Practitioner/"+oTrackedEntity.attributes[i].value}];
							break;
						case observationAttributesMapping.valueQuantity_unit:
								oValueQuantity.unit=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueQuantity_value:
								oValueQuantity.value=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueCodeableConcept:
								var  oConcept={};
								oConcept= Object.create(CodeableConcept);
								oConcept.text=oTrackedEntity.attributes[i].value;
								oObservation.valueCodeableConcept=oConcept;
								break;
						case observationAttributesMapping.valueString:
								oObservation.valueString=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueRange_sup:
								var rangeQuantity=Object.create(Quantity);
								rangeQuantity.value=oTrackedEntity.attributes[i].value;
								oObservationRange.high=rangeQuantity;
								break;
						case observationAttributesMapping.valueRange_Inf:
								var rangeQuantity=Object.create(Quantity);
								rangeQuantity.value=oTrackedEntity.attributes[i].value;
								oObservationRange.low=rangeQuantity;
								break;
						case observationAttributesMapping.valueRatio_num:
								oObservationRatio.numerator=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueRatio_denom:
								oObservationRatio.denominator=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_origin:
								oOriginQuantity.value=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_period:
								oSampledData.period=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_factor:
								oSampledData.factor=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_lowerLimit:
								oSampledData.lowerLimit=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_upperLimit:
								oSampledData.upperLimit=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_dimensions:
								oSampledData.dimensions=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueSampledData_data:
								oSampledData.data=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueTime:
								oObservation.valueTime=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valueDateTime:
								oObservation.valueDateTime=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valuePeriod_start:
								oPeriodResult.start=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.valuePeriod_end:
								oPeriodResult.end=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.dataAbsentReason:
								oAbsentRaisonConcept.text=oTrackedEntity.attributes[i].value;
								oObservation.dataAbsentReason=oAbsentRaisonConcept;
								break;
						case observationAttributesMapping.interpretation:
								var  oConcept={};
								oConcept= Object.create(CodeableConcept);
								oConcept.text=oTrackedEntity.attributes[i].value;
								oObservation.interpretation=oConcept;
								break;
						case observationAttributesMapping.comments:
								oObservation.comments=oTrackedEntity.attributes[i].value;
								break;
						case observationAttributesMapping.bodySite:
								oBodySiteConcept.text=oTrackedEntity.attributes[i].value;
								oObservation.bodySite=oBodySiteConcept;
								break;
						case observationAttributesMapping.method:
								var  oConcept={};
								oConcept= Object.create(CodeableConcept);
								oConcept.text=oTrackedEntity.attributes[i].value;
								oObservation.method=oConcept;
								break;
						case observationAttributesMapping.specimen:
								oObservation.specimen={"reference":"Specimen/"+oTrackedEntity.attributes[i].value};
								break;
						case observationAttributesMapping.device:
								oObservation.device={"reference":"Device/"+oTrackedEntity.attributes[i].value};
								break;
						case observationAttributesMapping.referenceRange:
								oObservation.referenceRange=[];
								break;
						case observationAttributesMapping.related:
								oObservation.related=[];
								break;
						case observationAttributesMapping.component:
								oObservation.component=[];
								break;
						}
					
				}
					oSampledData.origin=oOriginQuantity;
					oObservation.valueSampledData=oSampledData;
					oObservation.identifier=listOfIdentifier;
					oObservation.effectivePeriod=oPeriodEffective;
					oObservation.valueQuantity=oValueQuantity;
					oObservation.valueRange=oObservationRange;
					//checkIfAsProperties(oObservationRange);
					oObservation.valueRatio=oObservationRatio;
					oObservation.valuePeriod=oPeriodResult;
					
					entityObject=oObservation;
				break;
			case entitieTrackedMapping.diagnosticReport:
				var oDiagnosticReport={};
				oDiagnosticReport= Object.create(DiagnosticReport);
				oDiagnosticReport.resourceType="DiagnosticReport";
				oDiagnosticReport.id=oTrackedEntity.trackedEntityInstance;
				oDiagnosticReport.meta={"lastUpdated":formatDateInZform(oTrackedEntity.lastUpdated)};
				var listOfIdentifier=[];
				var oEffectivePeriod={};
				oEffectivePeriod= Object.create(Period);
				for(var i=0;i<oTrackedEntity.attributes.length;i++)
				{
					var oAttribute=oTrackedEntity.attributes[i].displayName;
					switch(oAttribute)
					{
						case diagnosticReportAttributesMapping.identifier:
							var orgIdentifier={};
							orgIdentifier=Object.create(Identifier);
							//assignment of Identifier
							orgIdentifier.use="official";
							orgIdentifier.type={"text":"DiagnosticReport Identification"};
							orgIdentifier.system="http://hl7.org/fhir/";
							orgIdentifier.value=oTrackedEntity.attributes[i].value;
							listOfIdentifier.push(orgIdentifier);
							break;
						case diagnosticReportAttributesMapping.status:
							oDiagnosticReport.status=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.category:
							var oConcept={};
							oConcept=Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oDiagnosticReport.category=oConcept;
							break;
						case diagnosticReportAttributesMapping.code:
							var oConcept={};
							oConcept=Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oDiagnosticReport.code=oConcept;
							break;
						case diagnosticReportAttributesMapping.subject:
							oDiagnosticReport.subject={"reference":"Patient/"+oTrackedEntity.attributes[i].value};
							break;
						case diagnosticReportAttributesMapping.encounter:
							oDiagnosticReport.encounter={"reference":"Encounter/"+oTrackedEntity.attributes[i].value};
							break;
						case diagnosticReportAttributesMapping.effectiveDateTime:
							oDiagnosticReport.effectiveDateTime=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.effectivePeriod_start:
							oEffectivePeriod.start=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.effectivePeriod_end:
							oEffectivePeriod.end=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.issued:
							oDiagnosticReport.issued=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.performer:
							oDiagnosticReport.performer={"reference":"Practitioner/"+oTrackedEntity.attributes[i].value};
							break;
						case diagnosticReportAttributesMapping.request:
							oDiagnosticReport.request=[{"reference":"DiagnosticOrder/"+oTrackedEntity.attributes[i].value}];
							break;
						case diagnosticReportAttributesMapping.specimen:
							oDiagnosticReport.specimen=[{"reference":"Specimen/"+oTrackedEntity.attributes[i].value}];
							break;
						case diagnosticReportAttributesMapping.result:
							oDiagnosticReport.result=[{"reference":"Observation/"+oTrackedEntity.attributes[i].value}];
							break;
						case diagnosticReportAttributesMapping.imagingStudy:
							oDiagnosticReport.imagingStudy=[];
							break;
						case diagnosticReportAttributesMapping.image:
							oDiagnosticReport.image=[];
							break;
						case diagnosticReportAttributesMapping.conclusion:
							oDiagnosticReport.conclusion=oTrackedEntity.attributes[i].value;
							break;
						case diagnosticReportAttributesMapping.codedDiagnosis:
							var oConcept={};
							oConcept=Object.create(CodeableConcept);
							oConcept.text=oTrackedEntity.attributes[i].value;
							oDiagnosticReport.codedDiagnosis=[oConcept];
							break;
						case diagnosticReportAttributesMapping.presentedForm:
							oDiagnosticReport.presentedForm=[];
							break;
					}
					
				}
				oDiagnosticReport.identifier=listOfIdentifier;
				oDiagnosticReport.effectivePeriod=oEffectivePeriod;
				entityObject=oDiagnosticReport;
				break;
		}
		return entityObject;
		
	}
function BuildBundleResponse(listOrganisation,listPatient,listPractitioner,listSpecimen,listDiagnosticOrder,listObservation,listDiagnosticReport)
{
	var oBundle={};
	oBundle= Object.create(Bundle);
	oBundle.resourceType="Bundle";
	//Bundle ID, Build the BundleId : Id Of one of the organization +totalnumber of the resource within the bundle
	var totalNumberOfResource=listOrganisation.length+listPatient.length+listPractitioner.length;
	totalNumberOfResource+=listSpecimen.length+listDiagnosticOrder.length+listObservation.length+listDiagnosticReport.length;
	var idBundle=listOrganisation[0].id+totalNumberOfResource;
	oBundle.id=idBundle;
	oBundle.meta={"lastUpdated":new Date().toJSON()};
	oBundle.type="collection";
	oBundle.total=totalNumberOfResource;
	var listOfEntries=[];
	for (var i=0;i<listOrganisation.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listOrganisation[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
		
	}
	for (var i=0;i<listPractitioner.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listPractitioner[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
		
	}
	for (var i=0;i<listPatient.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listPatient[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
	}
	for (var i=0;i<listSpecimen.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listSpecimen[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
	}
	for (var i=0;i<listDiagnosticOrder.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listDiagnosticOrder[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
	}
	for (var i=0;i<listObservation.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listObservation[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
	}
	for (var i=0;i<listDiagnosticReport.length;i++)
	{
		var oEntry=Object.create(Entry);
		oEntry.resource=listDiagnosticReport[i];
		oEntry.search.mode="match";
		listOfEntries.push(oEntry);
	}
	oBundle.entry=listOfEntries;
	return oBundle;
}
//console.log('----------------call-------------------');


GetAllOrganisationUnits(function(listOrgUnits)
{
	
	
	var fhirOrganizationlist=[];
	var fhirPatientList=[];
	var fhirPractitionerList=[];
	var fhirSpecimenList=[];
	var fhirDiagnosticOrderList=[];
	var fhirObservationList=[];
	var fhirDiagnosticReport=[];
	var fhirListOfResource=[];
	var reflist=[];
	//transform to list of orgunit to a list of Fhir Organization resource
	fhirOrganizationlist=BuildOrganizationHierarchy(listOrgUnits.organisationUnits);
	var listOfOrgUnitId=[]
	for(var i=0; i<fhirOrganizationlist.length;i++)
	{
		listOfOrgUnitId.push(fhirOrganizationlist[i].id);
	}
	GetTrackedEntityInstancesFromOrgunitList(listOfOrgUnitId,function(listTrackedEntities){
				
				//console.log(listTrackedEntities);
				if(listTrackedEntities.trackedEntityInstances.length>0)
				{
					//console.log("orgunit: "+id);
					//console.log(listTrackedEntities);
					for(var j=0;j<listTrackedEntities.trackedEntityInstances.length;j++)
					{
						//var jsonText=JSON.stringify(listTrackedEntities.trackedEntityInstances[j]);
						//var oEntity=JSON.parse(jsonText);
						var entityObject=GetAssociatedFhirResource(listTrackedEntities.trackedEntityInstances[j]);
						if(entityObject!=null)
						{
							if (entityObject.resourceType =="Patient")
							{
								fhirPatientList.push(entityObject);
								continue;
								//console.log(entityObject);
							}
							//console.log(fhirPatientList);
							
							else if (entityObject.resourceType =="Practitioner")
							{
								fhirPractitionerList.push(entityObject);
								continue;
							}
							else if (entityObject.resourceType =="Specimen")
							{
								fhirSpecimenList.push(entityObject);
								continue;
							}
							else if (entityObject.resourceType =="DiagnosticOrder")
							{
								//fhirSpecimenList.push(entityObject);
								fhirDiagnosticOrderList.push(entityObject);
								continue;
							}
							else if (entityObject.resourceType =="Observation")
							{
								//fhirSpecimenList.push(entityObject);
								fhirObservationList.push(entityObject);
								continue;
							}
							else if (entityObject.resourceType =="DiagnosticReport")
							{
								//fhirSpecimenList.push(entityObject);
								fhirDiagnosticReport.push(entityObject);
								continue;
							}
						}
						
					}//for EntityInstances
					//console.log(JSON.stringify(fhirDiagnosticReport));
					//extract Patient
					//console.log("orgunit: "+id);
					//reflist=fhirPatientList;
				}
				//Add All the resource in the table
			var oBundle={};
			oBundle=BuildBundleResponse(fhirOrganizationlist,fhirPatientList,fhirPractitionerList,fhirSpecimenList,fhirDiagnosticOrderList,
			fhirObservationList,fhirDiagnosticReport);
			//console.log(oBundle);
			//document.body.innerHTML = JSON.stringify(oBundle);
			document.write(JSON.stringify(oBundle));
			//document.body. = JSON.stringify(oBundle);
			});//GetTrackedEntityInstances
});


//WriteJsonFile("{'salut':'Bonjour'}");
