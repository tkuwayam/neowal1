/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.karaf.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.karaf.commands.internal.RegexUtil;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * Command to stop a route.
 */
@Command(scope = "camel", name = " route-stop", description = "Stop a Camel route or a group of routes.")
public class RouteStop extends OsgiCommandSupport {

    @Argument(index = 0, name = "route", description = "The Camel route ID or a wildcard expression.", required = true, multiValued = false)
    String route;

    @Argument(index = 1, name = "context", description = "The Camel context name.", required = false, multiValued = false)
    String context;
    
    @Option(name = "-t", valueToShowInHelp = "", description = "Specifies the timeout in seconds which is used to stop a route.", required = false, multiValued = false)
    int timeout = -1;


    private CamelController camelController;

    public void setCamelController(CamelController camelController) {
        this.camelController = camelController;
    }

    public Object doExecute() throws Exception {
        List<Route> camelRoutes = camelController.getRoutes(context, RegexUtil.wildcardAsRegex(route));
        if (camelRoutes == null || camelRoutes.isEmpty()) {
            System.err.println("Camel routes using " + route + " not found.");
            return null;
        }
        for (Route camelRoute : camelRoutes) {
            CamelContext camelContext = camelRoute.getRouteContext().getCamelContext();
            if (timeout > 0) {
                camelContext.stopRoute(camelRoute.getId(), timeout, TimeUnit.SECONDS);
            } else {
                camelContext.stopRoute(camelRoute.getId());
            }
        }

        return null;
    }

}
