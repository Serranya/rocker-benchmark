package org.sample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.fizzed.rocker.ContentType;
import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;
import com.fizzed.rocker.runtime.InputStreamOutput;

public class MyBenchmark {

	@State(Scope.Benchmark)
	public static class BenchmarkState {
		@Param({"100", "1000", "10000"})
		int numberStrings;
		@Param({"50", "500", "5000"})
		int stringLength;

		String[] strings;

		@Setup
		public void setup() {
			strings = new String[numberStrings];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = RandomStringUtils.random(stringLength);
			}
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public InputStream arrayOfByteArrays(BenchmarkState s) throws IOException {
		ArrayOfByteArraysOutput out = new ArrayOfByteArraysOutput(ContentType.HTML, "UTF-8");
		// write
		for (String l : s.strings) {
			out.w(l);
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public InputStream inputStream(BenchmarkState s) throws IOException {
		InputStreamOutput out = new InputStreamOutput(ContentType.HTML, "UTF-8");
		// write
		for (String l : s.strings) {
			out.w(l);
		}

		return out.getStream();
	}
}
