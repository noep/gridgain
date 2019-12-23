/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Selector, t} from 'testcafe';
import {CustomFormField} from '../components/FormField';
import {activeLoadingOverlay} from '../components/ignite-loading'

export const pageSignin = {
    email: new CustomFormField({model: '$ctrl.data.email'}),
    password: new CustomFormField({model: '$ctrl.data.password'}),
    signinButton: Selector('button').withText('Sign In'),
    selector: Selector('page-signin'),
    async login(email, password) {
        return await t
            .expect(activeLoadingOverlay.exists).notOk()
            .typeText(this.email.control, email, {paste: true})
            .typeText(this.password.control, password, {paste: true})
            .click(this.signinButton);
    }
};
