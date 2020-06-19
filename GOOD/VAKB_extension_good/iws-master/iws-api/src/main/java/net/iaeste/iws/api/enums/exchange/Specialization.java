/*
 * Licensed to IAESTE A.s.b.l. (IAESTE) under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The Authors
 * (See the AUTHORS file distributed with this work) licenses this file to
 * You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a
 * copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.iaeste.iws.api.enums.exchange;

import net.iaeste.iws.api.enums.Descriptable;

import javax.xml.bind.annotation.XmlType;

/**
 * <p>Contains all predefined Specializations. Note, these values are mapped
 * into the database entities, and thus stored there. If a value is changed. The
 * reverse mapping (valueOf) will fail. It is therefore *not* allowed to change
 * the spelling of an existing entry, without verification that it is not used
 * in the database.</p>
 *
 * <p>If it is used in the database, then it can also be that it is used
 * elsewhere, i.e. by third-party systems - and if so, then the entry may no
 * longer be touched.</p>
 *
 * <p>As third-party systems may also use the enum, changes to the list may only
 * be added to the end, since the ordinal values of records may otherwise
 * change, again causing various problems. It is *not* allowed to delete any
 * records from this list.</p>
 *
 * <p>Dealing with deprecation of the records is still a pending topic, but as
 * it very rarely happens, this is more an academic topic than a practical
 * matter.</p>
 *
 * <p>The initial listing is generated from the SID provided Excel Sheet, see
 * Trac ticket #416.</p>
 *
 * @author  Matej Kosco / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@XmlType(name = "specialization")
public enum Specialization implements Descriptable<Specialization> {

    ADMINISTRATION("Administration"),
    AEROSPACE_ENGINEERING("Aerospace engineering"),
    AGRIBUSINESS("Agribusiness"),
    AGRONOMY_AGRO_ECOLOGY("Agronomy/Agro ecology"),
    ANALYTICAL_CHEMISTRY("Analytical Chemistry"),
    ANIMAL_BEHAVIOUR("Animal Behaviour"),
    ANIMAL_BIOLOGY("Animal Biology"),
    ANIMAL_HUSBANDRY("Animal Husbandry"),
    ANIMAL_SCIENCE("Animal Science"),
    APPLIED_MATHEMATICS("Applied Mathematics"),
    APPLIED_STATISTICS("Applied Statistics"),
    AQUACULTURE_AQUAFARMING("Aquaculture/Aquafarming"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    ASTROPHYSICS("Astrophysics"),
    AUDIO_TECHNOLOGY("Audio Technology"),
    AUTOMATION("Automation"),
    AUTOMOTIVE_ENGINEERING("Automotive Engineering"),
    AVIONICS("Avionics"),
    BIOCHEMISTRY("Biochemistry"),
    BIODIVERSITY("Biodiversity"),
    BIOENGINEERING("Bioengineering"),
    BIOINFORMATICS("Bioinformatics"),
    BIOINSTRUMENTATION("Bioinstrumentation"),
    BIOMATERIALS("Biomaterials"),
    BIOMECHANICS("Biomechanics"),
    BIONICS("Bionics"),
    BIOPHYSICS("Biophysics"),
    BIOVETERINARY_SCIENCE("Bioveterinary Science"),
    BOTANY("Botany"),
    BREWING("Brewing"),
    BUSINESS_INFORMATICS("Business Informatics"),
    BUSINESS_INTELLIGENCE("Business Intelligence"),
    CALCULUS("Calculus"),
    CELL_BIOLOGY("Cell Biology"),
    CHEMICAL_ENGINEERING("Chemical Engineering"),
    COMMERCE("Commerce"),
    COMMUNICATIONS_TECHNOLOGY("Communications Technology"),
    COMPUTATIONAL_PHYSICS("Computational Physics"),
    COMPUTER_ENGINEERING("Computer Engineering"),
    COMPUTER_SCIENCE("Computer Science"),
    CONSTRUCTION("Construction"),
    CYBERNETICS("Cybernetics"),
    DAIRY_FARMING("Dairy Farming"),
    DATABASE_ADMINISTRATION("Database Administration"),
    DESIGN("Design"),
    DEVELOPMENT_STUDIES("Development studies"),
    DIGITAL_MEDIA("Digital Media"),
    DISCRETE_MATHEMATICS("Discrete Mathematics"),
    DRAWING("Drawing"),
    EARTH_SCIENCE("Earth Science"),
    // Duplicate from the list of Specializations
    //EARTH_SCIENCES("Earth Sciences"),
    ECOLOGY("Ecology"),
    ECONOMIC_GEOGRAPHY("Economic Geography"),
    ECONOMICS("Economics"),
    ELECTRIC_MOBILITY("Electric Mobility"),
    ELECTRONICS("Electronics"),
    ENERGY_AND_PROCESS_ENGINEERING("Energy and Process Engineering"),
    ENERGY_SYSTEMS_PLANNING("Energy Systems Planning"),
    ENTERPRISE_ENGINEERING("Enterprise Engineering"),
    ENTOMOLOGY("Entomology"),
    ETHOLOGY("Ethology"),
    FACILITY_SURVEYING("Facility Surveying"),
    FINANCIAL_MATHEMATICS("Financial Mathematics"),
    FISHERIES("Fisheries"),
    FLUID_MECHANICS("Fluid Mechanics"),
    FOOD_CHEMISTRY("Food Chemistry"),
    FOOD_QUALITY_AND_SAFETY("Food Quality and Safety"),
    FOOD_TECHNOLOGY("Food Technology"),
    FOREST_ENGINEERING("Forest Engineering"),
    FORESTRY("Forestry"),
    GENETICS("Genetics"),
    GEOCHEMISTRY("Geochemistry"),
    GEODESY_AND_CARTOGRAPHY("Geodesy and Cartography"),
    GEOECOLOGY("Geoecology"),
    GEOGRAPHY("Geography"),
    GEOLOGY("Geology"),
    GEOMATICS("Geomatics"),
    GEOMETRY("Geometry"),
    GEOPHYSICS("Geophysics"),
    GEOTECHNOLOGY("Geotechnology"),
    GIS("GIS"),
    GRAPHIC_DESIGN("Graphic Design"),
    GRAPHICS_AND_PRINTING("Graphics & printing"),
    HORTICULTURE("Horticulture"),
    HOTEL_MANAGEMENT("Hotel Management"),
    HYDRAULICS("Hydraulics"),
    IMMUNOLOGY("Immunology"),
    INDUSTRIAL_DESIGN("Industrial Design"),
    INDUSTRIAL_ECONOMICS("Industrial Economics"),
    INDUSTRIAL_LOGISTICS("Industrial Logistics"),
    INFORMATICS("Informatics"),
    INFORMATION_SYSTEMS("Information Systems"),
    INFORMATION_TECHNOLOGY("Information Technology"),
    INNOVATION_MANAGEMENT("Innovation Management"),
    INNOVATION_STRATEGY("Innovation Strategy"),
    INORGANIC_CHEMISTRY("Inorganic Chemistry"),
    INSTRUMENTATION("Instrumentation"),
    INTERIOR_DESIGN("Interior Design"),
    LAND_SURVEYING("Land Surveying"),
    LANDSCAPE_ARCHITECTURE("Landscape Architecture"),
    LANGUAGE("Language"),
    LIMNOLOGY("Limnology"),
    LOGISTICS("Logistics"),
    MACHINE_TECHNOLOGY("Machine Technology"),
    MARINE_AND_OFFSHORE_ENGINEERING("Marine and Offshore Engineering"),
    MARINE_BIOLOGY("Marine Biology"),
    MARINE_GEOLOGY("Marine Geology"),
    MARINE_TECHNOLOGY("Marine Technology"),
    MARKETING("Marketing"),
    MATERIAL_TECHNOLOGY("Material Technology"),
    MECHATRONICS("Mechatronics"),
    MEDIA_TECHNOLOGY("Media Technology"),
    MEDICAL_ENGINEERING("Medical engineering"),
    MEDICAL_PHYSICS("Medical Physics"),
    METALLURGY("Metallurgy"),
    MICROBIOLOGY("Microbiology"),
    MILLING("Milling"),
    MINERAL_PROCESSING("Mineral Processing"),
    MINERALOGY("Mineralogy"),
    MINING("Mining"),
    MOLECULAR_BIOLOGY("Molecular Biology"),
    MYCOLOGY("Mycology"),
    NANOBIOTECHNOLOGY("Nanobiotechnology"),
    NANOELECTRONICS("Nanoelectronics"),
    NANOMATERIALS("Nanomaterials"),
    NANOTECHNOLOGY("Nanotechnology"),
    NATURAL_RESOURCE_MANAGEMENT("Natural Resource Management"),
    NAVAL_ENGINEERING("Naval Engineering"),
    NETWORKS("Networks"),
    NEUROSCIENCE("Neuroscience"),
    NUCLEAR_ENERGY_ENGINEERING("Nuclear Energy Engineering"),
    NUTRITIONAL_SCIENCE("Nutritional Science"),
    OCEANOGRAPHY("Oceanography"),
    OECOTROPHOLOGY("Oecotrophology"),
    OPTICS("Optics"),
    OPTIMIZATION("Optimization"),
    ORGANIC_CHEMISTRY("Organic Chemistry"),
    PETROLEUM_ENGINEERING("Petroleum Engineering"),
    PHARMACY("Pharmacy"),
    PHASE_TRANSITIONS("Phase Transitions"),
    PHOTONICS("Photonics"),
    PHYSICAL_CHEMISTRY("Physical Chemistry"),
    PLANT_BIOLOGY("Plant Biology"),
    PLANT_SCIENCE("Plant science"),
    POLYMER_ENGINEERING("Polymer Engineering"),
    POLYMER_SCIENCE("Polymer Science"),
    PORT_LOGISTICS("Port Logistics"),
    POWER_ENGINEERING("Power Engineering"),
    POWER_GENERATION("Power Generation"),
    PROCESS_ENGINEERING("Process Engineering"),
    PRODUCT_DESIGN("Product Design"),
    PRODUCT_DEVELOPMENT("Product Development"),
    PRODUCTION_MANAGEMENT("Production Management"),
    PROJECT_MANAGEMENT("Project Management"),
    PROTEOMICS("Proteomics"),
    QUANTUM_MECHANICS("Quantum Mechanics"),
    RENEWABLE_ENERGY("Renewable Energy"),
    ROBOTICS("Robotics"),
    SCIENCE("Science"),
    SEAFOOD("Seafood"),
    SECURITY("Security"),
    SEISMOLOGY("Seismology"),
    SHIPBUILDING("Shipbuilding"),
    SIMULATION("Simulation"),
    SOCIAL_MEDIA("Social Media"),
    SOFTWARE_DEVELOPMENT("Software Development"),
    SOFTWARE_ENGINEERING("Software Engineering"),
    SOIL_AND_AIR_POLLUTION("Soil and Air Pollution"),
    STATISTICAL_PHYSICS("Statistical Physics"),
    STATISTICS("Statistics"),
    STRUCTURAL_ENGINEERING("Structural Engineering"),
    SUSTAINABILITY("Sustainability"),
    SYSTEM_ADMINISTRATION("System Administration"),
    SYSTEM_DEVELOPMENT("System Development"),
    SYSTEM_ENGINEERING("System Engineering"),
    TELECOMMUNICATIONS("Telecommunications"),
    TESTING("Testing"),
    TEXTILE_TECHNOLOGY("Textile Technology"),
    THERMODYNAMICS("Thermodynamics"),
    TOPOLOGY("Topology"),
    TOXICOLOGY("Toxicology"),
    TRAFFIC_ENGINEERING("Traffic Engineering"),
    URBAN_PLANNING("Urban Planning"),
    VETERINARY_MEDICINE("Veterinary Medicine"),
    VETERINARY_SCIENCE("Veterinary Science"),
    WASTEWATER_TREATMENT("Wastewater Treatment"),
    WATER_ENGINEERING("Water Engineering"),
    WEB_DESIGN("Web design"),
    WEB_DEVELOPMENT("Web development"),
    WINERY("Winery"),
    WOOD_AND_PAPER_ENGINEERING("Wood and Paper Engineering"),
    WOOD_SCIENCE("Wood Science"),
    ZOOLOGY("Zoology");

    // =========================================================================
    // Private Constructor & functionality
    // =========================================================================

    private final String description;

    Specialization(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }
}
