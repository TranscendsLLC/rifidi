/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Decode from a data buffer stream to a {@code String} stream. Before decoding, this decoder
 * realigns the incoming data buffers so that each buffer ends with a newline.
 * This is to make sure that multibyte characters are decoded properly, and do not cross buffer
 * boundaries. The default delimiters ({@code \n}, {@code \r\n})can be customized.
 *
 * <p>Partially inspired by Netty's {@code DelimiterBasedFrameDecoder}.
 *
 * @author Sebastien Deleuze
 * @author Brian Clozel
 * @author Arjen Poutsma
 * @author Mark Paluch
 * @since 5.0
 * @see CharSequenceEncoder
 */
public final class StringDecoder extends AbstractDataBufferDecoder<String> {

	private static final DataBuffer END_FRAME = new DefaultDataBufferFactory().wrap(new byte[0]);

	/** The default charset to use, i.e. "UTF-8". */
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	/** The default delimiter strings to use, i.e. {@code \r\n} and {@code \n}. */
	public static final List<String> DEFAULT_DELIMITERS = Arrays.asList("\r\n", "\n");


	private final List<String> delimiters;

	private final boolean stripDelimiter;

	private final ConcurrentMap<Charset, List<byte[]>> delimitersCache = new ConcurrentHashMap<>();


	private StringDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
		super(mimeTypes);
		Assert.notEmpty(delimiters, "'delimiters' must not be empty");
		this.delimiters = new ArrayList<>(delimiters);
		this.stripDelimiter = stripDelimiter;
	}


	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		return (elementType.resolve() == String.class && super.canDecode(elementType, mimeType));
	}

	@Override
	public Flux<String> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		List<byte[]> delimiterBytes = getDelimiterBytes(mimeType);

		Flux<DataBuffer> inputFlux = Flux.from(inputStream)
				.flatMapIterable(dataBuffer -> splitOnDelimiter(dataBuffer, delimiterBytes))
				.bufferUntil(StringDecoder::isEndFrame)
				.map(StringDecoder::joinUntilEndFrame)
				.doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);

		return super.decode(inputFlux, elementType, mimeType, hints);
	}

	private List<byte[]> getDelimiterBytes(@Nullable MimeType mimeType) {
		return this.delimitersCache.computeIfAbsent(getCharset(mimeType),
				charset -> this.delimiters.stream()
						.map(s -> s.getBytes(charset))
						.collect(Collectors.toList()));
	}

	/**
	 * Split the given data buffer on delimiter boundaries.
	 * The returned Flux contains an {@link #END_FRAME} buffer after each delimiter.
	 */
	private List<DataBuffer> splitOnDelimiter(DataBuffer dataBuffer, List<byte[]> delimiterBytes) {
		List<DataBuffer> frames = new ArrayList<>();
		do {
			int length = Integer.MAX_VALUE;
			byte[] matchingDelimiter = null;
			for (byte[] delimiter : delimiterBytes) {
				int index = indexOf(dataBuffer, delimiter);
				if (index >= 0 && index < length) {
					length = index;
					matchingDelimiter = delimiter;
				}
			}
			DataBuffer frame;
			int readPosition = dataBuffer.readPosition();
			if (matchingDelimiter != null) {
				if (this.stripDelimiter) {
					frame = dataBuffer.slice(readPosition, length);
				}
				else {
					frame = dataBuffer.slice(readPosition, length + matchingDelimiter.length);
				}
				dataBuffer.readPosition(readPosition + length + matchingDelimiter.length);

				frames.add(DataBufferUtils.retain(frame));
				frames.add(END_FRAME);
			}
			else {
				frame = dataBuffer.slice(readPosition, dataBuffer.readableByteCount());
				dataBuffer.readPosition(readPosition + dataBuffer.readableByteCount());
				frames.add(DataBufferUtils.retain(frame));
			}
		}
		while (dataBuffer.readableByteCount() > 0);

		DataBufferUtils.release(dataBuffer);
		return frames;
	}

	/**
	 * Find the given delimiter in the given data buffer.
	 * @return the index of the delimiter, or -1 if not found.
	 */
	private static int indexOf(DataBuffer dataBuffer, byte[] delimiter) {
		for (int i = dataBuffer.readPosition(); i < dataBuffer.writePosition(); i++) {
			int dataBufferPos = i;
			int delimiterPos = 0;
			while (delimiterPos < delimiter.length) {
				if (dataBuffer.getByte(dataBufferPos) != delimiter[delimiterPos]) {
					break;
				}
				else {
					dataBufferPos++;
					if (dataBufferPos == dataBuffer.writePosition() &&
							delimiterPos != delimiter.length - 1) {
						return -1;
					}
				}
				delimiterPos++;
			}
			if (delimiterPos == delimiter.length) {
				return i - dataBuffer.readPosition();
			}
		}
		return -1;
	}

	/**
	 * Check whether the given buffer is {@link #END_FRAME}.
	 */
	private static boolean isEndFrame(DataBuffer dataBuffer) {
		return dataBuffer == END_FRAME;
	}

	/**
	 * Join the given list of buffers into a single buffer.
	 */
	private static DataBuffer joinUntilEndFrame(List<DataBuffer> dataBuffers) {
		if (!dataBuffers.isEmpty()) {
			int lastIdx = dataBuffers.size() - 1;
			if (isEndFrame(dataBuffers.get(lastIdx))) {
				dataBuffers.remove(lastIdx);
			}
		}
		return dataBuffers.get(0).factory().join(dataBuffers);
	}

	@Override
	protected String decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		Charset charset = getCharset(mimeType);
		CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
		DataBufferUtils.release(dataBuffer);
		String value = charBuffer.toString();
		LogFormatUtils.traceDebug(logger, traceOn -> {
			String formatted = LogFormatUtils.formatValue(value, !traceOn);
			return Hints.getLogPrefix(hints) + "Decoded " + formatted;
		});
		return value;
	}

	private static Charset getCharset(@Nullable MimeType mimeType) {
		if (mimeType != null && mimeType.getCharset() != null) {
			return mimeType.getCharset();
		}
		else {
			return DEFAULT_CHARSET;
		}
	}


	/**
	 * Create a {@code StringDecoder} for {@code "text/plain"}.
	 * @param ignored ignored
	 * @deprecated as of Spring 5.0.4, in favor of {@link #textPlainOnly()} or
	 * {@link #textPlainOnly(List, boolean)}
	 */
	@Deprecated
	public static StringDecoder textPlainOnly(boolean ignored) {
		return textPlainOnly();
	}

	/**
	 * Create a {@code StringDecoder} for {@code "text/plain"}.
	 */
	public static StringDecoder textPlainOnly() {
		return textPlainOnly(DEFAULT_DELIMITERS, true);
	}

	/**
	 * Create a {@code StringDecoder} for {@code "text/plain"}.
	 * @param delimiters delimiter strings to use to split the input stream
	 * @param stripDelimiter whether to remove delimiters from the resulting
	 * input strings
	 */
	public static StringDecoder textPlainOnly(List<String> delimiters, boolean stripDelimiter) {
		return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET));
	}

	/**
	 * Create a {@code StringDecoder} that supports all MIME types.
	 * @param ignored ignored
	 * @deprecated as of Spring 5.0.4, in favor of {@link #allMimeTypes()} or
	 * {@link #allMimeTypes(List, boolean)}
	 */
	@Deprecated
	public static StringDecoder allMimeTypes(boolean ignored) {
		return allMimeTypes();
	}

	/**
	 * Create a {@code StringDecoder} that supports all MIME types.
	 */
	public static StringDecoder allMimeTypes() {
		return allMimeTypes(DEFAULT_DELIMITERS, true);
	}

	/**
	 * Create a {@code StringDecoder} that supports all MIME types.
	 * @param delimiters delimiter strings to use to split the input stream
	 * @param stripDelimiter whether to remove delimiters from the resulting
	 * input strings
	 */
	public static StringDecoder allMimeTypes(List<String> delimiters, boolean stripDelimiter) {
		return new StringDecoder(delimiters, stripDelimiter,
				new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
	}

}
