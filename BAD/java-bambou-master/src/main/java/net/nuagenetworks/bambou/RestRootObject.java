/*
  Copyright (c) 2015, Alcatel-Lucent Inc
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the copyright holder nor the names of its contributors
        may be used to endorse or promote products derived from this software without
        specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.nuagenetworks.bambou;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.nuagenetworks.bambou.annotation.RestEntity;

public class RestRootObject extends RestObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "userName")
    protected String userName;

    @JsonProperty(value = "password")
    protected String password;

    @JsonProperty(value = "APIKey")
    protected String apiKey;

    private transient String newPassword;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void prepareChangePassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void save(RestSession<?> session) throws RestException {
        if (newPassword != null) {
            password = DigestUtils.sha1Hex(newPassword);
        }

        session.setPassword(newPassword);
        session.setApiKey(null);

        super.save(session);

        newPassword = null;
        session.setPassword(null);
        session.setApiKey(apiKey);
    }

    protected String getResourceUrl(RestSession<?> session) {
        // Get the object's resource name
        RestEntity annotation = getClass().getAnnotation(RestEntity.class);
        String resourceName = annotation.resourceName();

        // Build the base URL
        String url = session.getRestBaseUrl();

        return String.format("%s/%s", url, resourceName);
    }

    protected String getResourceUrlForChildType(RestSession<?> session, Class<?> childRestObjClass) {
        // Get the child object's resource name
        RestEntity annotation = childRestObjClass.getAnnotation(RestEntity.class);
        String resourceName = annotation.resourceName();

        return String.format("%s/%s", session.getRestBaseUrl(), resourceName);
    }

    @Override
    public String toString() {
        return "RestRootObject [userName=" + userName + ", password=" + password + ", apiKey=" + apiKey + ", newPassword=" + newPassword + "]";
    }
}
