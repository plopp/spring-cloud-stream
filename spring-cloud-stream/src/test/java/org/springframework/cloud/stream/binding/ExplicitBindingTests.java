/*
 * Copyright 2022-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 *
 */
public class ExplicitBindingTests {

	@Test
	public void testExplicitBindings() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(EmptyConfiguration.class))
						.web(WebApplicationType.NONE)
						.run("--spring.jmx.enabled=false",
								"--spring.cloud.stream.input-bindings=fooin;barin",
								"--spring.cloud.stream.output-bindings=fooout;barout")) {

			assertThat(context.getBean("fooin-in-0", MessageChannel.class)).isNotNull();
			assertThat(context.getBean("barin-in-0", MessageChannel.class)).isNotNull();
			assertThat(context.getBean("fooout-out-0", MessageChannel.class)).isNotNull();
			assertThat(context.getBean("barout-out-0", MessageChannel.class)).isNotNull();
		}
	}

	@Test
	public void testExplicitBindingsWithExistingConsumer() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(ConsumerConfiguration.class))
						.web(WebApplicationType.NONE)
						.run("--spring.jmx.enabled=false",
								"--spring.cloud.stream.output-bindings=consume")) {

			assertThat(context.getBean("consume-in-0", MessageChannel.class)).isNotNull();
			assertThat(context.getBean("consume-out-0", MessageChannel.class)).isNotNull();
		}
	}

	@Test
	public void testExplicitBindingsWithExistingSupplier() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(SupplierConfiguration.class))
						.web(WebApplicationType.NONE)
						.run("--spring.jmx.enabled=false",
								"--spring.cloud.stream.input-bindings=supply")) {

			assertThat(context.getBean("supply-in-0", MessageChannel.class)).isNotNull();
			assertThat(context.getBean("supply-out-0", MessageChannel.class)).isNotNull();
		}
	}

	@EnableAutoConfiguration
	@Configuration
	public static class EmptyConfiguration {

	}

	@EnableAutoConfiguration
	@Configuration
	public static class ConsumerConfiguration {

		@Bean
		public Consumer<String> consume() {
			return System.out::println;
		}
	}

	@EnableAutoConfiguration
	@Configuration
	public static class SupplierConfiguration {

		@Bean
		public Supplier<String> supply() {
			return () -> "hello";
		}
	}
}
