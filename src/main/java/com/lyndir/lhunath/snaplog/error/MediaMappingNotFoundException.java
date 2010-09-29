/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.error;

import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;


/**
 * <h2>{@link MediaMappingNotFoundException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 17, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaMappingNotFoundException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final String mapping;

    /**
     * @param mapping The mapping that is no longer available.
     */
    public MediaMappingNotFoundException(final String mapping) {

        this.mapping = mapping;
    }

    /**
     * @return The mapping that is no longer available.
     */
    public String getMapping() {

        return mapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {

        return String.format( "No media found with mapping: %s.", mapping );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message( mapping );
    }

    interface Messages {

        String message(String mapping);
    }
}
