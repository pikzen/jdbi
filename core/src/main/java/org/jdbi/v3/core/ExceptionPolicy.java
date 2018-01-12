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

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.core.result.ResultSetException;
import org.jdbi.v3.core.result.UnableToProduceResultException;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.UnableToCreateStatementException;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.core.transaction.UnableToManipulateTransactionIsolationLevelException;
import org.jdbi.v3.core.transaction.UnableToRestoreAutoCommitStateException;

import java.sql.SQLException;

/**
 * Controls exceptions thrown by JDBI when errors happen.
 * Overriding this class can allow you to implement database-specific exceptions.
 * In particular, {@link #unableToExecuteStatement(String, Throwable, StatementContext)} can let you parse
 * the exception string to give out more detailed responses to {@link java.sql.SQLException}.
 */
public class ExceptionPolicy {
    public final JdbiException unableToCreateStatement(Throwable cause) throws UnableToCreateStatementException {
        throw this.unableToCreateStatement("Unable to create statement.", cause, null);
    }
    public JdbiException unableToCreateStatement(String reason, Throwable cause, StatementContext ctx)  throws UnableToCreateStatementException{
        throw new UnableToCreateStatementException("Unable to create statement.", cause, ctx);
    }

    public JdbiException unableToExecuteStatement(String reason, Throwable cause, StatementContext ctx) throws UnableToExecuteStatementException {
        throw new UnableToExecuteStatementException(reason, cause, ctx);
    }
    public final JdbiException unableToExecuteStatement(Throwable cause, StatementContext ctx) throws UnableToExecuteStatementException {
        throw this.unableToExecuteStatement("Unable to execute statement", cause, ctx);
    }

    public JdbiException unableToManipulateTransactionIsolationLevel(Integer level, String reason, Throwable cause) {
        if (level == null && reason == null) throw new UnableToManipulateTransactionIsolationLevelException(null, (SQLException) cause);
        if (level != null) throw new UnableToManipulateTransactionIsolationLevelException(level, (SQLException) cause);

        throw new UnableToManipulateTransactionIsolationLevelException(reason, (SQLException) cause);
    }
    public final JdbiException unableToManipulateTransactionIsolationLevel(Integer level, Throwable cause) {
        throw this.unableToManipulateTransactionIsolationLevel(level, null, cause);
    }
    public final JdbiException unableToManipulateTransactionIsolationLevel(String reason, Throwable cause) {
        throw this.unableToManipulateTransactionIsolationLevel(null, reason, cause);
    }

    public JdbiException unableToProduceResult(String reason, Throwable cause, StatementContext ctx) {
        throw new UnableToProduceResultException(reason, cause, ctx);
    }
    public final JdbiException unableToProduceResult(Throwable cause, StatementContext ctx) {
        throw this.unableToProduceResult("Unable to produce result.", cause, ctx);
    }

    public JdbiException unableToRestoreAutoCommitStateException(Throwable cause) {
        throw new UnableToRestoreAutoCommitStateException(cause);
    }

    public JdbiException connection(Throwable cause) {
        throw new ConnectionException(cause);
    }

    public JdbiException transaction(String reason, Throwable cause) {
        throw new TransactionException(reason, cause);
    }
    public final JdbiException transaction(Throwable cause) {
        throw this.transaction(null, cause);
    }
    public final JdbiException transaction(String reason) {
        throw this.transaction(reason, null);
    }

    public JdbiException close(String reason, Throwable cause) {
        throw new CloseException(reason, cause);
    }

    public JdbiException noResults(String reason, Throwable cause, StatementContext ctx) {
        throw new NoResultsException(reason, cause, ctx);
    }

    public JdbiException resultSet(String reason, Throwable cause, StatementContext ctx) {
        throw new ResultSetException(reason, (Exception) cause, ctx);
    }

    public final JdbiException sql(Throwable cause) throws SQLException {
        throw this.sql(null, cause);
    }
    public final JdbiException sql(String reason) throws SQLException {
        throw this.sql(reason, null);
    }
    public JdbiException sql(String reason, Throwable cause) throws SQLException {
        throw new SQLException(reason, cause);
    }
}
