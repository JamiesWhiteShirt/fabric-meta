/*
 * Copyright (c) 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.meta.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.function.Function;
import java.util.function.Supplier;

public class WebServer {

	public static Javalin javalin;
	public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void start() {
		javalin = Javalin.create()
			.enableRouteOverview("/")
			.disableStartupBanner()
			.start(5555);

		EndpointsV1.setup();
		EndpointsV2.setup();
	}

	public static <T> void jsonGet(String route, Supplier<T> supplier) {
		javalin.get(route, ctx -> {
			T object = supplier.get();
			handleJson(ctx, object);
		});
	}

	public static <T> void jsonGet(String route, Function<Context, T> supplier) {
		javalin.get(route, ctx -> {
			T object = supplier.apply(ctx);
			handleJson(ctx, object);
		});
	}

	private static void handleJson(Context ctx, Object object) {
		if (object == null) {
			object = new Object();
			ctx.status(400);
		}
		String response = GSON.toJson(object);
		ctx.contentType("application/json").result(response);
	}

}
