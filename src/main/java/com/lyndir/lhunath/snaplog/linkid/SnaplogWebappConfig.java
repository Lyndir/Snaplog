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
package com.lyndir.lhunath.snaplog.linkid;

import javax.servlet.http.HttpServletRequest;

import net.link.safeonline.sdk.common.configuration.WebappConfig;


/**
 * <h2>{@link SnaplogWebappConfig}<br>
 * <sub>Configuration of the snaplog web application for linkID.</sub></h2>
 *
 * <p>
 * <i>Jan 1, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class SnaplogWebappConfig extends WebappConfig {

    /**
     * Use this WebappConfig implementation.
     */
    public void use() {

        config = this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String appbase() {

        return "http://localhost:8080";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String applandingbase() {

        return "http://localhost:8080";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String authbase() {

        return "https://demo.linkid.be/linkid-auth";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String webappPath(HttpServletRequest request) {

        return "/";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String wsbase() {

        return "https://demo.linkid.be/safe-online-ws";
    }
}
