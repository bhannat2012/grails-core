/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.web.binding.bindingsource

import groovy.transform.CompileStatic
import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.web.mime.MimeType
import org.grails.databinding.CollectionDataBindingSource
import org.grails.databinding.DataBindingSource
import org.grails.databinding.SimpleMapDataBindingSource
import org.grails.databinding.bindingsource.AbstractRequestBodyDataBindingSourceCreator
import org.grails.databinding.bindingsource.DataBindingSourceCreationException
import org.grails.databinding.bindingsource.InvalidRequestBodyException
import org.grails.databinding.xml.GPathResultCollectionDataBindingSource
import org.grails.databinding.xml.GPathResultMap
import org.xml.sax.SAXParseException

/**
 * Creates DataBindingSource objects from XML in the request body
 *
 * @since 2.3
 * @see DataBindingSource
 * @see DataBindingSourceCreator
 */
@CompileStatic
class XmlDataBindingSourceCreator extends AbstractRequestBodyDataBindingSourceCreator {

    @Override
    MimeType[] getMimeTypes() {
        [MimeType.XML, MimeType.TEXT_XML] as MimeType[]
    }

    @Override
    DataBindingSource createDataBindingSource(MimeType mimeType, Class bindingTargetType, Object bindingSource) {
        if(bindingSource instanceof GPathResult) {
            def gpathMap = new GPathResultMap((GPathResult)bindingSource)
            return new SimpleMapDataBindingSource(gpathMap)
        }
        return super.createDataBindingSource(mimeType, bindingTargetType, bindingSource)
    }

    @Override
    protected DataBindingSource createBindingSource(Reader reader) {
        def gpath = new XmlSlurper().parse(reader)
        def gpathMap = new GPathResultMap(gpath)
        return new SimpleMapDataBindingSource(gpathMap)
    }

    @Override
    CollectionDataBindingSource createCollectionDataBindingSource(MimeType mimeType, Class bindingTargetType, Object bindingSource) {
        if(bindingSource instanceof GPathResult) {
            new GPathResultCollectionDataBindingSource(bindingSource)
        }
        else {
            return super.createCollectionDataBindingSource(mimeType, bindingTargetType, bindingSource)
        }
    }

    @Override
    protected CollectionDataBindingSource createCollectionBindingSource(Reader reader) {
        def gpath = new XmlSlurper().parse(reader)
        return new GPathResultCollectionDataBindingSource(gpath)
    }
    
    @Override
    protected DataBindingSourceCreationException createBindingSourceCreationException(Exception e) {
        if(e instanceof SAXParseException) {
            return new InvalidRequestBodyException(e)
        }
        return super.createBindingSourceCreationException(e)
    }

}
