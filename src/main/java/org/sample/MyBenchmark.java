package org.sample;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.lang.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import com.fizzed.rocker.ContentType;
import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;

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
	public void writeAndReadByteArray(BenchmarkState s, Blackhole bh) throws IOException {
		ArrayOfByteArraysOutput out = new ArrayOfByteArraysOutput(ContentType.HTML, "UTF-8");
		// write
		for (String l : s.strings) {
			out.w(l);
		}

		byte[] src = out.toByteArray();
		for (int i = 0; i< src.length; i++) {
			bh.consume(src[i]);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void writeAndReadByteChannel(BenchmarkState s, Blackhole bh) throws IOException {
		ArrayOfByteArraysOutput out = new ArrayOfByteArraysOutput(ContentType.HTML, "UTF-8");
		// write
		for (String l : s.strings) {
			out.w(l);
		}

		ReadableByteChannel src = out.asReadableByteChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1);
		while (src.read(buffer) != -1) {
			buffer.flip();
			bh.consume(buffer.get());
			buffer.clear();
		}
	}


	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void writeAndReadInputStream(BenchmarkState s, Blackhole bh) throws IOException {
		ArrayOfByteArraysOutput out = new ArrayOfByteArraysOutput(ContentType.HTML, "UTF-8");
		// write
		for (String l : s.strings) {
			out.w(l);
		}

		InputStream src = out.asInputStream();
		int buffer;
		while ((buffer = src.read()) != -1) {
			bh.consume(buffer);
		}
	}
}
