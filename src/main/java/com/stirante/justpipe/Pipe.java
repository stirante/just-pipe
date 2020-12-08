package com.stirante.justpipe;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Pipe {

    private InputStream in;

    public Pipe(InputStream in) {
        this.in = in;
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

    public Pipe through(Function<byte[], byte[]> processor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipe(in, baos);
        in.close();
        in = new ByteArrayInputStream(processor.apply(baos.toByteArray()));
        return this;
    }

    public Stream<Pipe> split(Function<byte[], List<byte[]>> processor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipe(in, baos);
        in.close();
        List<byte[]> bytes = processor.apply(baos.toByteArray());
        return bytes.stream().map(b -> new Pipe(new ByteArrayInputStream(b)));
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
     * Due to conflict with toString method, this method had to be called getString.
     * Note: This method reads all input and closes it
     *
     * @return string representation of the input
     */
    public String getString() throws IOException {
        StringWriter sw = new StringWriter();
        to(sw);
        return sw.toString();
    }

    /**
     * Returns string representation of the input.
     * Note: This method reads all input and closes it
     *
     * @return string representation of the input
     */
    public String toString() {
        try {
            return getString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
