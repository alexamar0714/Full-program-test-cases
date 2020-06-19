package org.hl7.fhir.dstu3.model.codesystems;

/*
  Copyright (c) 2011+, HL7, Inc.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, 
  are permitted provided that the following conditions are met:
  
   * Redistributions of source code must retain the above copyright notice, this 
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, 
     this list of conditions and the following disclaimer in the documentation 
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
  
*/

// Generated on Sat, Mar 25, 2017 21:03-0400 for FHIR v3.0.0


import org.hl7.fhir.exceptions.FHIRException;

public enum V3IntegrityCheckAlgorithm {

        /**
         * This algorithm is defined in FIPS PUB 180-1: Secure Hash Standard.  As of April 17, 1995.
         */
        SHA1, 
        /**
         * This algorithm is defined in FIPS PUB 180-2: Secure Hash Standard.
         */
        SHA256, 
        /**
         * added to help the parsers
         */
        NULL;
        public static V3IntegrityCheckAlgorithm fromCode(String codeString) throws FHIRException {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("SHA-1".equals(codeString))
          return SHA1;
        if ("SHA-256".equals(codeString))
          return SHA256;
        throw new FHIRException("Unknown V3IntegrityCheckAlgorithm code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case SHA1: return "SHA-1";
            case SHA256: return "SHA-256";
            default: return "?";
          }
        }
        public String getSystem() {
          return "http://hl7.org/fhir/v3/IntegrityCheckAlgorithm";
        }
        public String getDefinition() {
          switch (this) {
            case SHA1: return "This algorithm is defined in FIPS PUB 180-1: Secure Hash Standard.  As of April 17, 1995.";
            case SHA256: return "This algorithm is defined in FIPS PUB 180-2: Secure Hash Standard.";
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case SHA1: return "secure hash algorithm - 1";
            case SHA256: return "secure hash algorithm - 256";
            default: return "?";
          }
    }


}

