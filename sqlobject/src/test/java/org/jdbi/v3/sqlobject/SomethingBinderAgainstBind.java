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
package org.jdbi.v3.sqlobject;

import java.lang.reflect.Parameter;

import org.jdbi.v3.SqlStatement;
import org.jdbi.v3.Something;

public class SomethingBinderAgainstBind implements Binder<Bind, Something>
{
    @Override
    public void bind(SqlStatement<?> q, Parameter param, int index, Bind bind, Something it)
    {
        q.bind(bind.value() + ".id", it.getId());
        q.bind(bind.value() + ".name", it.getName());
    }
}
