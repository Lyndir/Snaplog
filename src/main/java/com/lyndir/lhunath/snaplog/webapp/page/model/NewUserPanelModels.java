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
package com.lyndir.lhunath.snaplog.webapp.page.model;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.model.EmptyModelProvider;
import com.lyndir.lhunath.snaplog.util.LinkIDUtils;
import com.lyndir.lhunath.snaplog.webapp.page.NewUserPage.NewUserPanel;


/**
 * <h2>{@link NewUserPanelModels}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 17, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class NewUserPanelModels extends EmptyModelProvider<NewUserPanelModels, NewUserPanel> {

    private NewUserFormModels newUserForm;


    /**
     * <b>Do NOT forget to attach your component before using this model using {@link #attach(NewUserPanel)}</b>
     */
    @Inject
    public NewUserPanelModels() {

        this( null );
    }

    /**
     * @param component
     *            The page we'll attach to.
     */
    public NewUserPanelModels(NewUserPanel component) {

        super( component );

        newUserForm = new NewUserFormModels();
    }


    /**
     * <h2>{@link NewUserFormModels}<br>
     * <sub>Model provider for the New User Form.</sub></h2>
     * 
     * <p>
     * <i>Mar 17, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public class NewUserFormModels extends EmptyModelProvider<NewUserFormModels, Form<?>> {

        private IModel<String> userName;


        NewUserFormModels() {

            userName = new Model<String>() {

                @Override
                public String getObject() {

                    String object = super.getObject();
                    if (object == null)
                        return LinkIDUtils.findSingleAttribute( "device.password.login", String.class );

                    return object;
                }
            };
        }

        // Accessors.

        /**
         * @return A model that holds the user-specified name for the new album.
         */
        public IModel<String> userName() {

            return userName;
        }
    }


    // Accessors.

    /**
     * @return An object that provides models for the newUser form.
     */
    public NewUserFormModels newUserForm() {

        return newUserForm;
    }
}
