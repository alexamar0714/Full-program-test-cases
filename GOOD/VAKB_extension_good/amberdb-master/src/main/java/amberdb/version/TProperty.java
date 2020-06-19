package amberdb.version;

import amberdb.graph.DataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;


public class TProperty {

    
    private TId id;
    private String name; 
    private Object value;
    
    
    // session constructor
    public TProperty(TId id, String name, Object value) {
        this.id = id;
        this.name = name;
        this.value = value;
    } 
    
    
    public TId getId() {
        return id;
    }
    
    
    public String getName() {
        return name;
    }

    
    public Object getValue() {
        return value;
    }
    
    
    public static byte[] encode(Object value) {
        if (value instanceof String) {
            return ((String) value).getBytes(Charset.forName("UTF-8"));
        } else if (value instanceof Integer) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((int) value);
            return bb.array();
        } else if (value instanceof Long) {
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putLong((long) value);
            return bb.array();
        } else if (value instanceof Boolean) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((boolean) value ? 1 : 0);
            return bb.array();
        } else if (value instanceof Float) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putFloat((float) value);
            return bb.array();
        } else if (value instanceof Double) {
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putDouble((double) value);
            return bb.array();
        } else if (value instanceof Date) { 
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putLong(((Date) value).getTime());
            return bb.array();
        } else if (value instanceof Serializable) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                return baos.toByteArray();
            } catch (IOException e) {
                    throw new RuntimeException("Failed to encode serialized type " + value.getClass().getName(), e);
            }
        } else if (value == null) {
            return null;
        } else {
            throw new RuntimeException("Type not supported for encoding property: " + value.getClass());
        }
    }

    
    public static Object decode(byte[] blob, DataType type) {

        if (type == DataType.STR) return new String(blob, Charset.forName("UTF-8"));

        ByteBuffer bb = ByteBuffer.wrap(blob);
        if (type == DataType.INT) return bb.asIntBuffer().get();
        if (type == DataType.BLN) return bb.asIntBuffer().get() == 1 ? true : false;
        if (type == DataType.LNG) return bb.asLongBuffer().get();
        if (type == DataType.FLT) return bb.asFloatBuffer().get();
        if (type == DataType.DBL) return bb.asDoubleBuffer().get();
        if (type == DataType.DTE) return new Date(bb.asLongBuffer().get());
        if (type == DataType.SER) {
            try {
                return new ObjectInputStream(new ByteArrayInputStream(bb.array())).readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to decode serialized type ",e);
            }
        }
        throw new RuntimeException("Type not supported for decoding property: " + type.toString());
    }
}
