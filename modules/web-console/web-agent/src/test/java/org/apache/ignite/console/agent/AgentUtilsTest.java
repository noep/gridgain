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

package org.apache.ignite.console.agent;

import java.net.URL;
import org.apache.ignite.IgniteException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.apache.ignite.console.agent.AgentUtils.getPasswordFromKeyStore;
import static org.apache.ignite.console.agent.AgentUtils.split;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Agent utils tests.
 */
public class AgentUtilsTest {
    /** Rule for expected exception. */
    @Rule
    public final ExpectedException ruleForExpEx = ExpectedException.none();

    /**
     * @param fileName File name.
     * @return File path.
     */
    private String path(String fileName) {
        URL res = AgentUtilsTest.class.getClassLoader().getResource(fileName);

        assertNotNull(res);

        return res.getPath();
    }

    /**
     * Should return passwords from key store.
     */
    @Test
    public void shouldReturnPasswordFromKeyStore() {
        String path = path("passwords.p12");
        String nodePwd = getPasswordFromKeyStore("node-password", path, "123456");
        String nodeKeyStorePwd = getPasswordFromKeyStore("node-key-store-password", path, "123456");
        String nodeTrustStorePwd = getPasswordFromKeyStore("node-trust-store-password", path, "123456");
        String srvKeyStorePwd = getPasswordFromKeyStore("server-key-store-password", path, "123456");
        String srvTrustStorePwd = getPasswordFromKeyStore("server-trust-store-password", path, "123456");

        assertEquals("1234", nodePwd);
        assertEquals("123456", nodeKeyStorePwd);
        assertEquals("12345678", nodeTrustStorePwd);
        assertEquals("123123", srvKeyStorePwd);
        assertEquals("123123", srvTrustStorePwd);
    }

    /**
     * Should throw exception if store pass is incorrect.
     */
    @Test
    public void shouldThrowExceptionIfStorePassIncorrect() {
        ruleForExpEx.expect(IgniteException.class);
        ruleForExpEx.expectMessage("Failed to read password from key store, please check key store password");

        String path = path("passwords.p12");
        getPasswordFromKeyStore("node-password", path, "12345678");
    }

    /**
     * Should throw exception if store path is incorrect.
     */
    @Test
    public void shouldThrowExceptionIfStorePathIncorrect() {
        ruleForExpEx.expect(IgniteException.class);
        ruleForExpEx.expectMessage("Failed to open passwords key store: /super-key-store.p911");

        getPasswordFromKeyStore("node-password", "/super-key-store.p911", "12345678");
    }

    /**
     * Should throw exception if password name not exists in key sotre.
     */
    @Test
    public void shouldThrowExceptionIfPasswordNotExistsInKeyStore() {
        String name = "node-node-password";
        String path = path("passwords.p12");

        ruleForExpEx.expect(IgniteException.class);
        ruleForExpEx.expectMessage(String.format("Failed to find password in key store: [name=%s, keyStorePath=%s]", name, path));

        getPasswordFromKeyStore(name, path, "123456");
    }

    /**
     * Should throw exception if path is empty.
     */
    @Test
    public void shouldThrowExceptionIfPathIsEmpty() {
        ruleForExpEx.expect(IgniteException.class);
        ruleForExpEx.expectMessage("Empty path to key store with passwords");

        getPasswordFromKeyStore("node-password", "", "123456");
    }

    /**
     * GG-25379 Test case 6: Should correctly split comma-separated string.
     */
    @Test
    public void shouldSplitCorrectly() {
        assertEquals(0, split(null).size());
        assertEquals(0, split("").size());
        assertEquals(0, split(",,,").size());
        assertEquals(0, split(", ,   , ").size());
        assertEquals(3, split("1,2,3, ").size());
        assertEquals(4, split(" 1, 2, 3, 4 ").size());
    }
}