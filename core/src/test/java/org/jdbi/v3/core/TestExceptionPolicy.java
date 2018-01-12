/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jdbi.v3.core;

import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.core.statement.Batch;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.UnableToCreateStatementException;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestExceptionPolicy {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle h = null;
    private Jdbi db = null;

    @Before
    public void setUp() {
        h = dbRule.getSharedHandle();
        db = Jdbi.create(dbRule.getConnectionFactory());

        db.useHandle(handle ->
                handle.execute("CREATE TABLE exception_policy(name VARCHAR2(20) UNIQUE);")
        );
    }

    @Test
    public void testMalformedSQLThrowsException() {
        assertThatExceptionOfType(UnableToCreateStatementException.class).isThrownBy( () ->
                h.execute("SHOULDFAIL")
        );
    }

    @Test
    public void testReplacingExceptionPolicy() {
        ExceptionPolicy customPolicy = new ExceptionPolicy() {};
        db.setExceptionPolicy(customPolicy);

        assertThat(db.getExceptionPolicy()).isEqualTo(customPolicy);
    }

    @Test
    public void testEnsurePolicyIsUsedForCreateStatement() {
        db.setExceptionPolicy(new ExceptionPolicy() {
            @Override
            public JdbiException unableToCreateStatement(String reason, Throwable cause, StatementContext ctx) {
                throw new UnableToCreateStatementException("Custom reason", cause, null);
            }
        });

        assertThatExceptionOfType(UnableToCreateStatementException.class).isThrownBy(() -> db.useHandle(handle ->
                handle.execute("SHOULDFAIL")
        )).withMessage("Custom reason");
    }

    @Test
    public void testEnsurePolicyIsUsedForExecuteStatement() {
        db.setExceptionPolicy(new ExceptionPolicy() {
            @Override
            public JdbiException unableToExecuteStatement(String reason, Throwable cause, StatementContext ctx) {
                throw new UnableToExecuteStatementException("Execute statement", cause, null);
            }
        });

        Batch b = db.open().createBatch()
                .add("insert into exception_policy (name) VALUES ('test')")
                .add("insert into exception_policy (name) VALUES ('test')");
        assertThatExceptionOfType(UnableToExecuteStatementException.class)
                .isThrownBy(b::execute)
                .withMessage("Execute statement");
    }

}
