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
package net.nuagenetworks.bambou.operation;

import java.util.List;

import net.nuagenetworks.bambou.RestException;
import net.nuagenetworks.bambou.RestObject;
import net.nuagenetworks.bambou.RestSession;

public interface RestFetcherOperations<T extends RestObject> {

    List<T> get() throws RestException;

    List<T> fetch() throws RestException;

    T getFirst() throws RestException;

    int count() throws RestException;

    List<T> get(String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit) throws RestException;

    List<T> fetch(String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit) throws RestException;

    T getFirst(String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit) throws RestException;

    int count(String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit) throws RestException;

    List<T> get(RestSession<?> session) throws RestException;

    List<T> fetch(RestSession<?> session) throws RestException;

    T getFirst(RestSession<?> session) throws RestException;

    int count(RestSession<?> session) throws RestException;

    List<T> get(RestSession<?> session, String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit)
            throws RestException;

    List<T> fetch(RestSession<?> session, String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters,
            boolean commit) throws RestException;

    T getFirst(RestSession<?> session, String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit)
            throws RestException;

    int count(RestSession<?> session, String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize, String queryParameters, boolean commit)
            throws RestException;
}
