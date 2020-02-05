/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.modules;

import org.apache.james.GuiceModuleTestExtension;
import org.apache.james.linshare.LinshareConfiguration;
import org.apache.james.linshare.LinshareExtension;
import org.apache.james.linshare.LinshareFixture;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class LinshareGuiceExtension implements GuiceModuleTestExtension, ParameterResolver {

    private final LinshareExtension linshareExtension;

    public LinshareGuiceExtension() {
        linshareExtension = new LinshareExtension();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        linshareExtension.beforeAll(extensionContext);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        linshareExtension.beforeEach(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        linshareExtension.getLinshare().stop();
    }

    @Override
    public Module getModule() {
        return Modules.combine(
            binder -> binder.bind(BlobExportImplChoice.class)
                .toInstance(BlobExportImplChoice.LINSHARE),
            binder -> {
                try {
                    binder.bind(LinshareConfiguration.class)
                        .toInstance(linshareExtension.configurationWithBasicAuthFor(
                            new LinshareFixture.Credential(
                                linshareExtension.getTechnicalAccountUUID().toString(),
                                LinshareFixture.TECHNICAL_ACCOUNT.getPassword()))
                        );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return linshareExtension.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return linshareExtension.resolveParameter(parameterContext, extensionContext);
    }
}
