package com.stirante.justpipe;

import com.stirante.justpipe.exception.RuntimeIOException;
import com.stirante.justpipe.function.IOFunction;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Pipe {

    private InputStream in;
    private final Map<String, Object> metadata;

    public Pipe(InputStream in, Map<String, Object> metadata) {
        this.in = in;
        this.metadata = metadata;
    }

    public Pipe(InputStream in) {
        this(in, new HashMap<>());
    }

    public void to(OutputStream out) throws IOException {
        to(out, true);
    }

    public void to(OutputStream out, boolean close) throws IOException {
        pipe(in, out);
        in.close();
        if (close) {
            out.flush();
            out.close();
        }
    }

    public <T> T to(IOFunction<Pipe, T> converter, boolean close) throws IOException {
        T result = converter.apply(this);
        if (close) {
            in.close();
        }
        return result;
    }

    public <T> T to(IOFunction<Pipe, T> converter) throws IOException {
        return to(converter, true);
    }

    public void to(File out, boolean close) throws IOException {
        to(new FileOutputStream(out), close);
    }

    public void to(Writer out, boolean close) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        to(byteArrayOutputStream, close);
        out.write(byteArrayOutputStream.toString());
    }

    public void to(Writer out) throws IOException {
        to(out, true);
    }

    public void to(File out) throws IOException {
        to(out, true);
    }

    public Pipe through(IOFunction<byte[], byte[]> processor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipe(in, baos);
        in.close();
        in = new ByteArrayInputStream(processor.apply(baos.toByteArray()));
        return this;
    }

    public Stream<Pipe> split(IOFunction<Pipe, List<Pipe>> processor) throws IOException {
        List<Pipe> bytes = processor.apply(this);
        return bytes.stream().peek(pipe -> pipe.copyMetadata(Pipe.this));
    }

    private void copyMetadata(Pipe pipe) {
        for (String s : pipe.metadata.keySet()) {
            if (!metadata.containsKey(s)) {
                metadata.put(s, pipe.metadata.get(s));
            }
        }
    }

    public Pipe with(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    public Object get(String key) {
        return metadata.get(key);
    }

    public boolean has(String key) {
        return metadata.containsKey(key);
    }

    public static Pipe from(URL url) throws IOException {
        return new Pipe(url.openStream());
    }

    public static Pipe from(InputStream in) {
        return new Pipe(in);
    }

    public static Pipe from(String in) {
        return from(new ByteArrayInputStream(in.getBytes()));
    }

    public static Pipe from(File in) throws IOException {
        return from(new FileInputStream(in));
    }

    public static Pipe from(byte[] in) {
        return from(new ByteArrayInputStream(in));
    }

    private static void pipe(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Returns string representation of the input.
     * Note: This method reads all input and closes it
     *
     * @return string representation of the input
     */
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            to(sw);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Returns byte array of the input.
     * Note: This method reads all input and closes it
     *
     * @return byte array of the input
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        to(baos);
        return baos.toByteArray();
    }

}
